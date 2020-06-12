/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cd4codebasis.typescalculator;

import de.monticore.cd.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class DeriveSymTypeOfCD4CodeBasis extends CD4CodeDelegatorVisitor
    implements ITypesCalculator {

  private TypeCheckResult typeCheckResult;

  public DeriveSymTypeOfCD4CodeBasis() {
    setRealThis(this);
    init();
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression ex) {
    ex.accept(getRealThis());
    return Optional.of(getTypeCheckResult().getLast());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(getRealThis());
    return Optional.of(getTypeCheckResult().getLast());
  }

  public Optional<SymTypeExpression> calculateType(ASTMCType type) {
    type.accept(getRealThis());
    return Optional.of(getTypeCheckResult().getLast());
  }

  public Optional<SymTypeExpression> calculateType(ASTMCBasicTypesNode node) {
    node.accept(getRealThis());
    return Optional.of(getTypeCheckResult().getLast());
  }

  @Override
  public void init() {
    this.typeCheckResult = new TypeCheckResult();

    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setRealThis(getRealThis());
    deriveSymTypeOfLiterals.setResult(getTypeCheckResult());
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setRealThis(getRealThis());
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    final DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setRealThis(getRealThis());
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(getTypeCheckResult());
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);

    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setRealThis(getRealThis());
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    setMCBasicTypesVisitor(synthesizeSymTypeFromMCBasicTypes);

    final DeriveSymTypeOfCDBasis deriveSymTypeOfCDBasis = new DeriveSymTypeOfCDBasis();
    deriveSymTypeOfCDBasis.setRealThis(getRealThis());
    deriveSymTypeOfCDBasis.setTypeCheckResult(getTypeCheckResult());
    setCDBasisVisitor(deriveSymTypeOfCDBasis);
  }
}
