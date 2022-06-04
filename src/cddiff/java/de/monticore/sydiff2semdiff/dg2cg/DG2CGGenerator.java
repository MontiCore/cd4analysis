package de.monticore.sydiff2semdiff.dg2cg;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;

import java.util.*;

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
      List<String> attributesDiffList = compClassWhichAttributesDiffHelper(based, compared);

      // calculate boolean isContentDiff
      boolean isContentDiff = attributesDiffList.size() != 0 ? true : false;

      // calculate class category
      CompareGroup.CompClassCategory category = compClassCategoryHelper(based, compared, isContentDiff);

      // create compClass
      CompClass compClass = createCompClassHelper(based, isInComparedDG, isContentDiff, category, attributesDiffList);

      // distinguish compClass by CompCategory
      if (compClass.getCompCategory() == CompareGroup.CompClassCategory.DELETED || compClass.getCompCategory() == CompareGroup.CompClassCategory.EDITED) {
        compClassResultQueueWithDiff.offer(compClass);
      }
      else {
        compClassResultQueueWithoutDiff.offer(compClass);
      }

    } else {
      compClassResultQueueWithDiff.offer(createCompClassHelper(based, isInComparedDG, true, CompareGroup.CompClassCategory.DELETED, compClassWhichAttributesDiffHelper(based, null)));
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

      String assocName2 = getExchangedLeftDiffClassWithRightDiffClassAssocName(basedDiffAssociation);

      boolean isInComparedDG1 = comparedDG.getDiffAssociationGroup().containsKey(assocName1);
      boolean isInComparedDG2 = comparedDG.getDiffAssociationGroup().containsKey(assocName2);

      if (isInComparedDG1 && !isInComparedDG2) {
        DiffAssociation comparedDiffAssociation = comparedDG.getDiffAssociationGroup().get(assocName1);
        createCompareAssociation(basedDiffAssociation, comparedDiffAssociation, true, false);
      }
      else if (!isInComparedDG1 && isInComparedDG2) {
        DiffAssociation comparedDiffAssociation = null;
        try {
          comparedDiffAssociation = exchangeLeftDiffClassWithRightDiffClass(comparedDG.getDiffAssociationGroup().get(assocName2).clone());
        }
        catch (CloneNotSupportedException e) {
          throw new RuntimeException(e);
        }
        createCompareAssociation(basedDiffAssociation, comparedDiffAssociation, true, true);
      }
      else {
        createCompareAssociation(basedDiffAssociation, null, false, false);
      }
    });
  }

  /**
   * generate CompAssociation object and
   * put into compAssociationResultQueueWithDiff if exists semantic difference,
   * put into compAssociationResultQueueWithoutDiff if exists no semantic difference
   */
  public void createCompareAssociation(DiffAssociation based, DiffAssociation compared, boolean isInComparedDG, boolean isAssocNameExchanged) {
    List<CompAssociation> compAssociationList = new ArrayList<>();

    if (isInComparedDG) {
      CompareGroup.CompAssociationCategory categoryResult = null;

      // check direction type
      CompareGroup.CompAssociationDirection directionResult = compAssociationDirectionHelper(based.getDiffDirection(), compared.getDiffDirection());
      boolean isDirectionChanged = based.getDiffDirection() == compared.getDiffDirection() ? false : true;
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
      CompareGroup.CompAssociationCardinality leftCardinalityResult = compAssociationCardinalityHelper(based.getDiffLeftClassCardinality(), compared.getDiffLeftClassCardinality());
      boolean isLeftCardinalityDiff = based.getDiffLeftClassCardinality() == compared.getDiffLeftClassCardinality() ? false : true;
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
      CompareGroup.CompAssociationCardinality rightCardinalityResult = compAssociationCardinalityHelper(based.getDiffRightClassCardinality(), compared.getDiffRightClassCardinality());
      boolean isRightCardinalityDiff = based.getDiffRightClassCardinality() == compared.getDiffRightClassCardinality() ? false : true;
      categoryResult = compAssociationCategoryByCardinalityHelper(isRightCardinalityDiff, leftCardinalityResult);
      switch (categoryResult) {
        case CARDINALITY_CHANGED:
          compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isRightCardinalityDiff, categoryResult, Optional.of(CompareGroup.WhichPartDiff.RIGHT_CARDINALITY), Optional.of(rightCardinalityResult)));
          break;
        default:
          compAssociationResultQueueWithoutDiff.offer(createCompareAssociationHelper(based, isInComparedDG, isRightCardinalityDiff, categoryResult));
          break;
      }
    }
    else {
      compAssociationResultQueueWithDiff.offer(createCompareAssociationHelper(based, isInComparedDG, true, CompareGroup.CompAssociationCategory.DELETED));
    }
  }

}
