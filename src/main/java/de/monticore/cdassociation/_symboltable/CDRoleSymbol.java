/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

public class CDRoleSymbol extends CDRoleSymbolTOP {

  public CDRoleSymbol(String name) {
    super(name);
  }

  public CDRoleSymbol getOtherSide() {
    return getAssociation().getOtherRole(this);
  }
}
