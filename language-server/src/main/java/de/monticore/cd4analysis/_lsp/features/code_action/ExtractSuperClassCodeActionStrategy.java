package de.monticore.cd4analysis._lsp.features.code_action;

import de.mclsg.PositionUtils;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_action.visitor.FindClassVisitor;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDExtendUsageBuilder;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolTOP;
import de.monticore.symboltable.IScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedTypeBuilder;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
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

public class ExtractSuperClassCodeActionStrategy implements CodeActionStrategy {
  private final DocumentManager documentManager;
  private final AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter;

  public ExtractSuperClassCodeActionStrategy(
      DocumentManager documentManager, AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter) {
    this.documentManager = documentManager;
    this.prettyPrinter = prettyPrinter;
  }

  @Override
  public Optional<Either<Command, CodeAction>> apply(
      TextDocumentItem document, CodeActionContext context, Range range) {
    Optional<DocumentInformation> documentInformation =
        documentManager.getDocumentInformation(document);
    if (documentInformation.isEmpty()) return Optional.empty();

    List<CDTypeSymbol> symbols =
        documentInformation.get().symbols.stream()
            .filter(
                symbol ->
                    PositionUtils.toRange(symbol)
                        .filter(symbolRange -> PositionUtils.contains(range, symbolRange))
                        .isPresent())
            .filter(CDTypeSymbol.class::isInstance)
            .map(CDTypeSymbol.class::cast)
            .filter(symbol -> symbol.getAstNode() instanceof ASTCDClass)
            .filter(symbol -> !symbol.isPresentSuperClass())
            .collect(Collectors.toList());

    if (symbols.size() < 2) return Optional.empty();

    // Only consider classes within the same level in the hierarchy.
    IScope firstScope = symbols.get(0).getEnclosingScope();
    symbols =
        symbols.stream()
            .filter(symbol -> symbol.getEnclosingScope() == firstScope)
            .collect(Collectors.toList());

    if (symbols.size() < 2) return Optional.empty();

    List<ASTField> equalFields =
        symbols.get(0).getFieldList().stream()
            .map(FieldSymbolTOP::getAstNode)
            .collect(Collectors.toList());
    BiPredicate<ASTField, ASTCDType> equalsFieldsContains =
        (equalsField, symbol) ->
            symbol.getCDAttributeList().stream().anyMatch(field -> field.deepEquals(equalsField));

    List<ASTField> unequalFields =
        symbols.stream()
            .flatMap(
                symbol ->
                    equalFields.stream()
                        .filter(
                            field ->
                                equalsFieldsContains.negate().test(field, symbol.getAstNode())))
            .collect(Collectors.toList());
    equalFields.removeAll(unequalFields);

    if (equalFields.isEmpty()) return Optional.empty();

    ASTCDClassBuilder builder =
        new ASTCDClassBuilder().setModifier(new ASTModifierBuilder().build()).setName("SuperClass");
    equalFields.forEach(field -> builder.addCDMember((ASTCDMember) field));

    ASTCDClass superClass = builder.build();
    ASTCDCompilationUnit compilationUnit =
        (ASTCDCompilationUnit) documentInformation.get().ast.deepClone();
    compilationUnit.getCDDefinition().addCDElement(superClass);

    for (CDTypeSymbol symbol : symbols) {
      ASTCDClass clazz =
          (ASTCDClass) FindClassVisitor.findClass(compilationUnit, symbol.getAstNode());
      clazz
          .getCDMemberList()
          .removeIf(
              member ->
                  member instanceof ASTField
                      && equalsFieldsContains.test((ASTField) member, superClass));
      clazz.setCDExtendUsage(
          new ASTCDExtendUsageBuilder()
              .addSuperclass(
                  new ASTMCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                          new ASTMCQualifiedNameBuilder()
                              .setPartsList(List.of(superClass.getName()))
                              .build())
                      .build())
              .build());
    }

    String printed = prettyPrinter.prettyPrint(compilationUnit);
    TextEdit classEdit =
        new TextEdit(PositionUtils.toRange(compilationUnit).orElseThrow(), printed);
    WorkspaceEdit workspaceEdit = new WorkspaceEdit(Map.of(document.getUri(), List.of(classEdit)));

    CodeAction codeAction = new CodeAction("Extract Superclass");
    codeAction.setEdit(workspaceEdit);
    codeAction.setKind(CodeActionKind.Refactor);

    return Optional.of(Either.forRight(codeAction));
  }
}
