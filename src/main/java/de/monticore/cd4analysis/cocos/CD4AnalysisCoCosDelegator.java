/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.cocos;

import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.cdbasis.cocos.CDBasisCoCos;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;

public class CD4AnalysisCoCosDelegator {
  public CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForEbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();

    checker.addChecker(new CDBasisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForEbnfCoCos());

    addEbnfCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForMcgCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();

    checker.addChecker(new CDBasisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcgCoCos());

    addMcgCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();

    checker.addChecker(new CDBasisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcg2EbnfCoCos());

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

  private void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
  }
}
