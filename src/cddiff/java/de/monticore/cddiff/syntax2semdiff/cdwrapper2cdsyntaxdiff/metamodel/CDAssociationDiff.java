package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

import java.util.Optional;
import java.util.UUID;

import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDSyntaxDiffHelper.getCDAssociationDiffKindHelper;

/**
 * Each CDAssociationWrapper in based CDWrapper will generate three corresponding CompAssociations
 * 1. for direction
 * 2. for left cardinality
 * 3. for right cardinality
 *
 * @attribute cDDiffId:
 * unique CDDiff id
 * @attribute originalElement:
 * original CDAssociationWrapper
 * @attribute isInCompareCDW:
 * whether this CDAssociationWrapper exists in compared CDWrapper (only check
 * CDAssociationWrapper name)
 * @attribute isContentDiff:
 * if this CDAssociationWrapper exists in compared CDWrapper (only check CDAssociationWrapper name),
 * then check whether the content of those two CDAssociationWrappers are different:
 *    1. for direction
 *    2. for left cardinality
 *    3. for right cardinality
 * @attribute cDDiffCategory:
 * - has semantic difference:
 *    DELETED, DIRECTION_CHANGED, CARDINALITY_CHANGED, SUBCLASS_DIFF
 * - has no semantic difference:
 *    ORIGINAL, DIRECTION_CHANGED_BUT_SAME_MEANING, DIRECTION_SUBSET, CARDINALITY_SUBSET
 * @attribute cDDiffDirectionResult:
 * The result after comparison:
 *    NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT
 * @attribute cDDiffLeftClassCardinalityResult:
 * The result after comparison:
 *    NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
 * @attribute cDDiffRightClassCardinalityResult:
 * The result after comparison:
 *    NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
 * @attribute whichPartDiff:
 * mark which part has syntactic differences:
 *    1. direction
 *    2. left cardinality
 *    3. right cardinality
 */
public class CDAssociationDiff {
  protected final UUID cDDiffId;

  protected final CDAssociationWrapper originalElement;

  protected final boolean isInCompareCDW;

  protected final boolean isContentDiff;

  protected final CDSyntaxDiff.CDAssociationDiffCategory cDDiffCategory;

  protected Optional<CDSyntaxDiff.CDAssociationDiffDirection> cDDiffDirectionResult;

  protected Optional<CDSyntaxDiff.CDAssociationDiffCardinality> cDDiffLeftClassCardinalityResult;

  protected Optional<CDSyntaxDiff.CDAssociationDiffCardinality> cDDiffRightClassCardinalityResult;

  protected Optional<CDSyntaxDiff.WhichPartDiff> whichPartDiff;

  protected Optional<CDTypeWrapper> leftInstanceClass;

  protected Optional<CDTypeWrapper> rightInstanceClass;

  public CDAssociationDiff(CDAssociationWrapper originalElement, boolean isInCompareCDW,
      boolean isContentDiff, CDSyntaxDiff.CDAssociationDiffCategory cDDiffCategory) {
    this.originalElement = originalElement;
    this.isInCompareCDW = isInCompareCDW;
    this.isContentDiff = isContentDiff;
    this.cDDiffCategory = cDDiffCategory;
    this.cDDiffId = UUID.randomUUID();
  }

  public UUID getCDDiffId() {
    return cDDiffId;
  }

  public String getName(boolean is4Print) {
    String prefix = is4Print ? "Association_" : "CDDiffAssociation_";
    return prefix + this.originalElement.getName()
        .substring(this.originalElement.getName().indexOf("_") + 1);
  }

  public CDSyntaxDiff.CDAssociationDiffKind getCDDiffKind() {
    return getCDAssociationDiffKindHelper(this.originalElement.getCDWrapperKind());
  }

  public boolean isInCompareCDW() {
    return isInCompareCDW;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CDSyntaxDiff.CDAssociationDiffCategory getCDDiffCategory() {
    return cDDiffCategory;
  }

  public Optional<CDSyntaxDiff.CDAssociationDiffDirection> getCDDiffDirectionResult() {
    return cDDiffDirectionResult;
  }

  public void setCDDiffDirectionResult(
      Optional<CDSyntaxDiff.CDAssociationDiffDirection> cDDiffDirectionResult) {
    this.cDDiffDirectionResult = cDDiffDirectionResult;
  }

  public Optional<CDSyntaxDiff.CDAssociationDiffCardinality> getCDDiffLeftClassCardinalityResult() {
    return cDDiffLeftClassCardinalityResult;
  }

  public void setCDDiffLeftClassCardinalityResult(
      Optional<CDSyntaxDiff.CDAssociationDiffCardinality> cDDiffLeftClassCardinalityResult) {
    this.cDDiffLeftClassCardinalityResult = cDDiffLeftClassCardinalityResult;
  }

  public Optional<CDSyntaxDiff.CDAssociationDiffCardinality> getCDDiffRightClassCardinalityResult() {
    return cDDiffRightClassCardinalityResult;
  }

  public void setCDDiffRightClassCardinalityResult(
      Optional<CDSyntaxDiff.CDAssociationDiffCardinality> cDDiffRightClassCardinalityResult) {
    this.cDDiffRightClassCardinalityResult = cDDiffRightClassCardinalityResult;
  }

  public Optional<CDSyntaxDiff.WhichPartDiff> getWhichPartDiff() {
    return whichPartDiff;
  }

  public void setWhichPartDiff(Optional<CDSyntaxDiff.WhichPartDiff> whichPartDiff) {
    this.whichPartDiff = whichPartDiff;
  }

  public CDAssociationWrapper getOriginalElement() {
    return originalElement;
  }

  public Optional<CDTypeWrapper> getLeftInstanceClass() {
    return leftInstanceClass;
  }

  public void setLeftInstanceClass(Optional<CDTypeWrapper> leftInstanceClass) {
    this.leftInstanceClass = leftInstanceClass;
  }

  public Optional<CDTypeWrapper> getRightInstanceClass() {
    return rightInstanceClass;
  }

  public void setRightInstanceClass(Optional<CDTypeWrapper> rightInstanceClass) {
    this.rightInstanceClass = rightInstanceClass;
  }

}
