/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbolLoader;
import de.monticore.cdbasis.modifier.ModifierHandler;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSymbolTableCreator
    extends CDInterfaceAndEnumSymbolTableCreatorTOP {
  protected DeriveSymTypeOfCDBasis typeCheck;
  protected ModifierHandler modifierHandler;
  protected Stack<String> enumStack;

  public CDInterfaceAndEnumSymbolTableCreator(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDInterfaceAndEnumSymbolTableCreator(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
    init();
  }

  private void init() {
    typeCheck = new DeriveSymTypeOfCDBasis();
    modifierHandler = new ModifierHandler();
    enumStack = new Stack<>();
  }

  public DeriveSymTypeOfCDBasis getTypeCheck() {
    return this.typeCheck;
  }

  public ModifierHandler getModifierHandler() {
    return this.modifierHandler;
  }

  public void setTypeCheck(DeriveSymTypeOfCDBasis typeCheck) {
    this.typeCheck = typeCheck;
  }

  public void setModifierHandler(ModifierHandler modifierHandler) {
    this.modifierHandler = modifierHandler;
  }

  public Stack<String> getEnumStack() {
    return this.enumStack;
  }

  public void setEnumStack(Stack<String> enumStack) {
    this.enumStack = enumStack;
  }

  @Override
  public void visit(ASTCDEnum node) {
    super.visit(node);
    getEnumStack().add(node.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    getEnumStack().pop();
  }

  @Override
  protected void initialize_CDInterface(CDTypeSymbol symbol, ASTCDInterface ast) {
    super.initialize_CDInterface(symbol, ast);
    symbol.setIsInterface(true);

    getModifierHandler().handle(ast.getModifier(), symbol);

    symbol.setSuperTypeList(ast.getCDExtendUsage().getSuperclassList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeCheck().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the extended interfaces (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
  }

  @Override
  protected void initialize_CDEnum(CDTypeSymbol symbol, ASTCDEnum ast) {
    super.initialize_CDEnum(symbol, ast);
    symbol.setIsEnum(true);

    getModifierHandler().handle(ast.getModifier(), symbol);

    symbol.setInterfaceList(ast.getCDInterfaceUsage().getInterfaceList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeCheck().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the interface (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
  }

  @Override
  protected void initialize_CDEnumConstant(FieldSymbol symbol, ASTCDEnumConstant ast) {
    super.initialize_CDEnumConstant(symbol, ast);
    symbol.setIsVariable(true);
    symbol.setIsStatic(true);
    // symbol.setIsReadOnly(true); // TODO SVa
    symbol.setIsPublic(true);

    symbol.setType(new SymTypeOfObject(new CDTypeSymbolLoader(getEnumStack().peek(), ast.getEnclosingScope())));
  }
}
