/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;

public interface ASTCDAssocDir extends ASTCDAssocDirTOP {
  default CDAssociationNavigableVisitor getNavigableVisitor() {
    return new CDAssociationNavigableVisitor();
  }

  default boolean isBidirectional() {
    final CDAssociationNavigableVisitor navigableVisitor = getNavigableVisitor();
    CDAssociationTraverser t = CDAssociationMill.traverser();
    t.add4CDAssociation(navigableVisitor);
    this.accept(t);
    return navigableVisitor.isDefinitiveNavigableLeft()
        && navigableVisitor.isDefinitiveNavigableRight();
  }

  default boolean isDefinitiveNavigableLeft() {
    final CDAssociationNavigableVisitor navigableVisitor = getNavigableVisitor();
    CDAssociationTraverser t = CDAssociationMill.traverser();
    t.add4CDAssociation(navigableVisitor);
    this.accept(t);
    return navigableVisitor.isDefinitiveNavigableLeft();
  }

  default boolean isDefinitiveNavigableRight() {
    final CDAssociationNavigableVisitor navigableVisitor = getNavigableVisitor();
    CDAssociationTraverser t = CDAssociationMill.traverser();
    t.add4CDAssociation(navigableVisitor);
    this.accept(t);
    return navigableVisitor.isDefinitiveNavigableRight();
  }
}
