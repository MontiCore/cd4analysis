/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.typescalculator;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.types.check.*;

public class FullDeriveFromCD4Analysis extends AbstractDerive {

  public FullDeriveFromCD4Analysis(){
    this(CD4AnalysisMill.traverser());
  }

  public FullDeriveFromCD4Analysis(CD4AnalysisTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(CD4AnalysisTraverser traverser){
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

    final DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setTypeCheckResult(getTypeCheckResult());
    traverser.add4BitExpressions(deriveSymTypeOfBitExpressions);
    traverser.setBitExpressionsHandler(deriveSymTypeOfBitExpressions);
  }

}
