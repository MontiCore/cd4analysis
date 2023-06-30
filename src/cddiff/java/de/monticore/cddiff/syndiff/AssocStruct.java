package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.syndiff.imp.AssocDirection;
import de.monticore.cddiff.syndiff.imp.ClassSide;

public class AssocStruct {
  ASTCDAssociation association;
  AssocDirection direction;
  ClassSide side;
  boolean isSuperAssoc = false;

  public AssocStruct(ASTCDAssociation association, AssocDirection direction, ClassSide side) {
    this.association = association;
    this.direction = direction;
    this.side = side;
  }

  public ASTCDAssociation getAssociation() {
    return association;
  }

  public void setAssociation(ASTCDAssociation association) {
    this.association = association;
  }

  public AssocDirection getDirection() {
    return direction;
  }

  public void setDirection(AssocDirection direction) {
    this.direction = direction;
  }

  public ClassSide getSide() {
    return side;
  }

  public void setSide(ClassSide side) {
    this.side = side;
  }

  public boolean isSuperAssoc() {
    return isSuperAssoc;
  }

  public void setSuperAssoc(boolean superAssoc) {
    isSuperAssoc = superAssoc;
  }
}
