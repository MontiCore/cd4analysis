/* (c) https://github.com/MontiCore/monticore */
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

public class MatchCDAssocsBySrcTypeAndTgtRole implements
    ExternalCandidatesMatchingStrategy<ASTCDAssociation> {
  
  protected final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  protected final ASTCDCompilationUnit srcCD;
  protected final ASTCDCompilationUnit tgtCD;
  
  public MatchCDAssocsBySrcTypeAndTgtRole(BooleanMatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.typeMatcher = typeMatcher;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }
  
  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream().filter(assoc -> isMatched(
        srcElem, assoc)).collect(Collectors.toList());
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
      match = checkReference(srcElem.getLeftQualifiedName().getQName(), tgtElem
          .getLeftQualifiedName().getQName()) && checkRole(srcElem.getRight(), tgtElem.getRight());
    }
    
    // same as above but for the right side of the association
    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableRight()) && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match = match || (checkReference(srcElem.getRightQualifiedName().getQName(), tgtElem
          .getRightQualifiedName().getQName()) && checkRole(srcElem.getLeft(), tgtElem.getLeft()));
    }
    
    return match;
  }

  /**
   * Match two associations, assuming both are written in opposite orientations.
   * <p>
   * Note: {@link #checkRole} is always called with the src side first and the tgt side second,
   * consistent with {@link #check} and all {@code checkRole} overrides.
   */
  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    boolean match = false;

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableRight() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableLeft()) && (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableRight())) {
      match = checkReference(srcElem.getRightQualifiedName().getQName(), tgtElem
          .getLeftQualifiedName().getQName()) && checkRole(srcElem.getLeft(), tgtElem.getRight());
    }

    if ((tgtElem.getCDAssocDir().isDefinitiveNavigableLeft() || !tgtElem.getCDAssocDir()
        .isDefinitiveNavigableRight()) && (srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            || !srcElem.getCDAssocDir().isDefinitiveNavigableLeft())) {
      match = match || (checkReference(srcElem.getLeftQualifiedName().getQName(), tgtElem
          .getRightQualifiedName().getQName()) && checkRole(srcElem.getRight(), tgtElem.getLeft()));
    }

    return match;
  }

  /** We check if the referenced types match using the provided type-matcher. */
  protected boolean checkReference(String srcElem, String tgtElem) {
    Optional<ASTCDType> srcType = resolveConcreteCDTyp(srcElem);
    Optional<ASTCDType> tgtType = resolveReferenceCDTyp(tgtElem);

    if (srcType.isPresent() && tgtType.isPresent()) {
      return typeMatcher.isMatched(srcType.get(), tgtType.get());
    }
    return false;
  }

  protected Optional<ASTCDType> resolveConcreteCDTyp(String qName) {
    return srcCD.getEnclosingScope().resolveCDTypeDown(qName).map(CDTypeSymbol::getAstNode);
  }

  protected Optional<ASTCDType> resolveReferenceCDTyp(String qName) {
    return tgtCD.getEnclosingScope().resolveCDTypeDown(qName).map(CDTypeSymbol::getAstNode);
  }

  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    return CDDiffUtil.inferRole(srcElem).equals(CDDiffUtil.inferRole(tgtElem));
  }

}
