/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbolBuilder;
import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeBasisScopeSkeletonsCreator
    extends CD4CodeBasisScopeSkeletonsCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeBasisScopeSkeletonsCreator(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CD4CodeBasisScopeSkeletonsCreator(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper(CD4CodeBasisMill.deriveSymTypeOfCD4CodeBasis());
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void visit(ASTCDClass node) {
    symbolTableHelper.addToCDTypeStack(node.getName());
    super.visit(node);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  protected void initialize_CDMethod(CDMethodSignatureSymbolBuilder symbol, ASTCDMethod ast) {
    super.initialize_CDMethod(symbol, ast);

    symbol.setIsMethod(true);

    //ast.getMCReturnType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed

    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));
    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDConstructor(CDMethodSignatureSymbolBuilder symbol, ASTCDConstructor ast) {
    super.initialize_CDConstructor(symbol, ast);

    symbol.setIsConstructor(true);
    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    // the symbol is already created, because a constructor can only be defined in a type
    symbol.setReturnType(SymTypeExpressionFactory.createTypeObject(
        symbolTableHelper.getCurrentCDTypeOnStack(),
        scopeStack.peekLast()
    ));

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CD4CodeEnumConstant(FieldSymbolBuilder symbol, ASTCD4CodeEnumConstant ast) {
    super.initialize_CD4CodeEnumConstant(symbol, ast);

    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    // the symbol of the enum is already created
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, scopeStack.getLast());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
