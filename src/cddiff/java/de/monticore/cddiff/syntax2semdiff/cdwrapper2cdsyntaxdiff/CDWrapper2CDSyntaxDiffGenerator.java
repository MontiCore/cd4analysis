package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.GenerateODHelper.mappingCardinality;
import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDSyntaxDiffHelper.*;

public class CDWrapper2CDSyntaxDiffGenerator {

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithoutDiff = new LinkedList<>();

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithoutDiff = new LinkedList<>();

  protected Map<CDAssociationWrapper, Boolean> checkList4AssocInCompareCDW = new HashMap<>();

  /**
   * generating CDSyntaxDiff
   */
  public CDSyntaxDiff generateCDSyntaxDiff(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      CDSemantics cdSemantics) {

    CDSyntaxDiff cDSyntaxDiff = new CDSyntaxDiff(baseCDW, compareCDW);
    createCheckList4AssocInCompareCDW(compareCDW, checkList4AssocInCompareCDW);
    updateCheckList4AssocInCompareCDW(baseCDW, compareCDW, checkList4AssocInCompareCDW);
    cDAssociationDiffs(baseCDW, compareCDW, cdSemantics);
    cDTypeDiffs(baseCDW, compareCDW, cdSemantics);

    // special CDType that is in the not matched assoc in compareCDW

    cDTypeDiffs4NotMatchedAssocInCompareCDW(baseCDW);

    cDSyntaxDiff.setBaseCDW(baseCDW);
    cDSyntaxDiff.setCompareCDW(compareCDW);
    cDSyntaxDiff.setCDTypeDiffResultQueueWithDiff(cDTypeDiffResultQueueWithDiff);
    cDSyntaxDiff.setCDAssociationDiffResultQueueWithDiff(cDAssociationDiffResultQueueWithDiff);
    cDSyntaxDiff.setCDTypeDiffResultQueueWithoutDiff(cDTypeDiffResultQueueWithoutDiff);
    cDSyntaxDiff.setCDAssociationDiffResultQueueWithoutDiff(cDAssociationDiffResultQueueWithoutDiff);
    return cDSyntaxDiff;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * create CDTypeDiff for each CDTypeWrapper in base CDWrapper
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
      createCDTypeDiff(baseCDTypeWrapper,
          Optional.empty(),
          Optional.empty(),
          isInCompareSG,
          false);
    } else {
      createCDTypeDiff(baseCDTypeWrapper,
          Optional.of(compareCDTypeWrapper),
          Optional.empty(),
          isInCompareSG,
          false);
    }

  }

  /**
   * generate CDTypeDiff object and
   * put into cDTypeDiffResultQueueWithDiff if exists semantic difference,
   * put into cDTypeDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDTypeDiff(
      CDTypeWrapper base,
      Optional<CDTypeWrapper> optCompareClass,
      Optional<CDAssociationWrapper> optCompareAssoc,
      boolean isInCompareSG,
      boolean isNotMatchedAssocInCompareCDW) {

    // check whether CDTypeWrapper Name is also in CompareSG
    if (isInCompareSG) {

      // calculate WhichAttributesDiffList
      List<String> attributesDiffList = cDTypeDiffWhichAttributesDiffHelper(base, optCompareClass);

      // calculate boolean isContentDiff
      boolean isContentDiff = attributesDiffList.size() != 0;

      // calculate class category
      CDTypeDiffCategory category = cDTypeDiffCategoryHelper(base, optCompareClass.get(),
          isContentDiff);

      // create cDTypeDiff
      CDTypeDiff cDTypeDiff =
          createCDTypeDiffHelper(base,
              optCompareClass,
              optCompareAssoc,
              isInCompareSG,
              isContentDiff,
              category,
              attributesDiffList);

      // distinguish cDTypeDiff by CompCategory
      if (cDTypeDiff.getCDDiffCategory() == CDTypeDiffCategory.DELETED
          || cDTypeDiff.getCDDiffCategory() == CDTypeDiffCategory.EDITED) {
        cDTypeDiffResultQueueWithDiff.offer(cDTypeDiff);
      }
      else {
        cDTypeDiffResultQueueWithoutDiff.offer(cDTypeDiff);
      }

    }
    else {
      if (isNotMatchedAssocInCompareCDW) {
        cDTypeDiffResultQueueWithDiff.offer(
            createCDTypeDiffHelper(base,
                optCompareClass,
                optCompareAssoc,
                isInCompareSG,
                true,
                CDTypeDiffCategory.FREED,
                cDTypeDiffWhichAttributesDiffHelper(base, Optional.empty())));
      } else {
        cDTypeDiffResultQueueWithDiff.offer(
            createCDTypeDiffHelper(base,
                optCompareClass,
                optCompareAssoc,
                isInCompareSG,
                true,
                CDTypeDiffCategory.DELETED,
                cDTypeDiffWhichAttributesDiffHelper(base, Optional.empty())));
      }
    }
  }

  public void cDTypeDiffs4NotMatchedAssocInCompareCDW(CDWrapper baseCDW) {
    checkList4AssocInCompareCDW.forEach((assoc, flag) -> {
      if (!flag && assoc.getCDWrapperKind() == CDAssociationWrapperKind.CDWRAPPER_ASC) {

        // left cardinality
        if (assoc.getCDWrapperLeftClassCardinality()
            == CDAssociationWrapperCardinality.ONE
            || assoc.getCDWrapperLeftClassCardinality()
            == CDAssociationWrapperCardinality.ONE_TO_MORE) {
          if (baseCDW.getCDTypeWrapperGroup().containsKey(assoc.getCDWrapperRightClass().getName())) {
            CDTypeWrapper baseCDTypeWrapper =
                baseCDW.getCDTypeWrapperGroup().get(assoc.getCDWrapperRightClass().getName());
            createCDTypeDiff(baseCDTypeWrapper,
                Optional.empty(),
                Optional.of(assoc),
                false,
                true);
          }
        }

        // right cardinality
        if (assoc.getCDWrapperRightClassCardinality()
            == CDAssociationWrapperCardinality.ONE
            || assoc.getCDWrapperRightClassCardinality()
            == CDAssociationWrapperCardinality.ONE_TO_MORE) {
          if (baseCDW.getCDTypeWrapperGroup().containsKey(assoc.getCDWrapperLeftClass().getName())) {
            CDTypeWrapper baseCDTypeWrapper =
                baseCDW.getCDTypeWrapperGroup().get(assoc.getCDWrapperLeftClass().getName());
            createCDTypeDiff(baseCDTypeWrapper,
                Optional.empty(),
                Optional.of(assoc),
                false,
                true);
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
   * generate CDAssociationDiff object and
   * put into cDAssociationDiffResultQueueWithDiff if exists semantic difference,
   * put into cDAssociationDiffResultQueueWithoutDiff if exists no semantic difference
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
      CDAssociationDiffCategory categoryResult = null;
      boolean mutexSemaphore = false;

      // STEP 1: check direction type
      CDAssociationDiffDirection directionResult = !isAssocNameExchanged ?
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
            cDAssociationDiffResultQueueWithDiff.offer(
                createCDAssociationDiffHelper(base,
                    Optional.of(compare),
                    isInCompareSG,
                    isDirectionChanged,
                    categoryResult,
                    Optional.of(WhichPartDiff.DIRECTION),
                    Optional.of(directionResult)));
          } else {
            cDAssociationDiffResultQueueWithoutDiff.offer(
                createCDAssociationDiffHelper(base,
                    Optional.of(compare),
                    isInCompareSG,
                    isDirectionChanged,
                    categoryResult));
          }
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base,
                  Optional.of(compare),
                  isInCompareSG,
                  isDirectionChanged,
                  categoryResult));
          break;
      }


      // STEP 2: check left cardinality
      if (!mutexSemaphore) {
        CDAssociationDiffCardinality leftCardinalityResult = !isAssocNameExchanged ?
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

                if ((base.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ZERO_TO_ONE ||
                    base.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE) &&
                    mappingCardinality(base.getCDWrapperLeftClassCardinality().toString()) != 1 &&
                    (mappingCardinality(otherCDAssociationWrapper.getCDWrapperLeftClassCardinality().toString()) == 1 &&
                        mappingCardinality(otherCDAssociationWrapper.getCDWrapperRightClassCardinality().toString()) == 1)) {
                  cDAssociationDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base,
                          Optional.of(compare),
                          isInCompareSG,
                          isLeftCardinalityDiff,
                          categoryResult,
                          Optional.of(WhichPartDiff.LEFT_SPECIAL_CARDINALITY),
                          Optional.of(CDAssociationDiffCardinality.TWO_TO_MORE)));
                } else {
                  cDAssociationDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base,
                          Optional.of(compare),
                          isInCompareSG,
                          isLeftCardinalityDiff,
                          categoryResult,
                          Optional.of(WhichPartDiff.LEFT_CARDINALITY),
                          Optional.of(leftCardinalityResult)));
                }
              } else {
                cDAssociationDiffResultQueueWithDiff.offer(
                    createCDAssociationDiffHelper(base,
                        Optional.of(compare),
                        isInCompareSG,
                        isLeftCardinalityDiff,
                        categoryResult,
                        Optional.of(WhichPartDiff.LEFT_CARDINALITY),
                        Optional.of(leftCardinalityResult)));
              }

            } else {
              cDAssociationDiffResultQueueWithoutDiff.offer(
                  createCDAssociationDiffHelper(base,
                      Optional.of(compare),
                      isInCompareSG,
                      isLeftCardinalityDiff,
                      categoryResult));
            }
            break;
          default:
            cDAssociationDiffResultQueueWithoutDiff.offer(
                createCDAssociationDiffHelper(base,
                    Optional.of(compare),
                    isInCompareSG,
                    isLeftCardinalityDiff,
                    categoryResult));
            break;
        }
      }


      // STEP 3: check right cardinality
      if (!mutexSemaphore) {
        CDAssociationDiffCardinality rightCardinalityResult = !isAssocNameExchanged ?
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

                if ((base.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ZERO_TO_ONE ||
                    base.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE) &&
                    mappingCardinality(base.getCDWrapperRightClassCardinality().toString()) != 1 &&
                    (mappingCardinality(otherCDAssociationWrapper.getCDWrapperLeftClassCardinality().toString()) == 1 &&
                        mappingCardinality(otherCDAssociationWrapper.getCDWrapperRightClassCardinality().toString()) == 1)) {
                  cDAssociationDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base,
                          Optional.of(compare),
                          isInCompareSG,
                          isRightCardinalityDiff,
                          categoryResult,
                          Optional.of(WhichPartDiff.RIGHT_SPECIAL_CARDINALITY),
                          Optional.of(CDAssociationDiffCardinality.TWO_TO_MORE)));
                } else {
                  cDAssociationDiffResultQueueWithDiff.offer(
                      createCDAssociationDiffHelper(base,
                          Optional.of(compare),
                          isInCompareSG,
                          isRightCardinalityDiff,
                          categoryResult,
                          Optional.of(WhichPartDiff.RIGHT_CARDINALITY),
                          Optional.of(rightCardinalityResult)));
                }
              } else {
                cDAssociationDiffResultQueueWithDiff.offer(
                    createCDAssociationDiffHelper(base,
                        Optional.of(compare),
                        isInCompareSG,
                        isRightCardinalityDiff,
                        categoryResult,
                        Optional.of(WhichPartDiff.RIGHT_CARDINALITY),
                        Optional.of(rightCardinalityResult)));
              }

            } else {
              cDAssociationDiffResultQueueWithoutDiff.offer(
                  createCDAssociationDiffHelper(base,
                      Optional.of(compare),
                      isInCompareSG,
                      isRightCardinalityDiff,
                      categoryResult));
            }
            break;
          default:
            cDAssociationDiffResultQueueWithoutDiff.offer(
                createCDAssociationDiffHelper(base,
                    Optional.of(compare),
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
      if (base.getCDWrapperLeftClass().getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS
          || base.getCDWrapperLeftClass().getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_INTERFACE) {

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
                  == CDTypeWrapperKind.CDWRAPPER_CLASS)
              .collect(Collectors.toSet());

          if (differenceSetWithoutAbstractAndInterface.size() > 0) {
            leftInstanceClass =
                Optional.of(baseCDW.getCDTypeWrapperGroup().get(
                    differenceSetWithoutAbstractAndInterface.iterator().next()));
          }

        }
      }

      // right
      if (base.getCDWrapperRightClass().getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS
          || base.getCDWrapperRightClass().getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_INTERFACE) {

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
                  == CDTypeWrapperKind.CDWRAPPER_CLASS)
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
          cDAssociationDiffResultQueueWithDiff.offer(
              createCDAssociationDiffHelperWithInstanceClass(base,
                  Optional.of(compare),
                  isInCompareSG,
                  true,
                  CDAssociationDiffCategory.SUBCLASS_DIFF,
                  leftInstanceClass,
                  rightInstanceClass));
        }
      }
    }
    else {
      List<CDAssociationWrapperPack> cdAssociationWrapperPackList =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutCardinality(compareCDW.getCDAssociationWrapperGroup(), base);
      if (cdAssociationWrapperPackList.stream().anyMatch(e ->
          e.getCDAssociationWrapper().getStatus() == CDStatus.CONFLICTING)) {
        cDAssociationDiffResultQueueWithDiff.offer(
            createCDAssociationDiffHelper(base,
                Optional.empty(),
                isInCompareSG,
                true,
                CDAssociationDiffCategory.CONFLICTING));
      } else {
        cDAssociationDiffResultQueueWithDiff.offer(
            createCDAssociationDiffHelper(base,
                Optional.empty(),
                isInCompareSG,
                true,
                CDAssociationDiffCategory.DELETED));
      }

    }
  }

}
