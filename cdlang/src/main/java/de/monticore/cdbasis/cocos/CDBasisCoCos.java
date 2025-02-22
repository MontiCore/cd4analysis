/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;
import de.monticore.cdbasis.cocos.ebnf.*;
import de.monticore.cdbasis.cocos.mcg.ModifierNotMultipleVisibilitiesCoCo;
import de.monticore.cdbasis.cocos.mcg2ebnf.CDPackageNotContainingCDPackage;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDClassExtendsOnlyClasses;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDClassImplementsNotCyclic;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDClassImplementsOnlyInterfaces;
import de.monticore.types.check.AbstractDerive;

public class CDBasisCoCos extends CoCoParent<CDBasisCoCoChecker> {

  private final AbstractDerive calculator;

  public CDBasisCoCos(AbstractDerive calculator) {
    this.calculator = calculator;
  }

  @Override
  public CDBasisCoCoChecker createNewChecker() {
    return new CDBasisCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CDBasisCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }

  @Override
  protected void addEbnfCoCos(CDBasisCoCoChecker checker) {
    // CDAttribute
    checker.addCoCo(new CDAttributeTypeExists());
    checker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
    checker.addCoCo(new CDAttributeInitialTypeCompatible(calculator));
    checker.addCoCo(new CDAttributeUniqueInClass());

    // CDClass
    checker.addCoCo(new CDClassExtendsNotCyclic());
    checker.addCoCo(new CDClassExtendsOnlyClasses());
    checker.addCoCo(new CDClassImplementsOnlyInterfaces());
    checker.addCoCo(new CDClassImplementsNotCyclic());
    checker.addCoCo(new CDClassNameUpperCase());

    // CDType
    checker.addCoCo(new CDTypeNoInitializationOfDerivedAttribute());
    checker.addCoCo(new CDTypeModifierNotPrivate());
    checker.addCoCo(new CDTypeModifierNotProtected());

    // CDDefinition
    checker.addCoCo(new CDDefinitionNameUpperCase());
    checker.addCoCo(new CDDefinitionUniqueCDTypeNames());

    // CDPackage
    checker.addCoCo(new CDPackageNameUnique());
    checker.addCoCo(new CDPackageUniqueCDTypeNames());
  }

  @Override
  protected void addMcgCoCos(CDBasisCoCoChecker checker) {
    // Modifier
    checker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());
  }

  @Override
  protected void addMcg2EbnfCoCos(CDBasisCoCoChecker checker) {
    // CDPackage
    checker.addCoCo(new CDPackageNotContainingCDPackage());
  }
}
