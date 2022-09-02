package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperPack;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDAssociationDiff;

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
  public static CDSyntaxDiff.CDTypeDiffKind getCDTypeDiffKindHelper(
      CDWrapper.CDTypeWrapperKind cDTypeWrapperKind) {
    switch (cDTypeWrapperKind) {
      case CDWRAPPER_CLASS:
        return CDSyntaxDiff.CDTypeDiffKind.CDDIFF_CLASS;
      case CDWRAPPER_ENUM:
        return CDSyntaxDiff.CDTypeDiffKind.CDDIFF_ENUM;
      case CDWRAPPER_ABSTRACT_CLASS:
        return CDSyntaxDiff.CDTypeDiffKind.CDDIFF_ABSTRACT_CLASS;
      case CDWRAPPER_INTERFACE:
        return CDSyntaxDiff.CDTypeDiffKind.CDDIFF_INTERFACE;
      default:
        return null;
    }
  }

  /**
   * get the corresponding prefix CDTypeDiff name by cDTypeDiffKind
   */
  public static String getCDTypeDiffKindStrHelper(CDSyntaxDiff.CDTypeDiffKind cDTypeDiffKind,
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
          if (!base.getCDWrapperKind().equals(CDWrapper.CDTypeWrapperKind.CDWRAPPER_ENUM)) {
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
  public static CDSyntaxDiff.CDTypeDiffCategory cDTypeDiffCategoryHelper(CDTypeWrapper base,
      CDTypeWrapper compare, boolean isContentDiff) {
    // check whether attributes in BaseCDTypeWrapper are the subset of attributes in
    // Compare CDTypeWrapper
    if (!isContentDiff) {
      if (compare.getAttributes().keySet().containsAll(base.getAttributes().keySet())
          && compare.getAttributes().size() > base.getAttributes().size()) {
        return CDSyntaxDiff.CDTypeDiffCategory.SUBSET;
      }
      else {
        return CDSyntaxDiff.CDTypeDiffCategory.ORIGINAL;
      }
    }
    else {
      return CDSyntaxDiff.CDTypeDiffCategory.EDITED;
    }
  }

  /**
   * helper for creating CDTypeDiff without attributesDiffList
   */
  public static CDTypeDiff createCDTypeDiffHelper(CDTypeWrapper base, boolean isInCompareCDW,
      boolean isContentDiff, CDSyntaxDiff.CDTypeDiffCategory category) {
    CDTypeDiff cDTypeDiff = new CDTypeDiff(base, isInCompareCDW, isContentDiff, category);
    cDTypeDiff.setWhichAttributesDiff(Optional.empty());
    return cDTypeDiff;
  }

  /**
   * helper for creating CDTypeDiff with attributesDiffList
   */
  public static CDTypeDiff createCDTypeDiffHelper(CDTypeWrapper base, boolean isInCompareCDW,
      boolean isContentDiff, CDSyntaxDiff.CDTypeDiffCategory category,
      List<String> attributesDiffList) {
    CDTypeDiff cDTypeDiff = createCDTypeDiffHelper(base, isInCompareCDW, isContentDiff, category);
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
      if (assoc.getCDWrapperKind() == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_ASC) {
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
      // by matching [leftClass], [rightClass]
      List<CDAssociationWrapperPack> DiffAssocMapInCompareSG =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndRoleName(
              compareCDW.getCDAssociationWrapperGroup(), intersectedBaseCDAssociationWrapper);
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
              == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_ASC) {
            // change CDWrapperKind to ensure display this inherited assoc
            if (baseCDAssociationWrapper.getCDWrapperKind()
                == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC) {
              baseCDAssociationWrapper.setCDWrapperKind(
                  CDWrapper.CDAssociationWrapperKind.CDWRAPPER_INHERIT_DISPLAY_ASC);
            }
            checkList4AssocInCompareCDW.put(compareCDAssociationWrapper, true);
          }
        });
      }
      if (!isInCompareSG4ForwardAssocName && isInCompareSG4ReverseAssocName) {
        reverseDiffAssocListInCompareSG.forEach(compareCDAssociationWrapper -> {

          // change the flag of current compareCDAssociationWrapper to True (is used)
          if (compareCDAssociationWrapper.getCDWrapperKind()
              == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_ASC) {
            // change CDWrapperKind to ensure display this inherited assoc
            if (baseCDAssociationWrapper.getCDWrapperKind()
                == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC) {
              baseCDAssociationWrapper.setCDWrapperKind(
                  CDWrapper.CDAssociationWrapperKind.CDWRAPPER_INHERIT_DISPLAY_ASC);
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
  public static CDSyntaxDiff.CDAssociationDiffCardinality cDAssociationDiffCardinalityHelper(
      CDWrapper.CDAssociationWrapperCardinality baseCDAssociationWrapperCardinality,
      CDWrapper.CDAssociationWrapperCardinality compareCDAssociationWrapperCardinality) {
    switch (baseCDAssociationWrapperCardinality) {
      case ONE:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
          case ZERO_TO_ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
          case ONE_TO_MORE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
          default:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
        }
      case ZERO_TO_ONE:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.ZERO;
          case ZERO_TO_ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
          case ONE_TO_MORE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.ZERO;
          default:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
        }
      case ONE_TO_MORE:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE;
          case ZERO_TO_ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
          default:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
        }
      default:
        switch (compareCDAssociationWrapperCardinality) {
          case ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.ZERO_AND_TWO_TO_MORE;
          case ZERO_TO_ONE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CDSyntaxDiff.CDAssociationDiffCardinality.ZERO;
          default:
            return CDSyntaxDiff.CDAssociationDiffCardinality.NONE;
        }
    }
  }

  /**
   * return the result for direction of association after comparison
   * between base CDAssociationWrapper and compare CDAssociationWrapper
   */
  public static CDSyntaxDiff.CDAssociationDiffDirection cDAssociationDiffDirectionHelper(
      CDWrapper.CDAssociationWrapperDirection baseDirection,
      CDWrapper.CDAssociationWrapperDirection compareDirection) {
    switch (baseDirection) {
      case LEFT_TO_RIGHT:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
          case RIGHT_TO_LEFT:
            return CDSyntaxDiff.CDAssociationDiffDirection.LEFT_TO_RIGHT;
          case BIDIRECTIONAL:
            return CDSyntaxDiff.CDAssociationDiffDirection.LEFT_TO_RIGHT;
          default:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
        }
      case RIGHT_TO_LEFT:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDSyntaxDiff.CDAssociationDiffDirection.RIGHT_TO_LEFT;
          case RIGHT_TO_LEFT:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
          case BIDIRECTIONAL:
            return CDSyntaxDiff.CDAssociationDiffDirection.RIGHT_TO_LEFT;
          default:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
        }
      case BIDIRECTIONAL:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDSyntaxDiff.CDAssociationDiffDirection.BIDIRECTIONAL;
          case RIGHT_TO_LEFT:
            return CDSyntaxDiff.CDAssociationDiffDirection.BIDIRECTIONAL;
          case BIDIRECTIONAL:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
          default:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
        }
      default:
        switch (compareDirection) {
          case LEFT_TO_RIGHT:
            return CDSyntaxDiff.CDAssociationDiffDirection.RIGHT_TO_LEFT;
          case RIGHT_TO_LEFT:
            return CDSyntaxDiff.CDAssociationDiffDirection.LEFT_TO_RIGHT;
          case BIDIRECTIONAL:
            return CDSyntaxDiff.CDAssociationDiffDirection.LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT;
          default:
            return CDSyntaxDiff.CDAssociationDiffDirection.NONE;
        }
    }
  }

  /**
   * return the CDAssociationDiff category that helps to determine if there is a semantic
   * difference for direction
   */
  public static CDSyntaxDiff.CDAssociationDiffCategory cDAssociationDiffCategoryByDirectionHelper(
      boolean isDirectionChanged, boolean isAssocNameExchanged,
      CDSyntaxDiff.CDAssociationDiffDirection directionResult) {
    if (isDirectionChanged) {
      // check directionResult
      if (directionResult == CDSyntaxDiff.CDAssociationDiffDirection.NONE) {
        return CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_SUBSET;
      }
      return CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_CHANGED;
    }
    else {
      if (isAssocNameExchanged) {
        return CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_CHANGED_BUT_SAME_MEANING;
      }
      else {
        return CDSyntaxDiff.CDAssociationDiffCategory.ORIGINAL;
      }
    }
  }

  /**
   * return the CDAssociationDiff category that helps to determine if there is a semantic
   * difference for cardinality
   */
  public static CDSyntaxDiff.CDAssociationDiffCategory cDAssociationDiffCategoryByCardinalityHelper(
      boolean isCardinalityDiff, CDSyntaxDiff.CDAssociationDiffCardinality cardinalityResult) {
    if (isCardinalityDiff) {
      // check cardinalityResult
      if (cardinalityResult == CDSyntaxDiff.CDAssociationDiffCardinality.NONE) {
        return CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_SUBSET;
      }
      return CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_CHANGED;
    }
    else {
      return CDSyntaxDiff.CDAssociationDiffCategory.ORIGINAL;
    }
  }

  /**
   * get the corresponding CDDiff kind for association by cDAssociationWrapperKind
   */
  public static CDSyntaxDiff.CDAssociationDiffKind getCDAssociationDiffKindHelper(
      CDWrapper.CDAssociationWrapperKind cDAssociationWrapperKind) {
    switch (cDAssociationWrapperKind) {
      case CDWRAPPER_ASC:
        return CDSyntaxDiff.CDAssociationDiffKind.CDDIFF_ASC;
      case CDWRAPPER_INHERIT_ASC:
        return CDSyntaxDiff.CDAssociationDiffKind.CDDIFF_INHERIT_ASC;
      case CDWRAPPER_INHERIT_DISPLAY_ASC:
        return CDSyntaxDiff.CDAssociationDiffKind.CDDIFF_INHERIT_DISPLAY_ASC;
      default:
        return null;
    }
  }

  /**
   * helper for creating CDAssociationDiff without whichPartDiff and the result after comparison
   */
  public static CDAssociationDiff createCDAssociationDiffHelper(
      CDAssociationWrapper base,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDSyntaxDiff.CDAssociationDiffCategory category) {
    CDAssociationDiff cDAssociationDiff =
        new CDAssociationDiff(base, isInCompareCDW, isContentDiff, category);
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
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDSyntaxDiff.CDAssociationDiffCategory category,
      Optional<CDSyntaxDiff.WhichPartDiff> whichPartDiff,
      Optional<Object> compResult) {
    CDAssociationDiff cDAssociationDiff = createCDAssociationDiffHelper(base, isInCompareCDW,
        isContentDiff, category);
    cDAssociationDiff.setWhichPartDiff(whichPartDiff);

    if (whichPartDiff.isPresent() && compResult.isPresent()) {
      switch (whichPartDiff.get()) {
        case DIRECTION:
          cDAssociationDiff.setCDDiffDirectionResult(
              Optional.of((CDSyntaxDiff.CDAssociationDiffDirection) compResult.get()));
          cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
          cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
          break;
        case LEFT_CARDINALITY:
          cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
          cDAssociationDiff.setCDDiffLeftClassCardinalityResult(
              Optional.of((CDSyntaxDiff.CDAssociationDiffCardinality) compResult.get()));
          cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
          break;
        case RIGHT_CARDINALITY:
          cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
          cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
          cDAssociationDiff.setCDDiffRightClassCardinalityResult(
              Optional.of((CDSyntaxDiff.CDAssociationDiffCardinality) compResult.get()));
          break;
      }
    }
    return cDAssociationDiff;
  }

  public static CDAssociationDiff createCDAssociationDiffHelperWithInstanceClass(
      CDAssociationWrapper base,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDSyntaxDiff.CDAssociationDiffCategory category,
      Optional<CDTypeWrapper> leftInstanceClass,
      Optional<CDTypeWrapper> rightInstanceClass) {
    CDAssociationDiff cDAssociationDiff =
        new CDAssociationDiff(base, isInCompareCDW, isContentDiff, category);
    cDAssociationDiff.setCDDiffDirectionResult(Optional.empty());
    cDAssociationDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
    cDAssociationDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
    cDAssociationDiff.setWhichPartDiff(Optional.empty());
    cDAssociationDiff.setLeftInstanceClass(leftInstanceClass);
    cDAssociationDiff.setRightInstanceClass(rightInstanceClass);
    return cDAssociationDiff;
  }

}
