package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syndiff.AssocStruct;

public class DeleteStruc {
  private AssocStruct association;
  private AssocStruct superAssoc;
  private ASTCDClass astcdClass;

  public DeleteStruc(AssocStruct association, AssocStruct superAssoc, ASTCDClass astcdClass) {
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

  public ASTCDClass getAstcdClass() {
    return astcdClass;
  }

  public void setAstcdClass(ASTCDClass astcdClass) {
    this.astcdClass = astcdClass;
  }
}
