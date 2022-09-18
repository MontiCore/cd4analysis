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
