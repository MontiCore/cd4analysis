/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

public class SymAssociation {
  protected CDAssociationSymbol association;
  protected CDRoleSymbol left, right;
  protected boolean isAssociation, isComposition;
  protected boolean isDerived;

  public SymAssociation(CDAssociationSymbol association, CDRoleSymbol left, CDRoleSymbol right) {
    setAssociation(association);
    setLeft(left);
    setRight(right);
  }

  public CDRoleSymbol getOtherRole(CDRoleSymbol source) {
    if (source.equals(this.left)) {
      return this.right;
    }
    else if (source.equals(this.right)) {
      return this.left;
    }
    else {
      throw new RuntimeException("unknown role, the passed role is not part of the association"); // TODO Error code
    }
  }

  public CDAssociationSymbol getAssociation() {
    return association;
  }

  public void setAssociation(CDAssociationSymbol association) {
    this.association = association;
    this.association.setAssociation(this);
  }

  public CDRoleSymbol getLeft() {
    return left;
  }

  public void setLeft(CDRoleSymbol left) {
    this.left = left;
    this.left.setAssociation(this);
  }

  public CDRoleSymbol getRight() {
    return right;
  }

  public void setRight(CDRoleSymbol right) {
    this.right = right;
    this.right.setAssociation(this);
  }

  public boolean isAssociation() {
    return isAssociation;
  }

  public void setIsAssociation(boolean association) {
    isAssociation = association;
  }

  public boolean isComposition() {
    return isComposition;
  }

  public void setIsComposition(boolean composition) {
    isComposition = composition;
  }

  public boolean isDerived() {
    return isDerived;
  }

  public void setIsDerived(boolean derived) {
    isDerived = derived;
  }
}
