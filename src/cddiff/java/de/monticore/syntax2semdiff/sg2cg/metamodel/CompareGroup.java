package de.monticore.syntax2semdiff.sg2cg.metamodel;

import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportGroup;

import java.util.Deque;

/**
 * CompareGroup is to compare each element of two SupportGroup and return the result after comparison.
 * We should determine which SupportGroup is the baseSG and which one is the compareSG.
 * Determine whether there is a semantic difference between SupportGroup A and SupportGroup B,
 * We should create two CompareGroup:
 *    1. baseSG = A, compareSG = B
 *    1. baseSG = B, compareSG = A
 * If there are no objects in compClassResultQueueWithDiff and compAssociationResultQueueWithDiff of above two CompareGroups,
 * then we can say there is no semantic difference between CD A and CD B,
 * otherwise there are semantic differences between CD A and CD B.
 *
 * @attribute baseSG:
 *    the based SupportGroup
 * @attribute compareSG:
 *    the compared SupportGroup
 * @attribute compClassResultQueueWithDiff:
 *    store the CompClass that has semantic difference
 * @attribute compAssociationResultQueueWithDiff:
 *    store the CompAssociation that has semantic difference
 * @attribute compClassResultQueueWithoutDiff:
 *    store the CompClass that has no semantic difference
 * @attribute compAssociationResultQueueWithoutDiff:
 *    store the CompAssociation that has no semantic difference
 */
public class CompareGroup {
  protected SupportGroup baseSG;
  protected SupportGroup compareSG;
  protected Deque<CompClass> compClassResultQueueWithDiff;
  protected Deque<CompAssociation> compAssociationResultQueueWithDiff;
  protected Deque<CompClass> compClassResultQueueWithoutDiff;
  protected Deque<CompAssociation> compAssociationResultQueueWithoutDiff;

  public enum CompClassKind {
    COMP_CLASS, COMP_ENUM, COMP_ABSTRACT_CLASS, COMP_INTERFACE
  }

  public enum CompAssociationKind {
    COMP_ASC, COMP_INHERIT_ASC
  }

  public enum CompClassCategory {
    ORIGINAL, EDITED, DELETED, SUBSET
  }

  public enum CompAssociationCategory {
    ORIGINAL, DELETED, DIRECTION_CHANGED, DIRECTION_CHANGED_BUT_SAME_MEANING, DIRECTION_SUBSET, CARDINALITY_CHANGED, CARDINALITY_SUBSET
  }

  public enum CompAssociationDirection {
    NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, BIDIRECTIONAL, LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT
  }

  public enum CompAssociationCardinality {
    NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
  }

  public enum WhichPartDiff {
    DIRECTION, LEFT_CARDINALITY, RIGHT_CARDINALITY
  }

  public CompareGroup(SupportGroup baseSG, SupportGroup compareSG) {
    this.baseSG = baseSG;
    this.compareSG = compareSG;
  }

  public CompareGroup(SupportGroup baseSG,
                      SupportGroup compareSG,
                      Deque<CompClass> compClassResultQueueWithDiff,
                      Deque<CompAssociation> compAssociationResultQueueWithDiff,
                      Deque<CompClass> compClassResultQueueWithoutDiff,
                      Deque<CompAssociation> compAssociationResultQueueWithoutDiff) {
    this.baseSG = baseSG;
    this.compareSG = compareSG;
    this.compClassResultQueueWithDiff = compClassResultQueueWithDiff;
    this.compAssociationResultQueueWithDiff = compAssociationResultQueueWithDiff;
    this.compClassResultQueueWithoutDiff = compClassResultQueueWithoutDiff;
    this.compAssociationResultQueueWithoutDiff = compAssociationResultQueueWithoutDiff;
  }

  public SupportGroup getBaseSG() {
    return baseSG;
  }

  public void setBaseSG(SupportGroup baseSG) {
    this.baseSG = baseSG;
  }

  public SupportGroup getCompareSG() {
    return compareSG;
  }

  public void setCompareSG(SupportGroup compareSG) {
    this.compareSG = compareSG;
  }

  public Deque<CompClass> getCompClassResultQueueWithDiff() {
    return compClassResultQueueWithDiff;
  }

  public void setCompClassResultQueueWithDiff(Deque<CompClass> compClassResultQueueWithDiff) {
    this.compClassResultQueueWithDiff = compClassResultQueueWithDiff;
  }

  public Deque<CompAssociation> getCompAssociationResultQueueWithDiff() {
    return compAssociationResultQueueWithDiff;
  }

  public void setCompAssociationResultQueueWithDiff(Deque<CompAssociation> compAssociationResultQueueWithDiff) {
    this.compAssociationResultQueueWithDiff = compAssociationResultQueueWithDiff;
  }

  public Deque<CompClass> getCompClassResultQueueWithoutDiff() {
    return compClassResultQueueWithoutDiff;
  }

  public void setCompClassResultQueueWithoutDiff(Deque<CompClass> compClassResultQueueWithoutDiff) {
    this.compClassResultQueueWithoutDiff = compClassResultQueueWithoutDiff;
  }

  public Deque<CompAssociation> getCompAssociationResultQueueWithoutDiff() {
    return compAssociationResultQueueWithoutDiff;
  }

  public void setCompAssociationResultQueueWithoutDiff(Deque<CompAssociation> compAssociationResultQueueWithoutDiff) {
    this.compAssociationResultQueueWithoutDiff = compAssociationResultQueueWithoutDiff;
  }

}
