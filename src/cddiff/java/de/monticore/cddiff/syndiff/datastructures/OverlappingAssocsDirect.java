package de.monticore.cddiff.syndiff.datastructures;

import edu.mit.csail.sdg.alloy4.Pair;

import java.util.Set;

public class OverlappingAssocsDirect {
  private Set<Pair<AssocStruct, AssocStruct>> directOverlappingAssocs;
  private Set<Pair<AssocStruct, AssocStruct>> directAssocsNoRelation;

  public OverlappingAssocsDirect(Set<Pair<AssocStruct, AssocStruct>> directOverlappingAssocs, Set<Pair<AssocStruct, AssocStruct>> directAssocsNoRelation) {
    this.directOverlappingAssocs = directOverlappingAssocs;
    this.directAssocsNoRelation = directAssocsNoRelation;
  }

  public Set<Pair<AssocStruct, AssocStruct>> getDirectOverlappingAssocs() {
    return directOverlappingAssocs;
  }

  public void setDirectOverlappingAssocs(Set<Pair<AssocStruct, AssocStruct>> directOverlappingAssocs) {
    this.directOverlappingAssocs = directOverlappingAssocs;
  }

  public Set<Pair<AssocStruct, AssocStruct>> getDirectAssocsNoRelation() {
    return directAssocsNoRelation;
  }

  public void setDirectAssocsNoRelation(Set<Pair<AssocStruct, AssocStruct>> directAssocsNoRelation) {
    this.directAssocsNoRelation = directAssocsNoRelation;
  }
}
