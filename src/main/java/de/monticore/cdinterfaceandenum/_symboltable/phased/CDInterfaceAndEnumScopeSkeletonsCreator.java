/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbolBuilder;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCreatorTOP;
import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumScope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumScopeSkeletonsCreator
    extends CDInterfaceAndEnumScopeSkeletonsCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDInterfaceAndEnumScopeSkeletonsCreator(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDInterfaceAndEnumScopeSkeletonsCreator(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void visit(ASTCDInterface node) {
    super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  public void visit(ASTCDEnum node) {
    super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  protected void initialize_CDInterface(CDTypeSymbolBuilder symbol, ASTCDInterface ast) {
    super.initialize_CDInterface(symbol, ast);
    symbol.setIsInterface(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDEnum(CDTypeSymbolBuilder symbol, ASTCDEnum ast) {
    super.initialize_CDEnum(symbol, ast);
    symbol.setIsEnum(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDEnumConstant(FieldSymbolBuilder symbol, ASTCDEnumConstant ast) {
    super.initialize_CDEnumConstant(symbol, ast);
    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, scopeStack.getLast());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
