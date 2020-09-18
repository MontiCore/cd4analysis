/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.typescalculator;

import de.monticore.cd._symboltable.TypesScopeHelper;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class DeriveSymTypeOfCD4Code extends CD4CodeDelegatorVisitor
    implements ITypesCalculator, CDTypesCalculator {

  private TypeCheckResult typeCheckResult;
  private TypesScopeHelper typesScopeHelper;

  public DeriveSymTypeOfCD4Code() {
    setRealThis(this);
    init();
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
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

    final SynthesizeSymTypeFromMCCollectionTypes synthesizeSymTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    synthesizeSymTypeFromMCCollectionTypes.setTypeCheckResult(getTypeCheckResult());
    setMCCollectionTypesVisitor(synthesizeSymTypeFromMCCollectionTypes);

    final SynthesizeSymTypeFromMCSimpleGenericTypes synthesizeSymTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synthesizeSymTypeFromMCSimpleGenericTypes.setTypeCheckResult(getTypeCheckResult());
    setMCSimpleGenericTypesVisitor(synthesizeSymTypeFromMCSimpleGenericTypes);

    final SynthesizeSymTypeFromMCFullGenericTypes synthesizeSymTypeFromMCFullGenericTypes = new SynthesizeSymTypeFromMCFullGenericTypes();
    synthesizeSymTypeFromMCFullGenericTypes.setTypeCheckResult(getTypeCheckResult());
    setMCFullGenericTypesVisitor(synthesizeSymTypeFromMCFullGenericTypes);

    final DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setTypeCheckResult(getTypeCheckResult());
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);
  }
}
