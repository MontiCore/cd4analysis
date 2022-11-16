/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.typescalculator;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.types.check.*;

public class FullDeriveFromCD4CodeBasis extends AbstractDerive {

  public FullDeriveFromCD4CodeBasis(CD4CodeBasisTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public FullDeriveFromCD4CodeBasis() {
    this(CD4CodeBasisMill.traverser());
  }

  public void init(CD4CodeBasisTraverser traverser) {
    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

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
