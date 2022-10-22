package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

import java.util.Optional;
import java.util.UUID;

import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiff4AssocHelper.getCDAssociationDiffKindHelper;

/**
 * Each CDAssociationWrapper in based CDWrapper will generate three corresponding CompAssociations
 *    1. for direction
 *    2. for left cardinality
 *    3. for right cardinality
 *
 * @attribute cDDiffId:
 *    unique CDDiff id
 * @attribute baseElement:
 *    base original CDAssociationWrapper
 * @attribute isInCompareCDW:
 *    whether this CDAssociationWrapper exists in compared CDWrapper (only check
 *    CDAssociationWrapper name)
 * @attribute isContentDiff:
 *    if this CDAssociationWrapper exists in compared CDWrapper (only check CDAssociationWrapper name),
 *    then check whether the content of those two CDAssociationWrappers are different:
 *        1. for direction
 *        2. for left cardinality
 *        3. for right cardinality
 * @attribute cDDiffCategory:
 *    - has semantic difference:
 *        DELETED, DIRECTION_CHANGED, CARDINALITY_CHANGED, SUBCLASS_DIFF, CONFLICTING
 *    - has no semantic difference:
 *        ORIGINAL, DIRECTION_CHANGED_BUT_SAME_MEANING, DIRECTION_SUBSET, CARDINALITY_SUBSET
 * @attribute cDDiffDirectionResult:
 *    The result after comparison:
 *        NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT
 * @attribute cDDiffLeftClassCardinalityResult:
 *    The result after comparison:
 *        NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
 * @attribute cDDiffRightClassCardinalityResult:
 *    The result after comparison:
 *        NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
 * @attribute whichPartDiff:
 *    mark which part has syntactic differences:
 *        1. direction
 *        2. left cardinality
 *        3. right cardinality
 *        4. special left cardinality
 *        5. special right cardinality
 */
public class CDAssocWrapperDiff {
  protected final UUID cDDiffId;

  protected final CDAssociationWrapper baseElement;

  protected Optional<CDAssociationWrapper> optCompareAssoc;

  protected final boolean isInCompareCDW;

  protected final boolean isContentDiff;

  protected final CDAssociationDiffCategory cDDiffCategory;

  protected Optional<CDAssociationDiffDirection> cDDiffDirectionResult;

  protected Optional<CDAssociationDiffCardinality> cDDiffLeftClassCardinalityResult;

  protected Optional<CDAssociationDiffCardinality> cDDiffRightClassCardinalityResult;

  protected Optional<WhichPartDiff> whichPartDiff;

  protected Optional<CDTypeWrapper> leftInstanceClass;

  protected Optional<CDTypeWrapper> rightInstanceClass;

  public CDAssocWrapperDiff(CDAssociationWrapper baseElement,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory cDDiffCategory) {
    this.baseElement = baseElement;
    this.optCompareAssoc = optCompareAssoc;
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
    return prefix + this.baseElement.getName()
        .substring(this.baseElement.getName().indexOf("_") + 1);
  }

  public CDAssociationDiffKind getCDDiffKind() {
    return getCDAssociationDiffKindHelper(this.baseElement.getCDWrapperKind());
  }

  public boolean isInCompareCDW() {
    return isInCompareCDW;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CDAssociationDiffCategory getCDDiffCategory() {
    return cDDiffCategory;
  }

  public Optional<CDAssociationDiffDirection> getCDDiffDirectionResult() {
    return cDDiffDirectionResult;
  }

  public void setCDDiffDirectionResult(
      Optional<CDAssociationDiffDirection> cDDiffDirectionResult) {
    this.cDDiffDirectionResult = cDDiffDirectionResult;
  }

  public Optional<CDAssociationDiffCardinality> getCDDiffLeftClassCardinalityResult() {
    return cDDiffLeftClassCardinalityResult;
  }

  public void setCDDiffLeftClassCardinalityResult(
      Optional<CDAssociationDiffCardinality> cDDiffLeftClassCardinalityResult) {
    this.cDDiffLeftClassCardinalityResult = cDDiffLeftClassCardinalityResult;
  }

  public Optional<CDAssociationDiffCardinality> getCDDiffRightClassCardinalityResult() {
    return cDDiffRightClassCardinalityResult;
  }

  public void setCDDiffRightClassCardinalityResult(
      Optional<CDAssociationDiffCardinality> cDDiffRightClassCardinalityResult) {
    this.cDDiffRightClassCardinalityResult = cDDiffRightClassCardinalityResult;
  }

  public Optional<WhichPartDiff> getWhichPartDiff() {
    return whichPartDiff;
  }

  public void setWhichPartDiff(Optional<WhichPartDiff> whichPartDiff) {
    this.whichPartDiff = whichPartDiff;
  }

  public CDAssociationWrapper getBaseElement() {
    return baseElement;
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

  public String getBaseSourcePositionStr() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("l.");
    stringBuilder.append(baseElement.getSourcePosition().getLine());
    stringBuilder.append(", ");
    stringBuilder.append("c.");
    stringBuilder.append(baseElement.getSourcePosition().getColumn());
    return stringBuilder.toString();
  }

  public String getCompareSourcePositionStr() {
    if (this.cDDiffCategory == CDAssociationDiffCategory.DIRECTION_CHANGED ||
        this.cDDiffCategory == CDAssociationDiffCategory.CARDINALITY_CHANGED ||
        this.cDDiffCategory == CDAssociationDiffCategory.SUBCLASS_DIFF) {
      if (optCompareAssoc.isPresent()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("l.");
        stringBuilder.append(optCompareAssoc.get().getSourcePosition().getLine());
        stringBuilder.append(", ");
        stringBuilder.append("c.");
        stringBuilder.append(optCompareAssoc.get().getSourcePosition().getColumn());
        return stringBuilder.toString();
      }
    }
    return "NULL";
  }

}
