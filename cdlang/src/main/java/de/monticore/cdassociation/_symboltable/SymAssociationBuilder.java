/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import java.util.Optional;

@Deprecated
public class SymAssociationBuilder {
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected Optional<CDAssociationSymbol> association = Optional.empty();

  protected CDRoleSymbol left, right;
  protected boolean isAssociation, isComposition;

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

  public SymAssociation build() {
    final SymAssociation symAssociation =
        new SymAssociation(this.association, this.left, this.right);

    symAssociation.setIsAssociation(this.isAssociation);
    symAssociation.setIsComposition(this.isComposition);

    return symAssociation;
  }
}
