package de.monticore.matcher;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SrcTgtAssocMatcher implements MatchingStrategy<ASTCDAssociation> {

  protected final MatchingStrategy<ASTCDType> typeMatcher;
  protected final ASTCDCompilationUnit srcCD;
  protected final ASTCDCompilationUnit tgtCD;

  public SrcTgtAssocMatcher(
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

    if (check(srcElem, tgtElem, srcCD, tgtCD) || checkReverse(srcElem, tgtElem, srcCD, tgtCD)) {
      return true;
    } else {
      System.out.println("There is a problem with isMatched() in SrcTgtAssocMatcher!");
    }
    return false;
  }

  // Check the directions of the associations and proving the source classes and the roles
  protected boolean check(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    if (tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
        && srcElem.getCDAssocDir().isDefinitiveNavigableRight()) {
      return checkReference(
              srcElem.getLeftQualifiedName().getQName(),
              tgtElem.getLeftQualifiedName().getQName(),
              srcCD,
              tgtCD)
          && checkRole(srcElem.getRight(), tgtElem.getRight());
    }

    if (tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
        && srcElem.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return checkReference(
              srcElem.getRightQualifiedName().getQName(),
              tgtElem.getRightQualifiedName().getQName(),
              srcCD,
              tgtCD)
          && checkRole(srcElem.getLeft(), tgtElem.getLeft());
    }

    if (!tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
        || !tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcElem.getCDAssocDir().isDefinitiveNavigableRight()) {
      return (checkReference(
                  srcElem.getRightQualifiedName().getQName(),
                  tgtElem.getRightQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getLeft(), srcElem.getLeft()))
          || (checkReference(
                  srcElem.getLeftQualifiedName().getQName(),
                  tgtElem.getLeftQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getRight(), srcElem.getRight()))
          || (checkReference(
                  srcElem.getRightQualifiedName().getQName(),
                  tgtElem.getLeftQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getRight(), srcElem.getLeft()))
          || (checkReference(
                  srcElem.getLeftQualifiedName().getQName(),
                  tgtElem.getRightQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getLeft(), srcElem.getRight()));
    }
    return false;
  }

  protected boolean checkReverse(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    if (tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
        && srcElem.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return checkReference(
              srcElem.getRightQualifiedName().getQName(),
              tgtElem.getLeftQualifiedName().getQName(),
              srcCD,
              tgtCD)
          && checkRole(tgtElem.getRight(), srcElem.getLeft());
    }

    if (tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
        && srcElem.getCDAssocDir().isDefinitiveNavigableRight()) {
      return checkReference(
              srcElem.getLeftQualifiedName().getQName(),
              tgtElem.getRightQualifiedName().getQName(),
              srcCD,
              tgtCD)
          && checkRole(tgtElem.getLeft(), srcElem.getRight());
    }

    if ((!tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())
        || (!tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      return (checkReference(
                  srcElem.getRightQualifiedName().getQName(),
                  tgtElem.getRightQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getLeft(), srcElem.getLeft()))
          || (checkReference(
                  srcElem.getLeftQualifiedName().getQName(),
                  tgtElem.getLeftQualifiedName().getQName(),
                  srcCD,
                  tgtCD))
              && checkRole(tgtElem.getRight(), srcElem.getRight())
          || (checkReference(
                  srcElem.getRightQualifiedName().getQName(),
                  tgtElem.getLeftQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getRight(), srcElem.getLeft()))
          || (checkReference(
                  srcElem.getLeftQualifiedName().getQName(),
                  tgtElem.getRightQualifiedName().getQName(),
                  srcCD,
                  tgtCD)
              && checkRole(tgtElem.getLeft(), srcElem.getRight()));
    }
    return false;
  }

  protected boolean checkReference(
      String srcElem, String tgt, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    Optional<CDTypeSymbol> srcTypeSymbol = srcCD.getEnclosingScope().resolveCDTypeDown(srcElem);
    Optional<CDTypeSymbol> tgtTypeSymbol = tgtCD.getEnclosingScope().resolveCDTypeDown(tgt);

    if (srcTypeSymbol.isPresent() && tgtTypeSymbol.isPresent()) {
      ASTCDType srcType = srcTypeSymbol.get().getAstNode();
      ASTCDType tgtType = tgtTypeSymbol.get().getAstNode();
      return typeMatcher.isMatched(tgtType, srcType);
    }
    Log.error("Could not resolve match source classes!");
    return false;
  }

  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {

    return CDDiffUtil.inferRole(srcElem).equals(CDDiffUtil.inferRole(tgtElem));
  }
}
