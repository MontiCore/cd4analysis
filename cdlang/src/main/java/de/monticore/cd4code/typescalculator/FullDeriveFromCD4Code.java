/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.typescalculator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.types.check.*;

public class FullDeriveFromCD4Code extends AbstractDerive {

  public FullDeriveFromCD4Code() {
    this(CD4CodeMill.inheritanceTraverser());
  }

  public FullDeriveFromCD4Code(CD4CodeTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(CD4CodeTraverser traverser) {
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

    final DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions =
        new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setTypeCheckResult(getTypeCheckResult());
    traverser.add4BitExpressions(deriveSymTypeOfBitExpressions);
    traverser.setBitExpressionsHandler(deriveSymTypeOfBitExpressions);
  }
}
