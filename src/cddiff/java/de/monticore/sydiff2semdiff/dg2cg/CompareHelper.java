package de.monticore.sydiff2semdiff.dg2cg;

import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;

import java.util.*;
import java.util.stream.Collectors;

public class CompareHelper {

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * get the corresponding prefix compare class name by compClassKind
   */
  public static String getCompClassKindStrHelper(CompareGroup.CompClassKind compClassKind) {
    switch (compClassKind) {
      case COMP_CLASS:
        return "CompClass";
      case COMP_ENUM:
        return "CompEnum";
      case COMP_ABSTRACT_CLASS:
        return "CompAbstractClass";
      case COMP_INTERFACE:
        return "CompInterface";
      default:
        return null;
    }
  }

  /**
   * get the corresponding compare kind for class by diffClassKind
   */
  public static CompareGroup.CompClassKind getCompClassKindHelper(DifferentGroup.DiffClassKind diffClassKind) {
    switch (diffClassKind) {
      case DIFF_CLASS:
        return CompareGroup.CompClassKind.COMP_CLASS;
      case DIFF_ENUM:
        return CompareGroup.CompClassKind.COMP_ENUM;
      case DIFF_ABSTRACT_CLASS:
        return CompareGroup.CompClassKind.COMP_ABSTRACT_CLASS;
      case DIFF_INTERFACE:
        return CompareGroup.CompClassKind.COMP_INTERFACE;
      default:
        return null;
    }
  }

  /**
   * generate the list of which attributes are different between based DiffClass and compared DiffClass
   */
  public static List<String> compClassWhichAttributesDiffHelper(DiffClass based, DiffClass compared) {

    if (compared == null) {
      List<String> attributesDiffList = new ArrayList<>();
      attributesDiffList = based.getAttributes().keySet().stream().collect(Collectors.toList());
      return attributesDiffList;
    } else {
      List<String> attributesDiffList = new ArrayList<>();
      // check each attributes
      based.getAttributes().forEach((attrName, attrMap) -> {
        // check attributes name
        if (compared.getAttributes().containsKey(attrName)) {
          // check attributes type
          if (!based.getDiffKind().equals(DifferentGroup.DiffClassKind.DIFF_ENUM)) {
            if (!attrMap.get("type").equals(compared.getAttributes().get(attrName).get("type"))) {
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
   * return the compare class category that helps to determine if there is a semantic difference
   */
  public static CompareGroup.CompClassCategory compClassCategoryHelper(DiffClass based, DiffClass compared, boolean isContentDiff) {
    // check whether attributes in BasedDiffClass are the subset of attributes in CompareDiffClass
    if (!isContentDiff) {
      if (compared.getAttributes().keySet().containsAll(based.getAttributes().keySet()) && compared.getAttributes().size() > based.getAttributes().size()) {
        return CompareGroup.CompClassCategory.SUBSET;
      }
      else {
        return CompareGroup.CompClassCategory.ORIGINAL;
      }
    }
    else {
      return CompareGroup.CompClassCategory.EDITED;
    }
  }

  /**
   * helper for creating compare class without attributesDiffList
   */
  public static CompClass createCompClassHelper(DiffClass based, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompClassCategory category) {
    CompClass compClass = new CompClass(based, isInComparedDG, isContentDiff, category);
    compClass.setWhichAttributesDiff(Optional.empty());
    return compClass;
  }

  /**
   * helper for creating compare class with attributesDiffList
   */
  public static CompClass createCompClassHelper(DiffClass based, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompClassCategory category, List<String> attributesDiffList) {
    CompClass compClass = createCompClassHelper(based, isInComparedDG, isContentDiff, category);
    compClass.setWhichAttributesDiff(Optional.of(attributesDiffList));
    return compClass;
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * return the result for cardinality of association after comparison between based DiffAssociation and compared DiffAssociation
   */
  public static CompareGroup.CompAssociationCardinality compAssociationCardinalityHelper(DifferentGroup.DiffAssociationCardinality basedDiffAssociationCardinality, DifferentGroup.DiffAssociationCardinality comparedDiffAssociationCardinality) {
    switch (basedDiffAssociationCardinality) {
      case ONE:
        switch (comparedDiffAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.NONE;
          case ZORE_TO_ONE:
            return CompareGroup.CompAssociationCardinality.NONE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.NONE;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
      case ZORE_TO_ONE:
        switch (comparedDiffAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.ZERO;
          case ZORE_TO_ONE:
            return CompareGroup.CompAssociationCardinality.NONE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.ZERO;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
      case ONE_TO_MORE:
        switch (comparedDiffAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.TWO_TO_MORE;
          case ZORE_TO_ONE:
            return CompareGroup.CompAssociationCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.NONE;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
      default:
        switch (comparedDiffAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.ZERO_AND_TWO_TO_MORE;
          case ZORE_TO_ONE:
            return CompareGroup.CompAssociationCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.ZERO;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
    }
  }

  /**
   * return the result for direction of association after comparison between based DiffAssociation and compared DiffAssociation
   */
  public static CompareGroup.CompAssociationDirection compAssociationDirectionHelper(DifferentGroup.DiffAssociationDirection basedDiffDirection, DifferentGroup.DiffAssociationDirection comparedDiffDirection) {
    switch (basedDiffDirection) {
      case LEFT_TO_RIGHT:
        switch (comparedDiffDirection) {
          case LEFT_TO_RIGHT:
            return CompareGroup.CompAssociationDirection.NONE;
          case RIGHT_TO_LEFT:
            return CompareGroup.CompAssociationDirection.LEFT_TO_RIGHT;
          case BIDIRECTIONAL:
            return CompareGroup.CompAssociationDirection.LEFT_TO_RIGHT;
          default:
            return CompareGroup.CompAssociationDirection.NONE;
        }
      case RIGHT_TO_LEFT:
        switch (comparedDiffDirection) {
          case LEFT_TO_RIGHT:
            return CompareGroup.CompAssociationDirection.RIGHT_TO_LEFT;
          case RIGHT_TO_LEFT:
            return CompareGroup.CompAssociationDirection.NONE;
          case BIDIRECTIONAL:
            return CompareGroup.CompAssociationDirection.RIGHT_TO_LEFT;
          default:
            return CompareGroup.CompAssociationDirection.NONE;
        }
      case BIDIRECTIONAL:
        switch (comparedDiffDirection) {
          case LEFT_TO_RIGHT:
            return CompareGroup.CompAssociationDirection.BIDIRECTIONAL;
          case RIGHT_TO_LEFT:
            return CompareGroup.CompAssociationDirection.BIDIRECTIONAL;
          case BIDIRECTIONAL:
            return CompareGroup.CompAssociationDirection.NONE;
          default:
            return CompareGroup.CompAssociationDirection.NONE;
        }
      default:
        switch (comparedDiffDirection) {
          case LEFT_TO_RIGHT:
            return CompareGroup.CompAssociationDirection.RIGHT_TO_LEFT;
          case RIGHT_TO_LEFT:
            return CompareGroup.CompAssociationDirection.LEFT_TO_RIGHT;
          case BIDIRECTIONAL:
            return CompareGroup.CompAssociationDirection.LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT;
          default:
            return CompareGroup.CompAssociationDirection.NONE;
        }
    }
  }

  /**
   * return the compare association category that helps to determine if there is a semantic difference for direction
   */
  public static CompareGroup.CompAssociationCategory compAssociationCategoryByDirectionHelper(boolean isDirectionChanged, boolean isAssocNameExchanged, CompareGroup.CompAssociationDirection directionResult) {
    if (isDirectionChanged) {
      // check directionResult
      switch (directionResult) {
        case NONE:
          return CompareGroup.CompAssociationCategory.DIRECTION_SUBSET;
        default:
          return CompareGroup.CompAssociationCategory.DIRECTION_CHANGED;
      }
    }
    else {
      if (isAssocNameExchanged) {
        return CompareGroup.CompAssociationCategory.DIRECTION_CHANGED_BUT_SAME_MEANING;
      }
      else {
        return CompareGroup.CompAssociationCategory.ORIGINAL;
      }
    }
  }

  /**
   * return the compare association category that helps to determine if there is a semantic difference for cardinality
   */
  public static CompareGroup.CompAssociationCategory compAssociationCategoryByCardinalityHelper(boolean isCardinalityDiff, CompareGroup.CompAssociationCardinality cardinalityResult) {
    if (isCardinalityDiff) {
      // check cardinalityResult
      switch (cardinalityResult) {
        case NONE:
          return CompareGroup.CompAssociationCategory.CARDINALITY_SUBSET;
        default:
          return CompareGroup.CompAssociationCategory.CARDINALITY_CHANGED;
      }
    }
    else {
      return CompareGroup.CompAssociationCategory.ORIGINAL;
    }
  }

  /**
   * create a new DiffAssociation that exchange the left side information and the right side information
   * but this new DiffAssociation is not added into DiffAssociationGroup
   */
  public static DiffAssociation exchangeLeftDiffClassWithRightDiffClass(DiffAssociation original) {
    DiffAssociation exchanged = new DiffAssociation(original.getOriginalElement(), false, true);

    // change right to left
    exchanged.setDiffLeftClass(original.getDiffRightClass());

    // change left to right
    exchanged.setDiffRightClass(original.getDiffLeftClass());

    return exchanged;
  }

  /**
   * get the association name for the new DiffAssociation that exchange the left side information and the right side information
   */
  public static String getExchangedLeftDiffClassWithRightDiffClassAssocName(DiffAssociation original) {
    String prefix = original.getName().split("_")[0];
    String leftClassName = original.getDiffLeftClass().getOriginalClassName();
    String leftRoleName = original.getDiffLeftClassRoleName();
    String rightRoleName = original.getDiffRightClassRoleName();
    String rightClassName = original.getDiffRightClass().getOriginalClassName();

    return prefix + "_" + rightClassName + "_" + rightRoleName + "_" + leftRoleName + "_" + leftClassName;
  }

  /**
   * get the corresponding compare kind for association by diffAssociationKind
   */
  public static CompareGroup.CompAssociationKind getCompAssociationKindHelper(DifferentGroup.DiffAssociationKind diffAssociationKind) {
    switch (diffAssociationKind) {
      case DIFF_ASC:
        return CompareGroup.CompAssociationKind.COMP_ASC;
      case DIFF_INHERIT_ASC:
        return CompareGroup.CompAssociationKind.COMP_INHERIT_ASC;
      default:
        return null;
    }
  }

  /**
   * helper for creating compare association without whichPartDiff and the result after comparison
   */
  public static CompAssociation createCompareAssociationHelper(DiffAssociation based, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompAssociationCategory category) {
    CompAssociation compAssociation = new CompAssociation(based, isInComparedDG, isContentDiff, category);
    compAssociation.setCompDirectionResult(Optional.empty());
    compAssociation.setCompLeftClassCardinalityResult(Optional.empty());
    compAssociation.setCompRightClassCardinalityResult(Optional.empty());
    compAssociation.setWhichPartDiff(Optional.empty());
    return compAssociation;
  }

  /**
   * helper for creating compare association with whichPartDiff and the result after comparison
   */
  public static CompAssociation createCompareAssociationHelper(DiffAssociation based, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompAssociationCategory category, Optional<CompareGroup.WhichPartDiff> whichPartDiff, Optional<Object> compResult) {
    CompAssociation compAssociation = createCompareAssociationHelper(based, isInComparedDG, isContentDiff, category);
    compAssociation.setWhichPartDiff(whichPartDiff);

    if (whichPartDiff.isPresent() && compResult.isPresent()) {
      switch (whichPartDiff.get()) {
        case DIRECTION:
          compAssociation.setCompDirectionResult(Optional.of((CompareGroup.CompAssociationDirection) compResult.get()));
          compAssociation.setCompLeftClassCardinalityResult(Optional.empty());
          compAssociation.setCompRightClassCardinalityResult(Optional.empty());
          break;
        case LEFT_CARDINALITY:
          compAssociation.setCompDirectionResult(Optional.empty());
          compAssociation.setCompLeftClassCardinalityResult(Optional.of((CompareGroup.CompAssociationCardinality) compResult.get()));
          compAssociation.setCompRightClassCardinalityResult(Optional.empty());
          break;
        case RIGHT_CARDINALITY:
          compAssociation.setCompDirectionResult(Optional.empty());
          compAssociation.setCompLeftClassCardinalityResult(Optional.empty());
          compAssociation.setCompRightClassCardinalityResult(Optional.of((CompareGroup.CompAssociationCardinality) compResult.get()));
          break;
      }
    }
    return compAssociation;
  }

}
