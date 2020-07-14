/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._ast;

import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;

public interface ASTCDAssocDir extends ASTCDAssocDirTOP {
  default CDAssociationNavigableVisitor getNavigableVisitor() {
    return new CDAssociationNavigableVisitor();
  }

  default boolean isBidirectional() {
    final CDAssociationNavigableVisitor navigableVisitor = getNavigableVisitor();
    this.accept(navigableVisitor);
    return navigableVisitor.isDefinitiveNavigableLeft() && navigableVisitor.isDefinitiveNavigableRight();
  }

  default boolean isDefinitiveNavigableLeft() {
    final CDAssociationNavigableVisitor navigableVisitor = getNavigableVisitor();
    this.accept(navigableVisitor);
    return navigableVisitor.isDefinitiveNavigableLeft();
  }

  default boolean isDefinitiveNavigableRight() {
    final CDAssociationNavigableVisitor navigableVisitor = getNavigableVisitor();
    this.accept(navigableVisitor);
    return navigableVisitor.isDefinitiveNavigableLeft();
  }
}
