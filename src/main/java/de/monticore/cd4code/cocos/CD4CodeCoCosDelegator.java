/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.cdbasis.cocos.CDBasisCoCos;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;

public class CD4CodeCoCosDelegator extends CoCoParent<CD4CodeCoCoChecker> {
  @Override
  public CD4CodeCoCoChecker createNewChecker() {
    return new CD4CodeCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }

  @Override
  protected void addEbnfCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().addCheckerForEbnfCoCos());
    checker.addChecker(new CD4CodeCoCos().addCheckerForEbnfCoCos());
  }

  @Override
  protected void addMcgCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CDAssociationCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CD4AnalysisCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().addCheckerForMcgCoCos());
    checker.addChecker(new CD4CodeCoCos().addCheckerForMcgCoCos());
  }

  @Override
  protected void addMcg2EbnfCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().addCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4CodeCoCos().addCheckerForMcg2EbnfCoCos());
  }
}
