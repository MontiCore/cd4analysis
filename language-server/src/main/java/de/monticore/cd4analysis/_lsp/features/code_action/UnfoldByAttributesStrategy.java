package de.monticore.cd4analysis._lsp.features.code_action;

import de.mclsg.PositionUtils;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_action.visitor.FindClassVisitor;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedTypeBuilder;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class UnfoldByAttributesStrategy implements CodeActionStrategy {
  private final DocumentManager documentManager;
  private final AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter;

  public UnfoldByAttributesStrategy(
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

    List<FieldSymbol> fieldSymbols =
        documentInformation.get().symbols.stream()
            .filter(
                symbol ->
                    PositionUtils.toRange(symbol)
                        .filter(symbolRange -> PositionUtils.contains(range, symbolRange))
                        .isPresent())
            .filter(FieldSymbol.class::isInstance)
            .map(FieldSymbol.class::cast)
            .filter(symbol -> symbol.getAstNode() instanceof ASTCDAttribute)
            .collect(Collectors.toList());

    if (fieldSymbols.size() == 0) return Optional.empty();

    // Only refactor if attributes are within the same class.
    final int selectedAttributes = fieldSymbols.size();
    IScope firstScope = fieldSymbols.get(0).getEnclosingScope();
    fieldSymbols =
        fieldSymbols.stream()
            .filter(symbol -> symbol.getEnclosingScope() == firstScope)
            .collect(Collectors.toList());

    if (fieldSymbols.size() < selectedAttributes) return Optional.empty();

    final String className = firstScope.getName();
    String superClassName = "Super" + className;

    // check if new name already exists
    List<String> allClassSymbolNames =
        documentInformation.get().symbols.stream()
            .filter(CDTypeSymbol.class::isInstance)
            .map(CDTypeSymbol.class::cast)
            .filter(symbol -> symbol.getAstNode() instanceof ASTCDClass)
            .map(symbol -> symbol.getName())
            .collect(Collectors.toList());

    if (allClassSymbolNames.contains(superClassName)) {
      int extension = 1;
      while (allClassSymbolNames.contains(superClassName + extension)) {
        extension++;
      }
      superClassName = superClassName + extension;
    }

    ASTCDClassBuilder builder =
        new ASTCDClassBuilder()
            .setModifier(new ASTModifierBuilder().build())
            .setName(superClassName);
    fieldSymbols.forEach(field -> builder.addCDMember((ASTCDMember) field.getAstNode()));

    ASTCDCompilationUnit compilationUnit =
        (ASTCDCompilationUnit) documentInformation.get().ast.deepClone();

    List<ASTCDAttribute> attributes =
        fieldSymbols.stream()
            .map(symbol -> (ASTCDAttribute) symbol.getAstNode())
            .collect(Collectors.toList());

    ASTCDClass clazz =
        (ASTCDClass)
            FindClassVisitor.findClass(compilationUnit, (ASTCDType) firstScope.getAstNode());
    for (ASTCDAttribute attr : attributes) {
      clazz
          .getCDMemberList()
          .removeIf(
              member ->
                  member instanceof ASTCDAttribute
                      && attr.getName().equals(((ASTCDAttribute) member).getName()));
    }

    // Check if original class has superclass, if yes, take superclass over to new superclass
    String origSuperClassName = null;
    if (clazz.getSuperclassList().size() > 0) {
      List<CDTypeSymbol> classSymbols =
          documentInformation.get().symbols.stream()
              .filter(CDTypeSymbol.class::isInstance)
              .map(CDTypeSymbol.class::cast)
              .filter(symbol -> symbol.getAstNode() instanceof ASTCDClass)
              .filter(symbol -> symbol.getAstNode().getName().equals(className))
              .filter(symbol -> symbol.isPresentSuperClass())
              .collect(Collectors.toList());
      if (classSymbols.size() == 1)
        origSuperClassName = classSymbols.get(0).getSuperTypes(0).getTypeInfo().getName();
    }

    clazz.setCDExtendUsage(
        new ASTCDExtendUsageBuilder()
            .addSuperclass(
                new ASTMCQualifiedTypeBuilder()
                    .setMCQualifiedName(
                        new ASTMCQualifiedNameBuilder()
                            .setPartsList(List.of(superClassName))
                            .build())
                    .build())
            .build());

    if (origSuperClassName != null) {
      builder.setCDExtendUsage(
          new ASTCDExtendUsageBuilder()
              .addSuperclass(
                  new ASTMCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                          new ASTMCQualifiedNameBuilder()
                              .setPartsList(List.of(origSuperClassName))
                              .build())
                      .build())
              .build());
    }

    ASTCDClass superClass = builder.build();
    compilationUnit.getCDDefinition().addCDElement(superClass);

    String printed = prettyPrinter.prettyPrint(compilationUnit);
    TextEdit classEdit =
        new TextEdit(PositionUtils.toRange(compilationUnit).orElseThrow(), printed);
    WorkspaceEdit workspaceEdit = new WorkspaceEdit(Map.of(document.getUri(), List.of(classEdit)));

    CodeAction codeAction = new CodeAction("Unfold attributes");
    codeAction.setEdit(workspaceEdit);
    codeAction.setKind(CodeActionKind.Refactor);

    return Optional.of(Either.forRight(codeAction));
  }
}
