package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperPack;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDAssociationDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeDiff;

import java.util.*;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDSyntaxDiffHelper.*;

public class CDWrapper2CDSyntaxDiffGenerator {

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithoutDiff = new LinkedList<>();

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithoutDiff = new LinkedList<>();

  /**
   * generating CDSyntaxDiff
   */
  public CDSyntaxDiff generateCDSyntaxDiff(
      CDWrapper baseCDW,
      CDWrapper compareCDW,
      CDSemantics cdSemantics) {

    CDSyntaxDiff cDSyntaxDiff = new CDSyntaxDiff(baseCDW, compareCDW);
    cDTypeDiffs(baseCDW, compareCDW, cdSemantics);
    cDAssociationDiffs(baseCDW, compareCDW, cdSemantics);

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
        compareCDW.getCDTypeWrapperGroup().get(baseCDTypeWrapper.getName());

    if (cdSemantics == CDSemantics.SIMPLE_CLOSED_WORLD) {
      isInCompareSG = compareCDW.getCDTypeWrapperGroup().containsKey(baseCDTypeWrapper.getName());
    } else if (cdSemantics == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD) {
      if (compareCDW.getCDTypeWrapperGroup().containsKey(baseCDTypeWrapper.getName())) {
        if (baseCDTypeWrapper.getSuperclasses().equals(compareCDTypeWrapper.getSuperclasses())) {
          isInCompareSG = true;
        }
      }
    }

    createCDTypeDiff(baseCDW, baseCDTypeWrapper, compareCDTypeWrapper, isInCompareSG);
  }

  /**
   * generate CDTypeDiff object and
   * put into cDTypeDiffResultQueueWithDiff if exists semantic difference,
   * put into cDTypeDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDTypeDiff(
      CDWrapper baseCDW,
      CDTypeWrapper base,
      CDTypeWrapper compare,
      boolean isInCompareSG) {

    // check whether CDTypeWrapper Name is also in CompareSG
    if (isInCompareSG) {

      // calculate WhichAttributesDiffList
      List<String> attributesDiffList = cDTypeDiffWhichAttributesDiffHelper(base,
          Optional.of(compare));

      // calculate boolean isContentDiff
      boolean isContentDiff = attributesDiffList.size() != 0;

      // calculate class category
      CDSyntaxDiff.CDTypeDiffCategory category = cDTypeDiffCategoryHelper(base, compare,
          isContentDiff);

      // create cDTypeDiff
      CDTypeDiff cDTypeDiff = createCDTypeDiffHelper(base, isInCompareSG, isContentDiff, category,
          attributesDiffList);

      // distinguish cDTypeDiff by CompCategory
      if (cDTypeDiff.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.DELETED
          || cDTypeDiff.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.EDITED) {
        cDTypeDiffResultQueueWithDiff.offer(cDTypeDiff);
      }
      else {
        cDTypeDiffResultQueueWithoutDiff.offer(cDTypeDiff);
      }

    }
    else {
      cDTypeDiffResultQueueWithDiff.offer(
          createCDTypeDiffHelper(base, isInCompareSG, true, CDSyntaxDiff.CDTypeDiffCategory.DELETED,
              cDTypeDiffWhichAttributesDiffHelper(base, Optional.empty())));
    }


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
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirection(
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

        forwardDiffAssocListInCompareSG.forEach(compareCDAssociationWrapper ->
            cDAssociationDiffsHelper4CDSemantics(baseCDW,
                compareCDW,
                intersectedBaseCDAssociationWrapper,
                compareCDAssociationWrapper,
                false,
                cdSemantics));
      }
      else if (!isInCompareSG4ForwardAssocName && isInCompareSG4ReverseAssocName) {
        reverseDiffAssocListInCompareSG.forEach(compareCDAssociationWrapper ->
            cDAssociationDiffsHelper4CDSemantics(baseCDW,
                compareCDW,
                intersectedBaseCDAssociationWrapper,
                compareCDAssociationWrapper,
                true,
                cdSemantics));
      } else {
        createCDAssociationDiff(baseCDW,
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
      if ((intersectedBaseCDAssociationWrapper.getCDWrapperLeftClass().getSuperclasses()
          .equals(intersectedCompareCDAssociationWrapper.getCDWrapperLeftClass().getSuperclasses()))
          && (intersectedBaseCDAssociationWrapper.getCDWrapperRightClass().getSuperclasses()
          .equals(intersectedCompareCDAssociationWrapper.getCDWrapperRightClass().getSuperclasses()))) {
        isInCompareSG = true;
      }
    }

    createCDAssociationDiff(baseCDW,
        intersectedBaseCDAssociationWrapper,
        Optional.of(intersectedCompareCDAssociationWrapper),
        isInCompareSG,
        isAssocNameExchanged);
  }

  /**
   * generate CDAssociationDiff object and
   * put into cDAssociationDiffResultQueueWithDiff if exists semantic difference,
   * put into cDAssociationDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDAssociationDiff(
      CDWrapper baseCDW,
      CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare,
      boolean isInCompareSG,
      boolean isAssocNameExchanged) {

    if (isInCompareSG) {
      CDAssociationWrapper compare = optCompare.get();
      CDSyntaxDiff.CDAssociationDiffCategory categoryResult = null;

      // STEP 1: check direction type
      CDSyntaxDiff.CDAssociationDiffDirection directionResult = !isAssocNameExchanged ?
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
          cDAssociationDiffResultQueueWithDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isDirectionChanged,
                  categoryResult,
                  Optional.of(CDSyntaxDiff.WhichPartDiff.DIRECTION),
                  Optional.of(directionResult)));
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isDirectionChanged,
                  categoryResult));
          break;
      }

      // STEP 2: check left cardinality
      CDSyntaxDiff.CDAssociationDiffCardinality leftCardinalityResult = !isAssocNameExchanged ?
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
          cDAssociationDiffResultQueueWithDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isLeftCardinalityDiff,
                  categoryResult,
                  Optional.of(CDSyntaxDiff.WhichPartDiff.LEFT_CARDINALITY),
                  Optional.of(leftCardinalityResult)));
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isLeftCardinalityDiff,
                  categoryResult));
          break;
      }

      // STEP 3: check right cardinality
      CDSyntaxDiff.CDAssociationDiffCardinality rightCardinalityResult = !isAssocNameExchanged ?
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
          cDAssociationDiffResultQueueWithDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isRightCardinalityDiff,
                  categoryResult,
                  Optional.of(CDSyntaxDiff.WhichPartDiff.RIGHT_CARDINALITY),
                  Optional.of(rightCardinalityResult)));
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base,
                  isInCompareSG,
                  isRightCardinalityDiff,
                  categoryResult));
          break;
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
          leftInstanceClass =
              Optional.of(baseCDW.getCDTypeWrapperGroup().get(differenceSet.iterator().next()));
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
          rightInstanceClass =
              Optional.of(baseCDW.getCDTypeWrapperGroup().get(differenceSet.iterator().next()));
        }
      }

      if (leftInstanceClass.isPresent() || rightInstanceClass.isPresent()) {
        cDAssociationDiffResultQueueWithDiff.offer(
            createCDAssociationDiffHelperWithInstanceClass(base,
                isInCompareSG,
                true,
                CDSyntaxDiff.CDAssociationDiffCategory.SUBCLASS_DIFF,
                leftInstanceClass,
                rightInstanceClass));
      }
    }
    else {
      cDAssociationDiffResultQueueWithDiff.offer(
          createCDAssociationDiffHelper(base,
              isInCompareSG,
              true,
              CDSyntaxDiff.CDAssociationDiffCategory.DELETED));
    }
  }

}
