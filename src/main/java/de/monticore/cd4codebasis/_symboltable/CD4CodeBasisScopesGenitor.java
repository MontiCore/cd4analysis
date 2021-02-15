/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeBasisScopesGenitor extends CD4CodeBasisScopesGenitorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeBasisScopesGenitor(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CD4CodeBasisScopesGenitor(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  public CD4CodeBasisScopesGenitor() {
    super();
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void endVisit(ASTCDMethod node) {
    initialize_CDMethod(node);
    super.endVisit(node);
  }

  @Override
  public void endVisit(ASTCDConstructor node) {
    initialize_CDConstructor(node);
    super.endVisit(node);
  }

  @Override
  public void endVisit(ASTCDParameter node) {
    initialize_CDParameter(node);
    super.endVisit(node);
  }

  @Override
  public void endVisit(ASTCD4CodeEnumConstant node) {
    initialize_CD4CodeEnumConstant(node);
    super.endVisit(node);
  }

  protected void initialize_CDMethod(ASTCDMethod ast) {
    CDMethodSignatureSymbol symbol = ast.getSymbol();
    symbol.setIsMethod(true);

    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(ast.getMCReturnType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xCDA90: The type of the return type (%s) could not be calculated", ast.getMCReturnType().getClass().getSimpleName()), ast.getMCReturnType().get_SourcePositionStart());
    }
    else {
      symbol.setReturnType(typeResult.get());
    }

    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    // the exception types don't have to be resolved
    if (ast.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(ast.getCDThrowsDeclaration().streamException().map(s -> {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA91: The type of the exception classes (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  protected void initialize_CDConstructor(ASTCDConstructor ast) {
    CDMethodSignatureSymbol symbol = ast.getSymbol();
    symbol.setIsConstructor(true);
    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    assert scopeStack.peekLast() != null;
    symbol.setReturnType(SymTypeExpressionFactory.createTypeObject(symbolTableHelper.getCurrentCDTypeOnStack(), scopeStack.peekLast().getEnclosingScope()));

    if (ast.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(ast.getCDThrowsDeclaration().streamException().map(s -> {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format("0xCDA92: The type of the exception classes (%s) could not be calculated", s.getClass().getSimpleName()), s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  protected void initialize_CDParameter(ASTCDParameter ast) {
    FieldSymbol symbol = ast.getSymbol();
    Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(ast.getMCType());

    if (!typeResult.isPresent()) {
      Log.error(String.format("0xCDA93: The type (%s) of the attribute (%s) could not be calculated", symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()), ast.getName()), ast.getMCType().get_SourcePositionStart());
    }
    else {
      final SymTypeExpression finalTypeResult;
      if (ast.isEllipsis()) {
        finalTypeResult = SymTypeExpressionFactory.createTypeArray(symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()), scopeStack.peekLast(), 1, typeResult.get());
      }
      else {
        finalTypeResult = typeResult.get();
      }

      symbol.setType(finalTypeResult);
    }
  }

  protected void initialize_CD4CodeEnumConstant(ASTCD4CodeEnumConstant ast) {
    FieldSymbol symbol = ast.getSymbol();

    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    assert scopeStack.peekLast() != null;
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, scopeStack.peekLast().getEnclosingScope());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
