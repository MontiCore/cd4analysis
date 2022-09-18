package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;

import java.util.Deque;

/**
 * CDWrapperSyntaxDiff is to compare each element of two CDWrapper and return the result after comparison.
 * We should determine which CDWrapper is the baseCDW and which one is the compareCDW.
 * Determine whether there is a semantic difference between CDWrapper A and CDWrapper B,
 * We should create two CDWrapperSyntaxDiff:
 *    1. baseCDW = A, compareCDW= B
 *    1. baseCDW = B, compareCDW = A
 * If there are no objects in cDTypeWrappeDiffResultQueueWithDiff and
 * cDAssocWrapperDiffResultQueueWithDiff of above two CDSyntaxDiffs,
 * then we can say there is no semantic difference between CD A and CD B,
 * otherwise there are semantic differences between CD A and CD B.
 *
 * @attribute baseCDW:
 *    the based CDWrapper
 * @attribute compareCDW:
 *    the compared CDWrapper
 * @attribute cDTypeWrappeDiffResultQueueWithDiff:
 *    store the CDTypeWrapperDiff that has semantic difference
 * @attribute cDAssocWrapperDiffResultQueueWithDiff:
 *    store the CDAssocWrapperDiff that has semantic difference
 * @attribute cDTypeWrappeDiffResultQueueWithoutDiff:
 *    store the CDTypeWrapperDiff that has no semantic difference
 * @attribute cDAssocWrapperDiffResultQueueWithoutDiff:
 *    store the CDAssocWrapperDiff that has no semantic difference
 */
public class CDWrapperSyntaxDiff {
  protected CDWrapper baseCDW;

  protected CDWrapper compareCDW;

  protected Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithDiff;

  protected Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff;

  protected Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithoutDiff;

  protected Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff;

  public enum CDTypeDiffKind {
    CDDIFF_CLASS, CDDIFF_ENUM, CDDIFF_ABSTRACT_CLASS, CDDIFF_INTERFACE
  }

  public enum CDAssociationDiffKind {
    CDDIFF_ASC, CDDIFF_INHERIT_ASC, CDDIFF_INHERIT_DISPLAY_ASC
  }

  public enum CDTypeDiffCategory {
    ORIGINAL, EDITED, DELETED, SUBSET, FREED
  }

  public enum CDAssociationDiffCategory {
    ORIGINAL, DIRECTION_CHANGED_BUT_SAME_MEANING, DIRECTION_SUBSET, CARDINALITY_SUBSET,
    DELETED, DIRECTION_CHANGED, CARDINALITY_CHANGED, SUBCLASS_DIFF, CONFLICTING
  }

  public enum CDAssociationDiffDirection {
    NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT
  }

  public enum CDAssociationDiffCardinality {
    NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
  }

  public enum WhichPartDiff {
    DIRECTION, LEFT_CARDINALITY, RIGHT_CARDINALITY,
    LEFT_SPECIAL_CARDINALITY, RIGHT_SPECIAL_CARDINALITY
  }

  public CDWrapperSyntaxDiff(CDWrapper baseCDW, CDWrapper compareCDW) {
    this.baseCDW = baseCDW;
    this.compareCDW = compareCDW;
  }

  public CDWrapperSyntaxDiff(CDWrapper baseCDW, CDWrapper compareCDW,
      Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithDiff,
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff,
      Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithoutDiff,
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff) {
    this.baseCDW = baseCDW;
    this.compareCDW = compareCDW;
    this.cDTypeWrappeDiffResultQueueWithDiff = cDTypeWrappeDiffResultQueueWithDiff;
    this.cDAssocWrapperDiffResultQueueWithDiff = cDAssocWrapperDiffResultQueueWithDiff;
    this.cDTypeWrappeDiffResultQueueWithoutDiff = cDTypeWrappeDiffResultQueueWithoutDiff;
    this.cDAssocWrapperDiffResultQueueWithoutDiff = cDAssocWrapperDiffResultQueueWithoutDiff;
  }

  public CDWrapper getBaseCDW() {
    return baseCDW;
  }

  public void setBaseCDW(CDWrapper baseCDW) {
    this.baseCDW = baseCDW;
  }

  public CDWrapper getCompareCDW() {
    return compareCDW;
  }

  public void setCompareCDW(CDWrapper compareCDW) {
    this.compareCDW = compareCDW;
  }

  public Deque<CDTypeWrapperDiff> getCDTypeDiffResultQueueWithDiff() {
    return cDTypeWrappeDiffResultQueueWithDiff;
  }

  public void setCDTypeDiffResultQueueWithDiff(Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithDiff) {
    this.cDTypeWrappeDiffResultQueueWithDiff = cDTypeWrappeDiffResultQueueWithDiff;
  }

  public Deque<CDAssocWrapperDiff> getCDAssociationDiffResultQueueWithDiff() {
    return cDAssocWrapperDiffResultQueueWithDiff;
  }

  public void setCDAssociationDiffResultQueueWithDiff(
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff) {
    this.cDAssocWrapperDiffResultQueueWithDiff = cDAssocWrapperDiffResultQueueWithDiff;
  }

  public Deque<CDTypeWrapperDiff> getCDTypeDiffResultQueueWithoutDiff() {
    return cDTypeWrappeDiffResultQueueWithoutDiff;
  }

  public void setCDTypeDiffResultQueueWithoutDiff(
      Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithoutDiff) {
    this.cDTypeWrappeDiffResultQueueWithoutDiff = cDTypeWrappeDiffResultQueueWithoutDiff;
  }

  public Deque<CDAssocWrapperDiff> getCDAssociationDiffResultQueueWithoutDiff() {
    return cDAssocWrapperDiffResultQueueWithoutDiff;
  }

  public void setCDAssociationDiffResultQueueWithoutDiff(
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff) {
    this.cDAssocWrapperDiffResultQueueWithoutDiff = cDAssocWrapperDiffResultQueueWithoutDiff;
  }

}
