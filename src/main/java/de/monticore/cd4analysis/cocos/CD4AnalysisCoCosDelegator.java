/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.cdbasis.cocos.CDBasisCoCos;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;

public class CD4AnalysisCoCosDelegator
    extends CoCoParent<CD4AnalysisCoCoChecker> {

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

  @Override
  protected void addEbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForEbnfCoCos());
  }

  @Override
  protected void addMcgCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcgCoCos());
  }

  @Override
  protected void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcg2EbnfCoCos());
  }
}
