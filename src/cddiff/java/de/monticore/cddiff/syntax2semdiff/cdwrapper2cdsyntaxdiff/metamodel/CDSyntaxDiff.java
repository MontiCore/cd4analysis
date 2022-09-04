package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;

import java.util.Deque;

/**
 * CDSyntaxDiff is to compare each element of two CDWrapper and return the result after comparison.
 * We should determine which CDWrapper is the baseCDW and which one is the compareCDW.
 * Determine whether there is a semantic difference between CDWrapper A and CDWrapper B,
 * We should create two CDSyntaxDiff:
 *    1. baseCDW = A, compareCDW= B
 *    1. baseCDW = B, compareCDW = A
 * If there are no objects in cDTypeDiffResultQueueWithDiff and
 * cDAssociationDiffResultQueueWithDiff of above two CDSyntaxDiffs,
 * then we can say there is no semantic difference between CD A and CD B,
 * otherwise there are semantic differences between CD A and CD B.
 *
 * @attribute baseCDW:
 *    the based CDWrapper
 * @attribute compareCDW:
 *    the compared CDWrapper
 * @attribute cDTypeDiffResultQueueWithDiff:
 *    store the CDTypeDiff that has semantic difference
 * @attribute cDAssociationDiffResultQueueWithDiff:
 *    store the CDAssociationDiff that has semantic difference
 * @attribute cDTypeDiffResultQueueWithoutDiff:
 *    store the CDTypeDiff that has no semantic difference
 * @attribute cDAssociationDiffResultQueueWithoutDiff:
 *    store the CDAssociationDiff that has no semantic difference
 */
public class CDSyntaxDiff {
  protected CDWrapper baseCDW;

  protected CDWrapper compareCDW;

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithDiff;

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithDiff;

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithoutDiff;

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithoutDiff;

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
    DIRECTION, LEFT_CARDINALITY, RIGHT_CARDINALITY
  }

  public CDSyntaxDiff(CDWrapper baseCDW, CDWrapper compareCDW) {
    this.baseCDW = baseCDW;
    this.compareCDW = compareCDW;
  }

  public CDSyntaxDiff(CDWrapper baseCDW, CDWrapper compareCDW,
      Deque<CDTypeDiff> cDTypeDiffResultQueueWithDiff,
      Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithDiff,
      Deque<CDTypeDiff> cDTypeDiffResultQueueWithoutDiff,
      Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithoutDiff) {
    this.baseCDW = baseCDW;
    this.compareCDW = compareCDW;
    this.cDTypeDiffResultQueueWithDiff = cDTypeDiffResultQueueWithDiff;
    this.cDAssociationDiffResultQueueWithDiff = cDAssociationDiffResultQueueWithDiff;
    this.cDTypeDiffResultQueueWithoutDiff = cDTypeDiffResultQueueWithoutDiff;
    this.cDAssociationDiffResultQueueWithoutDiff = cDAssociationDiffResultQueueWithoutDiff;
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

  public Deque<CDTypeDiff> getCDTypeDiffResultQueueWithDiff() {
    return cDTypeDiffResultQueueWithDiff;
  }

  public void setCDTypeDiffResultQueueWithDiff(Deque<CDTypeDiff> cDTypeDiffResultQueueWithDiff) {
    this.cDTypeDiffResultQueueWithDiff = cDTypeDiffResultQueueWithDiff;
  }

  public Deque<CDAssociationDiff> getCDAssociationDiffResultQueueWithDiff() {
    return cDAssociationDiffResultQueueWithDiff;
  }

  public void setCDAssociationDiffResultQueueWithDiff(
      Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithDiff) {
    this.cDAssociationDiffResultQueueWithDiff = cDAssociationDiffResultQueueWithDiff;
  }

  public Deque<CDTypeDiff> getCDTypeDiffResultQueueWithoutDiff() {
    return cDTypeDiffResultQueueWithoutDiff;
  }

  public void setCDTypeDiffResultQueueWithoutDiff(
      Deque<CDTypeDiff> cDTypeDiffResultQueueWithoutDiff) {
    this.cDTypeDiffResultQueueWithoutDiff = cDTypeDiffResultQueueWithoutDiff;
  }

  public Deque<CDAssociationDiff> getCDAssociationDiffResultQueueWithoutDiff() {
    return cDAssociationDiffResultQueueWithoutDiff;
  }

  public void setCDAssociationDiffResultQueueWithoutDiff(
      Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithoutDiff) {
    this.cDAssociationDiffResultQueueWithoutDiff = cDAssociationDiffResultQueueWithoutDiff;
  }

}
