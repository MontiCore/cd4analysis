/* (c) https://github.com/MontiCore/monticore */
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
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }

  @Override
  protected void addEbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().addCheckerForEbnfCoCos());
  }

  @Override
  protected void addMcgCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CDAssociationCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CD4AnalysisCoCos().addCheckerForMcgCoCos());
  }

  @Override
  protected void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().addCheckerForMcg2EbnfCoCos());
  }
}
