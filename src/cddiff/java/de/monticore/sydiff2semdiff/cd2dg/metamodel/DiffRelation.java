package de.monticore.sydiff2semdiff.cd2dg.metamodel;

public class DiffRelation {
  public String name;
  public DifferentGroup.DiffRelationKind diffKind;
  public DifferentGroup.DiffRelationNavigation diffNavigation;
  public DiffClass diffSourceClass;
  public DiffClass diffTargetClass;
  public DifferentGroup.DiffMultiplicities diffSourceClassMultiplicitiesType;
  public DifferentGroup.DiffMultiplicities diffTargetClassMultiplicitiesType;
  public String diffSourceClassRoleName;
  public String diffTargetClassRoleName;

  public DiffRelation() {
  }

  public DiffRelation(String name, DifferentGroup.DiffRelationKind diffKind, DifferentGroup.DiffRelationNavigation diffNavigation, DiffClass diffSourceClass, DiffClass diffTargetClass, DifferentGroup.DiffMultiplicities diffSourceClassMultiplicitiesType, DifferentGroup.DiffMultiplicities diffTargetClassMultiplicitiesType, String diffSourceClassRoleName, String diffTargetClassRoleName) {
    this.name = name;
    this.diffKind = diffKind;
    this.diffNavigation = diffNavigation;
    this.diffSourceClass = diffSourceClass;
    this.diffTargetClass = diffTargetClass;
    this.diffSourceClassMultiplicitiesType = diffSourceClassMultiplicitiesType;
    this.diffTargetClassMultiplicitiesType = diffTargetClassMultiplicitiesType;
    this.diffSourceClassRoleName = diffSourceClassRoleName;
    this.diffTargetClassRoleName = diffTargetClassRoleName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DifferentGroup.DiffRelationKind getDiffKind() {
    return diffKind;
  }

  public void setDiffKind(DifferentGroup.DiffRelationKind diffKind) {
    this.diffKind = diffKind;
  }

  public DifferentGroup.DiffRelationNavigation getDiffNavigation() {
    return diffNavigation;
  }

  public void setDiffNavigation(DifferentGroup.DiffRelationNavigation diffNavigation) {
    this.diffNavigation = diffNavigation;
  }

  public DiffClass getDiffSourceClass() {
    return diffSourceClass;
  }

  public void setDiffSourceClass(DiffClass diffSourceClass) {
    this.diffSourceClass = diffSourceClass;
  }

  public DiffClass getDiffTargetClass() {
    return diffTargetClass;
  }

  public void setDiffTargetClass(DiffClass diffTargetClass) {
    this.diffTargetClass = diffTargetClass;
  }

  public DifferentGroup.DiffMultiplicities getDiffSourceClassMultiplicitiesType() {
    return diffSourceClassMultiplicitiesType;
  }

  public void setDiffSourceClassMultiplicitiesType(DifferentGroup.DiffMultiplicities diffSourceClassMultiplicitiesType) {
    this.diffSourceClassMultiplicitiesType = diffSourceClassMultiplicitiesType;
  }

  public DifferentGroup.DiffMultiplicities getDiffTargetClassMultiplicitiesType() {
    return diffTargetClassMultiplicitiesType;
  }

  public void setDiffTargetClassMultiplicitiesType(DifferentGroup.DiffMultiplicities diffTargetClassMultiplicitiesType) {
    this.diffTargetClassMultiplicitiesType = diffTargetClassMultiplicitiesType;
  }

  public String getDiffSourceClassRoleName() {
    return diffSourceClassRoleName;
  }

  public void setDiffSourceClassRoleName(String diffSourceClassRoleName) {
    this.diffSourceClassRoleName = diffSourceClassRoleName;
  }

  public String getDiffTargetClassRoleName() {
    return diffTargetClassRoleName;
  }

  public void setDiffTargetClassRoleName(String diffTargetClassRoleName) {
    this.diffTargetClassRoleName = diffTargetClassRoleName;
  }
}
