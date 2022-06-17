package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import java.util.Set;

/**
 *  DiffRefSetAssociation is to solve the next problem in CD:
 *    In CD:
 *      Class A;
 *      Class B extends Class A;
 *      Class C extends Class B;
 *      Class D;
 *      association [1..*] A (a) -> (d) D [*];
 *
 *    Then in DiffAssociation Map:
 *      association [1..*] A (a) -> (d) D [*]; // original
 *      association [1..*] B (a) -> (d) D [*]; // inherited
 *      association [1..*] C (a) -> (d) D [*]; // inherited
 *
 *    The above three association will create a DiffRefSetAssociation object:
 *      leftRefSet    leftRoleName   rightRoleName    rightRefSet
 *      [A, B, C]         (a)     ->      (d)             [D]
 */
public class DiffRefSetAssociation {
  protected Set<DiffClass> leftRefSet;
  protected String leftRoleName;
  protected DifferentGroup.DiffAssociationDirection direction;
  protected String rightRoleName;
  protected Set<DiffClass> rightRefSet;

  public DiffRefSetAssociation() {
  }

  public DiffRefSetAssociation(Set<DiffClass> leftRefSet, String leftRoleName, DifferentGroup.DiffAssociationDirection direction, String rightRoleName, Set<DiffClass> rightRefSet) {
    this.leftRefSet = leftRefSet;
    this.leftRoleName = leftRoleName;
    this.direction = direction;
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

  public DifferentGroup.DiffAssociationDirection getDirection() {
    return direction;
  }

  public void setDirection(DifferentGroup.DiffAssociationDirection direction) {
    this.direction = direction;
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
