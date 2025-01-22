package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fall-back Matching Strategy for Tool-assisted Concretization Should be executed if no incarnation
 * is defined by the incarnation mapping. If multiple elements are matched, abort.
 * (CDMerge-compliant)
 */
public class MatchCDAssocsGreedy extends MatchCDAssocsBySrcNameAndTgtRole {

  public MatchCDAssocsGreedy(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }

  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(tgtElem -> isMatched(srcElem, tgtElem))
        .collect(Collectors.toList());
  }

  /**
   * Associations are matched iff (1) referenced types match (2) association names match if present
   * (3) role-names match if present
   */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (srcElem.isPresentName()
        && tgtElem.isPresentName()
        && !srcElem.getName().equals(tgtElem.getName())) {
      return false;
    }
    return check(srcElem, tgtElem) || checkReverse(srcElem, tgtElem);
  }

  @Override
  protected boolean check(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (srcElem.getLeft().isPresentCDRole()
        && tgtElem.getLeft().isPresentCDRole()
        && !srcElem
            .getLeft()
            .getCDRole()
            .getName()
            .equals(tgtElem.getLeft().getCDRole().getName())) {
      return false;
    }
    if (srcElem.getRight().isPresentCDRole()
        && tgtElem.getRight().isPresentCDRole()
        && !srcElem
            .getRight()
            .getCDRole()
            .getName()
            .equals(tgtElem.getRight().getCDRole().getName())) {
      return false;
    }
    return checkReference(
            srcElem.getLeftQualifiedName().getQName(), tgtElem.getLeftQualifiedName().getQName())
        && checkReference(
            srcElem.getRightQualifiedName().getQName(), tgtElem.getRightQualifiedName().getQName());
  }

  @Override
  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (srcElem.getLeft().isPresentCDRole()
        && tgtElem.getRight().isPresentCDRole()
        && !srcElem
            .getLeft()
            .getCDRole()
            .getName()
            .equals(tgtElem.getRight().getCDRole().getName())) {
      return false;
    }
    if (srcElem.getRight().isPresentCDRole()
        && tgtElem.getLeft().isPresentCDRole()
        && !srcElem
            .getRight()
            .getCDRole()
            .getName()
            .equals(tgtElem.getLeft().getCDRole().getName())) {
      return false;
    }
    return checkReference(
            srcElem.getLeftQualifiedName().getQName(), tgtElem.getRightQualifiedName().getQName())
        && checkReference(
            srcElem.getRightQualifiedName().getQName(), tgtElem.getLeftQualifiedName().getQName());
  }
}
