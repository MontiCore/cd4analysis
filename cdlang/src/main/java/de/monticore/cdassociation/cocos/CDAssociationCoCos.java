/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cdassociation._cocos.CDAssociationCoCoChecker;
import de.monticore.cdassociation.cocos.ebnf.*;

public class CDAssociationCoCos extends CoCoParent<CDAssociationCoCoChecker> {
  
  protected final boolean afterTrafos;
  
  /** CoCos for the model as written. */
  public CDAssociationCoCos() {
    this(false);
  }
  
  /**
   * @param afterTrafos whether the checked model has already been transformed (e.g., by the code
   * generator); changes how modifier conflicts are reported
   */
  public CDAssociationCoCos(boolean afterTrafos) {
    this.afterTrafos = afterTrafos;
  }
  
  @Override
  public CDAssociationCoCoChecker createNewChecker() {
    return new CDAssociationCoCoChecker();
  }
  
  @Override
  protected void addCheckerForAllCoCos(CDAssociationCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }
  
  @Override
  protected void addEbnfCoCos(CDAssociationCoCoChecker checker) {
    // CDAssociation
    checker.addCoCo(new CDAssociationNameLowerCase());
    checker.addCoCo(new CDAssociationHasSymbol());
    checker.addCoCo(new CDAssociationNameUnique());
    checker.addCoCo(new CDAssociationOrderedCardinalityGreaterOne());
    checker.addCoCo(new CDAssociationRoleNameLowerCase());
    checker.addCoCo(new CDAssociationRoleNameNoConflictWithLocalAttribute());
    checker.addCoCo(new CDAssociationModifierCoCo(afterTrafos));
    checker.addCoCo(new CDAssociationJavaClassTypeCoCo());
  }
  
}
