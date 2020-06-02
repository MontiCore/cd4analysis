/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation._symboltable;

import java.util.Optional;

public class SymAssocationBuilder {
  protected Optional<CDAssociationSymbol> association;
  protected CDRoleSymbol left, right;

  public SymAssocationBuilder setAssociationSymbol(CDAssociationSymbol associationSymbol) {
    this.association = Optional.ofNullable(association);
    return this;
  }

  public SymAssocationBuilder setLeftRole(CDRoleSymbol role) {
    this.left = role;
    return this;
  }

  public SymAssocationBuilder setRightRole(CDRoleSymbol role) {
    this.right = role;
    return this;
  }

  public SymAssocationBuilder setRoles(CDRoleSymbol leftRole, CDRoleSymbol rightRole) {
    this.left = leftRole;
    this.right = rightRole;
    return this;
  }

  public SymAssociation build() {
    return new SymAssociation(this.association, this.left, this.right);
  }
}
