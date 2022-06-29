package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.graph.MutableGraph;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DifferentHelper {

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * get the corresponding DiffClass kind by ASTCDType
   */
  public static DifferentGroup.DiffClassKind distinguishASTCDTypeHelper(ASTCDType astcdType) {
    if (astcdType.getClass().equals(ASTCDClass.class)) {
      if (astcdType.getModifier().isAbstract()) {
        return DifferentGroup.DiffClassKind.DIFF_ABSTRACT_CLASS;
      } else {
        return DifferentGroup.DiffClassKind.DIFF_CLASS;
      }
    } else if (astcdType.getClass().equals(ASTCDEnum.class)) {
      return DifferentGroup.DiffClassKind.DIFF_ENUM;
    } else {
      return DifferentGroup.DiffClassKind.DIFF_INTERFACE;
    }
  }

  /**
   * get the corresponding prefix of DiffClass name by diffClassKind
   */
  public static String getDiffClassKindStrHelper(DifferentGroup.DiffClassKind diffClassKind) {
    switch (diffClassKind) {
      case DIFF_CLASS:
        return "DiffClass";
      case DIFF_ENUM:
        return "DiffEnum";
      case DIFF_ABSTRACT_CLASS:
        return "DiffAbstractClass";
      case DIFF_INTERFACE:
        return "DiffInterface";
      default:
        return null;
    }
  }

  /**
   * using the original class name to find corresponding DiffClass in DiffClassGroup
   */
  public static DiffClass findDiffClass4OriginalClassName(Map<String, DiffClass> diffClassGroup, String originalClassName) {
    if (diffClassGroup.containsKey("DiffClass_" + originalClassName)) {
      return diffClassGroup.get("DiffClass_" + originalClassName);
    } else if (diffClassGroup.containsKey("DiffAbstractClass_" + originalClassName)) {
      return diffClassGroup.get("DiffAbstractClass_" + originalClassName);
    } else if (diffClassGroup.containsKey("DiffInterface_" + originalClassName)) {
      return diffClassGroup.get("DiffInterface_" + originalClassName);
    } else {
      return diffClassGroup.get("DiffEnum_" + originalClassName);
    }
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * get the corresponding the direction kind of DiffAssociation by ASTCDAssociation
   */
  public static DifferentGroup.DiffAssociationDirection distinguishAssociationDirectionHelper(ASTCDAssociation astcdAssociation) {
    Boolean left = astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft();
    Boolean right = astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight();
    Boolean bidirectional = astcdAssociation.getCDAssocDir().isBidirectional();
    if (!left && right && !bidirectional) {
      return DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT;
    } else if (left && !right && !bidirectional) {
      return DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT;
    } else if (left && right && bidirectional) {
      return DifferentGroup.DiffAssociationDirection.BIDIRECTIONAL;
    } else {
      return DifferentGroup.DiffAssociationDirection.UNDEFINED;
    }
  }

  /**
   * get the corresponding the left cardinality kind of DiffAssociation by ASTCDAssociation
   */
  public static DifferentGroup.DiffAssociationCardinality distinguishLeftAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().getCDCardinality().isOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE;
    } else if (astcdAssociation.getLeft().getCDCardinality().isOpt()) {
      return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
    } else if (astcdAssociation.getLeft().getCDCardinality().isAtLeastOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
    } else {
      return DifferentGroup.DiffAssociationCardinality.MORE;
    }
  }

  /**
   * get the corresponding the right cardinality kind of DiffAssociation by ASTCDAssociation
   */
  public static DifferentGroup.DiffAssociationCardinality distinguishRightAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().getCDCardinality().isOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE;
    } else if (astcdAssociation.getRight().getCDCardinality().isOpt()) {
      return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
    } else if (astcdAssociation.getRight().getCDCardinality().isAtLeastOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
    } else {
      return DifferentGroup.DiffAssociationCardinality.MORE;
    }
  }

  /**
   * get the left class role name in DiffAssociation
   * if it exists in ASTCDAssociation, then direct return the role name
   * otherwise set the lower case of the left class qualified name as role name
   */
  public static String getLeftClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().isPresentCDRole()) {
      return astcdAssociation.getLeft().getCDRole().getName();
    } else {
      return astcdAssociation.getLeftQualifiedName().getQName().toLowerCase();
    }
  }

  /**
   * get the right class role name in DiffAssociation
   * if it exists in ASTCDAssociation, then direct return the role name
   * otherwise set the lower case of the right class qualified name as role name
   */
  public static String getRightClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().isPresentCDRole()) {
      return astcdAssociation.getRight().getCDRole().getName();
    } else {
      return astcdAssociation.getRightQualifiedName().getQName().toLowerCase();
    }
  }

  /**
   * reverse LEFT_TO_RIGHT and RIGHT_TO_LEFT direction
   */
  public static DifferentGroup.DiffAssociationDirection reverseDirection(DifferentGroup.DiffAssociationDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT;
      case RIGHT_TO_LEFT:
        return DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT;
      default:
        return direction;
    }
  }

  /**
   * format direction to String
   */
  public static String formatDirection(DifferentGroup.DiffAssociationDirection direction) {
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
  public static DifferentGroup.DiffAssociationCardinality diffAssociationCardinalityHelper(DifferentGroup.DiffAssociationCardinality exist, DifferentGroup.DiffAssociationCardinality current) {
    switch (exist) {
      case ONE:
        switch (current) {
          case ONE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          case ZORE_TO_ONE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          case ONE_TO_MORE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          default:
            return DifferentGroup.DiffAssociationCardinality.ONE;
        }
      case ZORE_TO_ONE:
        switch (current) {
          case ONE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          case ZORE_TO_ONE:
            return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
          case ONE_TO_MORE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          default:
            return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
        }
      case ONE_TO_MORE:
        switch (current) {
          case ONE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          case ZORE_TO_ONE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          case ONE_TO_MORE:
            return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
          default:
            return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
        }
      default:
        switch (current) {
          case ONE:
            return DifferentGroup.DiffAssociationCardinality.ONE;
          case ZORE_TO_ONE:
            return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
          case ONE_TO_MORE:
            return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
          default:
            return DifferentGroup.DiffAssociationCardinality.MORE;
        }
    }
  }

  /**
   * return the intersection set for direction of association
   * @Return: "current" or "exist" or "both"
   *    "current" means using current direction
   *    "exist" means using exist direction
   *    "both" means using both directions
   */
  public static String diffAssociationDirectionHelper(DifferentGroup.DiffAssociationDirection exist, DifferentGroup.DiffAssociationDirection current) {
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
   * calculate the intersection set of cardinality of given DiffAssociation by its relevant DiffRefSetAssociations
   * only for A -> B and A <- B in the same CD
   */
  public static DiffAssociation intersectDiffAssociationCardinalityByDiffAssociationOnlyWithLeftToRightAndRightToLeft(DiffAssociation originalAssoc, DifferentGroup dg){
    DiffAssociation resultAssoc;
    try {
      resultAssoc = originalAssoc.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    if (originalAssoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT ||
      originalAssoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT) {
      List<DiffAssociation> diffAssociationList = dg.getDiffAssociationGroup().values()
        .stream()
        .filter(e ->
            // A <- B, A -> B
            (e.getLeftOriginalClassName().equals(originalAssoc.getLeftOriginalClassName()) &&
              e.getDiffLeftClassRoleName().equals(originalAssoc.getDiffLeftClassRoleName()) &&
              e.getDiffDirection().equals(reverseDirection(originalAssoc.getDiffDirection())) &&
              e.getDiffRightClassRoleName().equals(originalAssoc.getDiffRightClassRoleName()) &&
              e.getRightOriginalClassName().equals(originalAssoc.getRightOriginalClassName())) ||
            // A <- B, B <- A
            (e.getLeftOriginalClassName().equals(originalAssoc.getRightOriginalClassName()) &&
              e.getDiffLeftClassRoleName().equals(originalAssoc.getDiffRightClassRoleName()) &&
              e.getDiffDirection().equals(originalAssoc.getDiffDirection()) &&
              e.getDiffRightClassRoleName().equals(originalAssoc.getDiffLeftClassRoleName()) &&
              e.getRightOriginalClassName().equals(originalAssoc.getLeftOriginalClassName())))
        .collect(Collectors.toList());

      AtomicReference<DifferentGroup.DiffAssociationCardinality> finalLeftCardinality =
        new AtomicReference<>(originalAssoc.getDiffLeftClassCardinality());
      AtomicReference<DifferentGroup.DiffAssociationCardinality> finalRightCardinality =
        new AtomicReference<>(originalAssoc.getDiffRightClassCardinality());
      diffAssociationList.forEach(e -> {
        if (e.getDiffDirection().equals(reverseDirection(originalAssoc.getDiffDirection()))) {
          // A <- B, A -> B
          finalLeftCardinality.set(diffAssociationCardinalityHelper(finalLeftCardinality.get(), e.getDiffLeftClassCardinality()));
          finalRightCardinality.set(diffAssociationCardinalityHelper(finalRightCardinality.get(), e.getDiffRightClassCardinality()));
        } else {
          // A <- B, B <- A
          finalLeftCardinality.set(diffAssociationCardinalityHelper(finalLeftCardinality.get(), e.getDiffRightClassCardinality()));
          finalRightCardinality.set(diffAssociationCardinalityHelper(finalRightCardinality.get(), e.getDiffLeftClassCardinality()));
        }
      });

      resultAssoc.setDiffLeftClassCardinality(finalLeftCardinality.get());
      resultAssoc.setDiffRightClassCardinality(finalRightCardinality.get());
    }

    return resultAssoc;
  }

  /**
   * calculate the intersection set of cardinality of given DiffAssociation by its relevant DiffRefSetAssociations
   */
  public static DiffAssociation intersectDiffAssociationCardinalityByDiffAssociationWithOverlap(DiffAssociation originalAssoc, DifferentGroup dg){
    DiffAssociation resultAssoc = null;
    try {
      resultAssoc = originalAssoc.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    AtomicReference<DifferentGroup.DiffAssociationCardinality> intersectedLeftCardinality =
      new AtomicReference<>(originalAssoc.getDiffLeftClassCardinality());
    AtomicReference<DifferentGroup.DiffAssociationCardinality> intersectedRightCardinality =
      new AtomicReference<>(originalAssoc.getDiffRightClassCardinality());

    dg.getRefSetAssociationList().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getDiffLeftClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getDiffRightClassRoleName()) &&
        item.getDirection().equals(originalAssoc.getDiffDirection()) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffLeftClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffRightClass().getName()))) {
        Map<String, DifferentGroup.DiffAssociationCardinality> intersectedCardinalityMap =
          intersectDiffAssociationCardinalityHelper(originalAssoc, false, item, dg, intersectedLeftCardinality.get(), intersectedRightCardinality.get());
        intersectedLeftCardinality.set(intersectedCardinalityMap.get("leftCardinality"));
        intersectedRightCardinality.set(intersectedCardinalityMap.get("rightCardinality"));
      } else if (item.getLeftRoleName().equals(originalAssoc.getDiffRightClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getDiffLeftClassRoleName()) &&
        item.getDirection().equals(reverseDirection(originalAssoc.getDiffDirection())) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffRightClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffLeftClass().getName()))) {
        Map<String, DifferentGroup.DiffAssociationCardinality> intersectedCardinalityMap =
          intersectDiffAssociationCardinalityHelper(originalAssoc, true, item, dg, intersectedRightCardinality.get(), intersectedLeftCardinality.get());
        intersectedRightCardinality.set(intersectedCardinalityMap.get("leftCardinality"));
        intersectedLeftCardinality.set(intersectedCardinalityMap.get("rightCardinality"));
      }
    });

    // update Cardinality
    resultAssoc.setDiffLeftClassCardinality(intersectedLeftCardinality.get());
    resultAssoc.setDiffRightClassCardinality(intersectedRightCardinality.get());

    return resultAssoc;
  }

  /**
   * calculate the intersection set of left and right cardinality of related DiffAssociation in DiffRefSetAssociation
   * @Return:
   *  [{"leftCardinality"   : DifferentGroup.DiffAssociationCardinality
   *    "rightCardinality"  : DifferentGroup.DiffAssociationCardinality }]
   */
  public static Map<String, DifferentGroup.DiffAssociationCardinality> intersectDiffAssociationCardinalityHelper(
    DiffAssociation originalAssoc,
    boolean isReversed,
    DiffRefSetAssociation diffRefSetAssociation,
    DifferentGroup dg,
    DifferentGroup.DiffAssociationCardinality existLeftCardinality,
    DifferentGroup.DiffAssociationCardinality existRightCardinality) {

    Set<String> leftSuperClassSet = null;
    Set<String> rightSuperClassSet = null;
    if (!isReversed) {
      leftSuperClassSet = getSuperClassSet(dg.getInheritanceGraph(), originalAssoc.getDiffLeftClass().getName());
      rightSuperClassSet = getSuperClassSet(dg.getInheritanceGraph(), originalAssoc.getDiffRightClass().getName());
    } else {
      leftSuperClassSet = getSuperClassSet(dg.getInheritanceGraph(), originalAssoc.getDiffRightClass().getName());
      rightSuperClassSet = getSuperClassSet(dg.getInheritanceGraph(), originalAssoc.getDiffLeftClass().getName());
    }

    AtomicReference<DifferentGroup.DiffAssociationCardinality> leftResult = new AtomicReference<>(existLeftCardinality);
    AtomicReference<DifferentGroup.DiffAssociationCardinality> rightResult = new AtomicReference<>(existRightCardinality);
    Set<String> finalLeftSuperClassSet = leftSuperClassSet;
    Set<String> finalRightSuperClassSet = rightSuperClassSet;

    diffRefSetAssociation.getLeftRefSet().forEach(leftClass -> {
      diffRefSetAssociation.getRightRefSet().forEach(rightClass -> {
        StringBuilder sb = new StringBuilder();
        sb.append("DiffAssociation_");
        sb.append(leftClass.getOriginalClassName() + "_");
        sb.append(diffRefSetAssociation.getLeftRoleName() + "_");
        sb.append(formatDirection(diffRefSetAssociation.getDirection()) + "_");
        sb.append(diffRefSetAssociation.getRightRoleName() + "_");
        sb.append(rightClass.getOriginalClassName());
        if ((finalLeftSuperClassSet.contains(leftClass.getName()) || finalRightSuperClassSet.contains(rightClass.getName())) &&
          dg.getDiffAssociationGroup().containsKey(sb.toString())) {
          leftResult.set(diffAssociationCardinalityHelper(leftResult.get(), dg.getDiffAssociationGroup().get(sb.toString()).getDiffLeftClassCardinality()));
          rightResult.set(diffAssociationCardinalityHelper(rightResult.get(), dg.getDiffAssociationGroup().get(sb.toString()).getDiffRightClassCardinality()));
        } else {
          StringBuilder reversedSb = new StringBuilder();
          reversedSb.append("DiffAssociation_");
          reversedSb.append(rightClass.getOriginalClassName());
          reversedSb.append(diffRefSetAssociation.getRightRoleName() + "_");
          reversedSb.append(formatDirection(reverseDirection(diffRefSetAssociation.getDirection())) + "_");
          reversedSb.append(diffRefSetAssociation.getLeftRoleName() + "_");
          reversedSb.append(leftClass.getOriginalClassName() + "_");
          if ((finalLeftSuperClassSet.contains(leftClass.getName()) || finalRightSuperClassSet.contains(rightClass.getName())) &&
            dg.getDiffAssociationGroup().containsKey(reversedSb.toString())) {
            leftResult.set(diffAssociationCardinalityHelper(leftResult.get(), dg.getDiffAssociationGroup().get(reversedSb.toString()).getDiffLeftClassCardinality()));
            rightResult.set(diffAssociationCardinalityHelper(rightResult.get(), dg.getDiffAssociationGroup().get(reversedSb.toString()).getDiffRightClassCardinality()));
          }
        }
      });
    });
    return Map.of("leftCardinality", leftResult.get(), "rightCardinality", rightResult.get());
  }

  /**
   * Fuzzy search for DiffAssociation without matching direction
   * @Return:
   *  [{"diffAssociation" : DiffAssociation
   *    "isReverse"       : boolean         }]
   */
  public static List<Map<String, Object>> fuzzySearchDiffAssociationByDiffAssociationWithoutDirection(Map<String, DiffAssociation> map, DiffAssociation currentAssoc) {
    List<Map<String, Object>> result = new ArrayList<>();
    if (map == null) {
      return null;
    } else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getLeftOriginalClassName()) &&
          currentAssoc.getDiffLeftClassRoleName().equals(existAssoc.getDiffLeftClassRoleName()) &&
          currentAssoc.getDiffRightClassRoleName().equals(existAssoc.getDiffRightClassRoleName()) &&
          currentAssoc.getRightOriginalClassName().equals(existAssoc.getRightOriginalClassName())) {
          result.add(Map.of("diffAssociation", existAssoc, "isReverse", false));
        } else if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getRightOriginalClassName()) &&
          currentAssoc.getDiffLeftClassRoleName().equals(existAssoc.getDiffRightClassRoleName()) &&
          currentAssoc.getDiffRightClassRoleName().equals(existAssoc.getDiffLeftClassRoleName()) &&
          currentAssoc.getRightOriginalClassName().equals(existAssoc.getLeftOriginalClassName())) {
          result.add(Map.of("diffAssociation", existAssoc, "isReverse", true));
        }
      });
    }
    return result;
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  /**
   * Fuzzy search for DiffAssociation only matching leftRoleName, rightRoleName and direction
   * @Return:
   *  [{"diffAssociation" : DiffAssociation
   *    "isReverse"       : boolean         }]
   */
  public static List<Map<String, Object>> fuzzySearchDiffAssociationByDiffAssociationWithRoleNameAndDirection(Map<String, DiffAssociation> map, DiffAssociation currentAssoc) {
    List<Map<String, Object>> result = new ArrayList<>();
    if (map == null) {
      return null;
    } else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getDiffLeftClassRoleName().equals(existAssoc.getDiffLeftClassRoleName()) &&
          currentAssoc.getDiffRightClassRoleName().equals(existAssoc.getDiffRightClassRoleName()) &&
          currentAssoc.getDiffDirection().equals(existAssoc.getDiffDirection())) {
          result.add(Map.of("diffAssociation", existAssoc, "isReverse", false));
        } else if (currentAssoc.getDiffLeftClassRoleName().equals(existAssoc.getDiffRightClassRoleName()) &&
          currentAssoc.getDiffRightClassRoleName().equals(existAssoc.getDiffLeftClassRoleName()) &&
          currentAssoc.getDiffDirection().equals(reverseDirection(existAssoc.getDiffDirection()))) {
          result.add(Map.of("diffAssociation", existAssoc, "isReverse", true));
        }
      });
    }
    return result;
  }

  /**
   * Fuzzy search for DiffAssociation by ClassName
   */
  public static Map<String, DiffAssociation> fuzzySearchDiffAssociationByClassName(Map<String, DiffAssociation> map, String className) {
    Map<String, DiffAssociation> result = new HashMap<>();
    if (map == null) {
      return null;
    } else {
      result = map.values()
        .stream()
        .filter(e -> (e.getLeftOriginalClassName().equals(className) || e.getRightOriginalClassName().equals(className)))
        .collect(Collectors.toMap(e -> (String) e.getName(), e -> e));
    }
    return result;
  }

  /**
   * get all inheritance path for each top class by backtracking
   */
  public List<List<String>> getAllInheritancePath4DiffClass(DiffClass diffClass, MutableGraph<String> inheritanceGraph) {
    String root = diffClass.getName();
    List<List<String>> pathList = new ArrayList<>();
    getAllInheritancePath4DiffClassHelper(root, new LinkedList<>(), pathList, inheritanceGraph);
    return pathList;
  }

  /**
   * backtracking helper
   */
  private void getAllInheritancePath4DiffClassHelper(String root, LinkedList<String> path, List<List<String>> pathList, MutableGraph<String> inheritanceGraph) {
    if (inheritanceGraph.successors(root).isEmpty()) {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      pathList.add(newPath);
      return;
    } else {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      Iterator iterator = inheritanceGraph.successors(root).iterator();
      while (iterator.hasNext()) {
        String parentNode = iterator.next().toString();
        getAllInheritancePath4DiffClassHelper(parentNode, newPath, pathList, inheritanceGraph);
      }
    }
  }

  /**
   * getting all bottom class in inheritance graph
   */
  public static Set<String> getAllBottomDiffClassNode(MutableGraph<String> inheritanceGraph) {
    Set<String> result = new HashSet<>();
    inheritanceGraph.nodes().forEach(s -> {
      if (inheritanceGraph.predecessors(s).isEmpty()) {
        result.add(s);
      }
    });
    return result;
  }

  /**
   * getting inherited diffClass name by given diffClass name
   */
  public static Set<String> getInheritedClassSet(MutableGraph<String> inheritanceGraph, String diffClassName) {
    Set<String> result = new HashSet<>();
    result.add(diffClassName);
    Deque<String> currentDiffClassNameQueue = new LinkedList<>();
    currentDiffClassNameQueue.offer(diffClassName);
    while (!inheritanceGraph.predecessors(currentDiffClassNameQueue.peek()).isEmpty()) {
      inheritanceGraph.predecessors(currentDiffClassNameQueue.poll()).forEach(e -> {
        result.add(e);
        currentDiffClassNameQueue.offer(e);
      });
    }
    return result;
  }

  /**
   * getting super diffClass name by given diffClass name
   */
  public static Set<String> getSuperClassSet(MutableGraph<String> inheritanceGraph, String diffClassName) {
    Set<String> result = new HashSet<>();
    result.add(diffClassName);
    Deque<String> currentDiffClassNameQueue = new LinkedList<>();
    currentDiffClassNameQueue.offer(diffClassName);
    while (!inheritanceGraph.successors(currentDiffClassNameQueue.peek()).isEmpty()) {
      inheritanceGraph.successors(currentDiffClassNameQueue.poll()).forEach(e -> {
        result.add(e);
        currentDiffClassNameQueue.offer(e);
      });
    }
    return result;
  }


  /**
   * return all subclasses about given diffClass expect abstract class and interface
   */
  public static List<DiffClass> getAllSimpleSubClasses4DiffClass(DiffClass diffClass, MutableGraph<String> inheritanceGraph, Map<String, DiffClass> diffClassGroup) {
    List<DiffClass> result = new ArrayList<>();
    inheritanceGraph.predecessors(diffClass.getName()).forEach(e -> {
      if (diffClassGroup.get(e).getDiffKind() == DifferentGroup.DiffClassKind.DIFF_CLASS) {
        result.add(diffClassGroup.get(e));
      }
    });
    return result;
  }

  /**
   * generate the list of DiffRefSetAssociation
   * each original association has one DiffRefSetAssociation object
   */
  public static List<DiffRefSetAssociation> createDiffRefSetAssociation(Map<String, DiffAssociation> diffAssociationGroup, MutableGraph<String> inheritanceGraph) {

    List<DiffRefSetAssociation> refSetAssociationList = new ArrayList<>();

    List<DiffAssociation> originalDiffAssocList = diffAssociationGroup.values()
      .stream()
      .filter(e -> e.getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC)
      .collect(Collectors.toList());

    originalDiffAssocList.forEach(originalAssoc -> {
      Set<DiffClass> leftRefSet = new HashSet<>();
      leftRefSet.add(originalAssoc.getDiffLeftClass());
      Set<DiffClass> rightRefSet = new HashSet<>();
      rightRefSet.add(originalAssoc.getDiffRightClass());
      String leftRoleName = originalAssoc.getDiffLeftClassRoleName();
      String rightRoleName = originalAssoc.getDiffRightClassRoleName();
      DifferentGroup.DiffAssociationDirection direction = originalAssoc.getDiffDirection();

      List<Map<String, Object>> matchedAssocList = fuzzySearchDiffAssociationByDiffAssociationWithRoleNameAndDirection(diffAssociationGroup, originalAssoc);
      Set<String> leftInheritedClassSet = getInheritedClassSet(inheritanceGraph, originalAssoc.getDiffLeftClass().getName());
      Set<String> rightInheritedClassSet = getInheritedClassSet(inheritanceGraph, originalAssoc.getDiffRightClass().getName());
      matchedAssocList.forEach(e -> {
        if ((boolean) e.get("isReverse") == false) {
          DiffAssociation inheritedAssoc = (DiffAssociation) e.get("diffAssociation");
          if (leftInheritedClassSet.contains(inheritedAssoc.getDiffLeftClass().getName()) &&
            rightInheritedClassSet.contains(inheritedAssoc.getDiffRightClass().getName())) {
            leftRefSet.add(inheritedAssoc.getDiffLeftClass());
            rightRefSet.add(inheritedAssoc.getDiffRightClass());
          }
        } else {
          DiffAssociation inheritedAssoc = (DiffAssociation) e.get("diffAssociation");
          if (leftInheritedClassSet.contains(inheritedAssoc.getDiffRightClass().getName()) &&
            rightInheritedClassSet.contains(inheritedAssoc.getDiffLeftClass().getName())) {
            leftRefSet.add(inheritedAssoc.getDiffRightClass());
            rightRefSet.add(inheritedAssoc.getDiffLeftClass());
          }
        }
      });

      refSetAssociationList.add(new DiffRefSetAssociation(leftRefSet, leftRoleName, direction, rightRoleName, rightRefSet));
    });

    return refSetAssociationList;
  }

  /**
   * set the left side class name into ASTCDAssociation
   */
  public static ASTCDAssociation editASTCDAssociationLeftSideByDiffClass(ASTCDAssociation original, DiffClass diffClass) {
    ASTCDAssociation edited = original.deepClone();
    edited.getLeft().getMCQualifiedType().setMCQualifiedName(
      MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
        .addParts(diffClass.getOriginalClassName())
        .build());
    return edited;
  }

  /**
   * set the right side class name into ASTCDAssociation
   */
  public static ASTCDAssociation editASTCDAssociationRightSideByDiffClass(ASTCDAssociation original, DiffClass diffClass) {
    ASTCDAssociation edited = original.deepClone();
    edited.getRight().getMCQualifiedType().setMCQualifiedName(
      MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
        .addParts(diffClass.getOriginalClassName())
        .build());
    return edited;
  }

  /**
   * genetate the role name for ASTCDAssociation
   * if there is no role name in the original ASTCDAssociation
   * then set the lower case of the left/right class qualified name as role name
   */
  public static ASTCDAssociation generateASTCDAssociationRoleName(ASTCDAssociation astcdAssociation) {
    if (!astcdAssociation.getLeft().isPresentCDRole()) {
      String leftRoleName = astcdAssociation.getLeftQualifiedName().getQName().toLowerCase();
      astcdAssociation.getLeft().setCDRole(CD4AnalysisMill.cDRoleBuilder().setName(leftRoleName).build());
    }
    if (!astcdAssociation.getRight().isPresentCDRole()) {
      String rightRoleName = astcdAssociation.getRightQualifiedName().getQName().toLowerCase();
      astcdAssociation.getRight().setCDRole(CD4AnalysisMill.cDRoleBuilder().setName(rightRoleName).build());
    }
    return astcdAssociation;
  }
}
