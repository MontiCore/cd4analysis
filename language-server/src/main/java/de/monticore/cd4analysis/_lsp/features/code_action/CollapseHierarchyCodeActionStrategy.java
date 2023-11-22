package de.monticore.cd4analysis._lsp.features.code_action;

import de.mclsg.PositionUtils;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd4analysis._lsp.features.code_action.visitor.DeleteClassVisitor;
import de.monticore.cd4analysis._lsp.features.code_action.visitor.FindClassVisitor;
import de.monticore.cdbasis._ast.ASTCDAttributeTOP;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.prettyprint.AstPrettyPrinter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;

public class CollapseHierarchyCodeActionStrategy implements CodeActionStrategy {
  private final DocumentManager documentManager;
  private final AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter;
  private final ISymbolUsageResolutionProvider symbolUsageResolutionProvider;

  public CollapseHierarchyCodeActionStrategy(
      DocumentManager documentManager,
      AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    this.documentManager = documentManager;
    this.prettyPrinter = prettyPrinter;
    this.symbolUsageResolutionProvider = symbolUsageResolutionProvider;
  }

  @Override
  public Optional<Either<Command, CodeAction>> apply(
      TextDocumentItem document, CodeActionContext context, Range range) {
    Optional<DocumentInformation> information = documentManager.getDocumentInformation(document);
    if (information.isEmpty()) return Optional.empty();

    Optional<CDTypeSymbol> classSymbol =
        information
            .flatMap(
                info ->
                    info.getMatchedToken(range.getStart())
                        .map(
                            matchedToken ->
                                symbolUsageResolutionProvider.getSymbols(info, matchedToken)))
            .filter(symbols -> !symbols.isEmpty())
            .map(symbols -> symbols.get(0))
            .filter(CDTypeSymbol.class::isInstance)
            .map(CDTypeSymbol.class::cast);

    if (classSymbol.isEmpty()) return Optional.empty();

    if (!classSymbol.get().isPresentSuperClass()
        || !(classSymbol.get().getSuperClass().getTypeInfo() instanceof CDTypeSymbol))
      return Optional.empty();

    CDTypeSymbol superClassSymbol = (CDTypeSymbol) classSymbol.get().getSuperClass().getTypeInfo();
    Optional<String> classLocation = documentManager.getLocation(classSymbol.get());
    Optional<String> superClassLocation = documentManager.getLocation(superClassSymbol);

    if (superClassLocation.isEmpty()) {
      return Optional.empty();
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

    ASTCDType type = FindClassVisitor.findClass(superClassAst, superClassSymbol.getAstNode());

    if (type == null) return Optional.empty();

    List<String> attributes =
        type.getCDAttributeList().stream()
            .map(ASTCDAttributeTOP::getName)
            .collect(Collectors.toList());

    classSymbol.get().getAstNode().getCDAttributeList().stream()
        .filter(a -> attributes.contains(a.getName()))
        .forEach(
            a -> {
              String name = a.getName();
              int index =
                  IntStream.range(1, Integer.MAX_VALUE)
                      .filter(i -> !attributes.contains(name + i))
                      .findFirst()
                      .getAsInt();
              a.setName(name + index);
            });

    type.addAllCDMembers(
        classSymbol.get().getAstNode().getCDMemberList(CDMemberVisitor.Options.ALL));

    DeleteClassVisitor.deleteClass(classAst, classSymbol.get().getAstNode());

    CodeAction codeAction = new CodeAction("Collapse Hierarchy");
    WorkspaceEdit workspaceEdit = new WorkspaceEdit();
    workspaceEdit.getChanges().put(document.getUri(), getChanges(classAst));
    workspaceEdit.getChanges().put(superClassLocation.get(), getChanges(superClassAst));
    codeAction.setEdit(workspaceEdit);
    codeAction.setKind(CodeActionKind.RefactorRewrite);

    return Optional.of(Either.forRight(codeAction));
  }

  @NotNull
  private List<TextEdit> getChanges(ASTCDCompilationUnit compilationUnit) {
    String printed = prettyPrinter.prettyPrint(compilationUnit);

    return List.of(new TextEdit(PositionUtils.toRange(compilationUnit).orElseThrow(), printed));
  }
}
