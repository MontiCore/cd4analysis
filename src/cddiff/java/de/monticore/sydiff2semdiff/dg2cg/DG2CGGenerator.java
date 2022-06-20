package de.monticore.sydiff2semdiff.dg2cg;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;

import java.util.*;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.*;
import static de.monticore.sydiff2semdiff.dg2cg.CompareHelper.*;

public class DG2CGGenerator {

  protected Deque<CompClass> compClassResultQueueWithDiff = new LinkedList<>();
  protected Deque<CompAssociation> compAssociationResultQueueWithDiff = new LinkedList<>();
  protected Deque<CompClass> compClassResultQueueWithoutDiff = new LinkedList<>();
  protected Deque<CompAssociation> compAssociationResultQueueWithoutDiff = new LinkedList<>();

  /**
   * generating CompareGroup
   */
  public CompareGroup generateCompareGroup(DifferentGroup basedDG, DifferentGroup comparedDG) {
    CompareGroup compareGroup = new CompareGroup(basedDG, comparedDG);
    compareClasses(basedDG, comparedDG);
    compareAssociations(basedDG, comparedDG);

    compareGroup.setBasedDG(basedDG);
    compareGroup.setComparedDG(comparedDG);
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
   * create CompClass for each DiffClass in based DifferentGroup
   */
  public void compareClasses(DifferentGroup basedDG, DifferentGroup comparedDG) {
    basedDG.getDiffClassGroup().forEach((className, basedDiffClass) -> {
      createCompareClass(basedDG, basedDiffClass, comparedDG.getDiffClassGroup().get(className), comparedDG.getDiffClassGroup().containsKey(className));
    });
  }

  /**
   * generate CompClass object and
   * put into compClassResultQueueWithDiff if exists semantic difference,
   * put into compClassResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCompareClass(DifferentGroup basedDG, DiffClass based, DiffClass compared, boolean isInComparedDG) {

    // check whether DiffClass Name is also in ComparedDG
    if (isInComparedDG) {

      // calculate WhichAttributesDiffList
      List<String> attributesDiffList = compClassWhichAttributesDiffHelper(based, Optional.of(compared));

      // calculate boolean isContentDiff
      boolean isContentDiff = attributesDiffList.size() != 0 ? true : false;

      // calculate class category
      CompareGroup.CompClassCategory category = compClassCategoryHelper(based, compared, isContentDiff);

      // create compClass
      CompClass compClass = createCompClassHelper(based, isInComparedDG, isContentDiff, category, attributesDiffList);

      // distinguish compClass by CompCategory
      if (compClass.getCompCategory() == CompareGroup.CompClassCategory.DELETED || compClass.getCompCategory() == CompareGroup.CompClassCategory.EDITED) {
        compClassResultQueueWithDiff.offer(compClass);
      } else {
        compClassResultQueueWithoutDiff.offer(compClass);
      }

    } else {
      compClassResultQueueWithDiff.offer(createCompClassHelper(based, isInComparedDG, true, CompareGroup.CompClassCategory.DELETED, compClassWhichAttributesDiffHelper(based, Optional.empty())));
    }
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create CompAssociaion for each DiffAssociation in based DifferentGroup
   */
  public void compareAssociations(DifferentGroup basedDG, DifferentGroup comparedDG) {
    basedDG.getDiffAssociationGroup().forEach((assocName1, basedDiffAssociation) -> {

      DiffAssociation intersectedBasedDiffAssociation = intersectDiffAssociationCardinalityByDiffAssociation(basedDiffAssociation, basedDG);

      // get all associations including reversed association in ComparedDG by matching [leftClass], [leftRoleName], [rightRoleName], [rightClass]
      List<Map<String, Object>> DiffAssocMapInComparedDG = fuzzySearchDiffAssociationByDiffAssociationWithoutDirection(comparedDG.getDiffAssociationGroup(), intersectedBasedDiffAssociation);
      List<DiffAssociation> forwardDiffAssocListInComparedDG = new ArrayList<>();
      List<DiffAssociation> reverseDiffAssocListInComparedDG = new ArrayList<>();
      DiffAssocMapInComparedDG.forEach(e -> {
        if ((boolean) e.get("isReverse") == false) {
          forwardDiffAssocListInComparedDG.add((DiffAssociation) e.get("diffAssociation"));
        } else {
          reverseDiffAssocListInComparedDG.add((DiffAssociation) e.get("diffAssociation"));
        }
      });

      boolean isInComparedD4ForwardAssocName = forwardDiffAssocListInComparedDG.size() > 0 ? true : false;
      boolean isInComparedDG4ReverseAssocName = reverseDiffAssocListInComparedDG.size() > 0 ? true : false;

      if (isInComparedD4ForwardAssocName && !isInComparedDG4ReverseAssocName) {
        forwardDiffAssocListInComparedDG.forEach(comparedDiffAssociation ->
          createCompareAssociation(intersectedBasedDiffAssociation,
            Optional.of(intersectDiffAssociationCardinalityByDiffAssociation(comparedDiffAssociation, comparedDG)), true, false));
      } else if (!isInComparedD4ForwardAssocName && isInComparedDG4ReverseAssocName) {
        reverseDiffAssocListInComparedDG.forEach(comparedDiffAssociation ->
          createCompareAssociation(intersectedBasedDiffAssociation,
            Optional.of(intersectDiffAssociationCardinalityByDiffAssociation(comparedDiffAssociation, comparedDG)), true, true));
      } else {
        createCompareAssociation(intersectedBasedDiffAssociation, Optional.empty(), false, false);
      }
    });
  }

  /**
   * generate CompAssociation object and
   * put into compAssociationResultQueueWithDiff if exists semantic difference,
   * put into compAssociationResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCompareAssociation(DiffAssociation based, Optional<DiffAssociation> optCompared, boolean isInComparedDG, boolean isAssocNameExchanged) {

    if (isInComparedDG) {
      DiffAssociation compared = optCompared.get();
      CompareGroup.CompAssociationCategory categoryResult = null;

      // check direction type
      CompareGroup.CompAssociationDirection directionResult = !isAssocNameExchanged ?
        compAssociationDirectionHelper(based.getDiffDirection(), compared.getDiffDirection()) :
        compAssociationDirectionHelper(based.getDiffDirection(), reverseDirection(compared.getDiffDirection()));
      boolean isDirectionChanged = !isAssocNameExchanged ?
        (based.getDiffDirection() == compared.getDiffDirection() ? false : true) :
        (based.getDiffDirection() == reverseDirection(compared.getDiffDirection()) ? false : true);
      categoryResult = compAssociationCategoryByDirectionHelper(isDirectionChanged, isAssocNameExchanged, directionResult);
      switch (categoryResult) {
        case DIRECTION_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isDirectionChanged, categoryResult, Optional.of(CompareGroup.WhichPartDiff.DIRECTION), Optional.of(directionResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isDirectionChanged, categoryResult));
          break;
      }

      // check left cardinality
      CompareGroup.CompAssociationCardinality leftCardinalityResult = !isAssocNameExchanged ?
        compAssociationCardinalityHelper(based.getDiffLeftClassCardinality(), compared.getDiffLeftClassCardinality()) :
        compAssociationCardinalityHelper(based.getDiffLeftClassCardinality(), compared.getDiffRightClassCardinality());
      boolean isLeftCardinalityDiff = !isAssocNameExchanged ?
        (based.getDiffLeftClassCardinality() == compared.getDiffLeftClassCardinality() ? false : true) :
        (based.getDiffLeftClassCardinality() == compared.getDiffRightClassCardinality() ? false : true);
      categoryResult = compAssociationCategoryByCardinalityHelper(isLeftCardinalityDiff, leftCardinalityResult);
      switch (categoryResult) {
        case CARDINALITY_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isLeftCardinalityDiff, categoryResult, Optional.of(CompareGroup.WhichPartDiff.LEFT_CARDINALITY), Optional.of(leftCardinalityResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isLeftCardinalityDiff, categoryResult));
          break;
      }

      // check right cardinality
      CompareGroup.CompAssociationCardinality rightCardinalityResult = !isAssocNameExchanged ?
        (compAssociationCardinalityHelper(based.getDiffRightClassCardinality(), compared.getDiffRightClassCardinality())) :
        (compAssociationCardinalityHelper(based.getDiffRightClassCardinality(), compared.getDiffLeftClassCardinality()));
      boolean isRightCardinalityDiff = !isAssocNameExchanged ?
        (based.getDiffRightClassCardinality() == compared.getDiffRightClassCardinality() ? false : true) :
        (based.getDiffRightClassCardinality() == compared.getDiffLeftClassCardinality() ? false : true);
      categoryResult = compAssociationCategoryByCardinalityHelper(isRightCardinalityDiff, rightCardinalityResult);
      switch (categoryResult) {
        case CARDINALITY_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isRightCardinalityDiff, categoryResult, Optional.of(CompareGroup.WhichPartDiff.RIGHT_CARDINALITY), Optional.of(rightCardinalityResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isRightCardinalityDiff, categoryResult));
          break;
      }
    } else {
      compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(based, isInComparedDG, true, CompareGroup.CompAssociationCategory.DELETED));
    }
  }

}
