/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.typescalculator;

import de.monticore.cd._symboltable.TypesScopeHelper;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class DeriveSymTypeOfCD4CodeBasis extends CD4CodeBasisDelegatorVisitor
    implements ITypesCalculator, CDTypesCalculator {

  private TypeCheckResult typeCheckResult;
  private TypesScopeHelper typesScopeHelper;

  public DeriveSymTypeOfCD4CodeBasis() {
    setRealThis(this);
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
    reset();
    ex.accept(getRealThis());
    return getResult();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    reset();
    lit.accept(getRealThis());
    return getResult();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    reset();
    lit.accept(getRealThis());
    return getResult();
  }

  public Optional<SymTypeExpression> calculateType(ASTMCType type) {
    reset();
    type.accept(typesScopeHelper);
    type.accept(getRealThis());
    return getResult();
  }

  public Optional<SymTypeExpression> calculateType(ASTMCBasicTypesNode node) {
    reset();
    node.accept(typesScopeHelper);
    node.accept(getRealThis());
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
    this.typeCheckResult = new TypeCheckResult();
    this.typesScopeHelper = new TypesScopeHelper();

    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(getTypeCheckResult());
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);

    final DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(getTypeCheckResult());
    setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    final DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(getTypeCheckResult());
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);

    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    setMCBasicTypesVisitor(synthesizeSymTypeFromMCBasicTypes);
  }
}
