/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._visitor;

import de.monticore.cdassociation._ast.*;

public class CDAssociationNavigableVisitor implements CDAssociationVisitor {
  protected boolean isDefinitiveNavigableLeft;
  protected boolean isDefinitiveNavigableRight;

  @Override
  public CDAssociationNavigableVisitor getRealThis() {
    return this;
  }

  public boolean isDefinitiveNavigableLeft() {
    return this.isDefinitiveNavigableLeft;
  }

  public boolean isDefinitiveNavigableRight() {
    return this.isDefinitiveNavigableRight;
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    this.isDefinitiveNavigableLeft = false;
    this.isDefinitiveNavigableRight = true;
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    this.isDefinitiveNavigableLeft = true;
    this.isDefinitiveNavigableRight = false;
  }

  @Override
  public void visit(ASTCDBiDir node) {
    this.isDefinitiveNavigableLeft = true;
    this.isDefinitiveNavigableRight = true;
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    this.isDefinitiveNavigableLeft = false;
    this.isDefinitiveNavigableRight = false;
  }
}
