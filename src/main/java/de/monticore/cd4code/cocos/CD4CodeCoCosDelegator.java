/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

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
    checker.addChecker(getCheckerForEbnfCoCos(checker));
    checker.addChecker(getCheckerForMcgCoCos(checker));
    checker.addChecker(getCheckerForMcg2EbnfCoCos(checker));
  }

  @Override
  protected void addEbnfCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4CodeCoCos().getCheckerForEbnfCoCos());
  }

  @Override
  protected void addMcgCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4CodeCoCos().getCheckerForMcgCoCos());
  }

  @Override
  protected void addMcg2EbnfCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(new CDBasisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4CodeCoCos().getCheckerForMcg2EbnfCoCos());
  }
}
