/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;

public class CD4AnalysisCoCos extends CoCoParent<CD4AnalysisCoCoChecker> {
  @Override
  public CD4AnalysisCoCoChecker createNewChecker() {
    return new CD4AnalysisCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos(checker));
    checker.addChecker(getCheckerForMcgCoCos(checker));
    checker.addChecker(getCheckerForMcg2EbnfCoCos(checker));
  }
}
