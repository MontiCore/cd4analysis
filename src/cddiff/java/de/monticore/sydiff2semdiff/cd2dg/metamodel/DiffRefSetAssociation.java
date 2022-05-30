package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import java.util.Set;

public class DiffRefSetAssociation {
  protected Set<DiffClass> leftRefSet;
  protected String leftRoleName;
  protected String rightRoleName;
  protected Set<DiffClass> rightRefSet;

  public DiffRefSetAssociation() {
  }

  public DiffRefSetAssociation(Set<DiffClass> leftRefSet, String leftRoleName, String rightRoleName, Set<DiffClass> rightRefSet) {
    this.leftRefSet = leftRefSet;
    this.leftRoleName = leftRoleName;
    this.rightRoleName = rightRoleName;
    this.rightRefSet = rightRefSet;
  }

  public Set<DiffClass> getLeftRefSet() {
    return leftRefSet;
  }

  public void setLeftRefSet(Set<DiffClass> leftRefSet) {
    this.leftRefSet = leftRefSet;
  }

  public String getLeftRoleName() {
    return leftRoleName;
  }

  public void setLeftRoleName(String leftRoleName) {
    this.leftRoleName = leftRoleName;
  }

  public String getRightRoleName() {
    return rightRoleName;
  }

  public void setRightRoleName(String rightRoleName) {
    this.rightRoleName = rightRoleName;
  }

  public Set<DiffClass> getRightRefSet() {
    return rightRefSet;
  }

  public void setRightRefSet(Set<DiffClass> rightRefSet) {
    this.rightRefSet = rightRefSet;
  }
}
