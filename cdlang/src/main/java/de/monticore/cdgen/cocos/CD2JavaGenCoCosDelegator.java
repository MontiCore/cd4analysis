/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;

public class CD2JavaGenCoCosDelegator extends CD4CodeCoCosDelegator {
  
  /**
   * The generator checks the model after the trafos (e.g., visibility propagation and default
   * visibilities) have been applied; the model as written is checked in
   * {@link de.monticore.cdgen.CDGenTool#runBeforeSTCoCos}.
   */
  @Override
  protected CDAssociationCoCos createCDAssociationCoCos() {
    return new CDAssociationCoCos(true);
  }
  
  @Override
  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    super.addCheckerForAllCoCos(checker);
    checker.addCoCo(new CDAssociationUniqueInHierarchy());
    checker.addCoCo(new CDNoAttributesInInterfaces());
    checker.addCoCo(new CDNoOutgoingAssocs4Interfaces());
    checker.addCoCo(new CDNoOutgoingAssocs4LibraryTypes());
    checker.addCoCo(new CDSingleClassInheritance());
  }
  
}
