/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation._symboltable;

import java.util.Optional;

public class SymAssociationBuilder {
  protected Optional<CDAssociationSymbol> association;
  protected CDRoleSymbol left, right;

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

  public SymAssociation build() {
    return new SymAssociation(this.association, this.left, this.right);
  }
}
