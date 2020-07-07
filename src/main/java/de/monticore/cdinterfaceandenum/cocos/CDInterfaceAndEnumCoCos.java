/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;
import de.monticore.cdinterfaceandenum.cocos.mcg.CD4CodeEnumConstantParameterMatchConstructorArguments;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumCoCoChecker;
import de.monticore.cdinterfaceandenum.cocos.ebnf.*;

public class CDInterfaceAndEnumCoCos extends CoCoParent<CDInterfaceAndEnumCoCoChecker> {
  @Override
  public CDInterfaceAndEnumCoCoChecker createNewChecker() {
    return new CDInterfaceAndEnumCoCoChecker();
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
    // CD4CodeEnumConstant
    checker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments());
  }
}
