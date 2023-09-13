package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;

public class AssocStruct {
  ASTCDAssociation association;
  AssocDirection direction;
  ClassSide side;
  boolean isSuperAssoc = false;
  ASTCDAssociation unmodifiedAssoc;

  private boolean toBeProcessed = true;

  public AssocStruct(ASTCDAssociation association, AssocDirection direction, ClassSide side) {
    this.association = association;
    this.direction = direction;
    this.side = side;
    this.unmodifiedAssoc = association;
  }

  public AssocStruct(ASTCDAssociation association, AssocDirection direction, ClassSide side, boolean isSuperAssoc) {
    this.association = association;
    this.direction = direction;
    this.side = side;
    this.unmodifiedAssoc = association;
    this.isSuperAssoc = isSuperAssoc;
  }

  public AssocStruct deepClone(){
    return new AssocStruct(this.association.deepClone(), this.direction, this.side, this.isSuperAssoc);
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

  public ASTCDAssociation getUnmodifiedAssoc() {
    return unmodifiedAssoc;
  }

  public void setUnmodifiedAssoc(ASTCDAssociation unmodifiedAssoc) {
    this.unmodifiedAssoc = unmodifiedAssoc;
  }

  public boolean isToBeProcessed() {
    return toBeProcessed;
  }

  public void setToBeProcessed(boolean toBeProcessed) {
    this.toBeProcessed = toBeProcessed;
  }
}
