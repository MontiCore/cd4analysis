package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

import java.util.*;

import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDSyntaxDiffHelper.getCDTypeDiffKindHelper;
import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDSyntaxDiffHelper.getCDTypeDiffKindStrHelper;

/**
 * Each CDTypeWrapper in based CDWrapper will generate one corresponding CDTypeDiff
 *
 * @attribute cDDiffId:
 *    unique cDDiff id
 * @attribute originalElement:
 *    original CDTypeWrapper
 * @attribute isInCompareCDW:
 *    whether this CDTypeWrapper exists in compared CDWrapper (only check CDTypeWrapper name)
 * @attribute isContentDiff:
 *    if this CDTypeWrapper exists in compared CDWrapper (only check CDTypeWrapper name),
 *    then check whether the content of those two CDTypeWrappers are different
 * @attribute cDDiffCategory:
 *    - has semantic difference:
 *        EDITED, DELETED, FREED
 *    - has no semantic difference:
 *        ORIGINAL, SUBSET
 * @attribute whichAttributesDiff:
 *    mark which attribute has syntactic differences
 */
public class CDTypeDiff {
  protected final UUID cDDiffId;

  protected CDTypeWrapper originalElement;

  protected final boolean isInCompareCDW;

  protected final boolean isContentDiff;

  protected final CDSyntaxDiff.CDTypeDiffCategory cDDiffCategory;

  protected Optional<List<String>> whichAttributesDiff;

  public CDTypeDiff(CDTypeWrapper originalElement, boolean isInCompareCDW, boolean isContentDiff,
      CDSyntaxDiff.CDTypeDiffCategory cDDiffCategory) {
    this.originalElement = originalElement;
    this.isInCompareCDW = isInCompareCDW;
    this.isContentDiff = isContentDiff;
    this.cDDiffCategory = cDDiffCategory;
    this.cDDiffId = UUID.randomUUID();
  }

  public UUID getCDDiffId() {
    return cDDiffId;
  }

  public String getName(boolean is4Print) {
    return getCDTypeDiffKindStrHelper(getCDDiffKind(), is4Print) + "_"
        + this.originalElement.getOriginalClassName();
  }

  public CDSyntaxDiff.CDTypeDiffKind getCDDiffKind() {
    return getCDTypeDiffKindHelper(this.originalElement.getCDWrapperKind());
  }

  public boolean isInCompareCDW() {
    return isInCompareCDW;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CDSyntaxDiff.CDTypeDiffCategory getCDDiffCategory() {
    return cDDiffCategory;
  }

  public Optional<List<String>> getWhichAttributesDiff() {
    return whichAttributesDiff;
  }

  public void setWhichAttributesDiff(Optional<List<String>> whichAttributesDiff) {
    this.whichAttributesDiff = whichAttributesDiff;
  }

  public CDTypeWrapper getOriginalElement() {
    return originalElement;
  }

}
