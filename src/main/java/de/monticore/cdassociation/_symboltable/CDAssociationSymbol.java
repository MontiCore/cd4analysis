/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

public class CDAssociationSymbol extends CDAssociationSymbolTOP {
  public CDAssociationSymbol(String name) {
    super(name);
  }

  public CDRoleSymbol getLeft() {
    return getAssoc().getLeft();
  }

  public CDRoleSymbol getRight() {
    return getAssoc().getRight();
  }
}
