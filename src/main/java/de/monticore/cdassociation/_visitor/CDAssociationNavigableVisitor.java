/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._visitor;

import de.monticore.cdassociation._ast.ASTCDBiDir;
import de.monticore.cdassociation._ast.ASTCDLeftToRightDir;
import de.monticore.cdassociation._ast.ASTCDRightToLeftDir;
import de.monticore.cdassociation._ast.ASTCDUnspecifiedDir;

public class CDAssociationNavigableVisitor implements CDAssociationVisitor {
  protected boolean isDefinitiveNavigableLeft;
  protected boolean isDefinitiveNavigableRight;

  private CDAssociationNavigableVisitor realThis;

  public CDAssociationNavigableVisitor() {
    setRealThis(this);
  }

  @Override
  public CDAssociationNavigableVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(CDAssociationNavigableVisitor realThis) {
    this.realThis = realThis;
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
