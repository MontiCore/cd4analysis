package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperPack;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDAssocWrapperDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeWrapperDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDWrapperSyntaxDiff;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.GenerateODHelper.mappingCardinality;
import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiffHelper.*;

public class CDWrapperSyntaxDiffGenerator {

  protected Deque<CDTypeWrapperDiff> cDTypeWrapperDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDTypeWrapperDiff> cDTypeWrappeDiffResultQueueWithoutDiff = new LinkedList<>();

  protected Deque<CDAssocWrapperDiff> cDAssocWrapperDiffResultQueueWithoutDiff = new LinkedList<>();

  protected Map<CDAssociationWrapper, Boolean> checkList4AssocInCompareCDW = new HashMap<>();

  /**
   * generating CDWrapperSyntaxDiff
   */
  public CDWrapperSyntaxDiff generateCDSyntaxDiff(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      CDSemantics cdSemantics) {

    CDWrapperSyntaxDiff cDSyntaxDiff = new CDWrapperSyntaxDiff(baseCDW, compareCDW);
    createCheckList4AssocInCompareCDW(compareCDW, checkList4AssocInCompareCDW);
    updateCheckList4AssocInCompareCDW(baseCDW, compareCDW, checkList4AssocInCompareCDW);
    cDAssociationDiffs(baseCDW, compareCDW, cdSemantics);
    cDTypeDiffs(baseCDW, compareCDW, cdSemantics);

    // special CDType that is in the not matched assoc in compareCDW

    cDTypeDiffs4NotMatchedAssocInCompareCDW(baseCDW);

    cDSyntaxDiff.setBaseCDW(baseCDW);
    cDSyntaxDiff.setCompareCDW(compareCDW);
    cDSyntaxDiff.setCDTypeDiffResultQueueWithDiff(cDTypeWrapperDiffResultQueueWithDiff);
    cDSyntaxDiff.setCDAssociationDiffResultQueueWithDiff(cDAssocWrapperDiffResultQueueWithDiff);
    cDSyntaxDiff.setCDTypeDiffResultQueueWithoutDiff(cDTypeWrappeDiffResultQueueWithoutDiff);
    cDSyntaxDiff.setCDAssociationDiffResultQueueWithoutDiff(
        cDAssocWrapperDiffResultQueueWithoutDiff);
    return cDSyntaxDiff;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * create CDTypeWrapperDiff for each CDTypeWrapper in base CDWrapper
   */
  public void cDTypeDiffs(CDWrapper baseCDW, CDWrapper compareCDW, CDSemantics cdSemantics) {
    // general CDType
    baseCDW.getCDTypeWrapperGroup().forEach((className, baseCDTypeWrapper) ->
        cDTypeDiffsHelper4CDSemantics(baseCDW, compareCDW, baseCDTypeWrapper, cdSemantics));
  }

  /**
   * CDTypeDiffsHelper for different CDSemantics
   */
  public void cDTypeDiffsHelper4CDSemantics(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      CDTypeWrapper baseCDTypeWrapper,
      CDSemantics cdSemantics) {

    boolean isInCompareSG = false;
    CDTypeWrapper compareCDTypeWrapper =
        compareCDW.getCDTypeWrapperGroupOnlyWithStatusOPEN().get(baseCDTypeWrapper.getName());

    if (cdSemantics == CDSemantics.SIMPLE_CLOSED_WORLD) {
      isInCompareSG = compareCDW.getCDTypeWrapperGroupOnlyWithStatusOPEN().containsKey(baseCDTypeWrapper.getName());
    } else if (cdSemantics == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD) {
      if (compareCDW.getCDTypeWrapperGroupOnlyWithStatusOPEN().containsKey(baseCDTypeWrapper.getName())) {
        if (checkEquivalence4Superclasses(baseCDTypeWrapper.getSuperclasses(), compareCDTypeWrapper.getSuperclasses())) {
          isInCompareSG = true;
        }
      }
    }

    if (compareCDTypeWrapper == null) {
      createCDTypeDiff(baseCDTypeWrapper, Optional.empty(), isInCompareSG, false);
    } else {
      createCDTypeDiff(baseCDTypeWrapper, Optional.of(compareCDTypeWrapper), isInCompareSG, false);
    }

  }

  /**
   * generate CDTypeWrapperDiff object and
   * put into cDTypeWrappeDiffResultQueueWithDiff if exists semantic difference,
   * put into cDTypeWrappeDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDTypeDiff(
      CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompare,
      boolean isInCompareSG,
      boolean isNotMatchedAssocInCompareCDW) {

    // check whether CDTypeWrapper Name is also in CompareSG
    if (isInCompareSG) {

      // calculate WhichAttributesDiffList
      List<String> attributesDiffList = cDTypeDiffWhichAttributesDiffHelper(base, optCompare);

      // calculate boolean isContentDiff
      boolean isContentDiff = attributesDiffList.size() != 0;

      // calculate class category
      CDWrapperSyntaxDiff.CDTypeDiffCategory category = cDTypeDiffCategoryHelper(base, optCompare.get(),
          isContentDiff);

      // create cDTypeWrapperDiff
      CDTypeWrapperDiff cDTypeWrapperDiff = createCDTypeDiffHelper(base, isInCompareSG, isContentDiff, category,
          attributesDiffList);

      // distinguish cDTypeWrapperDiff by CompCategory
      if (cDTypeWrapperDiff.getCDDiffCategory() == CDWrapperSyntaxDiff.CDTypeDiffCategory.DELETED
          || cDTypeWrapperDiff.getCDDiffCategory() == CDWrapperSyntaxDiff.CDTypeDiffCategory.EDITED) {
        cDTypeWrapperDiffResultQueueWithDiff.offer(cDTypeWrapperDiff);
      }
      else {
        cDTypeWrappeDiffResultQueueWithoutDiff.offer(cDTypeWrapperDiff);
      }

    }
    else {
      if (isNotMatchedAssocInCompareCDW) {
        cDTypeWrapperDiffResultQueueWithDiff.offer(
            createCDTypeDiffHelper(base,
                isInCompareSG,
                true,
                CDWrapperSyntaxDiff.CDTypeDiffCategory.FREED,
                cDTypeDiffWhichAttributesDiffHelper(base, Optional.empty())));
      } else {
        cDTypeWrapperDiffResultQueueWithDiff.offer(
            createCDTypeDiffHelper(base,
                isInCompareSG,
                true,
                CDWrapperSyntaxDiff.CDTypeDiffCategory.DELETED,
                cDTypeDiffWhichAttributesDiffHelper(base, Optional.empty())));
      }
    }
  }

  public void cDTypeDiffs4NotMatchedAssocInCompareCDW(CDWrapper baseCDW) {
    checkList4AssocInCompareCDW.forEach((assoc, flag) -> {
      if (!flag && assoc.getCDWrapperKind() == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_ASC) {

        // left cardinality
        if (assoc.getCDWrapperLeftClassCardinality()
            == CDWrapper.CDAssociationWrapperCardinality.ONE
            || assoc.getCDWrapperLeftClassCardinality()
            == CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE) {
          if (baseCDW.getCDTypeWrapperGroup().containsKey(assoc.getCDWrapperRightClass().getName())) {
            CDTypeWrapper baseCDTypeWrapper =
                baseCDW.getCDTypeWrapperGroup().get(assoc.getCDWrapperRightClass().getName());
            createCDTypeDiff(baseCDTypeWrapper, Optional.empty(), false, true);
          }
        }

        // right cardinality
        if (assoc.getCDWrapperRightClassCardinality()
            == CDWrapper.CDAssociationWrapperCardinality.ONE
            || assoc.getCDWrapperRightClassCardinality()
            == CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE) {
          if (baseCDW.getCDTypeWrapperGroup().containsKey(assoc.getCDWrapperLeftClass().getName())) {
            CDTypeWrapper baseCDTypeWrapper =
                baseCDW.getCDTypeWrapperGroup().get(assoc.getCDWrapperLeftClass().getName());
            createCDTypeDiff(baseCDTypeWrapper, Optional.empty(), false, true);
          }
        }
      }

    });
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create CompAssociaion for each CDAssociationWrapper in base CDWrapper
   */
  public void cDAssociationDiffs(CDWrapper baseCDW, CDWrapper compareCDW, CDSemantics cdSemantics) {
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
          cDAssociationDiffsHelper4CDSemantics(baseCDW,
              compareCDW,
              intersectedBaseCDAssociationWrapper,
              compareCDAssociationWrapper,
              false,
              cdSemantics);
        });
      }
      else if (!isInCompareSG4ForwardAssocName && isInCompareSG4ReverseAssocName) {
        reverseDiffAssocListInCompareSG.forEach(compareCDAssociationWrapper -> {
          cDAssociationDiffsHelper4CDSemantics(baseCDW,
              compareCDW,
              intersectedBaseCDAssociationWrapper,
              compareCDAssociationWrapper,
              true,
              cdSemantics);
        });
      } else {
        createCDAssociationDiff(baseCDW,
            compareCDW,
            intersectedBaseCDAssociationWrapper,
            Optional.empty(),
            false,
            false);
      }
    });
  }

  /**
   * CDAssociationDiffsHelper for different CDSemantics
   */
  public void cDAssociationDiffsHelper4CDSemantics(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      CDAssociationWrapper intersectedBaseCDAssociationWrapper,
      CDAssociationWrapper compareCDAssociationWrapper,
      boolean isAssocNameExchanged,
      CDSemantics cdSemantics) {

    boolean isInCompareSG = false;
    CDAssociationWrapper intersectedCompareCDAssociationWrapper =
        intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
            compareCDAssociationWrapper, compareCDW);

    if (cdSemantics == CDSemantics.SIMPLE_CLOSED_WORLD) {
      isInCompareSG = true;
    } else if (cdSemantics == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD) {
      if (!isAssocNameExchanged) {
        if (checkClassSet4MultiInstance(
                intersectedBaseCDAssociationWrapper.getCDWrapperLeftClass().getSuperclasses(),
                intersectedCompareCDAssociationWrapper.getCDWrapperLeftClass().getSuperclasses()) &&
            checkClassSet4MultiInstance(
                intersectedBaseCDAssociationWrapper.getCDWrapperRightClass().getSuperclasses(),
                intersectedCompareCDAssociationWrapper.getCDWrapperRightClass().getSuperclasses())) {
          isInCompareSG = true;
        }
      } else {
        if (checkClassSet4MultiInstance(
                intersectedBaseCDAssociationWrapper.getCDWrapperLeftClass().getSuperclasses(),
                intersectedCompareCDAssociationWrapper.getCDWrapperRightClass().getSuperclasses()) &&
            checkClassSet4MultiInstance(
                intersectedBaseCDAssociationWrapper.getCDWrapperRightClass().getSuperclasses(),
                intersectedCompareCDAssociationWrapper.getCDWrapperLeftClass().getSuperclasses())) {
          isInCompareSG = true;
        }
      }
    }

    if (cdSemantics == CDSemantics.SIMPLE_CLOSED_WORLD) {
      createCDAssociationDiff(baseCDW,
          compareCDW,
          intersectedBaseCDAssociationWrapper,
          Optional.of(intersectedCompareCDAssociationWrapper),
          isInCompareSG,
          isAssocNameExchanged);
    } else if (cdSemantics == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD){
      if (intersectedBaseCDAssociationWrapper.getCDWrapperLeftClass().getSubclasses().size() == 1 &&
          intersectedBaseCDAssociationWrapper.getCDWrapperRightClass().getSubclasses().size() == 1) {
        createCDAssociationDiff(baseCDW,
            compareCDW,
            intersectedBaseCDAssociationWrapper,
            Optional.of(intersectedCompareCDAssociationWrapper),
            isInCompareSG,
            isAssocNameExchanged);
      }
    }
  }

  /**
   * generate CDAssocWrapperDiff object and
   * put into cDAssocWrapperDiffResultQueueWithDiff if exists semantic difference,
   * put into cDAssocWrapperDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDAssociationDiff(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optIntersectedCompare,
      boolean isInCompareSG,
      boolean isAssocNameExchanged) {

    if (isInCompareSG) {
      CDAssociationWrapper compare = optIntersectedCompare.get();
      CDWrapperSyntaxDiff.CDAssociationDiffCategory categoryResult = null;
      boolean mutexSemaphore = false;

      // STEP 1: check direction type
      CDWrapperSyntaxDiff.CDAssociationDiffDirection directionResult = !isAssocNameExchanged ?
          cDAssociationDiffDirectionHelper(base.getCDAssociationWrapperDirection(),
              compare.getCDAssociationWrapperDirection()) :
          cDAssociationDiffDirectionHelper(base.getCDAssociationWrapperDirection(),
              reverseDirection(compare.getCDAssociationWrapperDirection()));
      boolean isDirectionChanged = !isAssocNameExchanged ?
          (base.getCDAssociationWrapperDirection() != compare.getCDAssociationWrapperDirection()) :
          (base.getCDAssociationWrapperDirection() != reverseDirection(
              compare.getCDAssociationWrapperDirection()));
      categoryResult = cDAssociationDiffCategoryByDirectionHelper(isDirectionChanged,
          isAssocNameExchanged, directionResult);
      switch (categoryResult) {
        case DIRECTION_CHANGED:
          mutexSemaphore = true;

          // When there are both "->" and "<-" for same class, role name and cardinality in
          // compareCDW is to avoid a misjudgment.
          List<CDAssociationWrapperPack> cdAssociationWrapperPackListInbaseCDW =
              fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(baseCDW.getCDAssociationWrapperGroup(), base);
          List<CDAssociationWrapperPack> cdAssociationWrapperPackListInCompareCDW =
              fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(compareCDW.getCDAssociationWrapperGroup(), base);
          if (cdAssociationWrapperPackListInbaseCDW.size() == 1 ||
              (cdAssociationWrapperPackListInbaseCDW.size() == 2 && cdAssociationWrapperPackListInCompareCDW.size() == 1)) {
            cDAssocWrapperDiffResultQueueWithDiff.offer(
                createCDAssociationDiffHelper(base,
                    isInCompareSG,
                    isDirectionChanged,
                    categoryResult,
                    Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.DIRECTION),
                    Optional.of(directionResult)));
          } else {
            cDAssocWrapperDiffResultQueueWithoutDiff.offer(
                createCDAssociationDiffHelper(base,
                    isInCompareSG,
                    isDirectionChanged,
                    categoryResult));
          }
          break;
        default:
          cDAssocWrapperDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isDirectionChanged,
                  categoryResult));
          break;
      }


      // STEP 2: check left cardinality
      if (!mutexSemaphore) {
        CDWrapperSyntaxDiff.CDAssociationDiffCardinality leftCardinalityResult = !isAssocNameExchanged ?
            cDAssociationDiffCardinalityHelper(base.getCDWrapperLeftClassCardinality(),
                compare.getCDWrapperLeftClassCardinality()) :
            cDAssociationDiffCardinalityHelper(base.getCDWrapperLeftClassCardinality(),
                compare.getCDWrapperRightClassCardinality());
        boolean isLeftCardinalityDiff = !isAssocNameExchanged ?
            (base.getCDWrapperLeftClassCardinality() != compare.getCDWrapperLeftClassCardinality()) :
            (base.getCDWrapperLeftClassCardinality() != compare.getCDWrapperRightClassCardinality());
        categoryResult = cDAssociationDiffCategoryByCardinalityHelper(isLeftCardinalityDiff,
            leftCardinalityResult);
        switch (categoryResult) {
          case CARDINALITY_CHANGED:

            // When there are both "->" and "<-" for same class, role name and cardinality in
            // compareCDW is to avoid a misjudgment.
            List<CDAssociationWrapperPack> cdAssociationWrapperPackList =
                findSameCDAssociationWrapperByCDAssociationWrapper(compareCDW.getCDAssociationWrapperGroup(), base);
            if (cdAssociationWrapperPackList.isEmpty()) {

              // special situation for both "->" and "<-" in the same CD
              List<CDAssociationWrapperPack> fuzzySearchResult =
                  fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
                  baseCDW.getCDAssociationWrapperGroup(), base);

              if (fuzzySearchResult.size() == 2) {
                CDAssociationWrapper otherCDAssociationWrapper = fuzzySearchResult
                    .stream()
                    .filter(e -> !e.getCDAssociationWrapper().getName().equals(base.getName()))
                    .findFirst()
                    .get()
                    .getCDAssociationWrapper();

                if ((base.getCDWrapperRightClassCardinality() == CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE ||
                    base.getCDWrapperRightClassCardinality() == CDWrapper.CDAssociationWrapperCardinality.ONE) &&
                    mappingCardinality(base.getCDWrapperLeftClassCardinality().toString()) != 1 &&
                    (mappingCardinality(otherCDAssociationWrapper.getCDWrapperLeftClassCardinality().toString()) == 1 &&
                        mappingCardinality(otherCDAssociationWrapper.getCDWrapperRightClassCardinality().toString()) == 1)) {
                  cDAssocWrapperDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base, isInCompareSG, isLeftCardinalityDiff,
                          categoryResult,
                          Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.LEFT_SPECIAL_CARDINALITY),
                          Optional.of(CDWrapperSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE)));
                } else {
                  cDAssocWrapperDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base,
                          isInCompareSG,
                          isLeftCardinalityDiff,
                          categoryResult,
                          Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.LEFT_CARDINALITY),
                          Optional.of(leftCardinalityResult)));
                }
              } else {
                cDAssocWrapperDiffResultQueueWithDiff.offer(
                    createCDAssociationDiffHelper(base,
                        isInCompareSG,
                        isLeftCardinalityDiff,
                        categoryResult,
                        Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.LEFT_CARDINALITY),
                        Optional.of(leftCardinalityResult)));
              }

            } else {
              cDAssocWrapperDiffResultQueueWithoutDiff.offer(
                  createCDAssociationDiffHelper(base,
                      isInCompareSG,
                      isLeftCardinalityDiff,
                      categoryResult));
            }
            break;
          default:
            cDAssocWrapperDiffResultQueueWithoutDiff.offer(
                createCDAssociationDiffHelper(base,
                    isInCompareSG,
                    isLeftCardinalityDiff,
                    categoryResult));
            break;
        }
      }


      // STEP 3: check right cardinality
      if (!mutexSemaphore) {
        CDWrapperSyntaxDiff.CDAssociationDiffCardinality rightCardinalityResult = !isAssocNameExchanged ?
            (cDAssociationDiffCardinalityHelper(base.getCDWrapperRightClassCardinality(),
                compare.getCDWrapperRightClassCardinality())) :
            (cDAssociationDiffCardinalityHelper(base.getCDWrapperRightClassCardinality(),
                compare.getCDWrapperLeftClassCardinality()));
        boolean isRightCardinalityDiff = !isAssocNameExchanged ?
            (base.getCDWrapperRightClassCardinality()
                != compare.getCDWrapperRightClassCardinality()) :
            (base.getCDWrapperRightClassCardinality() != compare.getCDWrapperLeftClassCardinality());
        categoryResult = cDAssociationDiffCategoryByCardinalityHelper(isRightCardinalityDiff,
            rightCardinalityResult);
        switch (categoryResult) {
          case CARDINALITY_CHANGED:

            // When there are both "->" and "<-" for same class, role name and cardinality in
            // compareCDW is to avoid a misjudgment.
            List<CDAssociationWrapperPack> cdAssociationWrapperPackList =
                findSameCDAssociationWrapperByCDAssociationWrapper(compareCDW.getCDAssociationWrapperGroup(), base);
            if (cdAssociationWrapperPackList.isEmpty()) {

              // special situation for both "->" and "<-" in the same CD
              List<CDAssociationWrapperPack> fuzzySearchResult =
                  fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
                      baseCDW.getCDAssociationWrapperGroup(), base);

              if (fuzzySearchResult.size() == 2) {
                CDAssociationWrapper otherCDAssociationWrapper = fuzzySearchResult
                    .stream()
                    .filter(e -> !e.getCDAssociationWrapper().getName().equals(base.getName()))
                    .findFirst()
                    .get()
                    .getCDAssociationWrapper();

                if ((base.getCDWrapperLeftClassCardinality() == CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE ||
                    base.getCDWrapperLeftClassCardinality() == CDWrapper.CDAssociationWrapperCardinality.ONE) &&
                    mappingCardinality(base.getCDWrapperRightClassCardinality().toString()) != 1 &&
                    (mappingCardinality(otherCDAssociationWrapper.getCDWrapperLeftClassCardinality().toString()) == 1 &&
                        mappingCardinality(otherCDAssociationWrapper.getCDWrapperRightClassCardinality().toString()) == 1)) {
                  cDAssocWrapperDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base, isInCompareSG, isRightCardinalityDiff,
                          categoryResult,
                          Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.RIGHT_SPECIAL_CARDINALITY),
                          Optional.of(CDWrapperSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE)));
                } else {
                  cDAssocWrapperDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base,
                          isInCompareSG,
                          isRightCardinalityDiff,
                          categoryResult,
                          Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.RIGHT_CARDINALITY),
                          Optional.of(rightCardinalityResult)));
                }
              } else {
                cDAssocWrapperDiffResultQueueWithDiff.offer(
                    createCDAssociationDiffHelper(base,
                        isInCompareSG,
                        isRightCardinalityDiff,
                        categoryResult,
                        Optional.of(CDWrapperSyntaxDiff.WhichPartDiff.RIGHT_CARDINALITY),
                        Optional.of(rightCardinalityResult)));
              }

            } else {
              cDAssocWrapperDiffResultQueueWithoutDiff.offer(
                  createCDAssociationDiffHelper(base,
                      isInCompareSG,
                      isRightCardinalityDiff,
                      categoryResult));
            }
            break;
          default:
            cDAssocWrapperDiffResultQueueWithoutDiff.offer(
                createCDAssociationDiffHelper(base,
                    isInCompareSG,
                    isRightCardinalityDiff,
                    categoryResult));
            break;
        }
      }

      // STEP 4:  determine the subclass that is valid for CD1 not for CD2 in OD when the
      // original class in CD is abstract class or interface
      Optional<CDTypeWrapper> leftInstanceClass = Optional.empty();
      Optional<CDTypeWrapper> rightInstanceClass = Optional.empty();

      // left
      if (base.getCDWrapperLeftClass().getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS
          || base.getCDWrapperLeftClass().getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE) {

        Set<String> differenceSet = new LinkedHashSet<>();
        differenceSet.addAll(base.getCDWrapperLeftClass().getSubclasses());
        if (!isAssocNameExchanged) {
          differenceSet.removeAll(compare.getCDWrapperLeftClass().getSubclasses());
        } else {
          differenceSet.removeAll(compare.getCDWrapperRightClass().getSubclasses());
        }
        if (!differenceSet.isEmpty()) {
          Set<String> differenceSetWithoutAbstractAndInterface = differenceSet.stream()
              .filter(cDTypeName -> baseCDW.getCDTypeWrapperGroup().get(cDTypeName).getCDWrapperKind()
                  == CDWrapper.CDTypeWrapperKind.CDWRAPPER_CLASS)
              .collect(Collectors.toSet());

          if (differenceSetWithoutAbstractAndInterface.size() > 0) {
            leftInstanceClass =
                Optional.of(baseCDW.getCDTypeWrapperGroup().get(
                    differenceSetWithoutAbstractAndInterface.iterator().next()));
          }

        }
      }

      // right
      if (base.getCDWrapperRightClass().getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS
          || base.getCDWrapperRightClass().getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE) {

        Set<String> differenceSet = new LinkedHashSet<>();
        differenceSet.addAll(base.getCDWrapperRightClass().getSubclasses());
        if (!isAssocNameExchanged) {
          differenceSet.removeAll(compare.getCDWrapperRightClass().getSubclasses());
        } else {
          differenceSet.removeAll(compare.getCDWrapperLeftClass().getSubclasses());
        }
        if (!differenceSet.isEmpty()) {
          Set<String> differenceSetWithoutAbstractAndInterface = differenceSet.stream()
              .filter(cDTypeName -> baseCDW.getCDTypeWrapperGroup().get(cDTypeName).getCDWrapperKind()
                  == CDWrapper.CDTypeWrapperKind.CDWRAPPER_CLASS)
              .collect(Collectors.toSet());

          if (differenceSetWithoutAbstractAndInterface.size() > 0) {
            rightInstanceClass =
                Optional.of(baseCDW.getCDTypeWrapperGroup().get(
                    differenceSetWithoutAbstractAndInterface.iterator().next()));
          }
        }
      }

      if (leftInstanceClass.isPresent() || rightInstanceClass.isPresent()) {

        // When there are both "->" and "<-" for same class, role name and cardinality in CD
        // is to avoid a misjudgment.
        List<CDAssociationWrapperPack> cdAssociationWrapperPackList =
            findSameCDAssociationWrapperByCDAssociationWrapper(compareCDW.getCDAssociationWrapperGroup(), base);
        if (cdAssociationWrapperPackList.isEmpty()) {
          cDAssocWrapperDiffResultQueueWithDiff.offer(
              createCDAssociationDiffHelperWithInstanceClass(base,
                  isInCompareSG,
                  true,
                  CDWrapperSyntaxDiff.CDAssociationDiffCategory.SUBCLASS_DIFF,
                  leftInstanceClass,
                  rightInstanceClass));
        }
      }
    }
    else {
      List<CDAssociationWrapperPack> cdAssociationWrapperPackList =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutCardinality(compareCDW.getCDAssociationWrapperGroup(), base);
      if (cdAssociationWrapperPackList.stream().anyMatch(e ->
          e.getCDAssociationWrapper().getStatus() == CDWrapper.CDStatus.CONFLICTING)) {
        cDAssocWrapperDiffResultQueueWithDiff.offer(
            createCDAssociationDiffHelper(base,
                isInCompareSG,
                true,
                CDWrapperSyntaxDiff.CDAssociationDiffCategory.CONFLICTING));
      } else {
        cDAssocWrapperDiffResultQueueWithDiff.offer(
            createCDAssociationDiffHelper(base,
                isInCompareSG,
                true,
                CDWrapperSyntaxDiff.CDAssociationDiffCategory.DELETED));
      }

    }
  }

}
