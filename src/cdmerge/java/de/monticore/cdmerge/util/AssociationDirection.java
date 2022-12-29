/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cdassociation._ast.ASTCDAssociation;

public enum AssociationDirection {
  Unspecified,
  LeftToRight,
  RightToLeft,
  BiDirectional;

  public static AssociationDirection getDirection(ASTCDAssociation association) {
    if (association == null) {
      return Unspecified;
    }
    if (association.getCDAssocDir() == null) {
      return Unspecified;
    }
    if (association.getCDAssocDir().isBidirectional()) {
      return BiDirectional;
    }
    if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return RightToLeft;
    }
    if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return LeftToRight;
    }
    return Unspecified;
  }
}
