package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;

import java.util.Set;
import java.util.UUID;

public class CmpClass {
  public String name;
  public UUID cmpId;
  public CmpClassKind cmpKind;
  public boolean cmpDG1;
  public boolean cmpDG2;
  public boolean cmpDiff;
  public CmpClassCategory cmpCategory;
  public Set<Object> cmpResult;
  public DiffClass originalDiffClass;

  public CmpClass() {
  }

  public CmpClass(String name, UUID cmpId, CmpClassKind cmpKind, boolean cmpDG1, boolean cmpDG2, boolean cmpDiff, CmpClassCategory cmpCategory, Set<Object> cmpResult, DiffClass originalDiffClass) {
    this.name = name;
    this.cmpId = cmpId;
    this.cmpKind = cmpKind;
    this.cmpDG1 = cmpDG1;
    this.cmpDG2 = cmpDG2;
    this.cmpDiff = cmpDiff;
    this.cmpCategory = cmpCategory;
    this.cmpResult = cmpResult;
    this.originalDiffClass = originalDiffClass;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UUID getCmpId() {
    return cmpId;
  }

  public void setCmpId(UUID cmpId) {
    this.cmpId = cmpId;
  }

  public CmpClassKind getCmpKind() {
    return cmpKind;
  }

  public void setCmpKind(CmpClassKind cmpKind) {
    this.cmpKind = cmpKind;
  }

  public boolean isCmpDG1() {
    return cmpDG1;
  }

  public void setCmpDG1(boolean cmpDG1) {
    this.cmpDG1 = cmpDG1;
  }

  public boolean isCmpDG2() {
    return cmpDG2;
  }

  public void setCmpDG2(boolean cmpDG2) {
    this.cmpDG2 = cmpDG2;
  }

  public boolean isCmpDiff() {
    return cmpDiff;
  }

  public void setCmpDiff(boolean cmpDiff) {
    this.cmpDiff = cmpDiff;
  }

  public CmpClassCategory getCmpCategory() {
    return cmpCategory;
  }

  public void setCmpCategory(CmpClassCategory cmpCategory) {
    this.cmpCategory = cmpCategory;
  }

  public Set<Object> getCmpResult() {
    return cmpResult;
  }

  public void setCmpResult(Set<Object> cmpResult) {
    this.cmpResult = cmpResult;
  }

  public DiffClass getOriginalDiffClass() {
    return originalDiffClass;
  }

  public void setOriginalDiffClass(DiffClass originalDiffClass) {
    this.originalDiffClass = originalDiffClass;
  }

  @Override
  public String toString() {
    return "CmpClass{" + "name='" + name + '\'' + ", cmpId=" + cmpId + ", cmpKind=" + cmpKind + ", cmpDG1=" + cmpDG1 + ", cmpDG2=" + cmpDG2 + ", cmpDiff=" + cmpDiff + ", cmpCategory=" + cmpCategory + ", cmpResult=" + cmpResult + ", originalDiffClass=" + originalDiffClass + '}';
  }
}
