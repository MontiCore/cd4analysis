package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdassociation._ast.ASTCDAssociation;

/**
 * Data structure for associations. This is used as overlapping associations must be handled - merging and deleting of associations.
 * Attribute association: modified association - might not be changed.
 * Attribute direction: direction of the association. If the association is bidirectional, two AssocStructs for this association exist in the map, as it has two source classes.
 * Attribute side: side on which the source class is.
 * Attribute isSuperAssoc: true if the association is a super association.
 * Attribute unmodifiedAssoc: the base version of the association.
 * Attribute toBeProcessed: false if a loop association that is a subassociation of this one exists - smaller object diagrams can be generated through this.
 * There is a 1:1 relationship between associations and AssocStructs if the AssocStruct mustn't be deleted because of overlapping.
 * Because of that the unmodified association is used to get the corresponding AssocStruct for an association. If such isn't found, the association is deleted because of overlapping.
 */
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

  public AssocStruct(
      ASTCDAssociation association,
      AssocDirection direction,
      ClassSide side,
      boolean isSuperAssoc) {
    this.association = association;
    this.direction = direction;
    this.side = side;
    this.unmodifiedAssoc = association;
    this.isSuperAssoc = isSuperAssoc;
  }

  public AssocStruct deepClone() {
    return new AssocStruct(
        this.association.deepClone(), this.direction, this.side, this.isSuperAssoc);
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
