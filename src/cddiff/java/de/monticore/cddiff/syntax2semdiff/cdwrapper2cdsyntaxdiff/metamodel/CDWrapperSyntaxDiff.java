/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import java.util.Deque;

/**
 * CDWrapperSyntaxDiff is to compare each element of two CDWrapper and return the result after
 * comparison. We should determine which CDWrapper is the baseCDW and which one is the compareCDW.
 * Determine whether there is a semantic difference between CDWrapper A and CDWrapper B, We should
 * create two CDWrapperSyntaxDiff: 1. baseCDW = A, compareCDW= B 1. baseCDW = B, compareCDW = A If
 * there are no objects in cDTypeWrapperDiffResultQueueWithDiff and
 * cDAssocWrapperDiffResultQueueWithDiff of above two CDSyntaxDiffs, then we can say there is no
 * semantic difference between CD A and CD B, otherwise there are semantic differences between CD A
 * and CD B.
 *
 * @attribute baseCDW: the based CDWrapper
 * @attribute compareCDW: the compared CDWrapper
 * @attribute cDTypeWrapperDiffResultQueueWithDiff: store the CDTypeWrapperDiff that has semantic
 *     difference
 * @attribute cDAssocWrapperDiffResultQueueWithDiff: store the CDAssocWrapperDiff that has semantic
 *     difference
 * @attribute cDTypeWrapperDiffResultQueueWithoutDiff: store the CDTypeWrapperDiff that has no
 *     semantic difference
 * @attribute cDAssocWrapperDiffResultQueueWithoutDiff: store the CDAssocWrapperDiff that has no
 *     semantic difference
 */
public class CDWrapperSyntaxDiff {
  protected CDWrapper baseCDW;

  protected CDWrapper compareCDW;

  protected Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithDiff;

  protected Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff;

  protected Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithoutDiff;

  protected Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff;

  public CDWrapperSyntaxDiff(CDWrapper baseCDW, CDWrapper compareCDW) {
    this.baseCDW = baseCDW;
    this.compareCDW = compareCDW;
  }

  public CDWrapperSyntaxDiff(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithDiff,
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff,
      Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithoutDiff,
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff) {
    this.baseCDW = baseCDW;
    this.compareCDW = compareCDW;
    this.cDTypeWrapperDiffResultQueueWithDiff = cDTypeWrapperDiffResultQueueWithDiff;
    this.cDAssocWrapperDiffResultQueueWithDiff = cDAssocWrapperDiffResultQueueWithDiff;
    this.cDTypeWrapperDiffResultQueueWithoutDiff = cDTypeWrapperDiffResultQueueWithoutDiff;
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
    return cDTypeWrapperDiffResultQueueWithDiff;
  }

  public void setCDTypeDiffResultQueueWithDiff(
      Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithDiff) {
    this.cDTypeWrapperDiffResultQueueWithDiff = cDTypeWrapperDiffResultQueueWithDiff;
  }

  public Deque<CDAssocWrapperDiff> getCDAssociationDiffResultQueueWithDiff() {
    return cDAssocWrapperDiffResultQueueWithDiff;
  }

  public void setCDAssociationDiffResultQueueWithDiff(
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff) {
    this.cDAssocWrapperDiffResultQueueWithDiff = cDAssocWrapperDiffResultQueueWithDiff;
  }

  public Deque<CDTypeWrapperDiff> getCDTypeDiffResultQueueWithoutDiff() {
    return cDTypeWrapperDiffResultQueueWithoutDiff;
  }

  public void setCDTypeDiffResultQueueWithoutDiff(
      Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithoutDiff) {
    this.cDTypeWrapperDiffResultQueueWithoutDiff = cDTypeWrapperDiffResultQueueWithoutDiff;
  }

  public Deque<CDAssocWrapperDiff> getCDAssociationDiffResultQueueWithoutDiff() {
    return cDAssocWrapperDiffResultQueueWithoutDiff;
  }

  public void setCDAssociationDiffResultQueueWithoutDiff(
      Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff) {
    this.cDAssocWrapperDiffResultQueueWithoutDiff = cDAssocWrapperDiffResultQueueWithoutDiff;
  }
}
