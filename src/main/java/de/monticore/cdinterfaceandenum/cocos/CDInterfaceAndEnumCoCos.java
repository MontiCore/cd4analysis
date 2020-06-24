/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.cocos;

import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumCoCoChecker;
import de.monticore.cdinterfaceandenum.cocos.ebnf.*;

public class CDInterfaceAndEnumCoCos {
  public CDInterfaceAndEnumCoCoChecker getCheckerForAllCoCos() {
    CDInterfaceAndEnumCoCoChecker checker = new CDInterfaceAndEnumCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public CDInterfaceAndEnumCoCoChecker getCheckerForEbnfCoCos() {
    CDInterfaceAndEnumCoCoChecker checker = new CDInterfaceAndEnumCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CDInterfaceAndEnumCoCoChecker getCheckerForMcgCoCos() {
    CDInterfaceAndEnumCoCoChecker checker = new CDInterfaceAndEnumCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CDInterfaceAndEnumCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CDInterfaceAndEnumCoCoChecker checker = new CDInterfaceAndEnumCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CDInterfaceAndEnumCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

  protected void addEbnfCoCos(CDInterfaceAndEnumCoCoChecker checker) {
    // CDEnum
    checker.addCoCo(new CDEnumImplementsOnlyInterfaces());
    checker.addCoCo(new CDEnumImplementsNotCyclic());

    // CDInterface
    checker.addCoCo(new CDInterfaceExtendsNotCyclic());
    checker.addCoCo(new CDInterfaceExtendsOnlyInterfaces());

    // CDEnumConstant
    checker.addCoCo(new CDEnumConstantUnique());
  }

  protected void addMcgCoCos(CDInterfaceAndEnumCoCoChecker checker) {
  }

  protected void addMcg2EbnfCoCos(CDInterfaceAndEnumCoCoChecker checker) {
  }
}
