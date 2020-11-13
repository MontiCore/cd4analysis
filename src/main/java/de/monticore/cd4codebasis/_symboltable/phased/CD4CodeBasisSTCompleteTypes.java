package de.monticore.cd4codebasis._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisScope;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeBasisSTCompleteTypes implements CD4CodeBasisVisitor {
  protected Deque<ICD4CodeBasisScope> scopeStack; // TODO SVa: can be removed?
  protected CDSymbolTableHelper symbolTableHelper;
  protected CD4CodeBasisVisitor realThis = this;

  public CD4CodeBasisSTCompleteTypes(Deque<ICD4CodeBasisScope> scopeStack, CDSymbolTableHelper symbolTableHelper) {
    this.scopeStack = scopeStack;
    this.symbolTableHelper = symbolTableHelper;
  }

  @Override
  public CD4CodeBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDMethod node) {
    final CDMethodSignatureSymbol symbol = node.getSymbol();
    symbol.setIsMethod(true);

    //ast.getMCReturnType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed

    symbol.setIsElliptic(node.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));
    symbolTableHelper.getModifierHandler().handle(node.getModifier(), symbol);

    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(node.getMCReturnType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA90: The type of the return type (%s) could not be calculated",
          node.getMCReturnType().getClass().getSimpleName()),
          node.getMCReturnType().get_SourcePositionStart());
    }
    else {

      assert(typeResult.get().getTypeInfo() != null);
      symbol.setReturnType(typeResult.get());
    }

    if (node.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(node.getCDThrowsDeclaration().streamException().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA91: The type of the exception classes (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }

        assert(result.get().getTypeInfo() != null);
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  public void visit(ASTCDConstructor node) {
    final CDMethodSignatureSymbol symbol = node.getSymbol();
    symbol.setIsConstructor(true);
    symbol.setIsElliptic(node.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    // the symbol is already created, because a constructor can only be defined in a type
    symbol.setReturnType(SymTypeExpressionFactory.createTypeObject(
        symbolTableHelper.getCurrentCDTypeOnStack(),
        scopeStack.peekLast()
    ));

    symbolTableHelper.getModifierHandler().handle(node.getModifier(), symbol);

    if (node.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(node.getCDThrowsDeclaration().streamException().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA92: The type of the exception classes (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }

        assert(result.get().getTypeInfo() != null);
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  public void visit(ASTCDParameter node) {
    node.getMCType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(node.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA93: The type (%s) of the attribute (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(node.getMCType()),
          node.getName()),
          node.getMCType().get_SourcePositionStart());
    }
    else {
      final SymTypeExpression finalTypeResult;
      if (node.isEllipsis()) {
        finalTypeResult = SymTypeExpressionFactory.createTypeArray(
            symbolTableHelper.getPrettyPrinter().prettyprint(node.getMCType()),
            scopeStack.peekLast(),
            1,
            typeResult.get());
      }
      else {
        assert(typeResult.get().getTypeInfo() != null);
        finalTypeResult = typeResult.get();
      }

      node.getSymbol().setType(finalTypeResult);
    }
  }

  @Override
  public void visit(ASTCD4CodeEnumConstant node) {
    final FieldSymbol symbol = node.getSymbol();
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
