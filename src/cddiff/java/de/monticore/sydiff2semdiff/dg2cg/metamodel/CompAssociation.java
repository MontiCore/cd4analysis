package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;

import java.util.Optional;
import java.util.UUID;

import static de.monticore.sydiff2semdiff.dg2cg.CompareHelper.getCompAssociationKindHelper;

public class CompAssociation {
  protected final UUID compId;
  protected final DiffAssociation originalElement;
  protected final boolean isInComparedDG;
  protected final boolean isContentDiff;
  protected final CompareGroup.CompAssociationCategory compCategory;
  protected Optional<CompareGroup.CompAssociationDirection> compDirectionResult;
  protected Optional<CompareGroup.CompAssociationCardinality> compLeftClassCardinalityResult;
  protected Optional<CompareGroup.CompAssociationCardinality> compRightClassCardinalityResult;
  protected Optional<CompareGroup.WhichPartDiff> whichPartDiff;

  public CompAssociation(DiffAssociation originalElement, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompAssociationCategory compCategory) {
    this.originalElement = originalElement;
    this.isInComparedDG = isInComparedDG;
    this.isContentDiff = isContentDiff;
    this.compCategory = compCategory;
    this.compId =UUID.randomUUID();
  }

  public UUID getCompId() {
    return compId;
  }

  public String getName() {
    return "CompAssociation_" + this.originalElement.getName().substring(this.originalElement.getName().indexOf("_") + 1, this.originalElement.getName().length());
  }

  public CompareGroup.CompAssociationKind getCompKind() {
    return getCompAssociationKindHelper(this.originalElement.getDiffKind());
  }

  public boolean isInComparedDG() {
    return isInComparedDG;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CompareGroup.CompAssociationCategory getCompCategory() {
    return compCategory;
  }

  public Optional<CompareGroup.CompAssociationDirection> getCompDirectionResult() {
    return compDirectionResult;
  }

  public void setCompDirectionResult(Optional<CompareGroup.CompAssociationDirection> compDirectionResult) {
    this.compDirectionResult = compDirectionResult;
  }

  public Optional<CompareGroup.CompAssociationCardinality> getCompLeftClassCardinalityResult() {
    return compLeftClassCardinalityResult;
  }

  public void setCompLeftClassCardinalityResult(Optional<CompareGroup.CompAssociationCardinality> compLeftClassCardinalityResult) {
    this.compLeftClassCardinalityResult = compLeftClassCardinalityResult;
  }

  public Optional<CompareGroup.CompAssociationCardinality> getCompRightClassCardinalityResult() {
    return compRightClassCardinalityResult;
  }

  public void setCompRightClassCardinalityResult(Optional<CompareGroup.CompAssociationCardinality> compRightClassCardinalityResult) {
    this.compRightClassCardinalityResult = compRightClassCardinalityResult;
  }

  public Optional<CompareGroup.WhichPartDiff> getWhichPartDiff() {
    return whichPartDiff;
  }

  public void setWhichPartDiff(Optional<CompareGroup.WhichPartDiff> whichPartDiff) {
    this.whichPartDiff = whichPartDiff;
  }

  public DiffAssociation getOriginalElement() {
    return originalElement;
  }
}
