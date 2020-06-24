/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos;

import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;
import de.monticore.cdbasis.cocos.ebnf.*;

public class CDBasisCoCos {
  public CDBasisCoCoChecker getCheckerForAllCoCos() {
    CDBasisCoCoChecker checker = new CDBasisCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public CDBasisCoCoChecker getCheckerForEbnfCoCos() {
    CDBasisCoCoChecker checker = new CDBasisCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CDBasisCoCoChecker getCheckerForMcgCoCos() {
    CDBasisCoCoChecker checker = new CDBasisCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CDBasisCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CDBasisCoCoChecker checker = new CDBasisCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CDBasisCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

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

    // CDDefintion
    checker.addCoCo(new CDDefinitionNameUpperCase());
  }

  protected void addMcgCoCos(CDBasisCoCoChecker checker) {
  }

  protected void addMcg2EbnfCoCos(CDBasisCoCoChecker checker) {
  }
}
