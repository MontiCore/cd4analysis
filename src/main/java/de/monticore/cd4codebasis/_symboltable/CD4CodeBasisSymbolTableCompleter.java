/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._symboltable;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.FullSynthesizeFromMCCollectionTypes;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;

public class CD4CodeBasisSymbolTableCompleter implements CD4CodeBasisVisitor2, CDBasisVisitor2 {
  protected ISynthesize typeSynthesizer;

  public CD4CodeBasisSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }

  public CD4CodeBasisSymbolTableCompleter() {
    this(new FullSynthesizeFromMCCollectionTypes());
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

  @Override
  public void visit(ASTCDClass node) {
    if (node.getCDConstructorList().isEmpty()) {
      CDMethodSignatureSymbol constructor =
          CD4CodeBasisMill.cDMethodSignatureSymbolBuilder()
              .setIsPublic(true)
              .setIsStatic(true)
              .setIsConstructor(true)
              .setName(node.getName())
              .setType(SymTypeExpressionFactory.createTypeExpression(node.getSymbol()))
              .build();
      ICD4CodeBasisScope scope = CD4CodeBasisMill.scope();
      scope.setName(node.getName());
      constructor.setSpannedScope(scope);
      ((ICD4CodeBasisScope) node.getSpannedScope())
          .getCDMethodSignatureSymbols()
          .put(constructor.getName(), constructor);
    }
  }

  protected void initialize_CDMethod(ASTCDMethod ast) {
    CDMethodSignatureSymbol symbol = ast.getSymbol();
    symbol.setIsMethod(true);

    final TypeCheckResult typeResult = getTypeSynthesizer().synthesizeType(ast.getMCReturnType());
    if (!typeResult.isPresentResult()) {
      Log.error(
          String.format(
              "0xCDA90: The type of the return type (%s) could not be calculated",
              ast.getMCReturnType().getClass().getSimpleName()),
          ast.getMCReturnType().get_SourcePositionStart());
    } else {
      symbol.setType(typeResult.getResult());
    }

    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    // the exception types don't have to be resolved
    if (ast.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(
          ast.getCDThrowsDeclaration()
              .streamException()
              .map(
                  s -> {
                    final TypeCheckResult result = getTypeSynthesizer().synthesizeType(s);
                    if (!result.isPresentResult()) {
                      Log.error(
                          String.format(
                              "0xCDA91: The type of the exception classes (%s) could not be calculated",
                              s.getClass().getSimpleName()),
                          s.get_SourcePositionStart());
                    }
                    return result;
                  })
              .filter(TypeCheckResult::isPresentResult)
              .map(TypeCheckResult::getResult)
              .collect(Collectors.toList()));
    }

    setupModifiers(ast.getModifier(), symbol);
  }

  protected void initialize_CDConstructor(ASTCDConstructor ast) {
    CDMethodSignatureSymbol symbol = ast.getSymbol();
    symbol.setIsConstructor(true);
    symbol.setIsElliptic(ast.streamCDParameters().anyMatch(ASTCDParameter::isEllipsis));

    symbol.setType(
        SymTypeExpressionFactory.createTypeObject(symbol.getName(), symbol.getEnclosingScope()));

    if (ast.isPresentCDThrowsDeclaration()) {
      symbol.setExceptionsList(
          ast.getCDThrowsDeclaration()
              .streamException()
              .map(
                  s -> {
                    final TypeCheckResult result = getTypeSynthesizer().synthesizeType(s);
                    if (!result.isPresentResult()) {
                      Log.error(
                          String.format(
                              "0xCDA92: The type of the exception classes (%s) could not be calculated",
                              s.getClass().getSimpleName()),
                          s.get_SourcePositionStart());
                    }
                    return result;
                  })
              .filter(TypeCheckResult::isPresentResult)
              .map(TypeCheckResult::getResult)
              .collect(Collectors.toList()));
    }

    setupModifiers(ast.getModifier(), symbol);
  }

  protected void initialize_CDParameter(ASTCDParameter ast) {
    FieldSymbol symbol = ast.getSymbol();
    TypeCheckResult typeResult = getTypeSynthesizer().synthesizeType(ast.getMCType());

    if (!typeResult.isPresentResult()) {
      Log.error(
          String.format(
              "0xCDA93: The type (%s) of the attribute (%s) could not be calculated",
              CD4CodeBasisMill.prettyPrint(ast.getMCType(), false), ast.getName()),
          ast.getMCType().get_SourcePositionStart());
    } else {

      final SymTypeExpression finalTypeResult;
      if (ast.isEllipsis()) {
        finalTypeResult =
            SymTypeExpressionFactory.createTypeArray(
                CD4CodeBasisMill.prettyPrint(ast.getMCType(), false),
                symbol.getEnclosingScope(),
                1,
                typeResult.getResult());
      } else {
        finalTypeResult = typeResult.getResult();
      }

      symbol.setType(finalTypeResult);
    }
  }

  /*
   duplicate of CDInterfaceAndEnumSymbolTableCompleter.initialize_CDEnumConstant
  */
  protected void initialize_CD4CodeEnumConstant(ASTCD4CodeEnumConstant ast) {
    FieldSymbol symbol = ast.getSymbol();

    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = ast.getEnclosingScope().getName();
    // call getEnclosingScope() twice, so the full name of the type evaluates to the correct package
    final SymTypeOfObject typeObject =
        SymTypeExpressionFactory.createTypeObject(
            enumName, symbol.getEnclosingScope().getEnclosingScope());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }

  public void setupModifiers(ASTModifier modifier, MethodSymbol methodSymbol) {
    methodSymbol.setIsPublic(modifier.isPublic());
    methodSymbol.setIsPrivate(modifier.isPrivate());
    methodSymbol.setIsProtected(modifier.isProtected());
    methodSymbol.setIsStatic(modifier.isStatic());
  }

  public ISynthesize getTypeSynthesizer() {
    return typeSynthesizer;
  }

  public void setTypeSynthesizer(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }
}
