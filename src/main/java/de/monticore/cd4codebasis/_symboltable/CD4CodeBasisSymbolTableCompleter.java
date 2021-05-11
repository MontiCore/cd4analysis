/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeVoid;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4CodeBasisSymbolTableCompleter implements CD4CodeBasisVisitor2 {
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CodeBasisSymbolTableCompleter(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CD4CodeBasisSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper()
        .setImports(imports)
        .setPackageDeclaration(packageDeclaration);
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  @Override
  public void endVisit(ASTCDMethod node) {
    initialize_CDMethod(node);
    CD4CodeBasisVisitor2.super.endVisit(node);
  }

  @Override
  public void endVisit(ASTCDConstructor node) {
    initialize_CDConstructor(node);
    CD4CodeBasisVisitor2.super.endVisit(node);
  }

  @Override
  public void endVisit(ASTCDParameter node) {
    initialize_CDParameter(node);
    CD4CodeBasisVisitor2.super.endVisit(node);
  }

  @Override
  public void endVisit(ASTCD4CodeEnumConstant node) {
    initialize_CD4CodeEnumConstant(node);
    CD4CodeBasisVisitor2.super.endVisit(node);
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

    symbol.setReturnType(SymTypeExpressionFactory.createTypeObject(symbolTableHelper.getCurrentCDTypeOnStack(), symbol.getEnclosingScope()));

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
        finalTypeResult = SymTypeExpressionFactory.createTypeArray(symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()), symbol.getEnclosingScope(), 1, typeResult.get());
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
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, symbol.getEnclosingScope());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }


  /*
  The following visit methods must be overriden because both implemented interface
  provide default methods for the visit methods.
 */
  public void visit(de.monticore.symboltable.ISymbol node) {
  }

  public void endVisit(de.monticore.symboltable.ISymbol node) {
  }

  public void endVisit(de.monticore.ast.ASTNode node) {
  }

  public void visit(de.monticore.ast.ASTNode node) {
  }

  public void visit(de.monticore.symboltable.IScope node) {
  }

  public void endVisit(de.monticore.symboltable.IScope node) {
  }

}
