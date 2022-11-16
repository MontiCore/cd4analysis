package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapperKind;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeDiffCategory;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeDiffKind;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeWrapperDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CDWrapperSyntaxDiff4TypeHelper {

  /** get the corresponding CDDiff kind for class by cDTypeWrapperKind */
  public static CDTypeDiffKind getCDTypeDiffKindHelper(CDTypeWrapperKind cDTypeWrapperKind) {
    switch (cDTypeWrapperKind) {
      case CDWRAPPER_CLASS:
        return CDTypeDiffKind.CDDIFF_CLASS;
      case CDWRAPPER_ENUM:
        return CDTypeDiffKind.CDDIFF_ENUM;
      case CDWRAPPER_ABSTRACT_CLASS:
        return CDTypeDiffKind.CDDIFF_ABSTRACT_CLASS;
      case CDWRAPPER_INTERFACE:
        return CDTypeDiffKind.CDDIFF_INTERFACE;
      default:
        return null;
    }
  }

  /** get the corresponding prefix CDTypeWrapperDiff name by cDTypeDiffKind */
  public static String getCDTypeDiffKindStrHelper(CDTypeDiffKind cDTypeDiffKind, boolean is4Print) {
    switch (cDTypeDiffKind) {
      case CDDIFF_CLASS:
        return is4Print ? "Class" : "CDDiffClass";
      case CDDIFF_ENUM:
        return is4Print ? "Enum" : "CDDiffEnum";
      case CDDIFF_ABSTRACT_CLASS:
        return is4Print ? "AbstractClass" : "CDDiffAbstractClass";
      case CDDIFF_INTERFACE:
        return is4Print ? "Interface" : "CDDiffInterface";
      default:
        return null;
    }
  }

  /**
   * generate the list of which attributes are different between base CDTypeWrapper and compare
   * CDTypeWrapper
   */
  public static List<String> cDTypeDiffWhichAttributesDiffHelper(
      CDTypeWrapper base, Optional<CDTypeWrapper> optCompare) {
    if (optCompare.isEmpty()) {
      return new ArrayList<>(base.getAttributes().keySet());
    } else {
      CDTypeWrapper compare = optCompare.get();
      List<String> attributesDiffList = new ArrayList<>();
      // check each attributes
      base.getAttributes()
          .forEach(
              (attrName, attrType) -> {
                // check attributes name
                if (compare.getAttributes().containsKey(attrName)) {
                  // check attributes type
                  if (!base.getCDWrapperKind().equals(CDTypeWrapperKind.CDWRAPPER_ENUM)) {
                    if (!attrType.equals(compare.getAttributes().get(attrName))) {
                      // edited
                      attributesDiffList.add(attrName);
                    }
                  }
                } else {
                  attributesDiffList.add(attrName);
                }
              });
      return attributesDiffList;
    }
  }

  /**
   * return the CDTypeWrapperDiff category that helps to determine if there is a semantic difference
   */
  public static CDTypeDiffCategory cDTypeDiffCategoryHelper(
      CDTypeWrapper base, CDTypeWrapper compare, boolean isContentDiff) {
    // check whether attributes in BaseCDTypeWrapper are the subset of attributes in
    // Compare CDTypeWrapper
    if (!isContentDiff) {
      if (compare.getAttributes().keySet().containsAll(base.getAttributes().keySet())
          && compare.getAttributes().size() > base.getAttributes().size()) {
        return CDTypeDiffCategory.SUBSET;
      } else {
        return CDTypeDiffCategory.ORIGINAL;
      }
    } else {
      return CDTypeDiffCategory.EDITED;
    }
  }

  /** helper for creating CDTypeWrapperDiff without attributesDiffList */
  public static CDTypeWrapperDiff createCDTypeDiffHelper(
      CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompareClass,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDTypeDiffCategory category) {
    CDTypeWrapperDiff cDTypeWrapperDiff =
        new CDTypeWrapperDiff(
            base, optCompareClass, optCompareAssoc, isInCompareCDW, isContentDiff, category);
    cDTypeWrapperDiff.setWhichAttributesDiff(Optional.empty());
    return cDTypeWrapperDiff;
  }

  /** helper for creating CDTypeWrapperDiff with attributesDiffList */
  public static CDTypeWrapperDiff createCDTypeDiffHelper(
      CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompareClass,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDTypeDiffCategory category,
      List<String> attributesDiffList) {
    CDTypeWrapperDiff cDTypeWrapperDiff =
        createCDTypeDiffHelper(
            base, optCompareClass, optCompareAssoc, isInCompareCDW, isContentDiff, category);
    cDTypeWrapperDiff.setWhichAttributesDiff(Optional.of(attributesDiffList));
    return cDTypeWrapperDiff;
  }

  /**
   * check equivalence between given Superclasses s1 and s2 "Abstract Class", "Interface" and
   * "Class" are the same in this process only check the inheritance path
   */
  public static boolean checkEquivalence4Superclasses(Set<String> s1, Set<String> s2) {
    return s1.stream()
        .map(e -> e.split("_")[1])
        .collect(Collectors.toSet())
        .equals(s2.stream().map(e -> e.split("_")[1]).collect(Collectors.toSet()));
  }
}
