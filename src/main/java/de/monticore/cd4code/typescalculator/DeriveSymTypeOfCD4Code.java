/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.typescalculator;

import de.monticore.cd._symboltable.*;
import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class DeriveSymTypeOfCD4Code
    implements ITypesCalculator, ISynthesize, CDTypesCalculator {

  protected CD4CodeTraverser traverser;
  private TypeCheckResult typeCheckResult;
  private CD4CodeTraverser typesScopeHelper;

  public DeriveSymTypeOfCD4Code() {
    init();
  }

  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
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
    type.accept(typesScopeHelper);
    type.accept(getTraverser());
    return getResult();
  }

  public Optional<SymTypeExpression> calculateType(ASTMCBasicTypesNode node) {
    reset();
    node.accept(typesScopeHelper);
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
    this.traverser = CD4CodeMill.traverser();
    this.typeCheckResult = new TypeCheckResult();
  
    this.typesScopeHelper = CD4CodeMill.traverser();
  
    typesScopeHelper.add4MCBasicTypes(new MCBasicTypesScopeHelper());
    typesScopeHelper.add4MCCollectionTypes(new MCCollectionTypesScopeHelper());
    typesScopeHelper.add4MCSimpleGenericTypes(new MCSimpleGenericTypesScopeHelper());
    typesScopeHelper.add4MCArrayTypes(new MCArrayTypesScopeHelper());
    typesScopeHelper.add4MCFullGenericTypes(new MCFullGenericTypesScopeHelper());

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

    final SynthesizeSymTypeFromMCCollectionTypes synthesizeSymTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    synthesizeSymTypeFromMCCollectionTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCCollectionTypes(synthesizeSymTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(synthesizeSymTypeFromMCCollectionTypes);

    final SynthesizeSymTypeFromMCArrayTypes synthesizeSymTypeFromMCArrayTypes = new SynthesizeSymTypeFromMCArrayTypes();
    synthesizeSymTypeFromMCArrayTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCArrayTypes(synthesizeSymTypeFromMCArrayTypes);
    traverser.setMCArrayTypesHandler(synthesizeSymTypeFromMCArrayTypes);

    final SynthesizeSymTypeFromMCSimpleGenericTypes synthesizeSymTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synthesizeSymTypeFromMCSimpleGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCSimpleGenericTypes(synthesizeSymTypeFromMCSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(synthesizeSymTypeFromMCSimpleGenericTypes);

    final SynthesizeSymTypeFromMCFullGenericTypes synthesizeSymTypeFromMCFullGenericTypes = new SynthesizeSymTypeFromMCFullGenericTypes();
    synthesizeSymTypeFromMCFullGenericTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCFullGenericTypes(synthesizeSymTypeFromMCFullGenericTypes);
    traverser.setMCFullGenericTypesHandler(synthesizeSymTypeFromMCFullGenericTypes);

    final DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setTypeCheckResult(getTypeCheckResult());
    traverser.add4BitExpressions(deriveSymTypeOfBitExpressions);
    traverser.setBitExpressionsHandler(deriveSymTypeOfBitExpressions);
  }
}
