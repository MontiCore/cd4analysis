package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;

/**
 * This is used to merge overlapping associations.
 * Attribute association: base association to merge superAssoc into.
 * Attribute superAssoc: superAssociation.
 * Attribute astcdClass: class from which superAssoc must be removed.
 */
public class DeleteStruct {
  private AssocStruct association;
  private AssocStruct superAssoc;
  private ASTCDType astcdClass;

  public DeleteStruct(AssocStruct association, AssocStruct superAssoc, ASTCDType astcdClass) {
    this.association = association;
    this.superAssoc = superAssoc;
    this.astcdClass = astcdClass;
  }

  public AssocStruct getAssociation() {
    return association;
  }

  public void setAssociation(AssocStruct association) {
    this.association = association;
  }

  public AssocStruct getSuperAssoc() {
    return superAssoc;
  }

  public void setSuperAssoc(AssocStruct superAssoc) {
    this.superAssoc = superAssoc;
  }

  public ASTCDType getAstcdClass() {
    return astcdClass;
  }

  public void setAstcdClass(ASTCDClass astcdClass) {
    this.astcdClass = astcdClass;
  }
}
