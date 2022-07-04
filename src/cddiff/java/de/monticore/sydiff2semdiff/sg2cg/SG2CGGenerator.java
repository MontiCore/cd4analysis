package de.monticore.sydiff2semdiff.sg2cg;

import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportAssociationPack;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportGroup;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompareGroup;

import java.util.*;

import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.*;
import static de.monticore.sydiff2semdiff.sg2cg.CompareHelper.*;

public class SG2CGGenerator {

  protected Deque<CompClass> compClassResultQueueWithDiff = new LinkedList<>();
  protected Deque<CompAssociation> compAssociationResultQueueWithDiff = new LinkedList<>();
  protected Deque<CompClass> compClassResultQueueWithoutDiff = new LinkedList<>();
  protected Deque<CompAssociation> compAssociationResultQueueWithoutDiff = new LinkedList<>();

  /**
   * generating CompareGroup
   */
  public CompareGroup generateCompareGroup(SupportGroup baseSG, SupportGroup compareSG) {
    CompareGroup compareGroup = new CompareGroup(baseSG, compareSG);
    compareClasses(baseSG, compareSG);
    compareAssociations(baseSG, compareSG);

    compareGroup.setBaseSG(baseSG);
    compareGroup.setCompareSG(compareSG);
    compareGroup.setCompClassResultQueueWithDiff(compClassResultQueueWithDiff);
    compareGroup.setCompAssociationResultQueueWithDiff(compAssociationResultQueueWithDiff);
    compareGroup.setCompClassResultQueueWithoutDiff(compClassResultQueueWithoutDiff);
    compareGroup.setCompAssociationResultQueueWithoutDiff(compAssociationResultQueueWithoutDiff);
    return compareGroup;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * create CompClass for each SupportClass in base SupportGroup
   */
  public void compareClasses(SupportGroup baseSG, SupportGroup compareSG) {
    baseSG.getSupportClassGroup().forEach((className, baseSupportClass) -> {
      createCompareClass(
        baseSG,
        baseSupportClass,
        compareSG.getSupportClassGroup().get(className),
        compareSG.getSupportClassGroup().containsKey(className));
    });
  }

  /**
   * generate CompClass object and
   * put into compClassResultQueueWithDiff if exists semantic difference,
   * put into compClassResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCompareClass(SupportGroup baseSG, SupportClass base, SupportClass compare, boolean isInCompareSG) {

    // check whether SupportClass Name is also in CompareSG
    if (isInCompareSG) {

      // calculate WhichAttributesDiffList
      List<String> attributesDiffList = compClassWhichAttributesDiffHelper(base, Optional.of(compare));

      // calculate boolean isContentDiff
      boolean isContentDiff = attributesDiffList.size() != 0;

      // calculate class category
      CompareGroup.CompClassCategory category = compClassCategoryHelper(base, compare, isContentDiff);

      // create compClass
      CompClass compClass = createCompClassHelper(base, isInCompareSG, isContentDiff, category, attributesDiffList);

      // distinguish compClass by CompCategory
      if (compClass.getCompCategory() == CompareGroup.CompClassCategory.DELETED ||
        compClass.getCompCategory() == CompareGroup.CompClassCategory.EDITED) {
        compClassResultQueueWithDiff.offer(compClass);
      } else {
        compClassResultQueueWithoutDiff.offer(compClass);
      }

    } else {
      compClassResultQueueWithDiff.offer(createCompClassHelper(
        base,
        isInCompareSG,
        true,
        CompareGroup.CompClassCategory.DELETED,
        compClassWhichAttributesDiffHelper(base, Optional.empty())
      ));
    }
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create CompAssociaion for each SupportAssociation in base SupportGroup
   */
  public void compareAssociations(SupportGroup baseSG, SupportGroup compareSG) {
    baseSG.getSupportAssociationGroup().forEach((assocName1, baseSupportAssociation) -> {

      SupportAssociation intersectedBaseSupportAssociation =
        intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(
          intersectSupportAssociationCardinalityBySupportAssociationWithOverlap(baseSupportAssociation, baseSG),
          baseSG);

      // get all associations including reversed association in CompareSG
      // by matching [leftClass], [leftRoleName], [rightRoleName], [rightClass]
      List<SupportAssociationPack> DiffAssocMapInCompareSG =
        fuzzySearchSupportAssociationBySupportAssociationWithoutDirection(
          compareSG.getSupportAssociationGroup(),
          intersectedBaseSupportAssociation);
      List<SupportAssociation> forwardDiffAssocListInCompareSG = new ArrayList<>();
      List<SupportAssociation> reverseDiffAssocListInCompareSG = new ArrayList<>();
      DiffAssocMapInCompareSG.forEach(e -> {
        if (!e.isReverse()) {
          forwardDiffAssocListInCompareSG.add(e.getSupportAssociation());
        } else {
          reverseDiffAssocListInCompareSG.add(e.getSupportAssociation());
        }
      });

      boolean isInCompareSG4ForwardAssocName = forwardDiffAssocListInCompareSG.size() > 0;
      boolean isInCompareSG4ReverseAssocName = reverseDiffAssocListInCompareSG.size() > 0;

      if (isInCompareSG4ForwardAssocName && !isInCompareSG4ReverseAssocName) {
        forwardDiffAssocListInCompareSG.forEach(compareSupportAssociation ->
          createCompareAssociation(
            intersectedBaseSupportAssociation,
            Optional.of(intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(
              intersectSupportAssociationCardinalityBySupportAssociationWithOverlap(compareSupportAssociation, compareSG), compareSG)),
            true,
            false));
      } else if (!isInCompareSG4ForwardAssocName && isInCompareSG4ReverseAssocName) {
        reverseDiffAssocListInCompareSG.forEach(compareSupportAssociation ->
          createCompareAssociation(
            intersectedBaseSupportAssociation,
            Optional.of(intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(
              intersectSupportAssociationCardinalityBySupportAssociationWithOverlap(compareSupportAssociation, compareSG),
              compareSG)),
            true,
            true));
      } else {
        createCompareAssociation(
          intersectedBaseSupportAssociation,
          Optional.empty(),
          false,
          false);
      }
    });
  }

  /**
   * generate CompAssociation object and
   * put into compAssociationResultQueueWithDiff if exists semantic difference,
   * put into compAssociationResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCompareAssociation(SupportAssociation base,
                                       Optional<SupportAssociation> optCompare,
                                       boolean isInCompareSG,
                                       boolean isAssocNameExchanged) {

    if (isInCompareSG) {
      SupportAssociation compare = optCompare.get();
      CompareGroup.CompAssociationCategory categoryResult = null;

      // check direction type
      CompareGroup.CompAssociationDirection directionResult = !isAssocNameExchanged ?
        compAssociationDirectionHelper(base.getSupportDirection(), compare.getSupportDirection()) :
        compAssociationDirectionHelper(base.getSupportDirection(), reverseDirection(compare.getSupportDirection()));
      boolean isDirectionChanged = !isAssocNameExchanged ?
        (base.getSupportDirection() != compare.getSupportDirection()) :
        (base.getSupportDirection() != reverseDirection(compare.getSupportDirection()));
      categoryResult = compAssociationCategoryByDirectionHelper(isDirectionChanged, isAssocNameExchanged, directionResult);
      switch (categoryResult) {
        case DIRECTION_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(
            base,
            isInCompareSG,
            isDirectionChanged,
            categoryResult,
            Optional.of(CompareGroup.WhichPartDiff.DIRECTION),
            Optional.of(directionResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(
            base,
            isInCompareSG,
            isDirectionChanged,
            categoryResult));
          break;
      }

      // check left cardinality
      CompareGroup.CompAssociationCardinality leftCardinalityResult = !isAssocNameExchanged ?
        compAssociationCardinalityHelper(base.getSupportLeftClassCardinality(), compare.getSupportLeftClassCardinality()) :
        compAssociationCardinalityHelper(base.getSupportLeftClassCardinality(), compare.getSupportRightClassCardinality());
      boolean isLeftCardinalityDiff = !isAssocNameExchanged ?
        (base.getSupportLeftClassCardinality() != compare.getSupportLeftClassCardinality()) :
        (base.getSupportLeftClassCardinality() != compare.getSupportRightClassCardinality());
      categoryResult = compAssociationCategoryByCardinalityHelper(isLeftCardinalityDiff, leftCardinalityResult);
      switch (categoryResult) {
        case CARDINALITY_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(
            base,
            isInCompareSG,
            isLeftCardinalityDiff,
            categoryResult,
            Optional.of(CompareGroup.WhichPartDiff.LEFT_CARDINALITY), Optional.of(leftCardinalityResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(
            base,
            isInCompareSG,
            isLeftCardinalityDiff,
            categoryResult));
          break;
      }

      // check right cardinality
      CompareGroup.CompAssociationCardinality rightCardinalityResult = !isAssocNameExchanged ?
        (compAssociationCardinalityHelper(base.getSupportRightClassCardinality(), compare.getSupportRightClassCardinality())) :
        (compAssociationCardinalityHelper(base.getSupportRightClassCardinality(), compare.getSupportLeftClassCardinality()));
      boolean isRightCardinalityDiff = !isAssocNameExchanged ?
        (base.getSupportRightClassCardinality() != compare.getSupportRightClassCardinality()) :
        (base.getSupportRightClassCardinality() != compare.getSupportLeftClassCardinality());
      categoryResult = compAssociationCategoryByCardinalityHelper(isRightCardinalityDiff, rightCardinalityResult);
      switch (categoryResult) {
        case CARDINALITY_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(
            base,
            isInCompareSG,
            isRightCardinalityDiff,
            categoryResult,
            Optional.of(CompareGroup.WhichPartDiff.RIGHT_CARDINALITY),
            Optional.of(rightCardinalityResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(
            base,
            isInCompareSG,
            isRightCardinalityDiff,
            categoryResult));
          break;
      }
    } else {
      compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(
        base,
        isInCompareSG,
        true,
        CompareGroup.CompAssociationCategory.DELETED));
    }
  }

}
