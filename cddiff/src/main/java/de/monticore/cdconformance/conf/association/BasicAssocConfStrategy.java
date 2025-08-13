/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.association;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicAssocConfStrategy implements ConformanceStrategy<ASTCDAssociation> {
  
  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;
  protected CDIncarnationMapping incMapping;
  protected boolean allowCardRestriction;
  
  public BasicAssocConfStrategy(ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD,
      CDIncarnationMapping incMapping, boolean allowCardRestriction) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.incMapping = incMapping;
    this.allowCardRestriction = allowCardRestriction;
  }
  
  @Override
  public boolean checkConformance(ASTCDAssociation concrete) {
    Set<ASTCDAssociation> nonConformingTo = incMapping.getReferenceElements(concrete).stream()
        .filter(ref -> !checkConformance(concrete, ref)).collect(Collectors.toSet());
    for (ASTCDAssociation ref : nonConformingTo) {
      System.out.println(CD4CodeMill.prettyPrint(concrete, false)
          + " is not a valid incarnation of " + CD4CodeMill.prettyPrint(ref, false));
    }
    return nonConformingTo.isEmpty();
  }
  
  public boolean checkConformance(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if (check(concrete, ref)) {
      warnExplicitRoleNameMissing(concrete.getLeft(), ref.getLeft());
      warnExplicitRoleNameMissing(concrete.getRight(), ref.getRight());
      return true;
    }
    if (checkReverse(concrete, ref)) {
      warnExplicitRoleNameMissing(concrete.getLeft(), ref.getRight());
      warnExplicitRoleNameMissing(concrete.getRight(), ref.getLeft());
      return true;
    }
    return false;
  }
  
  protected boolean check(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight() || concrete.getCDAssocDir()
        .isDefinitiveNavigableRight()) && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())) {
      
      boolean leftRefs = checkReference(concrete.getLeftQualifiedName().getQName(), ref
          .getLeftQualifiedName().getQName());
      
      boolean leftCards = allowCardRestriction ? checkCardinality(concrete.getLeft(), ref.getLeft())
          : checkCardinalityStrict(concrete.getLeft(), ref.getLeft());
      
      boolean rightRefs = checkReference(concrete.getRightQualifiedName().getQName(), ref
          .getRightQualifiedName().getQName());
      
      boolean rightCards = allowCardRestriction ? checkCardinality(concrete.getRight(), ref
          .getRight()) : checkCardinalityStrict(concrete.getRight(), ref.getRight());
      
      return leftCards && leftRefs && rightCards && rightRefs;
    }
    return false;
  }
  
  public boolean checkReverse(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight() || concrete.getCDAssocDir()
        .isDefinitiveNavigableLeft()) && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())) {
      
      boolean leftReverseRef = checkReference(concrete.getLeftQualifiedName().getQName(), ref
          .getRightQualifiedName().getQName());
      boolean leftReverseCard = allowCardRestriction ? checkCardinality(concrete.getLeft(), ref
          .getRight()) : checkCardinalityStrict(concrete.getLeft(), ref.getRight());
      boolean rightReverseRef = checkReference(concrete.getRightQualifiedName().getQName(), ref
          .getLeftQualifiedName().getQName());
      
      boolean refReverseCard = allowCardRestriction ? checkCardinality(concrete.getRight(), ref
          .getLeft()) : checkCardinalityStrict(concrete.getRight(), ref.getLeft());
      
      return leftReverseRef && leftReverseCard && rightReverseRef && refReverseCard;
    }
    
    return false;
  }
  
  protected boolean checkCardinality(ASTCDAssocSide concrete, ASTCDAssocSide ref) {
    if (!ref.isPresentCDCardinality()) {
      return true;
    }
    if (!concrete.isPresentCDCardinality()) {
      return false;
    }
    if (ref.getCDCardinality().toCardinality().isNoUpperLimit()) {
      return ref.getCDCardinality().toCardinality().getLowerBound() <= concrete.getCDCardinality()
          .getLowerBound();
    }
    if (concrete.getCDCardinality().toCardinality().isNoUpperLimit()) {
      return false;
    }
    return (ref.getCDCardinality().toCardinality().getLowerBound() <= concrete.getCDCardinality()
        .getLowerBound()) && (ref.getCDCardinality().toCardinality().getUpperBound() >= concrete
            .getCDCardinality().toCardinality().getUpperBound());
  }
  
  protected boolean checkCardinalityStrict(ASTCDAssocSide concrete, ASTCDAssocSide ref) {
    if (ref.isPresentCDCardinality() != concrete.isPresentCDCardinality()) {
      return false;
    }
    return !ref.isPresentCDCardinality() || concrete.getCDCardinality().deepEquals(ref
        .getCDCardinality());
  }
  
  /**
   * Warns if the concrete association side does not have an explicit role name although the
   * matching reference association side has an explicit role name.
   *
   * @param concrete the concrete association side
   * @param reference the reference association side
   */
  protected void warnExplicitRoleNameMissing(ASTCDAssocSide concrete, ASTCDAssocSide reference) {
    if (reference.isPresentCDRole() && !concrete.isPresentCDRole()) {
      /*
       * Strategies like ImplicitRoleNameAssocIncStrategy might return matches where no concrete
       * role name is present, but the implicit name (by convention) is matches the reference
       * role name.
       * We warn the user and recommend to provide an explicit concrete role name if there is an
       * explicit reference role name. There are conventions for the implicit name but better not
       * rely on having the same convention in mind as the reference modeler.
       */
      Log.warn("There is no explicit concrete role name although the matching reference "
          + "association side has an explicit role name (" + reference.getCDRole().getName()
          + "). This is not recommended.", concrete.get_SourcePositionStart());
    }
  }
  
  protected boolean checkReference(String concrete, String ref) {
    Optional<CDTypeSymbol> conTypeSymbol = conCD.getEnclosingScope().resolveCDTypeDown(concrete);
    Optional<CDTypeSymbol> refTypeSymbol = refCD.getEnclosingScope().resolveCDTypeDown(ref);
    
    if (conTypeSymbol.isPresent() && refTypeSymbol.isPresent()) {
      ASTCDType conType = conTypeSymbol.get().getAstNode();
      ASTCDType refType = refTypeSymbol.get().getAstNode();
      return incMapping.isIncarnation(conType, refType);
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }
  
}
