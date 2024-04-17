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

  /**
   * A set for the matched elements which can be per definition modified and edited
   *
   * @return all elements which have been matched
   */
  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(srcElem, assoc))
        .collect(Collectors.toList());
  }

  /**
   * A boolean method which gives if the element1 from srcCD is matched with the element2 from srcCD
   *
   * @param srcElem element from srcCD
   * @param tgtElem element from srcCD
   * @return true if the source classes and the role names on the side of the target class match
   */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    return check(srcElem, tgtElem, srcCD, tgtCD) || checkReverse(srcElem, tgtElem, srcCD, tgtCD);
  }

  // Check the directions of the associations and proving the source classes and the roles
  protected boolean check(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    boolean match = false;

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableLeft())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match =
          match
              || checkReference(
                      srcElem.getLeftQualifiedName().getQName(),
                      tgtElem.getLeftQualifiedName().getQName(),
                      srcCD,
                      tgtCD)
                  && checkRole(srcElem.getRight(), tgtElem.getRight());
    }

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableRight())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match =
          match
              || checkReference(
                      srcElem.getRightQualifiedName().getQName(),
                      tgtElem.getRightQualifiedName().getQName(),
                      srcCD,
                      tgtCD)
                  && checkRole(srcElem.getLeft(), tgtElem.getLeft());
    }

    return match;
  }

  protected boolean checkReverse(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    boolean match = false;

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableLeft())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match =
          match
              || checkReference(
                      srcElem.getRightQualifiedName().getQName(),
                      tgtElem.getLeftQualifiedName().getQName(),
                      srcCD,
                      tgtCD)
                  && checkRole(tgtElem.getRight(), srcElem.getLeft());
    }

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !tgtElem.getCDAssocDir().isDefinitiveNavigableRight())
        && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match =
          match
              || checkReference(
                      srcElem.getLeftQualifiedName().getQName(),
                      tgtElem.getRightQualifiedName().getQName(),
                      srcCD,
                      tgtCD)
                  && checkRole(tgtElem.getLeft(), srcElem.getRight());
    }

    return match;
  }

  protected boolean checkReference(
      String srcElem, String tgtElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
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
