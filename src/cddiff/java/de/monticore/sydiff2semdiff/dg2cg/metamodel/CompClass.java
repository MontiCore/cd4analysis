package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CompClass {
  private UUID compId;
  private String name;
  private CompareGroup.CompClassKind compKind;
  private boolean isInBasedDG;
  private boolean isInComparedDG;
  private boolean isContentDiff;
  private CompareGroup.CompClassCategory compCategory;
  private Map<String, String> compAttributesResult;
  private List<String> whichAttributesDiff;
  private DiffClass originalDiffClass;

  public CompClass() {
  }

  public CompClass(UUID compId, String name, CompareGroup.CompClassKind compKind, boolean isInBasedDG, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompClassCategory compCategory, Map<String, String> compAttributesResult, List<String> whichAttributesDiff, DiffClass originalDiffClass) {
    this.compId = compId;
    this.name = name;
    this.compKind = compKind;
    this.isInBasedDG = isInBasedDG;
    this.isInComparedDG = isInComparedDG;
    this.isContentDiff = isContentDiff;
    this.compCategory = compCategory;
    this.compAttributesResult = compAttributesResult;
    this.whichAttributesDiff = whichAttributesDiff;
    this.originalDiffClass = originalDiffClass;
  }

  public UUID getCompId() {
    return compId;
  }

  public void setCompId(UUID compId) {
    this.compId = compId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CompareGroup.CompClassKind getCompKind() {
    return compKind;
  }

  public void setCompKind(CompareGroup.CompClassKind compKind) {
    this.compKind = compKind;
  }

  public boolean isInBasedDG() {
    return isInBasedDG;
  }

  public void setInBasedDG(boolean inBasedDG) {
    isInBasedDG = inBasedDG;
  }

  public boolean isInComparedDG() {
    return isInComparedDG;
  }

  public void setInComparedDG(boolean inComparedDG) {
    isInComparedDG = inComparedDG;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public void setContentDiff(boolean contentDiff) {
    isContentDiff = contentDiff;
  }

  public CompareGroup.CompClassCategory getCompCategory() {
    return compCategory;
  }

  public void setCompCategory(CompareGroup.CompClassCategory compCategory) {
    this.compCategory = compCategory;
  }

  public Map<String, String> getCompAttributesResult() {
    return compAttributesResult;
  }

  public void setCompAttributesResult(Map<String, String> compAttributesResult) {
    this.compAttributesResult = compAttributesResult;
  }

  public List<String> getWhichAttributesDiff() {
    return whichAttributesDiff;
  }

  public void setWhichAttributesDiff(List<String> whichAttributesDiff) {
    this.whichAttributesDiff = whichAttributesDiff;
  }

  public DiffClass getOriginalDiffClass() {
    return originalDiffClass;
  }

  public void setOriginalDiffClass(DiffClass originalDiffClass) {
    this.originalDiffClass = originalDiffClass;
  }

  @Override
  public String toString() {
    return "CompClass{" + "compId=" + compId + ", name='" + name + '\'' + ", compKind=" + compKind + ", isInBasedDG=" + isInBasedDG + ", isInComparedDG=" + isInComparedDG + ", isContentDiff=" + isContentDiff + ", compCategory=" + compCategory + ", compAttributesResult=" + compAttributesResult + ", whichAttributesDiff=" + whichAttributesDiff + ", originalDiffClass=" + originalDiffClass + '}';
  }
}
