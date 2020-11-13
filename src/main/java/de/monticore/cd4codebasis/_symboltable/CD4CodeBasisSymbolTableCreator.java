/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeBasisSymbolTableCreator
    extends CD4CodeBasisSymbolTableCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeBasisSymbolTableCreator() {
    super();
    init();
  }

  public CD4CodeBasisSymbolTableCreator(ICD4CodeBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CD4CodeBasisSymbolTableCreator(Deque<? extends ICD4CodeBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  protected void init() {
    setRealThis(this);
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
  protected void initialize_CDMethod(CDMethodSignatureSymbol symbol, ASTCDMethod ast) {
    super.initialize_CDMethod(symbol, ast);

    symbol.setIsMethod(true);

    ast.getMCReturnType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(ast.getMCReturnType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA90: The type of the return type (%s) could not be calculated",
          ast.getMCReturnType().getClass().getSimpleName()),
          ast.getMCReturnType().get_SourcePositionStart());
    }
    else {
      symbol.setReturnType(typeResult.get());
    }

    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    // the exception types don't have to be resolved
    /*if (ast.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionList(ast.getCDThrowsDeclaration().streamException().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA91: The type of the exception classes (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }*/

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDConstructor(CDMethodSignatureSymbol symbol, ASTCDConstructor ast) {
    super.initialize_CDConstructor(symbol, ast);

    symbol.setIsConstructor(true);
    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    symbol.setReturnType(SymTypeExpressionFactory.createTypeObject(
        symbolTableHelper.getCurrentCDTypeOnStack(),
        scopeStack.peekLast()
    ));

    if (ast.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(ast.getCDThrowsDeclaration().streamException().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA92: The type of the exception classes (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDParameter(FieldSymbol symbol, ASTCDParameter ast) {
    super.initialize_CDParameter(symbol, ast);

    ast.getMCType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(ast.getMCType());

    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA93: The type (%s) of the attribute (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()),
          ast.getName()),
          ast.getMCType().get_SourcePositionStart());
    }
    else {
      final SymTypeExpression finalTypeResult;
      if (ast.isEllipsis()) {
        finalTypeResult = SymTypeExpressionFactory.createTypeArray(
            symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()),
            scopeStack.peekLast(),
            1,
            typeResult.get());
      }
      else {
        finalTypeResult = typeResult.get();
      }

      symbol.setType(finalTypeResult);
    }
  }

  @Override
  protected void initialize_CD4CodeEnumConstant(FieldSymbol symbol, ASTCD4CodeEnumConstant ast) {
    super.initialize_CD4CodeEnumConstant(symbol, ast);

    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, scopeStack.peekLast());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
