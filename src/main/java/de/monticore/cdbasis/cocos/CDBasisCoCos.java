/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;
import de.monticore.cdbasis.cocos.ebnf.*;
import de.monticore.cdbasis.cocos.mcg2ebnf.CDPackageNotContainingCDPackage;

public class CDBasisCoCos extends CoCoParent<CDBasisCoCoChecker> {
  @Override
  public CDBasisCoCoChecker createNewChecker() {
    return new CDBasisCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CDBasisCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos(checker));
    checker.addChecker(getCheckerForMcgCoCos(checker));
    checker.addChecker(getCheckerForMcg2EbnfCoCos(checker));
  }

  @Override
  protected void addEbnfCoCos(CDBasisCoCoChecker checker) {
    // CDAttribute
    checker.addCoCo(new CDAttributeTypeExists());
    checker.addCoCo(new CDAttributeNameLowerCase());
    checker.addCoCo(new CDAttributeOverriddenTypeMatch());
    checker.addCoCo(new CDAttributeInitialTypeCompatible());
    checker.addCoCo(new CDAttributeUniqueInClassCoco());

    // CDClass
    checker.addCoCo(new CDClassExtendsNotCyclic());
    checker.addCoCo(new CDClassExtendsOnlyClasses());
    checker.addCoCo(new CDClassImplementsNotCyclic());
    checker.addCoCo(new CDClassNameUpperCase());

    // CDType
    checker.addCoCo(new CDTypeNoInitializationOfDerivedAttribute());
    checker.addCoCo(new UniqueCDTypeNames());

    // CDDefinition
    checker.addCoCo(new CDDefinitionNameUpperCase());

    // CDPackage
    checker.addCoCo(new CDPackageNameUnique());
  }

  @Override
  protected void addMcg2EbnfCoCos(CDBasisCoCoChecker checker) {
    // CDPackage
    checker.addCoCo(new CDPackageNotContainingCDPackage());
  }
}
