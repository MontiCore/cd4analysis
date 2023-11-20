/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.typescalculator;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;

public class FullDeriveFromCDBasis extends AbstractDerive {

  public FullDeriveFromCDBasis(CDBasisTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public FullDeriveFromCDBasis() {
    this(CDBasisMill.traverser());
  }

  public void init(CDBasisTraverser traverser) {
    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
  }
}
