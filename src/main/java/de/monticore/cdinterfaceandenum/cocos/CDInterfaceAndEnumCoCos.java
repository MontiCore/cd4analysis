/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumCoCoChecker;
import de.monticore.cdinterfaceandenum.cocos.ebnf.*;
import de.monticore.cd4codebasis.cocos.ebnf.CD4CodeEnumConstantParameterMatchConstructorArguments;

public class CDInterfaceAndEnumCoCos
    extends CoCoParent<CDInterfaceAndEnumCoCoChecker> {
  @Override
  public CDInterfaceAndEnumCoCoChecker createNewChecker() {
    return new CDInterfaceAndEnumCoCoChecker();
  }

  protected void addCheckerForAllCoCos(CDInterfaceAndEnumCoCoChecker checker) {
    addCheckerForEbnfCoCos();
    addCheckerForMcgCoCos();
    addCheckerForMcg2EbnfCoCos();
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
}
