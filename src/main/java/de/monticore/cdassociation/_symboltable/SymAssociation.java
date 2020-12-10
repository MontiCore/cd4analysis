/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdassociation._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SymAssociation {
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected Optional<CDAssociationSymbol> association;
  protected CDRoleSymbol left, right;
  protected boolean isAssociation, isComposition;

  public SymAssociation() {
  }

  public SymAssociation(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<CDAssociationSymbol> association, CDRoleSymbol left, CDRoleSymbol right) {
    this(left, right);
    association.ifPresent(this::setAssociation);
  }

  public SymAssociation(CDAssociationSymbol association, CDRoleSymbol left, CDRoleSymbol right) {
    this(left, right);
    setAssociation(association);
  }

  public SymAssociation(CDRoleSymbol left, CDRoleSymbol right) {
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
      throw new RuntimeException("0xCD000: unknown role, the passed role is not part of the association");
    }
  }

  public boolean isPresentAssociation() {
    return association.isPresent();
  }

  public CDAssociationSymbol getAssociation() {
    if (isPresentAssociation()) {
      return this.getAssociation();
    }
    Log.error("0xCD001: Association can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
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

  public void setAssociation(CDAssociationSymbol association) {
    this.association = Optional.ofNullable(association);
    this.association.ifPresent(a -> a.setAssociation(this));
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
}
