package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;

public class EnumStruc {
  private ASTCDClass astcdClass;
  private ASTCDAttribute attribute;
  private ASTCDEnumConstant enumConstant;

  public EnumStruc(
      ASTCDClass astcdClass, ASTCDAttribute attribute, ASTCDEnumConstant enumConstant) {
    this.astcdClass = astcdClass;
    this.attribute = attribute;
    this.enumConstant = enumConstant;
  }

  public ASTCDClass getAstcdClass() {
    return astcdClass;
  }

  public void setAstcdClass(ASTCDClass astcdClass) {
    this.astcdClass = astcdClass;
  }

  public ASTCDAttribute getAttribute() {
    return attribute;
  }

  public void setAttribute(ASTCDAttribute attribute) {
    this.attribute = attribute;
  }

  public ASTCDEnumConstant getEnumConstant() {
    return enumConstant;
  }

  public void setEnumConstant(ASTCDEnumConstant enumConstant) {
    this.enumConstant = enumConstant;
  }
}
