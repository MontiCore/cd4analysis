package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffRelation;

import java.util.Set;
import java.util.UUID;

public class CmpRelation {
  public String name;
  public UUID cmpId;
  public CmpRelationKind cmpKind;
  public boolean cmpDG1;
  public boolean cmpDG2;
  public boolean cmpDiff;
  public CmpRelationCategory cmpCategory;
  public CmpRelationDirection cmpDirection;
  public Set<Object> cmpFrom;
  public Set<Object> cmpTo;
  public CmpMultiplicities cmpFromType;
  public CmpMultiplicities cmpToType;
  public Set<Object> cmpFromRoleName;
  public Set<Object> cmpTomRoleName;
  public Set<Object> cmpResult;
  public DiffRelation originalDiffRelation;

  public CmpRelation() {
  }

  public CmpRelation(String name, UUID cmpId, CmpRelationKind cmpKind, boolean cmpDG1, boolean cmpDG2, boolean cmpDiff, CmpRelationCategory cmpCategory, CmpRelationDirection cmpDirection, Set<Object> cmpFrom, Set<Object> cmpTo, CmpMultiplicities cmpFromType, CmpMultiplicities cmpToType, Set<Object> cmpFromRoleName, Set<Object> cmpTomRoleName, Set<Object> cmpResult, DiffRelation originalDiffRelation) {
    this.name = name;
    this.cmpId = cmpId;
    this.cmpKind = cmpKind;
    this.cmpDG1 = cmpDG1;
    this.cmpDG2 = cmpDG2;
    this.cmpDiff = cmpDiff;
    this.cmpCategory = cmpCategory;
    this.cmpDirection = cmpDirection;
    this.cmpFrom = cmpFrom;
    this.cmpTo = cmpTo;
    this.cmpFromType = cmpFromType;
    this.cmpToType = cmpToType;
    this.cmpFromRoleName = cmpFromRoleName;
    this.cmpTomRoleName = cmpTomRoleName;
    this.cmpResult = cmpResult;
    this.originalDiffRelation = originalDiffRelation;
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

  public CmpRelationKind getCmpKind() {
    return cmpKind;
  }

  public void setCmpKind(CmpRelationKind cmpKind) {
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

  public CmpRelationCategory getCmpCategory() {
    return cmpCategory;
  }

  public void setCmpCategory(CmpRelationCategory cmpCategory) {
    this.cmpCategory = cmpCategory;
  }

  public CmpRelationDirection getCmpDirection() {
    return cmpDirection;
  }

  public void setCmpDirection(CmpRelationDirection cmpDirection) {
    this.cmpDirection = cmpDirection;
  }

  public Set<Object> getCmpFrom() {
    return cmpFrom;
  }

  public void setCmpFrom(Set<Object> cmpFrom) {
    this.cmpFrom = cmpFrom;
  }

  public Set<Object> getCmpTo() {
    return cmpTo;
  }

  public void setCmpTo(Set<Object> cmpTo) {
    this.cmpTo = cmpTo;
  }

  public CmpMultiplicities getCmpFromType() {
    return cmpFromType;
  }

  public void setCmpFromType(CmpMultiplicities cmpFromType) {
    this.cmpFromType = cmpFromType;
  }

  public CmpMultiplicities getCmpToType() {
    return cmpToType;
  }

  public void setCmpToType(CmpMultiplicities cmpToType) {
    this.cmpToType = cmpToType;
  }

  public Set<Object> getCmpFromRoleName() {
    return cmpFromRoleName;
  }

  public void setCmpFromRoleName(Set<Object> cmpFromRoleName) {
    this.cmpFromRoleName = cmpFromRoleName;
  }

  public Set<Object> getCmpTomRoleName() {
    return cmpTomRoleName;
  }

  public void setCmpTomRoleName(Set<Object> cmpTomRoleName) {
    this.cmpTomRoleName = cmpTomRoleName;
  }

  public Set<Object> getCmpResult() {
    return cmpResult;
  }

  public void setCmpResult(Set<Object> cmpResult) {
    this.cmpResult = cmpResult;
  }

  public DiffRelation getOriginalDiffRelation() {
    return originalDiffRelation;
  }

  public void setOriginalDiffRelation(DiffRelation originalDiffRelation) {
    this.originalDiffRelation = originalDiffRelation;
  }

  @Override
  public String toString() {
    return "CmpRelation{" + "name='" + name + '\'' + ", cmpId=" + cmpId + ", cmpKind=" + cmpKind + ", cmpDG1=" + cmpDG1 + ", cmpDG2=" + cmpDG2 + ", cmpDiff=" + cmpDiff + ", cmpCategory=" + cmpCategory + ", cmpDirection=" + cmpDirection + ", cmpFrom=" + cmpFrom + ", cmpTo=" + cmpTo + ", cmpFromType=" + cmpFromType + ", cmpToType=" + cmpToType + ", cmpFromRoleName=" + cmpFromRoleName + ", cmpTomRoleName=" + cmpTomRoleName + ", cmpResult=" + cmpResult + ", originalDiffRelation=" + originalDiffRelation + '}';
  }
}
