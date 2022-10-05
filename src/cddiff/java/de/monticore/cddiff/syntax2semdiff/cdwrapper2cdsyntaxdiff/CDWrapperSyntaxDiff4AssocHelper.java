package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;

import java.util.*;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4SearchHelper.*;

public class CDWrapperSyntaxDiff4AssocHelper {

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
   * return the CDAssocWrapperDiff category that helps to determine if there is a semantic
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
   * return the CDAssocWrapperDiff category that helps to determine if there is a semantic
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
   * helper for creating CDAssocWrapperDiff without whichPartDiff and the result after comparison
   */
  public static CDAssocWrapperDiff createCDAssociationDiffHelper(
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory category) {
    CDAssocWrapperDiff cDAssocWrapperDiff =
        new CDAssocWrapperDiff(base, optCompare, isInCompareCDW, isContentDiff, category);
    cDAssocWrapperDiff.setCDDiffDirectionResult(Optional.empty());
    cDAssocWrapperDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
    cDAssocWrapperDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
    cDAssocWrapperDiff.setWhichPartDiff(Optional.empty());
    cDAssocWrapperDiff.setLeftInstanceClass(Optional.empty());
    cDAssocWrapperDiff.setRightInstanceClass(Optional.empty());
    return cDAssocWrapperDiff;
  }

  /**
   * helper for creating CDAssocWrapperDiff with whichPartDiff and the result after comparison
   */
  public static CDAssocWrapperDiff createCDAssociationDiffHelper(
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory category,
      Optional<WhichPartDiff> whichPartDiff,
      Optional<Object> compResult) {
    CDAssocWrapperDiff cDAssocWrapperDiff =
        createCDAssociationDiffHelper(base, optCompare, isInCompareCDW, isContentDiff, category);
    cDAssocWrapperDiff.setWhichPartDiff(whichPartDiff);

    if (whichPartDiff.isPresent() && compResult.isPresent()) {
      switch (whichPartDiff.get()) {
        case DIRECTION:
          cDAssocWrapperDiff.setCDDiffDirectionResult(
              Optional.of((CDAssociationDiffDirection) compResult.get()));
          cDAssocWrapperDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
          cDAssocWrapperDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
          break;
        case LEFT_CARDINALITY:
        case RIGHT_SPECIAL_CARDINALITY:
          cDAssocWrapperDiff.setCDDiffDirectionResult(Optional.empty());
          cDAssocWrapperDiff.setCDDiffLeftClassCardinalityResult(
              Optional.of((CDAssociationDiffCardinality) compResult.get()));
          cDAssocWrapperDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
          break;
        case RIGHT_CARDINALITY:
        case LEFT_SPECIAL_CARDINALITY:
          cDAssocWrapperDiff.setCDDiffDirectionResult(Optional.empty());
          cDAssocWrapperDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
          cDAssocWrapperDiff.setCDDiffRightClassCardinalityResult(
              Optional.of((CDAssociationDiffCardinality) compResult.get()));
          break;
      }
    }
    return cDAssocWrapperDiff;
  }

  public static CDAssocWrapperDiff createCDAssociationDiffHelperWithInstanceClass(
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareCDW,
      boolean isContentDiff,
      CDAssociationDiffCategory category,
      Optional<CDTypeWrapper> leftInstanceClass,
      Optional<CDTypeWrapper> rightInstanceClass) {
    CDAssocWrapperDiff cDAssocWrapperDiff =
        new CDAssocWrapperDiff(base, optCompare, isInCompareCDW, isContentDiff, category);
    cDAssocWrapperDiff.setCDDiffDirectionResult(Optional.empty());
    cDAssocWrapperDiff.setCDDiffLeftClassCardinalityResult(Optional.empty());
    cDAssocWrapperDiff.setCDDiffRightClassCardinalityResult(Optional.empty());
    cDAssocWrapperDiff.setWhichPartDiff(Optional.empty());
    cDAssocWrapperDiff.setLeftInstanceClass(leftInstanceClass);
    cDAssocWrapperDiff.setRightInstanceClass(rightInstanceClass);
    return cDAssocWrapperDiff;
  }

}
