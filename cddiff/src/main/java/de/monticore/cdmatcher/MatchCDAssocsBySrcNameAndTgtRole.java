package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MatchCDAssocsBySrcNameAndTgtRole implements MatchingStrategy<ASTCDAssociation> {

  protected final MatchingStrategy<ASTCDType> typeMatcher;
  protected final ASTCDCompilationUnit srcCD;
  protected final ASTCDCompilationUnit tgtCD;

  public MatchCDAssocsBySrcNameAndTgtRole(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    this.typeMatcher = typeMatcher;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(srcElem, assoc))
        .collect(Collectors.toList());
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
    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableLeft())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match =
          checkReference(
                  srcElem.getLeftQualifiedName().getQName(),
                  tgtElem.getLeftQualifiedName().getQName())
              && checkRole(srcElem.getRight(), tgtElem.getRight());
    }

    // same as above but for the right side of the association
    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableRight())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match =
          match
              || (checkReference(
                      srcElem.getRightQualifiedName().getQName(),
                      tgtElem.getRightQualifiedName().getQName())
                  && checkRole(srcElem.getLeft(), tgtElem.getLeft()));
    }

    return match;
  }

  /** Match two associations, assuming both are written in opposite orientations. */
  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    boolean match = false;

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableLeft())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match =
          checkReference(
                  srcElem.getRightQualifiedName().getQName(),
                  tgtElem.getLeftQualifiedName().getQName())
              && checkRole(tgtElem.getRight(), srcElem.getLeft());
    }

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableRight())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match =
          match
              || (checkReference(
                      srcElem.getLeftQualifiedName().getQName(),
                      tgtElem.getRightQualifiedName().getQName())
                  && checkRole(tgtElem.getLeft(), srcElem.getRight()));
    }

    return match;
  }

  /** We check if the referenced types match using the provided type-matcher. */
  protected boolean checkReference(String srcElem, String tgtElem) {
    Optional<CDTypeSymbol> srcTypeSymbol = srcCD.getEnclosingScope().resolveCDTypeDown(srcElem);
    Optional<CDTypeSymbol> tgtTypeSymbol = tgtCD.getEnclosingScope().resolveCDTypeDown(tgtElem);

    if (srcTypeSymbol.isPresent() && tgtTypeSymbol.isPresent()) {
      ASTCDType srcType = srcTypeSymbol.get().getAstNode();
      ASTCDType tgtType = tgtTypeSymbol.get().getAstNode();
      return typeMatcher.isMatched(srcType, tgtType);
    }
    return false;
  }

  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    return CDDiffUtil.inferRole(srcElem).equals(CDDiffUtil.inferRole(tgtElem));
  }
}
