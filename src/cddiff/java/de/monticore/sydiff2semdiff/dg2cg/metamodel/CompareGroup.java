package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import java.util.Deque;

public class CompareGroup {
  protected DifferentGroup basedDG;
  protected DifferentGroup comparedDG;
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
    ORIGINAL, DELETED,
    DIRECTION_CHANGED, DIRECTION_CHANGED_BUT_SAME_MEANING, DIRECTION_SUBSET,
    CARDINALITY_CHANGED, CARDINALITY_SUBSET
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

  public CompareGroup(DifferentGroup basedDG, DifferentGroup comparedDG) {
    this.basedDG = basedDG;
    this.comparedDG = comparedDG;
  }

  public CompareGroup(DifferentGroup basedDG, DifferentGroup comparedDG, Deque<CompClass> compClassResultQueueWithDiff, Deque<CompAssociation> compAssociationResultQueueWithDiff, Deque<CompClass> compClassResultQueueWithoutDiff, Deque<CompAssociation> compAssociationResultQueueWithoutDiff) {
    this.basedDG = basedDG;
    this.comparedDG = comparedDG;
    this.compClassResultQueueWithDiff = compClassResultQueueWithDiff;
    this.compAssociationResultQueueWithDiff = compAssociationResultQueueWithDiff;
    this.compClassResultQueueWithoutDiff = compClassResultQueueWithoutDiff;
    this.compAssociationResultQueueWithoutDiff = compAssociationResultQueueWithoutDiff;
  }

  public DifferentGroup getBasedDG() {
    return basedDG;
  }

  public void setBasedDG(DifferentGroup basedDG) {
    this.basedDG = basedDG;
  }

  public DifferentGroup getComparedDG() {
    return comparedDG;
  }

  public void setComparedDG(DifferentGroup comparedDG) {
    this.comparedDG = comparedDG;
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

  @Override
  public String toString() {
    return "CompareGroup{" + "basedDG=" + basedDG + ", comparedDG=" + comparedDG + ", compClassResultQueueWithDiff=" + compClassResultQueueWithDiff + ", compAssociationResultQueueWithDiff=" + compAssociationResultQueueWithDiff + ", compClassResultQueueWithoutDiff=" + compClassResultQueueWithoutDiff + ", compAssociationResultQueueWithoutDiff=" + compAssociationResultQueueWithoutDiff + '}';
  }
}
