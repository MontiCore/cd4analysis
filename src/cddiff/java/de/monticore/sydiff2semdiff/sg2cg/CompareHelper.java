package de.monticore.sydiff2semdiff.sg2cg;

import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportGroup;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompareGroup;

import java.util.*;

public class CompareHelper {

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * get the corresponding compare kind for class by supportClassKind
   */
  public static CompareGroup.CompClassKind getCompClassKindHelper(SupportGroup.SupportClassKind supportClassKind) {
    switch (supportClassKind) {
      case SUPPORT_CLASS:
        return CompareGroup.CompClassKind.COMP_CLASS;
      case SUPPORT_ENUM:
        return CompareGroup.CompClassKind.COMP_ENUM;
      case SUPPORT_ABSTRACT_CLASS:
        return CompareGroup.CompClassKind.COMP_ABSTRACT_CLASS;
      case SUPPORT_INTERFACE:
        return CompareGroup.CompClassKind.COMP_INTERFACE;
      default:
        return null;
    }
  }

  /**
   * get the corresponding prefix compare class name by compClassKind
   */
  public static String getCompClassKindStrHelper(CompareGroup.CompClassKind compClassKind, boolean is4Print) {
    switch (compClassKind) {
      case COMP_CLASS:
        return is4Print ? "Class" : "CompClass";
      case COMP_ENUM:
        return is4Print ? "Enum" : "CompEnum";
      case COMP_ABSTRACT_CLASS:
        return is4Print ? "AbstractClass" : "CompAbstractClass";
      case COMP_INTERFACE:
        return is4Print ? "Interface" : "CompInterface";
      default:
        return null;
    }
  }

  /**
   * generate the list of which attributes are different between base SupportClass and compare SupportClass
   */
  public static List<String> compClassWhichAttributesDiffHelper(SupportClass base, Optional<SupportClass> optCompare) {
    if (optCompare.isEmpty()) {
      return new ArrayList<>(base.getAttributes().keySet());
    } else {
      SupportClass compare = optCompare.get();
      List<String> attributesDiffList = new ArrayList<>();
      // check each attributes
      base.getAttributes().forEach((attrName, attrType) -> {
        // check attributes name
        if (compare.getAttributes().containsKey(attrName)) {
          // check attributes type
          if (!base.getSupportKind().equals(SupportGroup.SupportClassKind.SUPPORT_ENUM)) {
            if (!attrType.equals(compare.getAttributes().get(attrName))) {
              // edited
              attributesDiffList.add(attrName);
            }
          }
        } else {
          attributesDiffList.add(attrName);
        }
      });
      return attributesDiffList;
    }
  }

  /**
   * return the compare class category that helps to determine if there is a semantic difference
   */
  public static CompareGroup.CompClassCategory compClassCategoryHelper(SupportClass base,
                                                                       SupportClass compare,
                                                                       boolean isContentDiff) {
    // check whether attributes in BaseSupportClass are the subset of attributes in CompareSupportClass
    if (!isContentDiff) {
      if (compare.getAttributes().keySet().containsAll(base.getAttributes().keySet()) &&
        compare.getAttributes().size() > base.getAttributes().size()) {
        return CompareGroup.CompClassCategory.SUBSET;
      } else {
        return CompareGroup.CompClassCategory.ORIGINAL;
      }
    } else {
      return CompareGroup.CompClassCategory.EDITED;
    }
  }

  /**
   * helper for creating compare class without attributesDiffList
   */
  public static CompClass createCompClassHelper(SupportClass base,
                                                boolean isInCompareSG,
                                                boolean isContentDiff,
                                                CompareGroup.CompClassCategory category) {
    CompClass compClass = new CompClass(base, isInCompareSG, isContentDiff, category);
    compClass.setWhichAttributesDiff(Optional.empty());
    return compClass;
  }

  /**
   * helper for creating compare class with attributesDiffList
   */
  public static CompClass createCompClassHelper(SupportClass base,
                                                boolean isInCompareSG,
                                                boolean isContentDiff,
                                                CompareGroup.CompClassCategory category,
                                                List<String> attributesDiffList) {
    CompClass compClass = createCompClassHelper(base, isInCompareSG, isContentDiff, category);
    compClass.setWhichAttributesDiff(Optional.of(attributesDiffList));
    return compClass;
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * return the result for cardinality of association after comparison
   * between base SupportAssociation and compare SupportAssociation
   */
  public static CompareGroup.CompAssociationCardinality compAssociationCardinalityHelper(SupportGroup.SupportAssociationCardinality baseSupportAssociationCardinality,
                                                                                         SupportGroup.SupportAssociationCardinality compareSupportAssociationCardinality) {
    switch (baseSupportAssociationCardinality) {
      case ONE:
        switch (compareSupportAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.NONE;
          case ZERO_TO_ONE:
            return CompareGroup.CompAssociationCardinality.NONE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.NONE;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
      case ZERO_TO_ONE:
        switch (compareSupportAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.ZERO;
          case ZERO_TO_ONE:
            return CompareGroup.CompAssociationCardinality.NONE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.ZERO;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
      case ONE_TO_MORE:
        switch (compareSupportAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.TWO_TO_MORE;
          case ZERO_TO_ONE:
            return CompareGroup.CompAssociationCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.NONE;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
      default:
        switch (compareSupportAssociationCardinality) {
          case ONE:
            return CompareGroup.CompAssociationCardinality.ZERO_AND_TWO_TO_MORE;
          case ZERO_TO_ONE:
            return CompareGroup.CompAssociationCardinality.TWO_TO_MORE;
          case ONE_TO_MORE:
            return CompareGroup.CompAssociationCardinality.ZERO;
          default:
            return CompareGroup.CompAssociationCardinality.NONE;
        }
    }
  }

  /**
   * return the result for direction of association after comparison
   * between base SupportAssociation and compare SupportAssociation
   */
  public static CompareGroup.CompAssociationDirection compAssociationDirectionHelper(SupportGroup.SupportAssociationDirection baseSupportDirection,
                                                                                     SupportGroup.SupportAssociationDirection compareSupportDirection) {
    switch (baseSupportDirection) {
      case LEFT_TO_RIGHT:
        switch (compareSupportDirection) {
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
        switch (compareSupportDirection) {
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
        switch (compareSupportDirection) {
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
        switch (compareSupportDirection) {
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
  public static CompareGroup.CompAssociationCategory compAssociationCategoryByDirectionHelper(boolean isDirectionChanged,
                                                                                              boolean isAssocNameExchanged,
                                                                                              CompareGroup.CompAssociationDirection directionResult) {
    if (isDirectionChanged) {
      // check directionResult
      if (directionResult == CompareGroup.CompAssociationDirection.NONE) {
        return CompareGroup.CompAssociationCategory.DIRECTION_SUBSET;
      }
      return CompareGroup.CompAssociationCategory.DIRECTION_CHANGED;
    } else {
      if (isAssocNameExchanged) {
        return CompareGroup.CompAssociationCategory.DIRECTION_CHANGED_BUT_SAME_MEANING;
      } else {
        return CompareGroup.CompAssociationCategory.ORIGINAL;
      }
    }
  }

  /**
   * return the compare association category that helps to determine if there is a semantic difference for cardinality
   */
  public static CompareGroup.CompAssociationCategory compAssociationCategoryByCardinalityHelper(boolean isCardinalityDiff,
                                                                                                CompareGroup.CompAssociationCardinality cardinalityResult) {
    if (isCardinalityDiff) {
      // check cardinalityResult
      if (cardinalityResult == CompareGroup.CompAssociationCardinality.NONE) {
        return CompareGroup.CompAssociationCategory.CARDINALITY_SUBSET;
      }
      return CompareGroup.CompAssociationCategory.CARDINALITY_CHANGED;
    } else {
      return CompareGroup.CompAssociationCategory.ORIGINAL;
    }
  }

  /**
   * get the corresponding compare kind for association by supportAssociationKind
   */
  public static CompareGroup.CompAssociationKind getCompAssociationKindHelper(SupportGroup.SupportAssociationKind supportAssociationKind) {
    switch (supportAssociationKind) {
      case SUPPORT_ASC:
        return CompareGroup.CompAssociationKind.COMP_ASC;
      case SUPPORT_INHERIT_ASC:
        return CompareGroup.CompAssociationKind.COMP_INHERIT_ASC;
      default:
        return null;
    }
  }

  /**
   * helper for creating compare association without whichPartDiff and the result after comparison
   */
  public static CompAssociation createCompareAssociationHelper(SupportAssociation base,
                                                               boolean isInCompareSG,
                                                               boolean isContentDiff,
                                                               CompareGroup.CompAssociationCategory category) {
    CompAssociation compAssociation = new CompAssociation(base, isInCompareSG, isContentDiff, category);
    compAssociation.setCompDirectionResult(Optional.empty());
    compAssociation.setCompLeftClassCardinalityResult(Optional.empty());
    compAssociation.setCompRightClassCardinalityResult(Optional.empty());
    compAssociation.setWhichPartDiff(Optional.empty());
    return compAssociation;
  }

  /**
   * helper for creating compare association with whichPartDiff and the result after comparison
   */
  public static CompAssociation createCompareAssociationHelper(SupportAssociation base,
                                                               boolean isInCompareSG,
                                                               boolean isContentDiff,
                                                               CompareGroup.CompAssociationCategory category,
                                                               Optional<CompareGroup.WhichPartDiff> whichPartDiff,
                                                               Optional<Object> compResult) {
    CompAssociation compAssociation = createCompareAssociationHelper(base, isInCompareSG, isContentDiff, category);
    compAssociation.setWhichPartDiff(whichPartDiff);

    if (whichPartDiff.isPresent() && compResult.isPresent()) {
      switch (whichPartDiff.get()) {
        case DIRECTION:
          compAssociation.setCompDirectionResult(
            Optional.of((CompareGroup.CompAssociationDirection) compResult.get()));
          compAssociation.setCompLeftClassCardinalityResult(Optional.empty());
          compAssociation.setCompRightClassCardinalityResult(Optional.empty());
          break;
        case LEFT_CARDINALITY:
          compAssociation.setCompDirectionResult(Optional.empty());
          compAssociation.setCompLeftClassCardinalityResult(
            Optional.of((CompareGroup.CompAssociationCardinality) compResult.get()));
          compAssociation.setCompRightClassCardinalityResult(Optional.empty());
          break;
        case RIGHT_CARDINALITY:
          compAssociation.setCompDirectionResult(Optional.empty());
          compAssociation.setCompLeftClassCardinalityResult(Optional.empty());
          compAssociation.setCompRightClassCardinalityResult(
            Optional.of((CompareGroup.CompAssociationCardinality) compResult.get()));
          break;
      }
    }
    return compAssociation;
  }
}
