package de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperPack;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDAssociationDiff;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeDiff;

import java.util.*;

import static de.monticore.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;
import static de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDSyntaxDiffHelper.*;

public class CDWrapper2CDSyntaxDiffGenerator {

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithDiff = new LinkedList<>();

  protected Deque<CDTypeDiff> cDTypeDiffResultQueueWithoutDiff = new LinkedList<>();

  protected Deque<CDAssociationDiff> cDAssociationDiffResultQueueWithoutDiff = new LinkedList<>();

  /**
   * generating CDSyntaxDiff
   */
  public CDSyntaxDiff generateCDSyntaxDiff(CDWrapper baseCDW, CDWrapper compareCDW) {
    CDSyntaxDiff cDSyntaxDiff = new CDSyntaxDiff(baseCDW, compareCDW);
    cDTypeDiffs(baseCDW, compareCDW);
    cDAssociationDiffs(baseCDW, compareCDW);

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
  public void cDTypeDiffs(CDWrapper baseCDW, CDWrapper compareCDW) {
    baseCDW.getCDTypeWrapperGroup().forEach((className, baseCDTypeWrapper) -> {
      createCDTypeDiff(baseCDW, baseCDTypeWrapper, compareCDW.getCDTypeWrapperGroup().get(className),
          compareCDW.getCDTypeWrapperGroup().containsKey(className));
    });
  }

  /**
   * generate CDTypeDiff object and
   * put into cDTypeDiffResultQueueWithDiff if exists semantic difference,
   * put into cDTypeDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDTypeDiff(CDWrapper baseCDW, CDTypeWrapper base, CDTypeWrapper compare,
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
  public void cDAssociationDiffs(CDWrapper baseCDW, CDWrapper compareCDW) {
    baseCDW.getCDAssociationWrapperGroup().forEach((assocName1, baseCDAssociationWrapper) -> {

      CDAssociationWrapper intersectedBaseCDAssociationWrapper =
          intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
              baseCDAssociationWrapper, baseCDW);
//      CDAssociationWrapper intersectedBaseCDAssociationWrapper =
//          intersectCDAssociationWrapperCardinalityByCDAssociationWrapperOnlyWithLeftToRightAndRightToLeft(
//              intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
//                  baseCDAssociationWrapper, baseCDW), baseCDW);

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
        forwardDiffAssocListInCompareSG.forEach(
            compareCDAssociationWrapper -> createCDAssociationDiff(
                intersectedBaseCDAssociationWrapper, Optional.of(
                    intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
                        compareCDAssociationWrapper, compareCDW)
//                    intersectCDAssociationWrapperCardinalityByCDAssociationWrapperOnlyWithLeftToRightAndRightToLeft(
//                        intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
//                            compareCDAssociationWrapper, compareCDW), compareCDW)
                ), true, false));
      }
      else if (!isInCompareSG4ForwardAssocName && isInCompareSG4ReverseAssocName) {
        reverseDiffAssocListInCompareSG.forEach(
            compareCDAssociationWrapper -> createCDAssociationDiff(
                intersectedBaseCDAssociationWrapper, Optional.of(
                    intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
                        compareCDAssociationWrapper, compareCDW)
//                    intersectCDAssociationWrapperCardinalityByCDAssociationWrapperOnlyWithLeftToRightAndRightToLeft(
//                        intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
//                            compareCDAssociationWrapper, compareCDW), compareCDW)
                ), true, true));
      }
      else {
        createCDAssociationDiff(intersectedBaseCDAssociationWrapper, Optional.empty(), false,
            false);
      }
    });
  }

  /**
   * generate CDAssociationDiff object and
   * put into cDAssociationDiffResultQueueWithDiff if exists semantic difference,
   * put into cDAssociationDiffResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCDAssociationDiff(CDAssociationWrapper base,
      Optional<CDAssociationWrapper> optCompare, boolean isInCompareSG,
      boolean isAssocNameExchanged) {

    if (isInCompareSG) {
      CDAssociationWrapper compare = optCompare.get();
      CDSyntaxDiff.CDAssociationDiffCategory categoryResult = null;

      // check direction type
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
              createCDAssociationDiffHelper(base, isInCompareSG, isDirectionChanged, categoryResult,
                  Optional.of(CDSyntaxDiff.WhichPartDiff.DIRECTION), Optional.of(directionResult)));
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base, isInCompareSG, isDirectionChanged,
                  categoryResult));
          break;
      }

      // check left cardinality
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
              createCDAssociationDiffHelper(base, isInCompareSG, isLeftCardinalityDiff,
                  categoryResult, Optional.of(CDSyntaxDiff.WhichPartDiff.LEFT_CARDINALITY),
                  Optional.of(leftCardinalityResult)));
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base, isInCompareSG, isLeftCardinalityDiff,
                  categoryResult));
          break;
      }

      // check right cardinality
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
              createCDAssociationDiffHelper(base, isInCompareSG, isRightCardinalityDiff,
                  categoryResult, Optional.of(CDSyntaxDiff.WhichPartDiff.RIGHT_CARDINALITY),
                  Optional.of(rightCardinalityResult)));
          break;
        default:
          cDAssociationDiffResultQueueWithoutDiff.offer(
              createCDAssociationDiffHelper(base, isInCompareSG, isRightCardinalityDiff,
                  categoryResult));
          break;
      }
    }
    else {
      cDAssociationDiffResultQueueWithDiff.offer(
          createCDAssociationDiffHelper(base, isInCompareSG, true,
              CDSyntaxDiff.CDAssociationDiffCategory.DELETED));
    }
  }

}
