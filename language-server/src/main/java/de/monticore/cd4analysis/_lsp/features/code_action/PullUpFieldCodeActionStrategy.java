package de.monticore.cd4analysis._lsp.features.code_action;

import de.mclsg.PositionUtils;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_action.visitor.FindClassVisitor;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class PullUpFieldCodeActionStrategy implements CodeActionStrategy {
  private final DocumentManager documentManager;
  private final ISymbolUsageResolutionProvider symbolUsageResolutionProvider;
  private final AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter;

  public PullUpFieldCodeActionStrategy(
      DocumentManager documentManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider,
      AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter) {
    this.documentManager = documentManager;
    this.symbolUsageResolutionProvider = symbolUsageResolutionProvider;
    this.prettyPrinter = prettyPrinter;
  }

  @Override
  public Optional<Either<Command, CodeAction>> apply(
      TextDocumentItem document, CodeActionContext context, Range range) {
    Optional<FieldSymbol> fieldSymbol =
        documentManager
            .getDocumentInformation(document)
            .flatMap(
                documentInformation ->
                    documentInformation
                        .getMatchedToken(range.getStart())
                        .filter(
                            matchedToken ->
                                matchedToken.tokenPathMatches(".*.cDMember.cDAttribute"))
                        .map(
                            matchedToken ->
                                symbolUsageResolutionProvider.getSymbols(
                                    documentInformation, matchedToken)))
            .filter(symbols -> !symbols.isEmpty())
            .map(symbols -> symbols.get(0))
            .filter(FieldSymbol.class::isInstance)
            .map(FieldSymbol.class::cast);

    if (fieldSymbol.isEmpty()) return Optional.empty();

    IScopeSpanningSymbol spanningSymbol = fieldSymbol.get().getEnclosingScope().getSpanningSymbol();
    if (!(spanningSymbol instanceof CDTypeSymbol)) return Optional.empty();

    CDTypeSymbol classSymbol = (CDTypeSymbol) spanningSymbol;
    if (!classSymbol.isPresentSuperClass()
        || !(classSymbol.getSuperClass().getTypeInfo() instanceof CDTypeSymbol))
      return Optional.empty();

    CDTypeSymbol superClassSymbol = (CDTypeSymbol) classSymbol.getSuperClass().getTypeInfo();

    Optional<String> classLocation = documentManager.getLocation(classSymbol);
    Optional<String> superClassLocation = documentManager.getLocation(superClassSymbol);
    if (classLocation.isEmpty() || superClassLocation.isEmpty()) return Optional.empty();

    AtomicReference<String> fieldName = new AtomicReference<>(fieldSymbol.get().getName());
    int appendix = 0;
    while (superClassSymbol.getFieldList().stream()
        .anyMatch(field -> field.getName().equals(fieldName.get()))) {
      appendix++;
      fieldName.set(fieldSymbol.get().getName() + appendix);
    }

    ASTCDCompilationUnit classAst =
        (ASTCDCompilationUnit)
            documentManager
                .getDocumentInformation(classLocation.get())
                .orElseThrow()
                .ast
                .deepClone();
    ASTCDCompilationUnit superClassAst =
        (ASTCDCompilationUnit)
            (classLocation.equals(superClassLocation)
                ? classAst
                : documentManager
                    .getDocumentInformation(superClassLocation.get())
                    .orElseThrow()
                    .ast
                    .deepClone());

    ASTCDAttribute field = (ASTCDAttribute) fieldSymbol.get().getAstNode().deepClone();
    ASTCDType changedClass = FindClassVisitor.findClass(classAst, classSymbol.getAstNode());
    List<ASTCDAttribute> changedAttributes =
        changedClass.getCDAttributeList().stream()
            .filter(astcdAttribute -> !astcdAttribute.deepEquals(field))
            .collect(Collectors.toList());
    changedClass.setCDAttributeList(changedAttributes);
    field.setName(fieldName.get());
    if (field.getModifier().isPrivate()) field.getModifier().setPrivate(false);

    ASTCDType changedSuperClass =
        FindClassVisitor.findClass(superClassAst, superClassSymbol.getAstNode());
    changedSuperClass.addCDMember(field);

    CodeAction codeAction = new CodeAction("Pull Up Field");
    WorkspaceEdit workspaceEdit = new WorkspaceEdit();
    codeAction.setEdit(workspaceEdit);
    codeAction.setKind(CodeActionKind.Refactor);

    String printedClass = prettyPrinter.prettyPrint(classAst);
    TextEdit classEdit = new TextEdit(PositionUtils.toRange(classAst).orElseThrow(), printedClass);
    workspaceEdit.getChanges().put(classLocation.get(), List.of(classEdit));

    if (classLocation.equals(superClassLocation)) return Optional.of(Either.forRight(codeAction));

    String printedSuperClass = prettyPrinter.prettyPrint(superClassAst);
    TextEdit superClassEdit =
        new TextEdit(PositionUtils.toRange(superClassAst).orElseThrow(), printedSuperClass);
    workspaceEdit.getChanges().putIfAbsent(superClassLocation.get(), List.of(superClassEdit));

    return Optional.of(Either.forRight(codeAction));
  }
}
