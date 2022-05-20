package de.monticore.sydiff2semdiff.cd2dg.metamodel;

public class DiffSuperClass {
  public String name;
  public DifferentGroup.DiffRelationKind diffKind;
  public String diffParentClass;
  public String diffChildClass;

  public DiffSuperClass() {
  }

  public DiffSuperClass(String name, DifferentGroup.DiffRelationKind diffKind, String diffParentClass, String diffChildClass) {
    this.name = name;
    this.diffKind = diffKind;
    this.diffParentClass = diffParentClass;
    this.diffChildClass = diffChildClass;
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

  public String getDiffParentClass() {
    return diffParentClass;
  }

  public void setDiffParentClass(String diffParentClass) {
    this.diffParentClass = diffParentClass;
  }

  public String getDiffChildClass() {
    return diffChildClass;
  }

  public void setDiffChildClass(String diffChildClass) {
    this.diffChildClass = diffChildClass;
  }

  @Override
  public String toString() {
    return "DiffSuperClass{" + "name='" + name + '\'' + ", diffKind=" + diffKind + ", diffParentClass='" + diffParentClass + '\'' + ", diffChildClass='" + diffChildClass + '\'' + '}';
  }
}
