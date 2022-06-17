package de.monticore.sydiff2semdiff.dg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;

import java.util.*;

import static de.monticore.sydiff2semdiff.dg2cg.CompareHelper.getCompClassKindHelper;
import static de.monticore.sydiff2semdiff.dg2cg.CompareHelper.getCompClassKindStrHelper;

/**
 * Each DiffClass in based DifferentGroup will generate one corresponding CompClass
 *
 * @attribute compId:
 *    unique compare id
 * @attribute originalElement:
 *    original DiffClass
 * @attribute isInComparedDG:
 *    whether this DiffClass exists in compared DifferentGroup (only check DiffClass name)
 * @attribute isContentDiff:
 *    if this DiffClass exists in compared DifferentGroup (only check DiffClass name),
 *    then check whether the content of those two DiffClasses are different
 * @attribute compCategory:
 *    - has semantic difference:
 *        EDITED, DELETED
 *    - has no semantic difference:
 *        ORIGINAL, SUBSET
 * @attribute whichAttributesDiff:
 *    mark which attribute has syntactic differences
 */
public class CompClass {
  protected final UUID compId;
  protected DiffClass originalElement;
  protected final boolean isInComparedDG;
  protected final boolean isContentDiff;
  protected final CompareGroup.CompClassCategory compCategory;
  protected Optional<List<String>> whichAttributesDiff;

  public CompClass(DiffClass originalElement, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompClassCategory compCategory) {
    this.originalElement = originalElement;
    this.isInComparedDG = isInComparedDG;
    this.isContentDiff = isContentDiff;
    this.compCategory = compCategory;
    this.compId = UUID.randomUUID();
  }

  public UUID getCompId() {
    return compId;
  }

  public String getName() {
    return getCompClassKindStrHelper(getCompKind()) + "_" + this.originalElement.getOriginalClassName();
  }

  public CompareGroup.CompClassKind getCompKind() {
    return getCompClassKindHelper(this.originalElement.getDiffKind());
  }

  public boolean isInComparedDG() {
    return isInComparedDG;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CompareGroup.CompClassCategory getCompCategory() {
    return compCategory;
  }

  public Optional<List<String>> getWhichAttributesDiff() {
    return whichAttributesDiff;
  }

  public void setWhichAttributesDiff(Optional<List<String>> whichAttributesDiff) {
    this.whichAttributesDiff = whichAttributesDiff;
  }

  public DiffClass getOriginalElement() {
    return originalElement;
  }
}
