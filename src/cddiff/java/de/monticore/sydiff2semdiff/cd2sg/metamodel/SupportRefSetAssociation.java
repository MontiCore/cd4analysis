package de.monticore.sydiff2semdiff.cd2sg.metamodel;

import java.util.Set;

/**
 *  SupportRefSetAssociation is to solve the next problem in CD:
 *    In CD:
 *      Class A;
 *      Class B extends Class A;
 *      Class C extends Class B;
 *      Class D;
 *      association [1..*] A (a) -> (d) D [*];
 *
 *    Then in SupportAssociation Map:
 *      association [1..*] A (a) -> (d) D [*]; // original
 *      association [1..*] B (a) -> (d) D [*]; // inherited
 *      association [1..*] C (a) -> (d) D [*]; // inherited
 *
 *    The above three association will create a SupportRefSetAssociation object:
 *      leftRefSet    leftRoleName   rightRoleName    rightRefSet
 *      [A, B, C]         (a)     ->      (d)             [D]
 */
public class SupportRefSetAssociation {
  protected Set<SupportClass> leftRefSet;
  protected String leftRoleName;
  protected SupportGroup.SupportAssociationDirection direction;
  protected String rightRoleName;
  protected Set<SupportClass> rightRefSet;

  protected SupportAssociation originalElement;

  public SupportRefSetAssociation() {
  }

  public SupportRefSetAssociation(Set<SupportClass> leftRefSet,
                                  String leftRoleName,
                                  SupportGroup.SupportAssociationDirection direction,
                                  String rightRoleName,
                                  Set<SupportClass> rightRefSet,
                                  SupportAssociation originalElement) {
    this.leftRefSet = leftRefSet;
    this.leftRoleName = leftRoleName;
    this.direction = direction;
    this.rightRoleName = rightRoleName;
    this.rightRefSet = rightRefSet;
    this.originalElement = originalElement;
  }

  public Set<SupportClass> getLeftRefSet() {
    return leftRefSet;
  }

  public void setLeftRefSet(Set<SupportClass> leftRefSet) {
    this.leftRefSet = leftRefSet;
  }

  public String getLeftRoleName() {
    return leftRoleName;
  }

  public void setLeftRoleName(String leftRoleName) {
    this.leftRoleName = leftRoleName;
  }

  public SupportGroup.SupportAssociationDirection getDirection() {
    return direction;
  }

  public void setDirection(SupportGroup.SupportAssociationDirection direction) {
    this.direction = direction;
  }

  public String getRightRoleName() {
    return rightRoleName;
  }

  public void setRightRoleName(String rightRoleName) {
    this.rightRoleName = rightRoleName;
  }

  public Set<SupportClass> getRightRefSet() {
    return rightRefSet;
  }

  public void setRightRefSet(Set<SupportClass> rightRefSet) {
    this.rightRefSet = rightRefSet;
  }

  public SupportAssociation getOriginalElement() {
    return originalElement;
  }

  public void setOriginalElement(SupportAssociation originalElement) {
    this.originalElement = originalElement;
  }
}
