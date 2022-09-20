package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

import java.util.*;

import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiffHelper.getCDTypeDiffKindHelper;
import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiffHelper.getCDTypeDiffKindStrHelper;

/**
 * Each CDTypeWrapper in based CDWrapper will generate one corresponding CDTypeWrapperDiff
 *
 * @attribute cDDiffId:
 *    unique cDDiff id
 * @attribute baseElement:
 *    base original CDTypeWrapper
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
public class CDTypeWrapperDiff {
  protected final UUID cDDiffId;

  protected CDTypeWrapper baseElement;

  protected Optional<CDTypeWrapper> optCompareClass;

  protected Optional<CDAssociationWrapper> optCompareAssoc;

  protected final boolean isInCompareCDW;

  protected final boolean isContentDiff;

  protected final CDTypeDiffCategory cDDiffCategory;

  protected Optional<List<String>> whichAttributesDiff;

  public CDTypeWrapperDiff(CDTypeWrapper baseElement,
      Optional<CDTypeWrapper> optCompareClass,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDTypeDiffCategory cDDiffCategory) {
    this.baseElement = baseElement;
    this.optCompareClass = optCompareClass;
    this.optCompareAssoc = optCompareAssoc;
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
        + this.baseElement.getOriginalClassName();
  }

  public CDTypeDiffKind getCDDiffKind() {
    return getCDTypeDiffKindHelper(this.baseElement.getCDWrapperKind());
  }

  public boolean isInCompareCDW() {
    return isInCompareCDW;
  }

  public boolean isContentDiff() {
    return isContentDiff;
  }

  public CDTypeDiffCategory getCDDiffCategory() {
    return cDDiffCategory;
  }

  public Optional<List<String>> getWhichAttributesDiff() {
    return whichAttributesDiff;
  }

  public void setWhichAttributesDiff(Optional<List<String>> whichAttributesDiff) {
    this.whichAttributesDiff = whichAttributesDiff;
  }

  public CDTypeWrapper getBaseElement() {
    return baseElement;
  }

  public String getBaseSourcePositionStr() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("l.");
    stringBuilder.append(baseElement.getSourcePosition().getLine());
    stringBuilder.append(", ");
    stringBuilder.append("c.");
    stringBuilder.append(baseElement.getSourcePosition().getColumn());
    return stringBuilder.toString();
  }
  public String getCompareSourcePositionStr() {
    if (this.cDDiffCategory != CDTypeDiffCategory.DELETED) {
      if (optCompareClass.isPresent()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("l.");
        stringBuilder.append(optCompareClass.get().getSourcePosition().getLine());
        stringBuilder.append(", ");
        stringBuilder.append("c.");
        stringBuilder.append(optCompareClass.get().getSourcePosition().getColumn());
        return stringBuilder.toString();
      }
      if (optCompareAssoc.isPresent()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("l.");
        stringBuilder.append(optCompareAssoc.get().getSourcePosition().getLine());
        stringBuilder.append(", ");
        stringBuilder.append("c.");
        stringBuilder.append(optCompareAssoc.get().getSourcePosition().getColumn());
        return stringBuilder.toString();
      }
    }
    return "NULL";
  }

}
