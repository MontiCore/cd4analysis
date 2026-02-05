/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;

public class CD2JavaGenCoCos extends CD4CodeCoCosDelegator {
  
  @Override
  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    super.addCheckerForAllCoCos(checker);
    checker.addCoCo(new CDAssociationUniqueInHierarchy());
    checker.addCoCo(new CDNoAttributesInInterfaces());
    checker.addCoCo(new CDNoOutgoingAssocs4Interfaces());
    checker.addCoCo(new CDSingleClassInheritance());
  }
  
}
