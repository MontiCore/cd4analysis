/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsBySrcTypeAndTgtRole;
import de.monticore.cdmatcher.caching.StructureCache;

public class RolePrefixIfPresentIncStrategy extends MatchCDAssocsBySrcTypeAndTgtRole {

  public RolePrefixIfPresentIncStrategy(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache) {
    super(typeMatcher, structureCache);
  }

  @Override
  protected boolean check(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    // associations are <- and -> or -> and <-
    boolean inverseNavigation = tgtElem.getCDAssocDir().isDefinitiveNavigableRight() == !srcElem
        .getCDAssocDir().isDefinitiveNavigableRight() && tgtElem.getCDAssocDir()
            .isDefinitiveNavigableLeft() == !srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
        && !(srcElem.getCDAssocDir().isBidirectional() || tgtElem.getCDAssocDir()
            .isBidirectional());

    return checkReference(srcElem, tgtElem, true, true) && checkRole(srcElem.getRight(), tgtElem.getRight())
      && checkReference(srcElem, tgtElem, false, false)
        && checkRole(srcElem.getLeft(), tgtElem.getLeft()) && !inverseNavigation;
  }

  @Override
  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    boolean inverseNavigation = tgtElem.getCDAssocDir().isDefinitiveNavigableRight() == !srcElem
        .getCDAssocDir().isDefinitiveNavigableLeft() && tgtElem.getCDAssocDir()
            .isDefinitiveNavigableLeft() == !srcElem.getCDAssocDir().isDefinitiveNavigableRight()
        && !(srcElem.getCDAssocDir().isBidirectional() || tgtElem.getCDAssocDir()
            .isBidirectional());

    return checkReference(srcElem, tgtElem, true, false) && checkRole(srcElem.getRight(), tgtElem.getLeft())
      && checkReference(srcElem, tgtElem, false, true)
        && checkRole(srcElem.getLeft(), tgtElem.getRight()) && !inverseNavigation;
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    if (srcElem.isPresentCDRole() && tgtElem.isPresentCDRole()) {
      return srcElem.getCDRole().getName().startsWith(tgtElem.getCDRole().getName());
    }
    return !tgtElem.isPresentCDRole();
  }

}
