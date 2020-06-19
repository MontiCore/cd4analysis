/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.typescalculator;

import de.monticore.cdbasis._visitor.CDBasisDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class DeriveSymTypeOfCDBasis extends CDBasisDelegatorVisitor
    implements ITypesCalculator {

  private TypeCheckResult typeCheckResult;

  public DeriveSymTypeOfCDBasis() {
    setRealThis(this);
    this.typeCheckResult = new TypeCheckResult();
    init();
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
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

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
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
    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(getTypeCheckResult());
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    setMCBasicTypesVisitor(synthesizeSymTypeFromMCBasicTypes);
  }
}
