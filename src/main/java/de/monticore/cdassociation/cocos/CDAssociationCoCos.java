/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cdassociation._cocos.CDAssociationCoCoChecker;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameLowerCase;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameNoConflictWithAttribute;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameUnique;

public class CDAssociationCoCos extends CoCoParent<CDAssociationCoCoChecker> {
  @Override
  public CDAssociationCoCoChecker createNewChecker() {
    return new CDAssociationCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CDAssociationCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos(checker));
    checker.addChecker(getCheckerForMcgCoCos(checker));
    checker.addChecker(getCheckerForMcg2EbnfCoCos(checker));
  }

  @Override
  protected void addEbnfCoCos(CDAssociationCoCoChecker checker) {
    // CDAssociation
    checker.addCoCo(new CDAssociationNameLowerCase());
    checker.addCoCo(new CDAssociationNameNoConflictWithAttribute());
    checker.addCoCo(new CDAssociationNameUnique());
  }
}
