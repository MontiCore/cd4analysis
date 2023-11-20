/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * CDRefSetAssociationWrapper is to solve the next problem in CD: In CD: Class A; Class B extends
 * Class A; Class C extends Class B; Class D; association [1..*] A (a) -> (d) D [*];
 *
 * <p>Then in CDAssociationWrapper Map: association [1..*] A (a) -> (d) D [*]; // original
 * association [1..*] B (a) -> (d) D [*]; // inherited association [1..*] C (a) -> (d) D [*]; //
 * inherited
 *
 * <p>The above three association will create a CDRefSetAssociationWrapper object: leftRefSet
 * leftRoleName rightRoleName rightRefSet [A, B, C] (a) -> (d) [D]
 */
public class CDRefSetAssociationWrapper {
  protected Set<CDTypeWrapper> leftRefSet;

  protected String leftRoleName;

  protected CDAssociationWrapperDirection direction;

  protected String rightRoleName;

  protected Set<CDTypeWrapper> rightRefSet;

  protected CDAssociationWrapper originalElement;

  public CDRefSetAssociationWrapper() {}

  public CDRefSetAssociationWrapper(
      Set<CDTypeWrapper> leftRefSet,
      String leftRoleName,
      CDAssociationWrapperDirection direction,
      String rightRoleName,
      Set<CDTypeWrapper> rightRefSet,
      CDAssociationWrapper originalElement) {
    this.leftRefSet = leftRefSet;
    this.leftRoleName = leftRoleName;
    this.direction = direction;
    this.rightRoleName = rightRoleName;
    this.rightRefSet = rightRefSet;
    this.originalElement = originalElement;
  }

  public Set<CDTypeWrapper> getLeftRefSet() {
    return leftRefSet;
  }

  public void setLeftRefSet(Set<CDTypeWrapper> leftRefSet) {
    this.leftRefSet = leftRefSet;
  }

  public String getLeftRoleName() {
    return leftRoleName;
  }

  public void setLeftRoleName(String leftRoleName) {
    this.leftRoleName = leftRoleName;
  }

  public CDAssociationWrapperDirection getDirection() {
    return direction;
  }

  public void setDirection(CDAssociationWrapperDirection direction) {
    this.direction = direction;
  }

  public String getRightRoleName() {
    return rightRoleName;
  }

  public void setRightRoleName(String rightRoleName) {
    this.rightRoleName = rightRoleName;
  }

  public Set<CDTypeWrapper> getRightRefSet() {
    return rightRefSet;
  }

  public void setRightRefSet(Set<CDTypeWrapper> rightRefSet) {
    this.rightRefSet = rightRefSet;
  }

  public CDAssociationWrapper getOriginalElement() {
    return originalElement;
  }

  public void setOriginalElement(CDAssociationWrapper originalElement) {
    this.originalElement = originalElement;
  }

  public boolean isPresentInCDRefSetAssociationWrapper(CDAssociationWrapper originalElement) {
    if (
    // original
    (this.getLeftRefSet().stream()
                .map(CDTypeWrapper::getOriginalClassName)
                .collect(Collectors.toSet())
                .contains(originalElement.getLeftOriginalClassName())
            && this.getLeftRoleName().equals(originalElement.getCDWrapperLeftClassRoleName())
            && this.getDirection().equals(originalElement.getCDAssociationWrapperDirection())
            && this.getRightRoleName().equals(originalElement.getCDWrapperRightClassRoleName())
            && this.getRightRefSet().stream()
                .map(CDTypeWrapper::getOriginalClassName)
                .collect(Collectors.toSet())
                .contains(originalElement.getRightOriginalClassName()))
        ||
        // reversed
        (this.getLeftRefSet().stream()
                .map(CDTypeWrapper::getOriginalClassName)
                .collect(Collectors.toSet())
                .contains(originalElement.getRightOriginalClassName())
            && this.getLeftRoleName().equals(originalElement.getCDWrapperRightClassRoleName())
            && this.getDirection()
                .equals(reverseDirection(originalElement.getCDAssociationWrapperDirection()))
            && this.getRightRoleName().equals(originalElement.getCDWrapperLeftClassRoleName())
            && this.getRightRefSet().stream()
                .map(CDTypeWrapper::getOriginalClassName)
                .collect(Collectors.toSet())
                .contains(originalElement.getLeftOriginalClassName()))) {
      return true;
    } else {
      return false;
    }
  }
}
