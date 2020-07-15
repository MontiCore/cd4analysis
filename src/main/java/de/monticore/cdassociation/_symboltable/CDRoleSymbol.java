/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

public class CDRoleSymbol extends CDRoleSymbolTOP {

  public CDRoleSymbol(String name) {
    super(name);
  }

  public boolean isLeft() {
    return this.association.getLeft() == this;
  }

  public CDRoleSymbol getOtherSide() {
    return getAssociation().getOtherRole(this);
  }
}
