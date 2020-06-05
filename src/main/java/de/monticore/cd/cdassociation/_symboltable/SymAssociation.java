/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdassociation._symboltable;

import java.util.Optional;
import de.monticore.cd.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cd.cdassociation._symboltable.CDRoleSymbol;

public class SymAssociation {
  protected Optional<CDAssociationSymbol> association;
  protected CDRoleSymbol left, right;

  public SymAssociation(Optional<CDAssociationSymbol> association, CDRoleSymbol left, CDRoleSymbol right) {
    setAssociation(association);
    setLeft(left);
    setRight(right);
  }

  public SymAssociation(CDRoleSymbol left, CDRoleSymbol right) {
    this(Optional.empty(), left, right);
  }

  // TODO add helper methods
  // get other etc ...
  public CDRoleSymbol getOtherRole(CDRoleSymbol source) {
    if (source.equals(this.left)) {
      return this.right;
    } else if (source.equals(this.right)) {
      return this.left;
    } else {
      throw new RuntimeException("unknown role, the passed role is not part of the association"); // TODO Error code
    }
  }

  public Optional<CDAssociationSymbol> getAssociation() {
    return association;
  }

  public void setAssociation(Optional<CDAssociationSymbol> association) {
    this.association = association;
    this.association.get().setAssociation(this);
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
}
