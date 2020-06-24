/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.cocos;

import de.monticore.cdassociation._cocos.CDAssociationCoCoChecker;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationHasSymAssociation;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameLowerCase;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameNoConflictWithAttribute;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameUnique;

public class CDAssociationCoCos {
  public CDAssociationCoCoChecker getCheckerForAllCoCos() {
    CDAssociationCoCoChecker checker = new CDAssociationCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CDAssociationCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

  public CDAssociationCoCoChecker getCheckerForEbnfCoCos() {
    CDAssociationCoCoChecker checker = new CDAssociationCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CDAssociationCoCoChecker getCheckerForMcgCoCos() {
    CDAssociationCoCoChecker checker = new CDAssociationCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CDAssociationCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CDAssociationCoCoChecker checker = new CDAssociationCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addEbnfCoCos(CDAssociationCoCoChecker checker) {
    // CDEnum
    checker.addCoCo(new CDAssociationHasSymAssociation());
    checker.addCoCo(new CDAssociationNameLowerCase());
    checker.addCoCo(new CDAssociationNameNoConflictWithAttribute());
    checker.addCoCo(new CDAssociationNameUnique());
  }

  protected void addMcgCoCos(CDAssociationCoCoChecker checker) {

  }

  protected void addMcg2EbnfCoCos(CDAssociationCoCoChecker checker) {

  }
}
