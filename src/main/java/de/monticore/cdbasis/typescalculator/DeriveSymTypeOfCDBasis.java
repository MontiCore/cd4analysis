/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.typescalculator;

import de.monticore.cd.typescalculator.CDTypesCalculator;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.TypeCheckResult;

public class DeriveSymTypeOfCDBasis extends CDTypesCalculator {

  public DeriveSymTypeOfCDBasis() {
    this(CDBasisMill.traverser());
  }

  public DeriveSymTypeOfCDBasis(CDBasisTraverser traverser) {
    this.traverser = traverser;
    init(traverser);
  }

  public void init(CDBasisTraverser traverser) {
    this.typeCheckResult = new TypeCheckResult();

    final DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

    final DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(getTypeCheckResult());
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);

    final SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(getTypeCheckResult());
    traverser.add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);
  }
}
