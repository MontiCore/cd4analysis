package de.monticore.conformance.conf.association;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.conformance.conf.ConformanceStrategy;
import de.monticore.matcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicAssocConfStrategy implements ConformanceStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;
  protected MatchingStrategy<ASTCDType> typeInc;
  protected MatchingStrategy<ASTCDAssociation> assocInc;
  protected boolean allowCardRestriction;

  public BasicAssocConfStrategy(
      ASTCDCompilationUnit conCD,
      ASTCDCompilationUnit refCD,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc,
      boolean allowCardRestriction) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
    this.allowCardRestriction = allowCardRestriction;
  }

  @Override
  public boolean checkConformance(ASTCDAssociation concrete) {
    Set<ASTCDAssociation> nonConformingTo =
        assocInc.getMatchedElements(concrete).stream()
            .filter(ref -> !checkConformance(concrete, ref))
            .collect(Collectors.toSet());
    for (ASTCDAssociation ref : nonConformingTo) {
      System.out.println(
          CD4CodeMill.prettyPrint(concrete, false)
              + " is not a valid incarnation of "
              + CD4CodeMill.prettyPrint(ref, false));
    }
    return nonConformingTo.isEmpty();
  }

  public boolean checkConformance(ASTCDAssociation concrete, ASTCDAssociation ref) {
    return check(concrete, ref) || checkReverse(concrete, ref);
  }

  protected boolean check(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())
        && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())) {

      boolean leftRefs =
          checkReference(
              concrete.getLeftQualifiedName().getQName(), ref.getLeftQualifiedName().getQName());

      boolean leftCards =
          allowCardRestriction
              ? checkCardinality(concrete.getLeft(), ref.getLeft())
              : checkCardinalityStrict(concrete.getLeft(), ref.getLeft());

      boolean rightRefs =
          checkReference(
              concrete.getRightQualifiedName().getQName(), ref.getRightQualifiedName().getQName());

      boolean rightCards =
          allowCardRestriction
              ? checkCardinality(concrete.getRight(), ref.getRight())
              : checkCardinalityStrict(concrete.getRight(), ref.getRight());

      return leftCards && leftRefs && rightCards && rightRefs;
    }
    return false;
  }

  public boolean checkReverse(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())
        && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())) {

      boolean leftReverseRef =
          checkReference(
              concrete.getLeftQualifiedName().getQName(), ref.getRightQualifiedName().getQName());
      boolean leftReverseCard =
          allowCardRestriction
              ? checkCardinality(concrete.getLeft(), ref.getRight())
              : checkCardinalityStrict(concrete.getLeft(), ref.getRight());
      boolean rightReverseRef =
          checkReference(
              concrete.getRightQualifiedName().getQName(), ref.getLeftQualifiedName().getQName());

      boolean refReverseCard =
          allowCardRestriction
              ? checkCardinality(concrete.getRight(), ref.getLeft())
              : checkCardinalityStrict(concrete.getRight(), ref.getLeft());

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
      return ref.getCDCardinality().toCardinality().getLowerBound()
          <= concrete.getCDCardinality().getLowerBound();
    }
    if (concrete.getCDCardinality().toCardinality().isNoUpperLimit()) {
      return false;
    }
    return (ref.getCDCardinality().toCardinality().getLowerBound()
            <= concrete.getCDCardinality().getLowerBound())
        && (ref.getCDCardinality().toCardinality().getUpperBound()
            >= concrete.getCDCardinality().toCardinality().getUpperBound());
  }

  protected boolean checkCardinalityStrict(ASTCDAssocSide concrete, ASTCDAssocSide ref) {
    if (ref.isPresentCDCardinality() != concrete.isPresentCDCardinality()) {
      return false;
    }
    return !ref.isPresentCDCardinality()
        || concrete.getCDCardinality().deepEquals(ref.getCDCardinality());
  }

  protected boolean checkReference(String concrete, String ref) {
    Optional<CDTypeSymbol> conTypeSymbol = conCD.getEnclosingScope().resolveCDTypeDown(concrete);
    Optional<CDTypeSymbol> refTypeSymbol = refCD.getEnclosingScope().resolveCDTypeDown(ref);

    if (conTypeSymbol.isPresent() && refTypeSymbol.isPresent()) {
      ASTCDType conType = conTypeSymbol.get().getAstNode();
      ASTCDType refType = refTypeSymbol.get().getAstNode();
      return typeInc.isMatched(conType, refType);
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }
}
