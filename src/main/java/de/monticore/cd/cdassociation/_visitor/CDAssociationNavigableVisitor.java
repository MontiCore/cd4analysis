/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation._visitor;

import de.monticore.cd.cdassociation._ast.*;

public class CDAssociationNavigableVisitor implements CDAssociationVisitor {
  protected boolean isNavigableLeft;
  protected boolean isNavigableRight;

  @Override
  public CDAssociationNavigableVisitor getRealThis() {
    return this;
  }

  public boolean isNavigableLeft() {
    return this.isNavigableLeft;
  }

  public boolean isNavigableRight() {
    return this.isNavigableRight;
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    this.isNavigableLeft = false;
    this.isNavigableRight = true;
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    this.isNavigableLeft = true;
    this.isNavigableRight = false;
  }

  @Override
  public void visit(ASTCDBiDir node) {
    this.isNavigableLeft = true;
    this.isNavigableRight = true;
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    // TODO SVa: what to do here?
    this.isNavigableLeft = true;
    this.isNavigableRight = true;
  }
}
