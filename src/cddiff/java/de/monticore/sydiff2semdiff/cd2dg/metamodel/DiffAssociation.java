package de.monticore.sydiff2semdiff.cd2dg.metamodel;

import de.monticore.cdassociation._ast.ASTCDAssociation;

public class DiffAssociation implements Cloneable{
  public String name;
  public DifferentGroup.DiffAssociationKind diffKind;
  public DifferentGroup.DiffAssociationDirection diffDirection;
  public DiffClass diffLeftClass;
  public DiffClass diffRightClass;
  public DifferentGroup.DiffAssociationCardinality diffLeftClassCardinality;
  public DifferentGroup.DiffAssociationCardinality diffRightClassCardinality;
  public String diffLeftClassRoleName;
  public String diffRightClassRoleName;
  public ASTCDAssociation originalElement;

  public DiffAssociation() {
  }

  public DiffAssociation(String name, DifferentGroup.DiffAssociationKind diffKind, DifferentGroup.DiffAssociationDirection diffDirection, DiffClass diffLeftClass, DiffClass diffRightClass, DifferentGroup.DiffAssociationCardinality diffLeftClassCardinality, DifferentGroup.DiffAssociationCardinality diffRightClassCardinality, String diffLeftClassRoleName, String diffRightClassRoleName, ASTCDAssociation originalElement) {
    this.name = name;
    this.diffKind = diffKind;
    this.diffDirection = diffDirection;
    this.diffLeftClass = diffLeftClass;
    this.diffRightClass = diffRightClass;
    this.diffLeftClassCardinality = diffLeftClassCardinality;
    this.diffRightClassCardinality = diffRightClassCardinality;
    this.diffLeftClassRoleName = diffLeftClassRoleName;
    this.diffRightClassRoleName = diffRightClassRoleName;
    this.originalElement = originalElement;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DifferentGroup.DiffAssociationKind getDiffKind() {
    return diffKind;
  }

  public void setDiffKind(DifferentGroup.DiffAssociationKind diffKind) {
    this.diffKind = diffKind;
  }

  public DifferentGroup.DiffAssociationDirection getDiffDirection() {
    return diffDirection;
  }

  public void setDiffDirection(DifferentGroup.DiffAssociationDirection diffDirection) {
    this.diffDirection = diffDirection;
  }

  public DiffClass getDiffLeftClass() {
    return diffLeftClass;
  }

  public void setDiffLeftClass(DiffClass diffLeftClass) {
    this.diffLeftClass = diffLeftClass;
  }

  public DiffClass getDiffRightClass() {
    return diffRightClass;
  }

  public void setDiffRightClass(DiffClass diffRightClass) {
    this.diffRightClass = diffRightClass;
  }

  public DifferentGroup.DiffAssociationCardinality getDiffLeftClassCardinality() {
    return diffLeftClassCardinality;
  }

  public void setDiffLeftClassCardinality(DifferentGroup.DiffAssociationCardinality diffLeftClassCardinality) {
    this.diffLeftClassCardinality = diffLeftClassCardinality;
  }

  public DifferentGroup.DiffAssociationCardinality getDiffRightClassCardinality() {
    return diffRightClassCardinality;
  }

  public void setDiffRightClassCardinality(DifferentGroup.DiffAssociationCardinality diffRightClassCardinality) {
    this.diffRightClassCardinality = diffRightClassCardinality;
  }

  public String getDiffLeftClassRoleName() {
    return diffLeftClassRoleName;
  }

  public void setDiffLeftClassRoleName(String diffLeftClassRoleName) {
    this.diffLeftClassRoleName = diffLeftClassRoleName;
  }

  public String getDiffRightClassRoleName() {
    return diffRightClassRoleName;
  }

  public void setDiffRightClassRoleName(String diffRightClassRoleName) {
    this.diffRightClassRoleName = diffRightClassRoleName;
  }

  public ASTCDAssociation getOriginalElement() {
    return originalElement;
  }

  public void setOriginalElement(ASTCDAssociation originalElement) {
    this.originalElement = originalElement;
  }

  @Override
  public DiffAssociation clone() throws CloneNotSupportedException {
    DiffAssociation cloned = (DiffAssociation) super.clone();
    cloned.diffLeftClass = diffLeftClass.clone();
    cloned.diffRightClass = diffRightClass.clone();
    return cloned;
  }

}
