/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._symboltable.CDTypeSymbolLoader;
import de.monticore.cdbasis.modifier.ModifierHandler;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class CD4CodeBasisSymbolTableCreator
    extends CD4CodeBasisSymbolTableCreatorTOP {
  protected DeriveSymTypeOfCDBasis typeChecker;
  protected ModifierHandler modifierHandler;
  protected Stack<String> classStack;
  protected Stack<String> enumStack;

  public CD4CodeBasisSymbolTableCreator(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CD4CodeBasisSymbolTableCreator(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  protected void init() {
    typeChecker = new DeriveSymTypeOfCDBasis();
    modifierHandler = new ModifierHandler();
  }

  public DeriveSymTypeOfCDBasis getTypeChecker() {
    return typeChecker;
  }

  public void setTypeChecker(DeriveSymTypeOfCDBasis typeChecker) {
    this.typeChecker = typeChecker;
  }

  public ModifierHandler getModifierHandler() {
    return modifierHandler;
  }

  public void setModifierHandler(ModifierHandler modifierHandler) {
    this.modifierHandler = modifierHandler;
  }

  public Stack<String> getClassStack() {
    return classStack;
  }

  public void setClassStack(Stack<String> classStack) {
    this.classStack = classStack;
  }

  public Stack<String> getEnumStack() {
    return enumStack;
  }

  public void setEnumStack(Stack<String> enumStack) {
    this.enumStack = enumStack;
  }

  @Override
  public void visit(ASTCDClass node) {
    super.visit(node);
    classStack.push(node.getName());
  }

  @Override
  public void endVisit(ASTCDClass node) {
    classStack.pop();
    super.endVisit(node);
  }

  @Override
  protected void initialize_CDMethod(CDMethodSignatureSymbol symbol, ASTCDMethod ast) {
    super.initialize_CDMethod(symbol, ast);

    symbol.setIsMethod(true);

    final Optional<SymTypeExpression> typeResult = getTypeChecker().calculateType(ast.getMCReturnType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xA0000: The type of the return type (%s) could not be calculated", ast.getMCReturnType().getClass().getSimpleName()));
    }
    else {
      symbol.setReturnType(typeResult.get());
    }

    symbol.setHasEllipsis(ast.getCDParameterList().stream().anyMatch(ASTCDParameter::isEllipsis));

    symbol.setExceptionList(ast.getCDThrowsDeclaration().getExceptionList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeChecker().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the exception classes (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDConstructor(CDMethodSignatureSymbol symbol, ASTCDConstructor ast) {
    super.initialize_CDConstructor(symbol, ast);

    symbol.setIsConstructor(true);
    symbol.setHasEllipsis(ast.getCDParameterList().stream().anyMatch(ASTCDParameter::isEllipsis));

    symbol.setReturnType(new SymTypeOfObject(new CDTypeSymbolLoader(classStack.peek(), ast.getEnclosingScope())));

    symbol.setExceptionList(ast.getCDThrowsDeclaration().getExceptionList().stream().map(s -> {
      final Optional<SymTypeExpression> result = getTypeChecker().calculateType(s);
      if (!result.isPresent()) {
        Log.error(String.format("0xA0000: The type of the exception classes (%s) could not be calculated", s.getClass().getSimpleName()));
      }
      return result;
    }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

    getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDParameter(FieldSymbol symbol, ASTCDParameter ast) {
    super.initialize_CDParameter(symbol, ast);

    symbol.setIsParameter(true);

    final Optional<SymTypeExpression> typeResult = getTypeChecker().calculateType(ast.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xA0000: The type (%s) of the attribute (%s) could not be calculated", ast.getMCType().getClass().getSimpleName(), ast.getName()));
    }
    else {
      symbol.setType(typeResult.get());
    }
  }

  @Override
  protected void initialize_CD4CodeEnumConstant(FieldSymbol symbol, ASTCD4CodeEnumConstant ast) {
    super.initialize_CD4CodeEnumConstant(symbol, ast);

    symbol.setIsVariable(true);
    symbol.setIsStatic(true);
    // symbol.setIsReadOnly(true); // TODO SVa
    symbol.setIsPublic(true);

    final String enumName = getEnumStack().peek();
    symbol.setType(new SymTypeOfObject(new CDTypeSymbolLoader(enumName, ast.getEnclosingScope())));

    // Don't store the arguments in the ST
  }

}
