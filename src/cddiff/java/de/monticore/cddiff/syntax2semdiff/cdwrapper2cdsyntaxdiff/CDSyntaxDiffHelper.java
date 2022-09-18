package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;

public class CDSyntaxDiffHelper {

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * get the corresponding CDDiff kind for class by cDTypeWrapperKind
   */
  public static CDTypeDiffKind getCDTypeDiffKindHelper(
      CDTypeWrapperKind cDTypeWrapperKind) {
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

  /**
   * get the corresponding prefix CDTypeDiff name by cDTypeDiffKind
   */
  public static String getCDTypeDiffKindStrHelper(CDTypeDiffKind cDTypeDiffKind,
      boolean is4Print) {
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
  public static List<String> cDTypeDiffWhichAttributesDiffHelper(CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompare) {
    if (optCompare.isEmpty()) {
      return new ArrayList<>(base.getAttributes().keySet());
    }
    else {
      CDTypeWrapper compare = optCompare.get();
      List<String> attributesDiffList = new ArrayList<>();
      // check each attributes
      base.getAttributes().forEach((attrName, attrType) -> {
        // check attributes name
        if (compare.getAttributes().containsKey(attrName)) {
          // check attributes type
          if (!base.getCDWrapperKind().equals(CDTypeWrapperKind.CDWRAPPER_ENUM)) {
            if (!attrType.equals(compare.getAttributes().get(attrName))) {
              // edited
              attributesDiffList.add(attrName);
            }
          }
        }
        else {
          attributesDiffList.add(attrName);
        }
      });
      return attributesDiffList;
    }
  }

  /**
   * return the CDTypeDiff category that helps to determine if there is a semantic difference
   */
  public static CDTypeDiffCategory cDTypeDiffCategoryHelper(CDTypeWrapper base,
      CDTypeWrapper compare, boolean isContentDiff) {
    // check whether attributes in BaseCDTypeWrapper are the subset of attributes in
    // Compare CDTypeWrapper
    if (!isContentDiff) {
      if (compare.getAttributes().keySet().containsAll(base.getAttributes().keySet())
          && compare.getAttributes().size() > base.getAttributes().size()) {
        return CDTypeDiffCategory.SUBSET;
      }
      else {
        return CDTypeDiffCategory.ORIGINAL;
      }
    }
    else {
      return CDTypeDiffCategory.EDITED;
    }
  }

  /**
   * helper for creating CDTypeDiff without attributesDiffList
   */
  public static CDTypeDiff createCDTypeDiffHelper(CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompareClass,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareCDW,
      boolean isContentDiff, CDTypeDiffCategory category) {
    CDTypeDiff cDTypeDiff =
        new CDTypeDiff(base, optCompareClass, optCompareAssoc, isInCompareCDW, isContentDiff,
            category);
    cDTypeDiff.setWhichAttributesDiff(Optional.empty());
    return cDTypeDiff;
  }

  /**
   * helper for creating CDTypeDiff with attributesDiffList
   */
  public static CDTypeDiff createCDTypeDiffHelper(CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompareClass,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareCDW,
      boolean isContentDiff, CDTypeDiffCategory category,
      List<String> attributesDiffList) {
    CDTypeDiff cDTypeDiff =
        createCDTypeDiffHelper(base, optCompareClass, optCompareAssoc, isInCompareCDW,
            isContentDiff, category);
    cDTypeDiff.setWhichAttributesDiff(Optional.of(attributesDiffList));
    return cDTypeDiff;
  }

  /**
   * check equivalence between given Superclasses s1 and s2
   * "Abstract Class", "Interface" and "Class" are the same in this process
   * only check the inheritance path
   */
  public static boolean checkEquivalence4Superclasses(Set<String> s1, Set<String> s2) {

    return s1.stream().map(e -> e.split("_")[1]).collect(Collectors.toSet())
        .equals(s2.stream().map(e -> e.split("_")[1]).collect(Collectors.toSet()));
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create check list for association in compareCDW
   */
  public static void createCheckList4AssocInCompareCDW(
      CDWrapper compareCDW,
      Map<CDAssociationWrapper, Boolean> checkList4AssocInCompareCDW) {
    compareCDW.getCDAssociationWrapperGroupOnlyWithStatusOPEN().forEach((assocName, assoc) -> {
      if (assoc.getCDWrapperKind() == CDAssociationWrapperKind.CDWRAPPER_ASC) {
        checkList4AssocInCompareCDW.put(assoc, false);
      }
    });
  }

  /**
   * update checkList4AssocInCompareCDW
   */
  public static void updateCheckList4AssocInCompareCDW(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      Map<CDAssociationWrapper, Boolean> checkList4AssocInCompareCDW) {
    baseCDW.getCDAssociationWrapperGroup().forEach((assocName, baseCDAssociationWrapper) -> {

      CDAssociationWrapper intersectedBaseCDAssociationWrapper =
          intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
              baseCDAssociationWrapper, baseCDW);

      // get all associations including reversed association in CompareSG
      // by matching [leftClass], [leftRoleName], [rightRoleName], [rightClass]
      List<CDAssociationWrapperPack> DiffAssocMapInCompareSG =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
              compareCDW.getCDAssociationWrapperGroupOnlyWithStatusOPEN(), intersectedBaseCDAssociationWrapper);

      List<CDAssociationWrapper> forwardDiffAssocListInCompareSG = new ArrayList<>();
      List<CDAssociationWrapper> reverseDiffAssocListInCompareSG = new ArrayList<>();
      DiffAssocMapInCompareSG.forEach(e -> {
        if (!e.isReverse()) {
          forwardDiffAssocListInCompareSG.add(e.getCDAssociationWrapper());
        }
        else {
          reverseDiffAssocListInCompareSG.add(e.getCDAssociationWrapper());
        }
      });

      boolean isInCompareSG4ForwardAssocName = forwardDiffAssocListInCompareSG.size() > 0;
      boolean isInCompareSG4ReverseAssocName = reverseDiffAssocListInCompareSG.size() > 0;

      if (isInCompareSG4ForwardAssocName && !isInCompareSG4ReverseAssocName) {
        forwardDiffAssocListInCompareSG.forEach(compareCDAssociationWrapper -> {

          // change the flag of current compareCDAssociationWrapper to True (is used)
          if (compareCDAssociationWrapper.getCDWrapperKind()
              == CDAssociationWrapperKind.CDWRAPPER_ASC) {
            // change CDWrapperKind to ensure display this inherited assoc
            if (baseCDAssociationWrapper.getCDWrapperKind()
                == CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC) {
              baseCDAssociationWrapper.setCDWrapperKind(
                  CDAssociationWrapperKind.CDWRAPPER_INHERIT_DISPLAY_ASC);
            }
            checkList4AssocInCompareCDW.put(compareCDAssociationWrapper, true);
          }
        });
      }
      if (!isInCompareSG4ForwardAssocName && isInCompareSG4ReverseAssocName) {
        reverseDiffAssocListInCompareSG.forEach(compareCDAssociationWrapper -> {

          // change the flag of current compareCDAssociationWrapper to True (is used)
          if (compareCDAssociationWrapper.getCDWrapperKind()
              == CDAssociationWrapperKind.CDWRAPPER_ASC) {
            // change CDWrapperKind to ensure display this inherited assoc
            if (baseCDAssociationWrapper.getCDWrapperKind()
                == CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC) {
              baseCDAssociationWrapper.setCDWrapperKind(
                  CDAssociationWrapperKind.CDWRAPPER_INHERIT_DISPLAY_ASC);
            }
            checkList4AssocInCompareCDW.put(compareCDAssociationWrapper, true);
          }
        });
      }
    });
  }

  /**
   * return the result for cardinality of association after comparison
   * between base CDAssociationWrapper and compare CDAssociationWrapper
   */
  public static CDAssociationDiffCardinality cDAssociationDiffCardinalityHelper(
      CDAssociationWrapperCardinality baseCDAssociationWrapperCardinality,
      CDAssociationWrapperCardinality compareCDAssociationWrapperCardinality) {
    switch (baseCDAssociationWrapperCardinality) {
      case ONE:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDAssociationDiffCardinality.NONE;
          case ZERO_TO_ONE:
            return CDAssociationDiffCardinality.NONE;
          case ONE_TO_MORE:
            return CDAssociationDiffCardinality.NONE;
          default:
            return CDAssociationDiffCardinality.NONE;
        }
      case ZERO_TO_ONE:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDAssociationDiffCardinality.ZERO;
          case ZERO_TO_ONE:
            return CDAssociationDiffCardinality.NONE;
          case ONE_TO_MORE:
            return CDAssociationDiffCardinality.ZERO;
          default:
            return CDAssociationDiffCardinality.NONE;
        }
      case ONE_TO_MORE:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDAssociationDiffCardinality.TWO_TO_MORE;
          case ZERO_TO_ONE:
            return CDAssociationDiffCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CDAssociationDiffCardinality.NONE;
          default:
            return CDAssociationDiffCardinality.NONE;
        }
      default:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDAssociationDiffCardinality.ZERO_AND_TWO_TO_MORE;
          case ZERO_TO_ONE:
            return CDAssociationDiffCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CDAssociationDiffCardinality.ZERO;
          default:
            return CDAssociationDiffCardinality.NONE;
        }
    }
  }

  /**
   * return the result for direction of association after comparison
   * between base CDAssociationWrapper and compare CDAssociationWrapper
   */
  public static CDAssociationDiffDirection cDAssociationDiffDirectionHelper(
      CDAssociationWrapperDirection baseDirection,
      CDAssociationWrapperDirection compareDirection) {
    switch (baseDirection) {
      case LEFT_TO_RIGHT:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDAssociationDiffDirection.NONE;
          case RIGHT_TO_LEFT:
            return CDAssociationDiffDirection.LEFT_TO_RIGHT;
          case BIDIRECTIONAL:
            return CDAssociationDiffDirection.LEFT_TO_RIGHT;
          default:
            return CDAssociationDiffDirection.NONE;
        }
      case RIGHT_TO_LEFT:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDAssociationDiffDirection.RIGHT_TO_LEFT;
          case RIGHT_TO_LEFT:
            return CDAssociationDiffDirection.NONE;
          case BIDIRECTIONAL:
            return CDAssociationDiffDirection.RIGHT_TO_LEFT;
          default:
            return CDAssociationDiffDirection.NONE;
        }
      case BIDIRECTIONAL:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDAssociationDiffDirection.BIDIRECTIONAL;
          case RIGHT_TO_LEFT:
            return CDAssociationDiffDirection.BIDIRECTIONAL;
          case BIDIRECTIONAL:
            return CDAssociationDiffDirection.NONE;
          default:
            return CDAssociationDiffDirection.NONE;
        }
      default:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDAssociationDiffDirection.RIGHT_TO_LEFT;
          case RIGHT_TO_LEFT:
            return CDAssociationDiffDirection.LEFT_TO_RIGHT;
          case BIDIRECTIONAL:
            return CDAssociationDiffDirection.LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT;
          default:
            return CDAssociationDiffDirection.NONE;
        }
    }
  }

  /**
   * return the CDAssociationDiff category that helps to determine if there is a semantic
   * difference for direction
   */
  public static CDAssociationDiffCategory cDAssociationDiffCategoryByDirectionHelper(
      boolean isDirectionChanged, boolean isAssocNameExchanged,
      CDAssociationDiffDirection directionResult) {
    if (isDirectionChanged) {
      // check directionResult
      if (directionResult == CDAssociationDiffDirection.NONE) {
        return CDAssociationDiffCategory.DIRECTION_SUBSET;
      }
      return CDAssociationDiffCategory.DIRECTION_CHANGED;
    }
    else {
      if (isAssocNameExchanged) {
        return CDAssociationDiffCategory.DIRECTION_CHANGED_BUT_SAME_MEANING;
      }
      else {
        return CDAssociationDiffCategory.ORIGINAL;
      }
    }
  }

  /**
   * return the CDAssociationDiff category that helps to determine if there is a semantic
   * difference for cardinality
   */
  public static CDAssociationDiffCategory cDAssociationDiffCategoryByCardinalityHelper(
      boolean isCardinalityDiff, CDAssociationDiffCardinality cardinalityResult) {
    if (isCardinalityDiff) {
      // check cardinalityResult
      if (cardinalityResult == CDAssociationDiffCardinality.NONE) {
        return CDAssociationDiffCategory.CARDINALITY_SUBSET;
      }
      return CDAssociationDiffCategory.CARDINALITY_CHANGED;
    }
    else {
      return CDAssociationDiffCategory.ORIGINAL;
    }
  }

  /**
   * get the corresponding CDDiff kind for association by cDAssociationWrapperKind
   */
  public static CDAssociationDiffKind getCDAssociationDiffKindHelper(
      CDAssociationWrapperKind cDAssociationWrapperKind) {
    switch (cDAssociationWrapperKind) {
      case CDWRAPPER_ASC:
        return CDAssociationDiffKind.CDDIFF_ASC;
      case CDWRAPPER_INHERIT_ASC:
        return CDAssociationDiffKind.CDDIFF_INHERIT_ASC;
      case CDWRAPPER_INHERIT_DISPLAY_ASC:
        return CDAssociationDiffKind.CDDIFF_INHERIT_DISPLAY_ASC;
      default:
        return null;
    }
  }

  /**
   * helper for creating CDAssociationDiff without whichPartDiff and the result after comparison
   */
  public static CDAssociationDiff createCDAssociationDiffHelper(
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory category) {
    CDAssociationDiff cDAssociationDiff =
        new CDAssociationDiff(base, optCompare, isInCompareCDW, isContentDiff, category);
    cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
    cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
    cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
    cDAssociationDiff.setWhichPartDiff(Optional.empty());
    cDAssociationDiff.setLeftInstanceClass(Optional.empty());
    cDAssociationDiff.setRightInstanceClass(Optional.empty());
    return cDAssociationDiff;
  }

  /**
   * helper for creating CDAssociationDiff with whichPartDiff and the result after comparison
   */
  public static CDAssociationDiff createCDAssociationDiffHelper(
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory category,
      Optional<WhichPartDiff> whichPartDiff,
      Optional<Object> compResult) {
    CDAssociationDiff cDAssociationDiff =
        createCDAssociationDiffHelper(base, optCompare, isInCompareCDW, isContentDiff, category);
    cDAssociationDiff.setWhichPartDiff(whichPartDiff);

    if (whichPartDiff.isPresent() && compResult.isPresent()) {
      switch (whichPartDiff.get()) {
        case DIRECTION:
          cDAssociationDiff.setCDDiffDirectionResult(
              Optional.of((CDAssociationDiffDirection) compResult.get()));
          cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
          cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
          break;
        case LEFT_CARDINALITY:
        case RIGHT_SPECIAL_CARDINALITY:
          cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
          cDAssociationDiff.setCDDiffLeftClassCardinalityResult(
              Optional.of((CDAssociationDiffCardinality) compResult.get()));
          cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
          break;
        case RIGHT_CARDINALITY:
        case LEFT_SPECIAL_CARDINALITY:
          cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
          cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
          cDAssociationDiff.setCDDiffRightClassCardinalityResult(
              Optional.of((CDAssociationDiffCardinality) compResult.get()));
          break;
      }
    }
    return cDAssociationDiff;
  }

  public static CDAssociationDiff createCDAssociationDiffHelperWithInstanceClass(
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory category,
      Optional<CDTypeWrapper> leftInstanceClass,
      Optional<CDTypeWrapper> rightInstanceClass) {
    CDAssociationDiff cDAssociationDiff =
        new CDAssociationDiff(base, optCompare, isInCompareCDW, isContentDiff, category);
    cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
    cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
    cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
    cDAssociationDiff.setWhichPartDiff(Optional.empty());
    cDAssociationDiff.setLeftInstanceClass(leftInstanceClass);
    cDAssociationDiff.setRightInstanceClass(rightInstanceClass);
    return cDAssociationDiff;
  }

}
