/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.cocos;

import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.cdbasis.cocos.CDBasisCoCos;
import de.monticore.cdinterfaceandenum.cocos.CDInterfaceAndEnumCoCos;

public class CD4CodeCoCosDelegator {
  public CD4CodeCoCoChecker getCheckerForAllCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public CD4CodeCoCoChecker getCheckerForEbnfCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();

    checker.addChecker(new CDBasisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().getCheckerForEbnfCoCos());
    checker.addChecker(new CD4CodeCoCos().getCheckerForEbnfCoCos());

    addEbnfCoCos(checker);
    return checker;
  }

  public CD4CodeCoCoChecker getCheckerForMcgCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();

    checker.addChecker(new CDBasisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().getCheckerForMcgCoCos());
    checker.addChecker(new CD4CodeCoCos().getCheckerForMcgCoCos());

    addMcgCoCos(checker);
    return checker;
  }

  public CD4CodeCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();

    checker.addChecker(new CDBasisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDInterfaceAndEnumCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CDAssociationCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4AnalysisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4CodeBasisCoCos().getCheckerForMcg2EbnfCoCos());
    checker.addChecker(new CD4CodeCoCos().getCheckerForMcg2EbnfCoCos());

    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

  protected void addEbnfCoCos(CD4CodeCoCoChecker checker) {
  }

  protected void addMcgCoCos(CD4CodeCoCoChecker checker) {
  }

  private void addMcg2EbnfCoCos(CD4CodeCoCoChecker checker) {

  }
}
