package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;

import java.util.Deque;
import java.util.Map;

enum CmpClassKind {
  CLASS_CMP_KIND, ENUM_CMP_KIND, ABSTRACT_CLASS_CMP_KIND
}

enum CmpRelationKind {
  ASC_CMP_KIND, INHERIT_ASC_CMP_KIND
}

enum CmpClassCategory {
  ORIGINAL, EDITED, DELETED, ONLY_CLASS_NAME_CHANGED, SUBSET
}

enum CmpRelationCategory {
  ORIGINAL, EDITED, DELETED, SUBSET, DIRECTION_TYPE_CHANGED, DIRECTION_DIFFERENT_BUT_SAME_MEANING
}

enum CmpRelationDirection {
  SINGLE, MULTI
}

enum CmpMultiplicities {
  NONE, ZERO, TWO_TO_MORE, ZERO_AND_TWO_TO_MORE
}

public class CompareGroup {
  private DifferentGroup baseDG;
  private DifferentGroup compareDG;
  public Map<String, CmpClass> cmpClassGroup;
  public Map<String, CmpRelation> cmpRelationGroup;
  public Deque<Object> cmpResultQueue;

  public CompareGroup() {
  }

  public CompareGroup(DifferentGroup baseDG, DifferentGroup compareDG, Map<String, CmpClass> cmpClassGroup, Map<String, CmpRelation> cmpRelationGroup, Deque<Object> cmpResultQueue) {
    this.baseDG = baseDG;
    this.compareDG = compareDG;
    this.cmpClassGroup = cmpClassGroup;
    this.cmpRelationGroup = cmpRelationGroup;
    this.cmpResultQueue = cmpResultQueue;
  }

  public DifferentGroup getBaseDG() {
    return baseDG;
  }

  public void setBaseDG(DifferentGroup baseDG) {
    this.baseDG = baseDG;
  }

  public DifferentGroup getCompareDG() {
    return compareDG;
  }

  public void setCompareDG(DifferentGroup compareDG) {
    this.compareDG = compareDG;
  }

  public Map<String, CmpClass> getCmpClassGroup() {
    return cmpClassGroup;
  }

  public void setCmpClassGroup(Map<String, CmpClass> cmpClassGroup) {
    this.cmpClassGroup = cmpClassGroup;
  }

  public Map<String, CmpRelation> getCmpRelationGroup() {
    return cmpRelationGroup;
  }

  public void setCmpRelationGroup(Map<String, CmpRelation> cmpRelationGroup) {
    this.cmpRelationGroup = cmpRelationGroup;
  }

  public Deque<Object> getCmpResultQueue() {
    return cmpResultQueue;
  }

  public void setCmpResultQueue(Deque<Object> cmpResultQueue) {
    this.cmpResultQueue = cmpResultQueue;
  }

  @Override
  public String toString() {
    return "CompareGroup{" + "baseDG=" + baseDG + ", compareDG=" + compareDG + ", cmpClassGroup=" + cmpClassGroup + ", cmpRelationGroup=" + cmpRelationGroup + ", cmpResultQueue=" + cmpResultQueue + '}';
  }
}
