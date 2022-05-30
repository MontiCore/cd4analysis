package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;

import java.util.Optional;
import java.util.UUID;

public class CompAssociation {
  protected UUID compId;
  protected String name;
  protected CompareGroup.CompAssociationKind compKind;
  protected boolean isInBasedDG;
  protected boolean isInComparedDG;
  protected boolean isContentDiff;
  protected CompareGroup.CompAssociationCategory compCategoryResult;
  protected CompareGroup.CompAssociationDirection compDirectionResult;
  protected DiffClass compLeftClassResult;
  protected DiffClass compRightClassResult;
  protected CompareGroup.CompAssociationCardinality compLeftClassCardinalityResult;
  protected CompareGroup.CompAssociationCardinality compRightClassCardinalityResult;
  protected String compLeftClassRoleName;
  protected String compRightClassRoleName;
  protected Optional<CompareGroup.WhichPartDiff> whichPartDiff;
  protected DiffAssociation originalDiffAssociation;

  public CompAssociation() {
  }

  public CompAssociation(UUID compId, String name, CompareGroup.CompAssociationKind compKind, boolean isInBasedDG, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompAssociationCategory compCategoryResult, CompareGroup.CompAssociationDirection compDirectionResult, DiffClass compLeftClassResult, DiffClass compRightClassResult, CompareGroup.CompAssociationCardinality compLeftClassCardinalityResult, CompareGroup.CompAssociationCardinality compRightClassCardinalityResult, String compLeftClassRoleName, String compRightClassRoleName, Optional<CompareGroup.WhichPartDiff> whichPartDiff, DiffAssociation originalDiffAssociation) {
    this.compId = compId;
    this.name = name;
    this.compKind = compKind;
    this.isInBasedDG = isInBasedDG;
    this.isInComparedDG = isInComparedDG;
    this.isContentDiff = isContentDiff;
    this.compCategoryResult = compCategoryResult;
    this.compDirectionResult = compDirectionResult;
    this.compLeftClassResult = compLeftClassResult;
    this.compRightClassResult = compRightClassResult;
    this.compLeftClassCardinalityResult = compLeftClassCardinalityResult;
    this.compRightClassCardinalityResult = compRightClassCardinalityResult;
    this.compLeftClassRoleName = compLeftClassRoleName;
    this.compRightClassRoleName = compRightClassRoleName;
    this.whichPartDiff = whichPartDiff;
    this.originalDiffAssociation = originalDiffAssociation;
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

  public CompareGroup.CompAssociationKind getCompKind() {
    return compKind;
  }

  public void setCompKind(CompareGroup.CompAssociationKind compKind) {
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

  public CompareGroup.CompAssociationCategory getCompCategoryResult() {
    return compCategoryResult;
  }

  public void setCompCategoryResult(CompareGroup.CompAssociationCategory compCategoryResult) {
    this.compCategoryResult = compCategoryResult;
  }

  public CompareGroup.CompAssociationDirection getCompDirectionResult() {
    return compDirectionResult;
  }

  public void setCompDirectionResult(CompareGroup.CompAssociationDirection compDirectionResult) {
    this.compDirectionResult = compDirectionResult;
  }

  public DiffClass getCompLeftClassResult() {
    return compLeftClassResult;
  }

  public void setCompLeftClassResult(DiffClass compLeftClassResult) {
    this.compLeftClassResult = compLeftClassResult;
  }

  public DiffClass getCompRightClassResult() {
    return compRightClassResult;
  }

  public void setCompRightClassResult(DiffClass compRightClassResult) {
    this.compRightClassResult = compRightClassResult;
  }

  public CompareGroup.CompAssociationCardinality getCompLeftClassCardinalityResult() {
    return compLeftClassCardinalityResult;
  }

  public void setCompLeftClassCardinalityResult(CompareGroup.CompAssociationCardinality compLeftClassCardinalityResult) {
    this.compLeftClassCardinalityResult = compLeftClassCardinalityResult;
  }

  public CompareGroup.CompAssociationCardinality getCompRightClassCardinalityResult() {
    return compRightClassCardinalityResult;
  }

  public void setCompRightClassCardinalityResult(CompareGroup.CompAssociationCardinality compRightClassCardinalityResult) {
    this.compRightClassCardinalityResult = compRightClassCardinalityResult;
  }

  public String getCompLeftClassRoleName() {
    return compLeftClassRoleName;
  }

  public void setCompLeftClassRoleName(String compLeftClassRoleName) {
    this.compLeftClassRoleName = compLeftClassRoleName;
  }

  public String getCompRightClassRoleName() {
    return compRightClassRoleName;
  }

  public void setCompRightClassRoleName(String compRightClassRoleName) {
    this.compRightClassRoleName = compRightClassRoleName;
  }

  public Optional<CompareGroup.WhichPartDiff> getWhichPartDiff() {
    return whichPartDiff;
  }

  public void setWhichPartDiff(Optional<CompareGroup.WhichPartDiff> whichPartDiff) {
    this.whichPartDiff = whichPartDiff;
  }

  public DiffAssociation getOriginalDiffAssociation() {
    return originalDiffAssociation;
  }

  public void setOriginalDiffAssociation(DiffAssociation originalDiffAssociation) {
    this.originalDiffAssociation = originalDiffAssociation;
  }

  @Override
  public String toString() {
    return "CompAssociation{" + "compId=" + compId + ", name='" + name + '\'' + ", compKind=" + compKind + ", isInBasedDG=" + isInBasedDG + ", isInComparedDG=" + isInComparedDG + ", isContentDiff=" + isContentDiff + ", compCategoryResult=" + compCategoryResult + ", compDirectionResult=" + compDirectionResult + ", compLeftClassResult=" + compLeftClassResult + ", compRightClassResult=" + compRightClassResult + ", compLeftClassCardinalityResult=" + compLeftClassCardinalityResult + ", compRightClassCardinalityResult=" + compRightClassCardinalityResult + ", compLeftClassRoleName='" + compLeftClassRoleName + '\'' + ", compRightClassRoleName='" + compRightClassRoleName + '\'' + ", whichPartDiff=" + whichPartDiff + ", originalDiffAssociation=" + originalDiffAssociation + '}';
  }
}
