/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSymbolTableCompleter
    implements CDInterfaceAndEnumVisitor2 {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDInterfaceAndEnumSymbolTableCompleter(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CDInterfaceAndEnumSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper()
        .setPackageDeclaration(packageDeclaration);
  }

  @Override
  public void visit(ASTCDInterface node) {
    CDInterfaceAndEnumVisitor2.super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    CDInterfaceAndEnumVisitor2.super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
    initialize_CDInterface(node);
  }

  @Override
  public void visit(ASTCDEnum node) {
    CDInterfaceAndEnumVisitor2.super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    CDInterfaceAndEnumVisitor2.super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
    initialize_CDEnum(node);
  }

  @Override
  public void endVisit(ASTCDEnumConstant node) {
    initialize_CDEnumConstant(node);
  }

  protected void initialize_CDInterface(ASTCDInterface ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsInterface(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(ast.getCDExtendUsage().streamSuperclass().map(s -> {
        final TypeCheckResult result = symbolTableHelper.getTypeChecker().synthesizeType(s);
        if (!result.isPresentCurrentResult()) {
          Log.error(String.format("0xCDA30: The type of the extended interfaces (%s) could not be calculated", symbolTableHelper.getPrettyPrinter().prettyprint(s)), s.get_SourcePositionStart());
        }
        return result;
      }).filter(TypeCheckResult::isPresentCurrentResult).map(TypeCheckResult::getCurrentResult).collect(Collectors.toList()));
    }
  }

  protected void initialize_CDEnum(ASTCDEnum ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsEnum(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(ast.getCDInterfaceUsage().streamInterface().map(s -> {
        final TypeCheckResult result = symbolTableHelper.getTypeChecker().synthesizeType(s);
        if (!result.isPresentCurrentResult()) {
          Log.error(String.format("0xCDA31: The type of the interface (%s) could not be calculated", s.getClass().getSimpleName()), s.get_SourcePositionStart());
        }
        return result;
      }).filter(TypeCheckResult::isPresentCurrentResult).map(TypeCheckResult::getCurrentResult).collect(Collectors.toList()));
    }
  }

  protected void initialize_CDEnumConstant(ASTCDEnumConstant ast) {
    // this is probably dead code, since it is never executed
    FieldSymbol symbol = ast.getSymbol();

    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    // call getEnclosingScope() twice, to achieve the correct package name
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, ast.getEnclosingScope().getEnclosingScope());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
