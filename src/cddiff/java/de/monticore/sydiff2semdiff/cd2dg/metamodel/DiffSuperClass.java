package de.monticore.sydiff2semdiff.cd2dg.metamodel;

public class DiffSuperClass {
  public String name;
  public DifferentGroup.DiffClassKind diffKind;
  public DiffClass diffParent;
  public DiffClass diffChild;

  public DiffSuperClass() {
  }

  public DiffSuperClass(String name, DifferentGroup.DiffClassKind diffKind, DiffClass diffParent, DiffClass diffChild) {
    this.name = name;
    this.diffKind = diffKind;
    this.diffParent = diffParent;
    this.diffChild = diffChild;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DifferentGroup.DiffClassKind getDiffKind() {
    return diffKind;
  }

  public void setDiffKind(DifferentGroup.DiffClassKind diffKind) {
    this.diffKind = diffKind;
  }

  public DiffClass getDiffParent() {
    return diffParent;
  }

  public void setDiffParent(DiffClass diffParent) {
    this.diffParent = diffParent;
  }

  public DiffClass getDiffChild() {
    return diffChild;
  }

  public void setDiffChild(DiffClass diffChild) {
    this.diffChild = diffChild;
  }
}
