/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor2;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.stream.Collectors;

public class CDBasisSymbolTableCompleter implements CDBasisVisitor2, OOSymbolsVisitor2 {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDBasisSymbolTableCompleter(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CDBasisSymbolTableCompleter(
      List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper().setPackageDeclaration(packageDeclaration);
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    CDBasisVisitor2.super.visit(node);
    final ICDBasisScope artifactScope = node.getCDDefinition().getEnclosingScope();
    if (artifactScope instanceof ICD4AnalysisArtifactScope) {
      ((ICD4AnalysisArtifactScope) artifactScope)
          .setImportsList(
              node.getMCImportStatementList().stream()
                  .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                  .collect(Collectors.toList()));
    }
  }

  @Override
  public void visit(ASTCDClass node) {
    symbolTableHelper.addToCDTypeStack(node.getName());

    final CDTypeSymbol symbol = node.getSymbol();

    if (node.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(
          node.getCDExtendUsage()
              .streamSuperclass()
              .map(
                  s -> {
                    final TypeCheckResult result =
                        symbolTableHelper.getTypeSynthesizer().synthesizeType(s);
                    if (!result.isPresentResult()) {
                      Log.error(
                          String.format(
                              "0xCDA00: The type of the extended classes (%s) could not be calculated",
                              symbolTableHelper.getPrettyPrinter().prettyprint(s)),
                          s.get_SourcePositionStart());
                    }
                    return result;
                  })
              .filter(TypeCheckResult::isPresentResult)
              .map(TypeCheckResult::getResult)
              .collect(Collectors.toList()));
    }

    if (node.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(
          node.getCDInterfaceUsage()
              .streamInterface()
              .map(
                  s -> {
                    final TypeCheckResult result =
                        symbolTableHelper.getTypeSynthesizer().synthesizeType(s);
                    if (!result.isPresentResult()) {
                      Log.error(
                          String.format(
                              "0xCDA01: The type of the interface (%s) could not be calculated",
                              s.getClass().getSimpleName()),
                          s.get_SourcePositionStart());
                    }
                    return result;
                  })
              .filter(TypeCheckResult::isPresentResult)
              .map(TypeCheckResult::getResult)
              .collect(Collectors.toList()));
    }
  }

  @Override
  public void endVisit(ASTCDClass node) {
    assert node.getSymbol() != null;
    initialize_CDClass(node);
    symbolTableHelper.removeFromCDTypeStack();
    CDBasisVisitor2.super.endVisit(node);
  }

  protected void initialize_CDClass(ASTCDClass ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsClass(true);
    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  public void visit(ASTCDAttribute node) {
    final FieldSymbol symbol = node.getSymbol();

    // Compute the !final! SymTypeExpression for the type of the field
    final TypeCheckResult typeResult =
        symbolTableHelper.getTypeSynthesizer().synthesizeType(node.getMCType());
    if (!typeResult.isPresentResult()) {
      Log.error(
          String.format(
              "0xCDA02: The type (%s) of the attribute (%s) could not be calculated",
              symbolTableHelper.getPrettyPrinter().prettyprint(node.getMCType()), node.getName()),
          node.getMCType().get_SourcePositionStart());
    } else {
      symbol.setType(typeResult.getResult());
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    assert node.getSymbol() != null;
    initialize_CDAttribute(node);
    CDBasisVisitor2.super.endVisit(node);
  }

  protected void initialize_CDAttribute(ASTCDAttribute ast) {
    FieldSymbol symbol = ast.getSymbol();
    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  /*
  The following visit methods must be overriden because both implemented interface
  provide default methods for the visit methods.
   */
  public void visit(de.monticore.symboltable.ISymbol node) {}

  public void endVisit(de.monticore.symboltable.ISymbol node) {}

  public void endVisit(de.monticore.ast.ASTNode node) {}

  public void visit(de.monticore.ast.ASTNode node) {}

  public void visit(de.monticore.symboltable.IScope node) {}

  public void endVisit(de.monticore.symboltable.IScope node) {}
}
