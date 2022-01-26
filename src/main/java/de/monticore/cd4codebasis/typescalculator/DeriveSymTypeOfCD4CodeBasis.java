/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.typescalculator;

import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class DeriveSymTypeOfCD4CodeBasis
    implements IDerive, CDTypesCalculator {

  protected CD4CodeBasisTraverser traverser;
  private TypeCheckResult typeCheckResult;

  public DeriveSymTypeOfCD4CodeBasis() {
    init();
  }

  @Override
  public CD4CodeBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeBasisTraverser traverser) {
    this.traverser = traverser;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression ex) {
    reset();
    ex.accept(getTraverser());
    return getResult();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    reset();
    lit.accept(getTraverser());
    return getResult();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    reset();
    lit.accept(getTraverser());
    return getResult();
  }

  public Optional<SymTypeExpression> calculateType(ASTMCType type) {
    reset();
    type.accept(getTraverser());
    return getResult();
  }

  public Optional<SymTypeExpression> calculateType(ASTMCBasicTypesNode node) {
    reset();
    node.accept(getTraverser());
    return getResult();
  }

  public void reset() {
    getTypeCheckResult().setCurrentResultAbsent();
  }

  public Optional<SymTypeExpression> getResult() {
    return getTypeCheckResult().isPresentCurrentResult() ? Optional.of(getTypeCheckResult().getCurrentResult()) : Optional.empty();
  }

  @Override
  public void init() {
    this.traverser = CD4CodeBasisMill.traverser();
    this.typeCheckResult = new TypeCheckResult();

    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

    final DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);

    final DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(getTypeCheckResult());
    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);

    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);
  }
}
