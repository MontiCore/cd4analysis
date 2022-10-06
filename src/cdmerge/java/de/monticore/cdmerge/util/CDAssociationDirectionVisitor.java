package de.monticore.cdmerge.util;

import de.monticore.cdassociation._ast.ASTCDBiDir;
import de.monticore.cdassociation._ast.ASTCDLeftToRightDir;
import de.monticore.cdassociation._ast.ASTCDRightToLeftDir;
import de.monticore.cdassociation._ast.ASTCDUnspecifiedDir;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;

public class CDAssociationDirectionVisitor extends CDAssociationNavigableVisitor {

  private AssociationDirection direction;

  public CDAssociationDirectionVisitor() {
    super();
    this.direction = AssociationDirection.Unspecified;
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    super.visit(node);
    this.direction = AssociationDirection.LeftToRight;
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    super.visit(node);
    this.direction = AssociationDirection.RightToLeft;
  }

  @Override
  public void visit(ASTCDBiDir node) {
    super.visit(node);
    this.direction = AssociationDirection.BiDirectional;
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    super.visit(node);
    this.direction = AssociationDirection.Unspecified;
  }

  public AssociationDirection getDirection() {
    return direction;
  }

}
