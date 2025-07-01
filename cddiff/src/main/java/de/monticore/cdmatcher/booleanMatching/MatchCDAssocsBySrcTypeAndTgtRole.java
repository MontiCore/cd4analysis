/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.caching.StructureCache;

import java.util.Optional;

public class MatchCDAssocsBySrcTypeAndTgtRole implements BooleanMatchingStrategy<ASTCDAssociation> {

  protected final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  protected final StructureCache structureCache;

  public MatchCDAssocsBySrcTypeAndTgtRole(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache) {
    this.typeMatcher = typeMatcher;
    this.structureCache = structureCache;
  }

  /**
   * Match two associations iff the role-names match in a navigable direction and the corresponding
   * source-types match, as well.
   */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    return check(srcElem, tgtElem) || checkReverse(srcElem, tgtElem);
  }

  /** Match two associations, assuming both are written in the same orientation. */
  protected boolean check(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    boolean match = false;

    // for the left side of both associations first check navigability, then the referenced classes
    // and role-names
    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableLeft()) && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match = checkReference(srcElem, tgtElem, true, true) && checkRole(srcElem.getRight(), tgtElem.getRight());
    }

    // same as above but for the right side of the association
    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableRight()) && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match = match || (checkReference(srcElem, tgtElem, false, false) && checkRole(srcElem.getLeft(), tgtElem.getLeft()));
    }

    return match;
  }

  /** Match two associations, assuming both are written in opposite orientations. */
  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    boolean match = false;

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableLeft()) && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match = checkReference(srcElem, tgtElem, false , true) && checkRole(tgtElem.getRight(), srcElem.getLeft());
    }

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableRight()) && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match = match || (checkReference(srcElem, tgtElem, true, false)) && checkRole(tgtElem.getLeft(), srcElem.getRight());
    }

    return match;
  }

  /** We check if the referenced types match using the provided type-matcher. */
  protected boolean checkReference(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, boolean srcIsLeft, boolean tgtIsLeft) {
    Optional<ASTCDType> srcType = srcIsLeft ? structureCache.getLeftType(srcElem) : structureCache.getRightType(srcElem);
    Optional<ASTCDType> tgtType = tgtIsLeft ? structureCache.getLeftType(tgtElem) : structureCache.getRightType(tgtElem);

    if (srcType.isPresent() && tgtType.isPresent()) {
      return typeMatcher.isMatched(srcType.get(), tgtType.get());
    }
    return false;
  }

  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    return CDDiffUtil.inferRole(srcElem).equals(CDDiffUtil.inferRole(tgtElem));
  }

}
