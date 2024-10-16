/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis;

import de.monticore.testcd4codebasis._visitor.TestCD4CodeBasisTraverser;
import de.monticore.types.check.*;

public class FullDeriveFromTestCD4CodeBasis extends AbstractDerive {

  public FullDeriveFromTestCD4CodeBasis() {
    this(TestCD4CodeBasisMill.inheritanceTraverser());
  }

  public FullDeriveFromTestCD4CodeBasis(TestCD4CodeBasisTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(TestCD4CodeBasisTraverser traverser) {
    final DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals =
        new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);

    final DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions =
        new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(getTypeCheckResult());
    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
  }
}
