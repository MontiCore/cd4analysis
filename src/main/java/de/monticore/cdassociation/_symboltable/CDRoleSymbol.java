/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation._visitor.CDAssociationVisitor;

public class CDRoleSymbol extends CDRoleSymbolTOP {

  public CDRoleSymbol(String name) {
    super(name);
  }

  public boolean isLeft() {
    return this.association.getLeft() == this;
  }

  @Override
  public void accept(CDAssociationVisitor visitor) {
    visitor.handle(this);
  }
}
