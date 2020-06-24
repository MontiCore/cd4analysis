/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import java.util.Optional;

public class SymAssociationBuilder {
  protected Optional<CDAssociationSymbol> association;
  protected CDRoleSymbol left, right;
  protected boolean isAssociation, isComposition;
  protected boolean isDerived;

  public SymAssociationBuilder setAssociationSymbol(CDAssociationSymbol associationSymbol) {
    this.association = Optional.ofNullable(associationSymbol);
    return this;
  }

  public SymAssociationBuilder setLeftRole(CDRoleSymbol role) {
    this.left = role;
    return this;
  }

  public SymAssociationBuilder setRightRole(CDRoleSymbol role) {
    this.right = role;
    return this;
  }

  public SymAssociationBuilder setRoles(CDRoleSymbol leftRole, CDRoleSymbol rightRole) {
    this.left = leftRole;
    this.right = rightRole;
    return this;
  }

  public void setIsAssociation(boolean association) {
    isAssociation = association;
  }

  public void setIsComposition(boolean composition) {
    isComposition = composition;
  }

  public void setIsDerived(boolean derived) {
    isDerived = derived;
  }

  public SymAssociation build() {
    final SymAssociation symAssociation = new SymAssociation(this.association, this.left, this.right);

    symAssociation.setIsAssociation(this.isAssociation);
    symAssociation.setIsComposition(this.isComposition);
    symAssociation.setIsDerived(this.isDerived);

    return symAssociation;
  }
}
