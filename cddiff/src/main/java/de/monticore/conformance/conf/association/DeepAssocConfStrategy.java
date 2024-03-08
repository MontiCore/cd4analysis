package de.monticore.conformance.conf.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.matcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.Set;

public class DeepAssocConfStrategy extends BasicAssocConfStrategy {

  public DeepAssocConfStrategy(
      ASTCDCompilationUnit conCD,
      ASTCDCompilationUnit refCD,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc,
      boolean allowCardRefinement) {
    super(conCD, refCD, typeInc, assocInc, allowCardRefinement);
  }

  @Override
  protected boolean check(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())
        && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())) {

      boolean leftRefs;
      if (ref.getCDAssocDir().isDefinitiveNavigableLeft()) {
        leftRefs =
            checkReferenceNavigable(
                concrete.getLeftQualifiedName().getQName(), ref.getLeftQualifiedName().getQName());
      } else {
        leftRefs =
            checkReferenceNonNavigable(
                concrete.getLeftQualifiedName().getQName(), ref.getLeftQualifiedName().getQName());
      }

      boolean leftCards =
          allowCardRestriction
              ? checkCardinality(concrete.getLeft(), ref.getLeft())
              : checkCardinalityStrict(concrete.getLeft(), ref.getLeft());

      boolean rightRefs;
      if (ref.getCDAssocDir().isDefinitiveNavigableRight()) {
        rightRefs =
            checkReferenceNavigable(
                concrete.getRightQualifiedName().getQName(),
                ref.getRightQualifiedName().getQName());
      } else {
        rightRefs =
            checkReferenceNonNavigable(
                concrete.getRightQualifiedName().getQName(),
                ref.getRightQualifiedName().getQName());
      }

      boolean rightCards =
          allowCardRestriction
              ? checkCardinality(concrete.getRight(), ref.getRight())
              : checkCardinalityStrict(concrete.getRight(), ref.getRight());

      return leftCards && leftRefs && rightCards && rightRefs;
    }

    return false;
  }

  @Override
  public boolean checkReverse(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if ((!ref.getCDAssocDir().isDefinitiveNavigableRight()
            || concrete.getCDAssocDir().isDefinitiveNavigableLeft())
        && (!ref.getCDAssocDir().isDefinitiveNavigableLeft()
            || concrete.getCDAssocDir().isDefinitiveNavigableRight())) {

      boolean leftReverseRef;
      if (ref.getCDAssocDir().isDefinitiveNavigableRight()) {
        leftReverseRef =
            checkReferenceNavigable(
                concrete.getLeftQualifiedName().getQName(), ref.getRightQualifiedName().getQName());
      } else {
        leftReverseRef =
            checkReferenceNonNavigable(
                concrete.getLeftQualifiedName().getQName(), ref.getRightQualifiedName().getQName());
      }

      boolean leftReverseCard =
          allowCardRestriction
              ? checkCardinality(concrete.getLeft(), ref.getRight())
              : checkCardinalityStrict(concrete.getLeft(), ref.getRight());

      boolean rightReverseRef;
      if (ref.getCDAssocDir().isDefinitiveNavigableLeft()) {
        rightReverseRef =
            checkReferenceNavigable(
                concrete.getRightQualifiedName().getQName(), ref.getLeftQualifiedName().getQName());
      } else {
        rightReverseRef =
            checkReferenceNonNavigable(
                concrete.getRightQualifiedName().getQName(), ref.getLeftQualifiedName().getQName());
      }

      boolean refReverseCard =
          allowCardRestriction
              ? checkCardinality(concrete.getRight(), ref.getLeft())
              : checkCardinalityStrict(concrete.getRight(), ref.getLeft());

      return leftReverseRef && leftReverseCard && rightReverseRef && refReverseCard;
    }

    return false;
  }

  protected boolean checkReferenceNonNavigable(String concrete, String ref) {
    Optional<CDTypeSymbol> conTypeSymbol = conCD.getEnclosingScope().resolveCDTypeDown(concrete);
    Optional<CDTypeSymbol> refTypeSymbol = refCD.getEnclosingScope().resolveCDTypeDown(ref);

    if (conTypeSymbol.isPresent() && refTypeSymbol.isPresent()) {
      ASTCDType conType = conTypeSymbol.get().getAstNode();
      ASTCDType refType = refTypeSymbol.get().getAstNode();
      return typeInc.isMatched(conType, refType)
          || CDDiffUtil.getAllStrictSubTypes(conType, conCD.getCDDefinition()).stream()
              .anyMatch(conSub -> typeInc.isMatched(conSub, refType));
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }

  protected boolean checkReferenceNavigable(String concrete, String ref) {
    Optional<CDTypeSymbol> conTypeSymbol = conCD.getEnclosingScope().resolveCDTypeDown(concrete);
    Optional<CDTypeSymbol> refTypeSymbol = refCD.getEnclosingScope().resolveCDTypeDown(ref);

    if (conTypeSymbol.isPresent() && refTypeSymbol.isPresent()) {
      ASTCDType conType = conTypeSymbol.get().getAstNode();
      ASTCDType refType = refTypeSymbol.get().getAstNode();

      Set<ASTCDType> concreteTypes =
          CDDiffUtil.getAllStrictSubTypes(conType, conCD.getCDDefinition());
      concreteTypes.add(conType);

      return concreteTypes.stream()
          .allMatch(
              conType1 ->
                  checkRule1(conType1, refType)
                      || checkRule2(conType1, refType)
                      || checkRule3(conType1, refType));
    }
    Log.error("0xCDD17: Could not resolve association reference!");
    return false;
  }

  /***
   * the concrete type incarnate the reference type.
   */
  protected boolean checkRule1(ASTCDType concrete, ASTCDType ref) {
    return typeInc.isMatched(concrete, ref);
  }

  /***
   * the concrete type is abstract or an interface and has a subtype that incarnates
   * the reference type.
   */

  protected boolean checkRule2(ASTCDType concrete, ASTCDType ref) {
    return (concrete.getModifier().isAbstract() || ref.getSymbol().isIsInterface())
        && CDDiffUtil.getAllStrictSubTypes(concrete, conCD.getCDDefinition()).stream()
            .anyMatch(subtype -> typeInc.isMatched(subtype, ref));
  }

  /***
   *the concrete type inherit from an incarnation of the reference type.
   */
  protected boolean checkRule3(ASTCDType concrete, ASTCDType ref) {
    return CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition()).stream()
        .anyMatch(supertype -> typeInc.isMatched(supertype, ref));
  }
}
