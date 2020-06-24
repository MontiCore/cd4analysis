/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.cocos;

import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;

public class CD4AnalysisCoCos {
  public CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForEbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForMcgCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

  protected void addEbnfCoCos(CD4AnalysisCoCoChecker checker) {
  }

  protected void addMcgCoCos(CD4AnalysisCoCoChecker checker) {
  }

  protected void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
  }
}
