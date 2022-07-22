package de.monticore.syntax2semdiff.cd2cdwrapper;

import com.google.common.graph.MutableGraph;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CDWrapperHelper {

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * get the corresponding CDTypeWrapper kind by ASTCDType
   */
  public static CDWrapper.CDTypeWrapperKind distinguishASTCDTypeHelper(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      if (astcdType.getModifier().isAbstract()) {
        return CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS;
      }
      else {
        return CDWrapper.CDTypeWrapperKind.CDWRAPPER_CLASS;
      }
    }
    else if (astcdType instanceof ASTCDEnum) {
      return CDWrapper.CDTypeWrapperKind.CDWRAPPER_ENUM;
    }
    else {
      return CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE;
    }
  }

  /**
   * get the corresponding prefix of CDTypeWrapper name by cDTypeWrapperKind
   */
  public static String getCDTypeWrapperKindStrHelper(CDWrapper.CDTypeWrapperKind cDTypeWrapperKind) {
    switch (cDTypeWrapperKind) {
      case CDWRAPPER_CLASS:
        return "CDWrapperClass";
      case CDWRAPPER_ENUM:
        return "CDWrapperEnum";
      case CDWRAPPER_ABSTRACT_CLASS:
        return "CDWrapperAbstractClass";
      case CDWRAPPER_INTERFACE:
        return "CDWrapperInterface";
      default:
        return null;
    }
  }

  /**
   * using the original class name to find corresponding CDTypeWrapper in CDTypeWrapperGroup
   */
  public static CDTypeWrapper findCDTypeWrapper4OriginalClassName(
      Map<String, CDTypeWrapper> cDTypeWrapperGroup, String originalClassName) {
    if (cDTypeWrapperGroup.containsKey("CDWrapperClass_" + originalClassName)) {
      return cDTypeWrapperGroup.get("CDWrapperClass_" + originalClassName);
    }
    else if (cDTypeWrapperGroup.containsKey("CDWrapperAbstractClass_" + originalClassName)) {
      return cDTypeWrapperGroup.get("CDWrapperAbstractClass_" + originalClassName);
    }
    else if (cDTypeWrapperGroup.containsKey("CDWrapperInterface_" + originalClassName)) {
      return cDTypeWrapperGroup.get("CDWrapperInterface_" + originalClassName);
    }
    else {
      return cDTypeWrapperGroup.get("CDWrapperEnum_" + originalClassName);
    }
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * get the corresponding the direction kind of CDAssociationWrapper by ASTCDAssociation
   */
  public static CDWrapper.CDAssociationWrapperDirection distinguishAssociationDirectionHelper(
      ASTCDAssociation astcdAssociation) {
    boolean left = astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean right = astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight();
    boolean bidirectional = astcdAssociation.getCDAssocDir().isBidirectional();
    if (!left && right && !bidirectional) {
      return CDWrapper.CDAssociationWrapperDirection.LEFT_TO_RIGHT;
    }
    else if (left && !right && !bidirectional) {
      return CDWrapper.CDAssociationWrapperDirection.RIGHT_TO_LEFT;
    }
    else if (left && right && bidirectional) {
      return CDWrapper.CDAssociationWrapperDirection.BIDIRECTIONAL;
    }
    else {
      return CDWrapper.CDAssociationWrapperDirection.UNDEFINED;
    }
  }

  /**
   * get the corresponding the left cardinality kind of CDAssociationWrapper by ASTCDAssociation
   */
  public static CDWrapper.CDAssociationWrapperCardinality distinguishLeftAssociationCardinalityHelper(
      ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().getCDCardinality().isOne()) {
      return CDWrapper.CDAssociationWrapperCardinality.ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isOpt()) {
      return CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isAtLeastOne()) {
      return CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE;
    }
    else {
      return CDWrapper.CDAssociationWrapperCardinality.MORE;
    }
  }

  /**
   * get the corresponding the right cardinality kind of CDAssociationWrapper by ASTCDAssociation
   */
  public static CDWrapper.CDAssociationWrapperCardinality distinguishRightAssociationCardinalityHelper(
      ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().getCDCardinality().isOne()) {
      return CDWrapper.CDAssociationWrapperCardinality.ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isOpt()) {
      return CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isAtLeastOne()) {
      return CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE;
    }
    else {
      return CDWrapper.CDAssociationWrapperCardinality.MORE;
    }
  }

  /**
   * get the left class role name in CDAssociationWrapper if it exists in ASTCDAssociation, then
   * direct return the role name otherwise set the lower case of the left class qualified name as
   * role name
   */
  public static String getLeftClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().isPresentCDRole()) {
      return astcdAssociation.getLeft().getCDRole().getName();
    }
    else {
      return astcdAssociation.getLeftQualifiedName().getQName().toLowerCase();
    }
  }

  /**
   * get the right class role name in CDAssociationWrapper if it exists in ASTCDAssociation, then
   * direct return the role name otherwise set the lower case of the right class qualified name as
   * role name
   */
  public static String getRightClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().isPresentCDRole()) {
      return astcdAssociation.getRight().getCDRole().getName();
    }
    else {
      return astcdAssociation.getRightQualifiedName().getQName().toLowerCase();
    }
  }

  /**
   * reverse LEFT_TO_RIGHT and RIGHT_TO_LEFT direction
   */
  public static CDWrapper.CDAssociationWrapperDirection reverseDirection(
      CDWrapper.CDAssociationWrapperDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return CDWrapper.CDAssociationWrapperDirection.RIGHT_TO_LEFT;
      case RIGHT_TO_LEFT:
        return CDWrapper.CDAssociationWrapperDirection.LEFT_TO_RIGHT;
      default:
        return direction;
    }
  }

  /**
   * format direction to String
   */
  public static String formatDirection(CDWrapper.CDAssociationWrapperDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return "LeftToRight";
      case RIGHT_TO_LEFT:
        return "RightToLeft";
      case BIDIRECTIONAL:
        return "Bidirectional";
      default:
        return "Undefined";
    }
  }

  /**
   * calculate the intersection set for exist Cardinality with current Cardinality
   */
  public static CDWrapper.CDAssociationWrapperCardinality cDAssociationWrapperCardinalityHelper(
      CDWrapper.CDAssociationWrapperCardinality exist,
      CDWrapper.CDAssociationWrapperCardinality current) {
    switch (exist) {
      case ONE:
        switch (current) {
          case ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          case ONE_TO_MORE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          default:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
        }
      case ZERO_TO_ONE:
        switch (current) {
          case ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE;
          case ONE_TO_MORE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          default:
            return CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE;
        }
      case ONE_TO_MORE:
        switch (current) {
          case ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          case ONE_TO_MORE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE;
          default:
            return CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE;
        }
      default:
        switch (current) {
          case ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDWrapper.CDAssociationWrapperCardinality.ZERO_TO_ONE;
          case ONE_TO_MORE:
            return CDWrapper.CDAssociationWrapperCardinality.ONE_TO_MORE;
          default:
            return CDWrapper.CDAssociationWrapperCardinality.MORE;
        }
    }
  }

  /**
   * return the intersection set for direction of association
   *
   * @Return: "current" or "exist" or "both" "current" means using current direction "exist" means
   * using exist direction "both" means using both directions
   */
  public static String cDAssociationWrapperDirectionHelper(
      CDWrapper.CDAssociationWrapperDirection exist,
      CDWrapper.CDAssociationWrapperDirection current) {
    switch (exist) {
      case LEFT_TO_RIGHT:
        switch (current) {
          case LEFT_TO_RIGHT:
            return "current";
          case RIGHT_TO_LEFT:
            return "both";
          case BIDIRECTIONAL:
            return "current";
          default:
            return "exist";
        }
      case RIGHT_TO_LEFT:
        switch (current) {
          case LEFT_TO_RIGHT:
            return "both";
          case RIGHT_TO_LEFT:
            return "current";
          case BIDIRECTIONAL:
            return "current";
          default:
            return "exist";
        }
      case BIDIRECTIONAL:
        switch (current) {
          case LEFT_TO_RIGHT:
            return "exist";
          case RIGHT_TO_LEFT:
            return "exist";
          case BIDIRECTIONAL:
            return "current";
          default:
            return "exist";
        }
      default:
        switch (current) {
          case LEFT_TO_RIGHT:
            return "current";
          case RIGHT_TO_LEFT:
            return "current";
          case BIDIRECTIONAL:
            return "current";
          default:
            return "current";
        }
    }
  }

  /**
   * calculate the intersection set of cardinality of given CDAssociationWrapper by its relevant
   * CDRefSetAssociationWrappers only for A -> B and A <- B in the same CD
   */
  public static CDAssociationWrapper intersectCDAssociationWrapperCardinalityByCDAssociationWrapperOnlyWithLeftToRightAndRightToLeft(
      CDAssociationWrapper originalAssoc, CDWrapper cdw) {
    CDAssociationWrapper resultAssoc;
    try {
      resultAssoc = originalAssoc.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    if (originalAssoc.getCDAssociationWrapperDirection() == CDWrapper.CDAssociationWrapperDirection.LEFT_TO_RIGHT
        || originalAssoc.getCDAssociationWrapperDirection()
        == CDWrapper.CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
      List<CDAssociationWrapper> CDAssociationWrapperList = cdw.getCDAssociationWrapperGroup()
          .values()
          .stream()
          .filter(e ->
              // A <- B, A -> B
              (e.getLeftOriginalClassName().equals(originalAssoc.getLeftOriginalClassName())
                  && e.getCDWrapperLeftClassRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
                  && e.getCDAssociationWrapperDirection().equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection()))
                  && e.getCDWrapperRightClassRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
                  && e.getRightOriginalClassName().equals(originalAssoc.getRightOriginalClassName())) ||
              // A <- B, B <- A
              (e.getLeftOriginalClassName().equals(originalAssoc.getRightOriginalClassName())
                  && e.getCDWrapperLeftClassRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
                  && e.getCDAssociationWrapperDirection().equals(originalAssoc.getCDAssociationWrapperDirection())
                  && e.getCDWrapperRightClassRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
                  && e.getRightOriginalClassName().equals(originalAssoc.getLeftOriginalClassName())))
          .collect(Collectors.toList());

      AtomicReference<CDWrapper.CDAssociationWrapperCardinality> finalLeftCardinality =
          new AtomicReference<>(
          originalAssoc.getCDWrapperLeftClassCardinality());
      AtomicReference<CDWrapper.CDAssociationWrapperCardinality> finalRightCardinality =
          new AtomicReference<>(
          originalAssoc.getCDWrapperRightClassCardinality());
      CDAssociationWrapperList.forEach(e -> {
        if (e.getCDAssociationWrapperDirection().equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection()))) {
          // A <- B, A -> B
          finalLeftCardinality.set(cDAssociationWrapperCardinalityHelper(finalLeftCardinality.get(),
              e.getCDWrapperLeftClassCardinality()));
          finalRightCardinality.set(cDAssociationWrapperCardinalityHelper(finalRightCardinality.get(),
              e.getCDWrapperRightClassCardinality()));
        }
        else {
          // A <- B, B <- A
          finalLeftCardinality.set(cDAssociationWrapperCardinalityHelper(finalLeftCardinality.get(),
              e.getCDWrapperRightClassCardinality()));
          finalRightCardinality.set(cDAssociationWrapperCardinalityHelper(finalRightCardinality.get(),
              e.getCDWrapperLeftClassCardinality()));
        }
      });

      resultAssoc.setCDWrapperLeftClassCardinality(finalLeftCardinality.get());
      resultAssoc.setCDWrapperRightClassCardinality(finalRightCardinality.get());
    }

    return resultAssoc;
  }

  /**
   * calculate the intersection set of cardinality of given CDAssociationWrapper by its relevant
   * CDRefSetAssociationWrappers
   */
  public static CDAssociationWrapper intersectCDAssociationWrapperCardinalityByCDAssociationWrapperWithOverlap(
      CDAssociationWrapper originalAssoc, CDWrapper cdw) {
    CDAssociationWrapper resultAssoc;
    try {
      resultAssoc = originalAssoc.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    AtomicReference<CDWrapper.CDAssociationWrapperCardinality> intersectedLeftCardinality =
        new AtomicReference<>(
        originalAssoc.getCDWrapperLeftClassCardinality());
    AtomicReference<CDWrapper.CDAssociationWrapperCardinality> intersectedRightCardinality =
        new AtomicReference<>(
        originalAssoc.getCDWrapperRightClassCardinality());

    cdw.getRefSetAssociationList().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && item.getDirection().equals(originalAssoc.getCDAssociationWrapperDirection()) && item.getLeftRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperLeftClass().getName()))
          && item.getRightRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperRightClass().getName()))) {
        CDAssociationWrapperCardinalityPack intersectedCardinalityPack =
            intersectCDAssociationWrapperCardinalityHelper(
            originalAssoc, false, item, cdw, intersectedLeftCardinality.get(),
            intersectedRightCardinality.get());
        intersectedLeftCardinality.set(intersectedCardinalityPack.getLeftCardinality());
        intersectedRightCardinality.set(intersectedCardinalityPack.getRightCardinality());
      }
      else if (item.getLeftRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && item.getDirection().equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection()))
          && item.getLeftRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperRightClass().getName()))
          && item.getRightRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperLeftClass().getName()))) {
        CDAssociationWrapperCardinalityPack intersectedCardinalityPack =
            intersectCDAssociationWrapperCardinalityHelper(
            originalAssoc, true, item, cdw, intersectedRightCardinality.get(),
            intersectedLeftCardinality.get());
        intersectedRightCardinality.set(intersectedCardinalityPack.getLeftCardinality());
        intersectedLeftCardinality.set(intersectedCardinalityPack.getRightCardinality());
      }
    });

    // update Cardinality
    resultAssoc.setCDWrapperLeftClassCardinality(intersectedLeftCardinality.get());
    resultAssoc.setCDWrapperRightClassCardinality(intersectedRightCardinality.get());

    return resultAssoc;
  }

  /**
   * calculate the intersection set of left and right cardinality of related CDAssociationWrapper in
   * CDRefSetAssociationWrapper
   *
   * @Return: CDAssociationWrapperCardinalityPack {"leftCardinality"   :
   * CDWrapper.CDAssociationWrapperCardinality "rightCardinality"  :
   * CDWrapper.CDAssociationWrapperCardinality }
   */
  public static CDAssociationWrapperCardinalityPack intersectCDAssociationWrapperCardinalityHelper(
      CDAssociationWrapper originalAssoc, boolean isReversed,
      CDRefSetAssociationWrapper CDRefSetAssociationWrapper, CDWrapper cdw,
      CDWrapper.CDAssociationWrapperCardinality existLeftCardinality,
      CDWrapper.CDAssociationWrapperCardinality existRightCardinality) {

    Set<String> leftSuperClassSet;
    Set<String> rightSuperClassSet;
    if (!isReversed) {
      leftSuperClassSet = getSuperClassSet(cdw.getInheritanceGraph(),
          originalAssoc.getCDWrapperLeftClass().getName());
      rightSuperClassSet = getSuperClassSet(cdw.getInheritanceGraph(),
          originalAssoc.getCDWrapperRightClass().getName());
    }
    else {
      leftSuperClassSet = getSuperClassSet(cdw.getInheritanceGraph(),
          originalAssoc.getCDWrapperRightClass().getName());
      rightSuperClassSet = getSuperClassSet(cdw.getInheritanceGraph(),
          originalAssoc.getCDWrapperLeftClass().getName());
    }

    AtomicReference<CDWrapper.CDAssociationWrapperCardinality> leftResult = new AtomicReference<>(
        existLeftCardinality);
    AtomicReference<CDWrapper.CDAssociationWrapperCardinality> rightResult = new AtomicReference<>(
        existRightCardinality);
    Set<String> finalLeftSuperClassSet = leftSuperClassSet;
    Set<String> finalRightSuperClassSet = rightSuperClassSet;

    CDRefSetAssociationWrapper.getLeftRefSet().forEach(leftClass -> {
      CDRefSetAssociationWrapper.getRightRefSet().forEach(rightClass -> {
        StringBuilder sb = new StringBuilder();
        sb.append("CDAssociationWrapper_");
        sb.append(leftClass.getOriginalClassName() + "_");
        sb.append(CDRefSetAssociationWrapper.getLeftRoleName() + "_");
        sb.append(formatDirection(CDRefSetAssociationWrapper.getDirection()) + "_");
        sb.append(CDRefSetAssociationWrapper.getRightRoleName() + "_");
        sb.append(rightClass.getOriginalClassName());
        if ((finalLeftSuperClassSet.contains(leftClass.getName())
            || finalRightSuperClassSet.contains(rightClass.getName()))
            && cdw.getCDAssociationWrapperGroup().containsKey(sb.toString())) {
          leftResult.set(cDAssociationWrapperCardinalityHelper(leftResult.get(),
              cdw.getCDAssociationWrapperGroup()
                  .get(sb.toString())
                  .getCDWrapperLeftClassCardinality()));
          rightResult.set(cDAssociationWrapperCardinalityHelper(rightResult.get(),
              cdw.getCDAssociationWrapperGroup()
                  .get(sb.toString())
                  .getCDWrapperRightClassCardinality()));
        }
        else {
          StringBuilder reversedSb = new StringBuilder();
          reversedSb.append("CDAssociationWrapper_");
          reversedSb.append(rightClass.getOriginalClassName());
          reversedSb.append(CDRefSetAssociationWrapper.getRightRoleName() + "_");
          reversedSb.append(
              formatDirection(reverseDirection(CDRefSetAssociationWrapper.getDirection())) + "_");
          reversedSb.append(CDRefSetAssociationWrapper.getLeftRoleName() + "_");
          reversedSb.append(leftClass.getOriginalClassName() + "_");
          if ((finalLeftSuperClassSet.contains(leftClass.getName())
              || finalRightSuperClassSet.contains(rightClass.getName()))
              && cdw.getCDAssociationWrapperGroup().containsKey(reversedSb.toString())) {
            leftResult.set(cDAssociationWrapperCardinalityHelper(leftResult.get(),
                cdw.getCDAssociationWrapperGroup()
                    .get(reversedSb.toString())
                    .getCDWrapperLeftClassCardinality()));
            rightResult.set(cDAssociationWrapperCardinalityHelper(rightResult.get(),
                cdw.getCDAssociationWrapperGroup()
                    .get(reversedSb.toString())
                    .getCDWrapperRightClassCardinality()));
          }
        }
      });
    });
    return new CDAssociationWrapperCardinalityPack(leftResult.get(), rightResult.get());
  }

  /**
   * Fuzzy search for CDAssociationWrapper without matching direction
   *
   * @Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirection(
      Map<String, CDAssociationWrapper> map, CDAssociationWrapper currentAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    }
    else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getLeftOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getRightOriginalClassName())) {
          result.add(new CDAssociationWrapperPack(existAssoc, false));
        }
        else if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getRightOriginalClassName())
                && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
                && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
                && currentAssoc.getRightOriginalClassName().equals(existAssoc.getLeftOriginalClassName())) {
          result.add(new CDAssociationWrapperPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  /**
   * Fuzzy search for CDAssociationWrapper only matching leftRoleName, rightRoleName and direction
   *
   * @Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithRoleNameAndDirection(
      Map<String, CDAssociationWrapper> map, CDAssociationWrapper currentAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    }
    else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getCDWrapperLeftClassRoleName()
            .equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName()
            .equals(existAssoc.getCDWrapperRightClassRoleName()) && currentAssoc.getCDAssociationWrapperDirection()
            .equals(existAssoc.getCDAssociationWrapperDirection())) {
          result.add(new CDAssociationWrapperPack(existAssoc, false));
        }
        else if (currentAssoc.getCDWrapperLeftClassRoleName()
            .equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName()
            .equals(existAssoc.getCDWrapperLeftClassRoleName()) && currentAssoc.getCDAssociationWrapperDirection()
            .equals(reverseDirection(existAssoc.getCDAssociationWrapperDirection()))) {
          result.add(new CDAssociationWrapperPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /**
   * Fuzzy search for CDAssociationWrapper by ClassName
   */
  public static Map<String, CDAssociationWrapper> fuzzySearchCDAssociationWrapperByClassName(
      Map<String, CDAssociationWrapper> map, String className) {
    Map<String, CDAssociationWrapper> result = new HashMap<>();
    if (map == null) {
      return null;
    }
    else {
      result = map.values()
          .stream()
          .filter(
              e -> (e.getLeftOriginalClassName().equals(className) || e.getRightOriginalClassName()
                  .equals(className)))
          .collect(Collectors.toMap(e -> (String) e.getName(), e -> e));
    }
    return result;
  }

  /**
   * get all inheritance path for each top class by backtracking
   */
  public List<List<String>> getAllInheritancePath4CDTypeWrapper(CDTypeWrapper cDTypeWrapper,
      MutableGraph<String> inheritanceGraph) {
    String root = cDTypeWrapper.getName();
    List<List<String>> pathList = new ArrayList<>();
    getAllInheritancePath4CDTypeWrapperHelper(root, new LinkedList<>(), pathList, inheritanceGraph);
    return pathList;
  }

  /**
   * backtracking helper
   */
  private void getAllInheritancePath4CDTypeWrapperHelper(String root, LinkedList<String> path,
      List<List<String>> pathList, MutableGraph<String> inheritanceGraph) {
    if (inheritanceGraph.successors(root).isEmpty()) {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      pathList.add(newPath);
      return;
    }
    else {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      Iterator iterator = inheritanceGraph.successors(root).iterator();
      while (iterator.hasNext()) {
        String parentNode = iterator.next().toString();
        getAllInheritancePath4CDTypeWrapperHelper(parentNode, newPath, pathList, inheritanceGraph);
      }
    }
  }

  /**
   * getting all bottom class in inheritance graph
   */
  public static Set<String> getAllBottomCDTypeWrapperNode(MutableGraph<String> inheritanceGraph) {
    Set<String> result = new HashSet<>();
    inheritanceGraph.nodes().forEach(s -> {
      if (inheritanceGraph.predecessors(s).isEmpty()) {
        result.add(s);
      }
    });
    return result;
  }

  /**
   * getting inherited CDTypeWrapper name by given CDTypeWrapper name
   */
  public static LinkedHashSet<String> getInheritedClassSet(MutableGraph<String> inheritanceGraph,
      String cDTypeWrapperName) {
    LinkedHashSet<String> result = new LinkedHashSet<>();
    result.add(cDTypeWrapperName);
    Deque<String> currentCDTypeWrapperNameQueue = new LinkedList<>();
    currentCDTypeWrapperNameQueue.offer(cDTypeWrapperName);
    while (!currentCDTypeWrapperNameQueue.isEmpty()) {
      String currentNode = currentCDTypeWrapperNameQueue.poll();
      if (!inheritanceGraph.predecessors(currentNode).isEmpty()) {
        inheritanceGraph.predecessors(currentNode).forEach(e -> {
          result.add(e);
          currentCDTypeWrapperNameQueue.offer(e);
        });
      }
    }
    return result;
  }

  /**
   * getting super CDTypeWrapper name by given CDTypeWrapper name
   */
  public static LinkedHashSet<String> getSuperClassSet(MutableGraph<String> inheritanceGraph,
      String cDTypeWrapperName) {
    List<String> temp = new ArrayList<>();
    Deque<String> currentCDTypeWrapperNameQueue = new LinkedList<>();
    currentCDTypeWrapperNameQueue.offer(cDTypeWrapperName);
    temp.add(cDTypeWrapperName);
    while (!currentCDTypeWrapperNameQueue.isEmpty()) {
      String currentNode = currentCDTypeWrapperNameQueue.poll();
      if (!inheritanceGraph.successors(currentNode).isEmpty()) {
        inheritanceGraph.successors(currentNode).forEach(e -> {
          temp.add(e);
          currentCDTypeWrapperNameQueue.offer(e);
        });
      }
    }
    Collections.reverse(temp);
    LinkedHashSet<String> result = new LinkedHashSet<>();
    temp.forEach(e -> result.add(e));
    return result;
  }

  /**
   * return all superclasses about given CDTypeWrapper expect abstract class and interface
   */
  public static Set<CDTypeWrapper> getAllSuperClasses4CDTypeWrapper(CDTypeWrapper cDTypeWrapper,
      MutableGraph<String> inheritanceGraph, Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    Set<CDTypeWrapper> result = new LinkedHashSet<>();
    inheritanceGraph.successors(cDTypeWrapper.getName()).forEach(e -> {
      result.add(cDTypeWrapperGroup.get(e));
    });
    return result;
  }

  /**
   * return all subclasses about given CDTypeWrapper expect abstract class and interface
   */
  public static Set<CDTypeWrapper> getAllSubClasses4CDTypeWrapper(CDTypeWrapper cDTypeWrapper,
      MutableGraph<String> inheritanceGraph, Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    Set<CDTypeWrapper> result = new LinkedHashSet<>();
    inheritanceGraph.predecessors(cDTypeWrapper.getName()).forEach(e -> {
      result.add(cDTypeWrapperGroup.get(e));
    });
    return result;
  }

  /**
   * return all simple subclasses about given CDTypeWrapper expect abstract class and interface
   */
  public static List<CDTypeWrapper> getAllSimpleSubClasses4CDTypeWrapper(CDTypeWrapper cDTypeWrapper,
      MutableGraph<String> inheritanceGraph, Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    List<CDTypeWrapper> result = new LinkedList<>();
    inheritanceGraph.predecessors(cDTypeWrapper.getName()).forEach(e -> {
      if (cDTypeWrapperGroup.get(e).getCDWrapperKind()
          == CDWrapper.CDTypeWrapperKind.CDWRAPPER_CLASS) {
        result.add(cDTypeWrapperGroup.get(e));
      }
    });
    return result;
  }

  /**
   * generate the list of CDRefSetAssociationWrapper each original association has one
   * CDRefSetAssociationWrapper object
   */
  public static List<CDRefSetAssociationWrapper> createCDRefSetAssociationWrapper(
      Map<String, CDAssociationWrapper> cDAssociationWrapperGroup,
      MutableGraph<String> inheritanceGraph) {

    List<CDRefSetAssociationWrapper> refSetAssociationList = new ArrayList<>();

    List<CDAssociationWrapper> originalCDAssocWrapperList = cDAssociationWrapperGroup.values()
        .stream()
        .filter(e -> e.getCDWrapperKind() == CDWrapper.CDAssociationWrapperKind.CDWRAPPER_ASC)
        .collect(Collectors.toList());

    originalCDAssocWrapperList.forEach(originalAssoc -> {
      Set<CDTypeWrapper> leftRefSet = new HashSet<>();
      leftRefSet.add(originalAssoc.getCDWrapperLeftClass());
      Set<CDTypeWrapper> rightRefSet = new HashSet<>();
      rightRefSet.add(originalAssoc.getCDWrapperRightClass());
      String leftRoleName = originalAssoc.getCDWrapperLeftClassRoleName();
      String rightRoleName = originalAssoc.getCDWrapperRightClassRoleName();
      CDWrapper.CDAssociationWrapperDirection direction = originalAssoc.getCDAssociationWrapperDirection();

      List<CDAssociationWrapperPack> matchedAssocList =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithRoleNameAndDirection(
          cDAssociationWrapperGroup, originalAssoc);
      Set<String> leftInheritedClassSet = getInheritedClassSet(inheritanceGraph,
          originalAssoc.getCDWrapperLeftClass().getName());
      Set<String> rightInheritedClassSet = getInheritedClassSet(inheritanceGraph,
          originalAssoc.getCDWrapperRightClass().getName());
      matchedAssocList.forEach(e -> {
        if (!e.isReverse()) {
          CDAssociationWrapper inheritedAssoc = e.getCDAssociationWrapper();
          if (leftInheritedClassSet.contains(inheritedAssoc.getCDWrapperLeftClass().getName())
              && rightInheritedClassSet.contains(
              inheritedAssoc.getCDWrapperRightClass().getName())) {
            leftRefSet.add(inheritedAssoc.getCDWrapperLeftClass());
            rightRefSet.add(inheritedAssoc.getCDWrapperRightClass());
          }
        }
        else {
          CDAssociationWrapper inheritedAssoc = e.getCDAssociationWrapper();
          if (leftInheritedClassSet.contains(inheritedAssoc.getCDWrapperRightClass().getName())
              && rightInheritedClassSet.contains(
              inheritedAssoc.getCDWrapperLeftClass().getName())) {
            leftRefSet.add(inheritedAssoc.getCDWrapperRightClass());
            rightRefSet.add(inheritedAssoc.getCDWrapperLeftClass());
          }
        }
      });

      refSetAssociationList.add(
          new CDRefSetAssociationWrapper(leftRefSet, leftRoleName, direction, rightRoleName,
              rightRefSet, originalAssoc));
    });

    return refSetAssociationList;
  }

  /**
   * set the left side class name into ASTCDAssociation
   */
  public static ASTCDAssociation editASTCDAssociationLeftSideByCDTypeWrapper(
      ASTCDAssociation original, CDTypeWrapper cDTypeWrapper) {
    ASTCDAssociation edited = original.deepClone();
    edited.getLeft()
        .getMCQualifiedType()
        .setMCQualifiedName(MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
            .addParts(cDTypeWrapper.getOriginalClassName())
            .build());
    return edited;
  }

  /**
   * set the right side class name into ASTCDAssociation
   */
  public static ASTCDAssociation editASTCDAssociationRightSideByCDTypeWrapper(
      ASTCDAssociation original, CDTypeWrapper cDTypeWrapper) {
    ASTCDAssociation edited = original.deepClone();
    edited.getRight()
        .getMCQualifiedType()
        .setMCQualifiedName(MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
            .addParts(cDTypeWrapper.getOriginalClassName())
            .build());
    return edited;
  }

  /**
   * genetate the role name for ASTCDAssociation if there is no role name in the original
   * ASTCDAssociation then set the lower case of the left/right class qualified name as role name
   */
  public static ASTCDAssociation generateASTCDAssociationRoleName(
      ASTCDAssociation astcdAssociation) {
    if (!astcdAssociation.getLeft().isPresentCDRole()) {
      String leftRoleName = astcdAssociation.getLeftQualifiedName().getQName().toLowerCase();
      astcdAssociation.getLeft()
          .setCDRole(CD4AnalysisMill.cDRoleBuilder().setName(leftRoleName).build());
    }
    if (!astcdAssociation.getRight().isPresentCDRole()) {
      String rightRoleName = astcdAssociation.getRightQualifiedName().getQName().toLowerCase();
      astcdAssociation.getRight()
          .setCDRole(CD4AnalysisMill.cDRoleBuilder().setName(rightRoleName).build());
    }
    return astcdAssociation;
  }

  /**
   * exchange left side and right side of CDAssociationWrapper
   */
  public static CDAssociationWrapper reverseCDAssociationWrapper(CDAssociationWrapper currentAssoc,
      ASTCDAssocDir reversedAstCDAssocDir) {
    CDAssociationWrapper reversedAssoc;
    try {
      reversedAssoc = currentAssoc.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    CDTypeWrapper currentLeftClass = currentAssoc.getCDWrapperLeftClass();
    CDTypeWrapper currentRightClass = currentAssoc.getCDWrapperRightClass();

    ASTCDAssociation reversedASTCDAssociation = reversedAssoc.getEditedElement();
    reversedASTCDAssociation = editASTCDAssociationLeftSideByCDTypeWrapper(reversedASTCDAssociation,
        currentRightClass);
    reversedASTCDAssociation = editASTCDAssociationRightSideByCDTypeWrapper(reversedASTCDAssociation,
        currentLeftClass);
    reversedASTCDAssociation.setCDAssocDir(reversedAstCDAssocDir);

    reversedAssoc.setEditedElement(reversedASTCDAssociation);
    reversedAssoc.setCDWrapperLeftClass(currentRightClass);
    reversedAssoc.setCDWrapperRightClass(currentLeftClass);

    return reversedAssoc;
  }

}