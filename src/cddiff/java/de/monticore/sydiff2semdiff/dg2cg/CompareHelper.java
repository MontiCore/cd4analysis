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

  public static List<String> compClassWhichAttributesDiffHelper(DiffClass based, DiffClass compared) {
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

  public static CompClass createCompClassHelper(DifferentGroup basedDG, DiffClass based, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompClassCategory category) {
    CompClass compClass = new CompClass();
    compClass.setOriginalDiffClass(based);
    compClass.setCompId(UUID.randomUUID());
    compClass.setCompKind(getCompClassKindHelper(based.getDiffKind()));
    compClass.setName(getCompClassKindStrHelper(compClass.getCompKind()) + "_" + based.getName().substring(based.getName().indexOf("_") + 1, based.getName().length()));
    compClass.setInBasedDG(true);
    compClass.setInComparedDG(isInComparedDG);
    compClass.setContentDiff(isContentDiff);
    compClass.setCompCategory(category);

    // add linked class A into EnumClass when the class A extends class B that using this EnumClass
    if (!compClass.getCompKind().equals(CompareGroup.CompClassKind.COMP_ENUM)) {
      compClass.setCompAttributesResult(based.getAttributes().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get("type").contains("DiffEnum") ? basedDG.getDiffClassGroup().get(e.getValue().get("type")).getAttributes().keySet().stream().collect(Collectors.toList()).get(0) : e.getValue().get("type"))));
    }
    else {
      compClass.setCompAttributesResult(based.getAttributes().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> "String")));
    }
    compClass.setWhichAttributesDiff(compClass.getCompAttributesResult().keySet().stream().collect(Collectors.toList()));

    return compClass;
  }

  public static CompClass createCompClassHelper(DifferentGroup basedDG, DiffClass based, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompClassCategory category, List<String> attributesDiffList) {
    CompClass compClass = createCompClassHelper(basedDG, based, isInComparedDG, isContentDiff, category);
    compClass.setWhichAttributesDiff(attributesDiffList);
    return compClass;
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/
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

  public static DiffAssociation exchangeLeftDiffClassWithRightDiffClass(DiffAssociation original) {
    DiffAssociation exchanged = new DiffAssociation();
    // change assoc name
    exchanged.setName(getExchangedLeftDiffClassWithRightDiffClassAssocName(original));

    exchanged.setDiffKind(original.getDiffKind());

    // change assoc direction
    switch (original.getDiffDirection()) {
      case LEFT_TO_RIGHT:
        exchanged.setDiffDirection(DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT);
        break;
      case RIGHT_TO_LEFT:
        exchanged.setDiffDirection(DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT);
        break;
      default:
        exchanged.setDiffDirection(original.getDiffDirection());
        break;
    }

    // change right to left
    exchanged.setDiffLeftClass(original.getDiffRightClass());
    exchanged.setDiffLeftClassCardinality(original.getDiffRightClassCardinality());
    exchanged.setDiffLeftClassRoleName(original.getDiffRightClassRoleName());

    // change left to right
    exchanged.setDiffRightClass(original.getDiffLeftClass());
    exchanged.setDiffRightClassCardinality(original.getDiffLeftClassCardinality());
    exchanged.setDiffRightClassRoleName(original.getDiffLeftClassRoleName());

    exchanged.setOriginalElement(original.getOriginalElement());

    return exchanged;
  }

  public static String getExchangedLeftDiffClassWithRightDiffClassAssocName(DiffAssociation originalDiffAssociation) {
    String assocName = originalDiffAssociation.getName();
    String prefix = assocName.split("_")[0];
    String leftClassName = assocName.split("_")[1];
    String leftRoleName = assocName.split("_")[2];
    String rightRoleName = assocName.split("_")[3];
    String rightClassName = assocName.split("_")[4];
    return prefix + "_" + rightClassName + "_" + rightRoleName + "_" + leftRoleName + "_" + leftClassName;
  }

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

  public static CompAssociation createCompareAssociationHelper(DiffAssociation diffAssociation, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompAssociationCategory category) {
    CompAssociation compAssociation = new CompAssociation();
    compAssociation.setCompId(UUID.randomUUID());
    compAssociation.setName("CompAssociation_" + diffAssociation.getName().substring(diffAssociation.getName().indexOf("_") + 1, diffAssociation.getName().length()));
    compAssociation.setCompKind(getCompAssociationKindHelper(diffAssociation.getDiffKind()));
    compAssociation.setInBasedDG(true);
    compAssociation.setInComparedDG(isInComparedDG);
    compAssociation.setContentDiff(isContentDiff);
    compAssociation.setCompCategoryResult(category);

    compAssociation.setCompLeftClassResult(diffAssociation.getDiffLeftClass());
    compAssociation.setCompLeftClassRoleName(diffAssociation.getDiffLeftClassRoleName());

    compAssociation.setCompRightClassResult(diffAssociation.getDiffRightClass());
    compAssociation.setCompRightClassRoleName(diffAssociation.getDiffRightClassRoleName());

    compAssociation.setOriginalDiffAssociation(diffAssociation);
    compAssociation.setWhichPartDiff(Optional.empty());

    compAssociation.setCompDirectionResult(CompareGroup.CompAssociationDirection.NONE);
    compAssociation.setCompLeftClassCardinalityResult(CompareGroup.CompAssociationCardinality.NONE);
    compAssociation.setCompRightClassCardinalityResult(CompareGroup.CompAssociationCardinality.NONE);

    return compAssociation;
  }

  public static CompAssociation createCompareAssociationHelper(DiffAssociation diffAssociation, boolean isInComparedDG, boolean isContentDiff, CompareGroup.CompAssociationCategory category, Optional<CompareGroup.WhichPartDiff> whichPartDiff, Optional<Object> compResult) {
    CompAssociation compAssociation = createCompareAssociationHelper(diffAssociation, isInComparedDG, isContentDiff, category);
    compAssociation.setWhichPartDiff(whichPartDiff);

    if (whichPartDiff.isPresent() && compResult.isPresent()) {
      switch (whichPartDiff.get()) {
        case DIRECTION:
          compAssociation.setCompDirectionResult((CompareGroup.CompAssociationDirection) compResult.get());
          compAssociation.setCompLeftClassCardinalityResult(CompareGroup.CompAssociationCardinality.NONE);
          compAssociation.setCompRightClassCardinalityResult(CompareGroup.CompAssociationCardinality.NONE);
          break;
        case LEFT_CARDINALITY:
          compAssociation.setCompDirectionResult(CompareGroup.CompAssociationDirection.NONE);
          compAssociation.setCompLeftClassCardinalityResult((CompareGroup.CompAssociationCardinality) compResult.get());
          compAssociation.setCompRightClassCardinalityResult(CompareGroup.CompAssociationCardinality.NONE);
          break;
        case RIGHT_CARDINALITY:
          compAssociation.setCompDirectionResult(CompareGroup.CompAssociationDirection.NONE);
          compAssociation.setCompLeftClassCardinalityResult(CompareGroup.CompAssociationCardinality.NONE);
          compAssociation.setCompRightClassCardinalityResult((CompareGroup.CompAssociationCardinality) compResult.get());
          break;
      }
    }
    return compAssociation;
  }

}
