package de.monticore.sydiff2semdiff.sg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportAssociation;

import java.util.Optional;
import java.util.UUID;

import static de.monticore.sydiff2semdiff.sg2cg.CompareHelper.getCompAssociationKindHelper;

/**
 * Each SupportAssociation in based SupportGroup will generate three corresponding CompAssociations
 * 1. for direction
 * 2. for left cardinality
 * 3. for right cardinality
 *
 * @attribute compId:
 * unique compare id
 * @attribute originalElement:
 * original SupportAssociation
 * @attribute isInCompareSG:
 * whether this SupportAssociation exists in compared SupportGroup (only check SupportAssociation name)
 * @attribute isContentDiff:
 * if this SupportAssociation exists in compared SupportGroup (only check SupportAssociation name),
 * then check whether the content of those two SupportAssociations are different:
 * 1. for direction
 * 2. for left cardinality
 * 3. for right cardinality
 * @attribute compCategory:
 * - has semantic difference:
 * DELETED, DIRECTION_CHANGED, CARDINALITY_CHANGED
 * - has no semantic difference:
 * ORIGINAL, DIRECTION_CHANGED_BUT_SAME_MEANING, DIRECTION_SUBSET, CARDINALITY_SUBSET
 * @attribute compDirectionResult:
 * The result after comparison:
 * NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT
 * @attribute compLeftClassCardinalityResult:
 * The result after comparison:
 * NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
 * @attribute compRightClassCardinalityResult:
 * The result after comparison:
 * NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
 * @attribute whichPartDiff:
 * mark which part has syntactic differences:
 * 1. direction
 * 2. left cardinality
 * 3. right cardinality
 */
public class CompAssociation {
  protected final UUID compId;
  protected final SupportAssociation originalElement;
  protected final boolean isInCompareSG;
  protected final boolean isContentDiff;
  protected final CompareGroup.CompAssociationCategory compCategory;
  protected Optional<CompareGroup.CompAssociationDirection> compDirectionResult;
  protected Optional<CompareGroup.CompAssociationCardinality> compLeftClassCardinalityResult;
  protected Optional<CompareGroup.CompAssociationCardinality> compRightClassCardinalityResult;
  protected Optional<CompareGroup.WhichPartDiff> whichPartDiff;

  public CompAssociation(SupportAssociation originalElement,
                         boolean isInCompareSG,
                         boolean isContentDiff,
                         CompareGroup.CompAssociationCategory compCategory) {
    this.originalElement = originalElement;
    this.isInCompareSG = isInCompareSG;
    this.isContentDiff = isContentDiff;
    this.compCategory = compCategory;
    this.compId = UUID.randomUUID();
  }

  public UUID getCompId() {
    return compId;
  }

  public String getName(boolean is4Print) {
    String prefix = is4Print ? "Association_" : "CompAssociation_";
    return prefix + this.originalElement.getName().substring(this.originalElement.getName().indexOf("_") + 1);
  }

  public CompareGroup.CompAssociationKind getCompKind() {
    return getCompAssociationKindHelper(this.originalElement.getSupportKind());
  }

  public boolean isInCompareSG() {
    return isInCompareSG;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CompareGroup.CompAssociationCategory getCompCategory() {
    return compCategory;
  }

  public Optional<CompareGroup.CompAssociationDirection> getCompDirectionResult() {
    return compDirectionResult;
  }

  public void setCompDirectionResult(Optional<CompareGroup.CompAssociationDirection> compDirectionResult) {
    this.compDirectionResult = compDirectionResult;
  }

  public Optional<CompareGroup.CompAssociationCardinality> getCompLeftClassCardinalityResult() {
    return compLeftClassCardinalityResult;
  }

  public void setCompLeftClassCardinalityResult(Optional<CompareGroup.CompAssociationCardinality> compLeftClassCardinalityResult) {
    this.compLeftClassCardinalityResult = compLeftClassCardinalityResult;
  }

  public Optional<CompareGroup.CompAssociationCardinality> getCompRightClassCardinalityResult() {
    return compRightClassCardinalityResult;
  }

  public void setCompRightClassCardinalityResult(Optional<CompareGroup.CompAssociationCardinality> compRightClassCardinalityResult) {
    this.compRightClassCardinalityResult = compRightClassCardinalityResult;
  }

  public Optional<CompareGroup.WhichPartDiff> getWhichPartDiff() {
    return whichPartDiff;
  }

  public void setWhichPartDiff(Optional<CompareGroup.WhichPartDiff> whichPartDiff) {
    this.whichPartDiff = whichPartDiff;
  }

  public SupportAssociation getOriginalElement() {
    return originalElement;
  }
}
