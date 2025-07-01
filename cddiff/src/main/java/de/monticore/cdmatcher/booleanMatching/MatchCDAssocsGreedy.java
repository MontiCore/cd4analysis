/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.caching.StructureCache;

import java.util.Optional;

/**
 * Fall-back Matching Strategy for Tool-assisted Concretization Should be executed if no incarnation
 * is defined by the incarnation mapping. If multiple elements are matched, abort.
 * (CDMerge-compliant)
 */
public class MatchCDAssocsGreedy implements BooleanMatchingStrategy<ASTCDAssociation> {
  private final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  private final StructureCache structureCache;


  public MatchCDAssocsGreedy(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache) {
    this.typeMatcher = typeMatcher;
    this.structureCache = structureCache;

  }

  /**
   * Associations are matched iff (1) referenced types match (2) association names match if present
   * (3) role-names match if present
   */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (srcElem.isPresentName() && tgtElem.isPresentName() && !srcElem.getName().equals(tgtElem
        .getName())) {
      return false;
    }
    return check(srcElem, tgtElem) || checkReverse(srcElem, tgtElem);
  }

    protected boolean check(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (checkNonMatchingRoleName(srcElem.getLeft(), tgtElem.getLeft())
    || checkNonMatchingRoleName(srcElem.getRight(), tgtElem.getRight())) {
      return false;
    }
    return checkTypes(srcElem, tgtElem, false);
  }

  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (checkNonMatchingRoleName(srcElem.getLeft(), tgtElem.getRight())
      || checkNonMatchingRoleName(srcElem.getRight(), tgtElem.getLeft())) {
      return false;
    }
    return checkTypes(srcElem, tgtElem, true);
  }

  /**
   * Check if the side's do not match, this is the case if both sides have a role but the names are not equal.
   * @param srcElem the source element
   * @param tgtElem the target element
   * @return true if the role-names are not equal, false otherwise
   */
  private boolean checkNonMatchingRoleName(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    if (srcElem.isPresentCDRole() && tgtElem.isPresentCDRole()) {
      return !srcElem.getCDRole().getName().equals(tgtElem.getCDRole().getName());
    }
    return false;
  }

  private boolean checkTypes(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, boolean reverse) {
    Optional<ASTCDType> srcLeft = structureCache.getLeftType(srcElem);
    Optional<ASTCDType> tgtLeft = structureCache.getLeftType(tgtElem);
    Optional<ASTCDType> srcRight = structureCache.getRightType(srcElem);
    Optional<ASTCDType> tgtRight = structureCache.getRightType(tgtElem);

    if(srcLeft.isEmpty() || tgtLeft.isEmpty() || srcRight.isEmpty() || tgtRight.isEmpty()) {
      return false;
    }

    if (reverse) {
      return typeMatcher.isMatched(srcRight.get(), tgtLeft.get()) && typeMatcher.isMatched(srcLeft.get(), tgtRight.get());
    } else {
      return typeMatcher.isMatched(srcLeft.get(), tgtLeft.get()) && typeMatcher.isMatched(srcRight.get(), tgtRight.get());
    }
  }

}
