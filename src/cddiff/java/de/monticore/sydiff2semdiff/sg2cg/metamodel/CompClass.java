package de.monticore.sydiff2semdiff.sg2cg.metamodel;

import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;

import java.util.*;

import static de.monticore.sydiff2semdiff.sg2cg.CompareHelper.getCompClassKindHelper;
import static de.monticore.sydiff2semdiff.sg2cg.CompareHelper.getCompClassKindStrHelper;

/**
 * Each SupportClass in based SupportGroup will generate one corresponding CompClass
 *
 * @attribute compId:
 *    unique compare id
 * @attribute originalElement:
 *    original SupportClass
 * @attribute isInCompareSG:
 *    whether this SupportClass exists in compared SupportGroup (only check SupportClass name)
 * @attribute isContentDiff:
 *    if this SupportClass exists in compared SupportGroup (only check SupportClass name),
 *    then check whether the content of those two SupportClasses are different
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
  protected SupportClass originalElement;
  protected final boolean isInCompareSG;
  protected final boolean isContentDiff;
  protected final CompareGroup.CompClassCategory compCategory;
  protected Optional<List<String>> whichAttributesDiff;

  public CompClass(SupportClass originalElement, boolean isInCompareSG, boolean isContentDiff, CompareGroup.CompClassCategory compCategory) {
    this.originalElement = originalElement;
    this.isInCompareSG = isInCompareSG;
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
    return getCompClassKindHelper(this.originalElement.getSupportKind());
  }

  public boolean isInCompareSG() {
    return isInCompareSG;
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

  public SupportClass getOriginalElement() {
    return originalElement;
  }
}
