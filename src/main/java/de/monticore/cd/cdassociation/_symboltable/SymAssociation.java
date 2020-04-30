/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdassociation._symboltable;

import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;

import java.util.Optional;

public class SymAssociation {
  protected Optional<CDAssociationSymbol> association;
  protected CDRoleSymbol left, right;

  public SymAssociation(Optional<CDAssociationSymbol> association, CDRoleSymbol left, CDRoleSymbol right) {
    this.association = association;
    this.left = left;
    this.right = right;
  }

  public SymAssociation(CDRoleSymbol left, CDRoleSymbol right) {
    this(Optional.empty(), left, right);
  }
}
