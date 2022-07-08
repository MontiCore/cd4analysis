package de.monticore.syntax2semdiff.cd2sg;

import com.google.common.graph.MutableGraph;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.syntax2semdiff.cd2sg.metamodel.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SupportHelper {

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * get the corresponding SupportClass kind by ASTCDType
   */
  public static SupportGroup.SupportClassKind distinguishASTCDTypeHelper(ASTCDType astcdType) {
    if (astcdType.getClass().equals(ASTCDClass.class)) {
      if (astcdType.getModifier().isAbstract()) {
        return SupportGroup.SupportClassKind.SUPPORT_ABSTRACT_CLASS;
      } else {
        return SupportGroup.SupportClassKind.SUPPORT_CLASS;
      }
    } else if (astcdType.getClass().equals(ASTCDEnum.class)) {
      return SupportGroup.SupportClassKind.SUPPORT_ENUM;
    } else {
      return SupportGroup.SupportClassKind.SUPPORT_INTERFACE;
    }
  }

  /**
   * get the corresponding prefix of SupportClass name by supportClassKind
   */
  public static String getSupportClassKindStrHelper(SupportGroup.SupportClassKind supportClassKind) {
    switch (supportClassKind) {
      case SUPPORT_CLASS:
        return "SupportClass";
      case SUPPORT_ENUM:
        return "SupportEnum";
      case SUPPORT_ABSTRACT_CLASS:
        return "SupportAbstractClass";
      case SUPPORT_INTERFACE:
        return "SupportInterface";
      default:
        return null;
    }
  }

  /**
   * using the original class name to find corresponding SupportClass in SupportClassGroup
   */
  public static SupportClass findSupportClass4OriginalClassName(Map<String, SupportClass> supportClassGroup,
                                                                String originalClassName) {
    if (supportClassGroup.containsKey("SupportClass_" + originalClassName)) {
      return supportClassGroup.get("SupportClass_" + originalClassName);
    } else if (supportClassGroup.containsKey("SupportAbstractClass_" + originalClassName)) {
      return supportClassGroup.get("SupportAbstractClass_" + originalClassName);
    } else if (supportClassGroup.containsKey("SupportInterface_" + originalClassName)) {
      return supportClassGroup.get("SupportInterface_" + originalClassName);
    } else {
      return supportClassGroup.get("SupportEnum_" + originalClassName);
    }
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * get the corresponding the direction kind of SupportAssociation by ASTCDAssociation
   */
  public static SupportGroup.SupportAssociationDirection distinguishAssociationDirectionHelper(ASTCDAssociation astcdAssociation) {
    boolean left = astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean right = astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight();
    boolean bidirectional = astcdAssociation.getCDAssocDir().isBidirectional();
    if (!left && right && !bidirectional) {
      return SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT;
    } else if (left && !right && !bidirectional) {
      return SupportGroup.SupportAssociationDirection.RIGHT_TO_LEFT;
    } else if (left && right && bidirectional) {
      return SupportGroup.SupportAssociationDirection.BIDIRECTIONAL;
    } else {
      return SupportGroup.SupportAssociationDirection.UNDEFINED;
    }
  }

  /**
   * get the corresponding the left cardinality kind of SupportAssociation by ASTCDAssociation
   */
  public static SupportGroup.SupportAssociationCardinality distinguishLeftAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().getCDCardinality().isOne()) {
      return SupportGroup.SupportAssociationCardinality.ONE;
    } else if (astcdAssociation.getLeft().getCDCardinality().isOpt()) {
      return SupportGroup.SupportAssociationCardinality.ZERO_TO_ONE;
    } else if (astcdAssociation.getLeft().getCDCardinality().isAtLeastOne()) {
      return SupportGroup.SupportAssociationCardinality.ONE_TO_MORE;
    } else {
      return SupportGroup.SupportAssociationCardinality.MORE;
    }
  }

  /**
   * get the corresponding the right cardinality kind of SupportAssociation by ASTCDAssociation
   */
  public static SupportGroup.SupportAssociationCardinality distinguishRightAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().getCDCardinality().isOne()) {
      return SupportGroup.SupportAssociationCardinality.ONE;
    } else if (astcdAssociation.getRight().getCDCardinality().isOpt()) {
      return SupportGroup.SupportAssociationCardinality.ZERO_TO_ONE;
    } else if (astcdAssociation.getRight().getCDCardinality().isAtLeastOne()) {
      return SupportGroup.SupportAssociationCardinality.ONE_TO_MORE;
    } else {
      return SupportGroup.SupportAssociationCardinality.MORE;
    }
  }

  /**
   * get the left class role name in SupportAssociation
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
   * get the right class role name in SupportAssociation
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
  public static SupportGroup.SupportAssociationDirection reverseDirection(SupportGroup.SupportAssociationDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return SupportGroup.SupportAssociationDirection.RIGHT_TO_LEFT;
      case RIGHT_TO_LEFT:
        return SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT;
      default:
        return direction;
    }
  }

  /**
   * format direction to String
   */
  public static String formatDirection(SupportGroup.SupportAssociationDirection direction) {
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
  public static SupportGroup.SupportAssociationCardinality supportAssociationCardinalityHelper(SupportGroup.SupportAssociationCardinality exist,
                                                                                               SupportGroup.SupportAssociationCardinality current) {
    switch (exist) {
      case ONE:
        switch (current) {
          case ONE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          case ZERO_TO_ONE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          case ONE_TO_MORE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          default:
            return SupportGroup.SupportAssociationCardinality.ONE;
        }
      case ZERO_TO_ONE:
        switch (current) {
          case ONE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          case ZERO_TO_ONE:
            return SupportGroup.SupportAssociationCardinality.ZERO_TO_ONE;
          case ONE_TO_MORE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          default:
            return SupportGroup.SupportAssociationCardinality.ZERO_TO_ONE;
        }
      case ONE_TO_MORE:
        switch (current) {
          case ONE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          case ZERO_TO_ONE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          case ONE_TO_MORE:
            return SupportGroup.SupportAssociationCardinality.ONE_TO_MORE;
          default:
            return SupportGroup.SupportAssociationCardinality.ONE_TO_MORE;
        }
      default:
        switch (current) {
          case ONE:
            return SupportGroup.SupportAssociationCardinality.ONE;
          case ZERO_TO_ONE:
            return SupportGroup.SupportAssociationCardinality.ZERO_TO_ONE;
          case ONE_TO_MORE:
            return SupportGroup.SupportAssociationCardinality.ONE_TO_MORE;
          default:
            return SupportGroup.SupportAssociationCardinality.MORE;
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
  public static String supportAssociationDirectionHelper(SupportGroup.SupportAssociationDirection exist,
                                                         SupportGroup.SupportAssociationDirection current) {
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
   * calculate the intersection set of cardinality of given SupportAssociation by its relevant SupportRefSetAssociations
   * only for A -> B and A <- B in the same CD
   */
  public static SupportAssociation intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(SupportAssociation originalAssoc,
                                                                                                                               SupportGroup sg){
    SupportAssociation resultAssoc;
    try {
      resultAssoc = originalAssoc.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    if (originalAssoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT ||
      originalAssoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.RIGHT_TO_LEFT) {
      List<SupportAssociation> supportAssociationList = sg.getSupportAssociationGroup().values()
        .stream()
        .filter(e ->
            // A <- B, A -> B
            (e.getLeftOriginalClassName().equals(originalAssoc.getLeftOriginalClassName()) &&
              e.getSupportLeftClassRoleName().equals(originalAssoc.getSupportLeftClassRoleName()) &&
              e.getSupportDirection().equals(reverseDirection(originalAssoc.getSupportDirection())) &&
              e.getSupportRightClassRoleName().equals(originalAssoc.getSupportRightClassRoleName()) &&
              e.getRightOriginalClassName().equals(originalAssoc.getRightOriginalClassName())) ||
            // A <- B, B <- A
            (e.getLeftOriginalClassName().equals(originalAssoc.getRightOriginalClassName()) &&
              e.getSupportLeftClassRoleName().equals(originalAssoc.getSupportRightClassRoleName()) &&
              e.getSupportDirection().equals(originalAssoc.getSupportDirection()) &&
              e.getSupportRightClassRoleName().equals(originalAssoc.getSupportLeftClassRoleName()) &&
              e.getRightOriginalClassName().equals(originalAssoc.getLeftOriginalClassName())))
        .collect(Collectors.toList());

      AtomicReference<SupportGroup.SupportAssociationCardinality> finalLeftCardinality =
        new AtomicReference<>(originalAssoc.getSupportLeftClassCardinality());
      AtomicReference<SupportGroup.SupportAssociationCardinality> finalRightCardinality =
        new AtomicReference<>(originalAssoc.getSupportRightClassCardinality());
      supportAssociationList.forEach(e -> {
        if (e.getSupportDirection().equals(reverseDirection(originalAssoc.getSupportDirection()))) {
          // A <- B, A -> B
          finalLeftCardinality.set(
            supportAssociationCardinalityHelper(finalLeftCardinality.get(), e.getSupportLeftClassCardinality()));
          finalRightCardinality.set(
            supportAssociationCardinalityHelper(finalRightCardinality.get(), e.getSupportRightClassCardinality()));
        } else {
          // A <- B, B <- A
          finalLeftCardinality.set(
            supportAssociationCardinalityHelper(finalLeftCardinality.get(), e.getSupportRightClassCardinality()));
          finalRightCardinality.set(
            supportAssociationCardinalityHelper(finalRightCardinality.get(), e.getSupportLeftClassCardinality()));
        }
      });

      resultAssoc.setSupportLeftClassCardinality(finalLeftCardinality.get());
      resultAssoc.setSupportRightClassCardinality(finalRightCardinality.get());
    }

    return resultAssoc;
  }

  /**
   * calculate the intersection set of cardinality of given SupportAssociation by its relevant SupportRefSetAssociations
   */
  public static SupportAssociation intersectSupportAssociationCardinalityBySupportAssociationWithOverlap(SupportAssociation originalAssoc,
                                                                                                         SupportGroup sg){
    SupportAssociation resultAssoc;
    try {
      resultAssoc = originalAssoc.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }

    AtomicReference<SupportGroup.SupportAssociationCardinality> intersectedLeftCardinality =
      new AtomicReference<>(originalAssoc.getSupportLeftClassCardinality());
    AtomicReference<SupportGroup.SupportAssociationCardinality> intersectedRightCardinality =
      new AtomicReference<>(originalAssoc.getSupportRightClassCardinality());

    sg.getRefSetAssociationList().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getSupportLeftClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getSupportRightClassRoleName()) &&
        item.getDirection().equals(originalAssoc.getSupportDirection()) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportLeftClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportRightClass().getName()))) {
        SupportAssociationCardinalityPack intersectedCardinalityPack =
          intersectSupportAssociationCardinalityHelper(originalAssoc,
            false, item, sg, intersectedLeftCardinality.get(), intersectedRightCardinality.get());
        intersectedLeftCardinality.set(intersectedCardinalityPack.getLeftCardinality());
        intersectedRightCardinality.set(intersectedCardinalityPack.getRightCardinality());
      } else if (item.getLeftRoleName().equals(originalAssoc.getSupportRightClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getSupportLeftClassRoleName()) &&
        item.getDirection().equals(reverseDirection(originalAssoc.getSupportDirection())) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportRightClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportLeftClass().getName()))) {
        SupportAssociationCardinalityPack intersectedCardinalityPack =
          intersectSupportAssociationCardinalityHelper(originalAssoc,
            true, item, sg, intersectedRightCardinality.get(), intersectedLeftCardinality.get());
        intersectedRightCardinality.set(intersectedCardinalityPack.getLeftCardinality());
        intersectedLeftCardinality.set(intersectedCardinalityPack.getRightCardinality());
      }
    });

    // update Cardinality
    resultAssoc.setSupportLeftClassCardinality(intersectedLeftCardinality.get());
    resultAssoc.setSupportRightClassCardinality(intersectedRightCardinality.get());

    return resultAssoc;
  }

  /**
   * calculate the intersection set of left and right cardinality of related SupportAssociation in SupportRefSetAssociation
   *
   * @Return: SupportAssociationCardinalityPack
   *  {"leftCardinality"   : SupportGroup.SupportAssociationCardinality
   *   "rightCardinality"  : SupportGroup.SupportAssociationCardinality }
   */
  public static SupportAssociationCardinalityPack intersectSupportAssociationCardinalityHelper(
    SupportAssociation originalAssoc,
    boolean isReversed,
    SupportRefSetAssociation supportRefSetAssociation,
    SupportGroup sg,
    SupportGroup.SupportAssociationCardinality existLeftCardinality,
    SupportGroup.SupportAssociationCardinality existRightCardinality) {

    Set<String> leftSuperClassSet;
    Set<String> rightSuperClassSet;
    if (!isReversed) {
      leftSuperClassSet = getSuperClassSet(sg.getInheritanceGraph(), originalAssoc.getSupportLeftClass().getName());
      rightSuperClassSet = getSuperClassSet(sg.getInheritanceGraph(), originalAssoc.getSupportRightClass().getName());
    } else {
      leftSuperClassSet = getSuperClassSet(sg.getInheritanceGraph(), originalAssoc.getSupportRightClass().getName());
      rightSuperClassSet = getSuperClassSet(sg.getInheritanceGraph(), originalAssoc.getSupportLeftClass().getName());
    }

    AtomicReference<SupportGroup.SupportAssociationCardinality> leftResult = new AtomicReference<>(existLeftCardinality);
    AtomicReference<SupportGroup.SupportAssociationCardinality> rightResult = new AtomicReference<>(existRightCardinality);
    Set<String> finalLeftSuperClassSet = leftSuperClassSet;
    Set<String> finalRightSuperClassSet = rightSuperClassSet;

    supportRefSetAssociation.getLeftRefSet().forEach(leftClass -> {
      supportRefSetAssociation.getRightRefSet().forEach(rightClass -> {
        StringBuilder sb = new StringBuilder();
        sb.append("SupportAssociation_");
        sb.append(leftClass.getOriginalClassName() + "_");
        sb.append(supportRefSetAssociation.getLeftRoleName() + "_");
        sb.append(formatDirection(supportRefSetAssociation.getDirection()) + "_");
        sb.append(supportRefSetAssociation.getRightRoleName() + "_");
        sb.append(rightClass.getOriginalClassName());
        if ((finalLeftSuperClassSet.contains(leftClass.getName()) || finalRightSuperClassSet.contains(rightClass.getName())) &&
          sg.getSupportAssociationGroup().containsKey(sb.toString())) {
          leftResult.set(supportAssociationCardinalityHelper(leftResult.get(),
            sg.getSupportAssociationGroup().get(sb.toString()).getSupportLeftClassCardinality()));
          rightResult.set(supportAssociationCardinalityHelper(rightResult.get(),
            sg.getSupportAssociationGroup().get(sb.toString()).getSupportRightClassCardinality()));
        } else {
          StringBuilder reversedSb = new StringBuilder();
          reversedSb.append("SupportAssociation_");
          reversedSb.append(rightClass.getOriginalClassName());
          reversedSb.append(supportRefSetAssociation.getRightRoleName() + "_");
          reversedSb.append(formatDirection(reverseDirection(supportRefSetAssociation.getDirection())) + "_");
          reversedSb.append(supportRefSetAssociation.getLeftRoleName() + "_");
          reversedSb.append(leftClass.getOriginalClassName() + "_");
          if ((finalLeftSuperClassSet.contains(leftClass.getName()) || finalRightSuperClassSet.contains(rightClass.getName())) &&
            sg.getSupportAssociationGroup().containsKey(reversedSb.toString())) {
            leftResult.set(supportAssociationCardinalityHelper(leftResult.get(),
              sg.getSupportAssociationGroup().get(reversedSb.toString()).getSupportLeftClassCardinality()));
            rightResult.set(supportAssociationCardinalityHelper(rightResult.get(),
              sg.getSupportAssociationGroup().get(reversedSb.toString()).getSupportRightClassCardinality()));
          }
        }
      });
    });
    return new SupportAssociationCardinalityPack(leftResult.get(), rightResult.get());
  }

  /**
   * Fuzzy search for SupportAssociation without matching direction
   *
   * @Return: List<SupportAssociationPack>
   *  [{"supportAssociation"  : SupportAssociation
   *    "isReverse"           : boolean         }]
   */
  public static List<SupportAssociationPack> fuzzySearchSupportAssociationBySupportAssociationWithoutDirection(Map<String, SupportAssociation> map,
                                                                                                               SupportAssociation currentAssoc) {
    List<SupportAssociationPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    } else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getLeftOriginalClassName()) &&
          currentAssoc.getSupportLeftClassRoleName().equals(existAssoc.getSupportLeftClassRoleName()) &&
          currentAssoc.getSupportRightClassRoleName().equals(existAssoc.getSupportRightClassRoleName()) &&
          currentAssoc.getRightOriginalClassName().equals(existAssoc.getRightOriginalClassName())) {
          result.add(new SupportAssociationPack(existAssoc, false));
        } else if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getRightOriginalClassName()) &&
          currentAssoc.getSupportLeftClassRoleName().equals(existAssoc.getSupportRightClassRoleName()) &&
          currentAssoc.getSupportRightClassRoleName().equals(existAssoc.getSupportLeftClassRoleName()) &&
          currentAssoc.getRightOriginalClassName().equals(existAssoc.getLeftOriginalClassName())) {
          result.add(new SupportAssociationPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  /**
   * Fuzzy search for SupportAssociation only matching leftRoleName, rightRoleName and direction
   *
   * @Return: List<SupportAssociationPack>
   *  [{"supportAssociation"  : SupportAssociation
   *    "isReverse"           : boolean         }]
   */
  public static List<SupportAssociationPack> fuzzySearchSupportAssociationBySupportAssociationWithRoleNameAndDirection(Map<String, SupportAssociation> map,
                                                                                                                       SupportAssociation currentAssoc) {
    List<SupportAssociationPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    } else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getSupportLeftClassRoleName().equals(existAssoc.getSupportLeftClassRoleName()) &&
          currentAssoc.getSupportRightClassRoleName().equals(existAssoc.getSupportRightClassRoleName()) &&
          currentAssoc.getSupportDirection().equals(existAssoc.getSupportDirection())) {
          result.add(new SupportAssociationPack(existAssoc, false));
        } else if (currentAssoc.getSupportLeftClassRoleName().equals(existAssoc.getSupportRightClassRoleName()) &&
          currentAssoc.getSupportRightClassRoleName().equals(existAssoc.getSupportLeftClassRoleName()) &&
          currentAssoc.getSupportDirection().equals(reverseDirection(existAssoc.getSupportDirection()))) {
          result.add(new SupportAssociationPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /**
   * Fuzzy search for SupportAssociation by ClassName
   */
  public static Map<String, SupportAssociation> fuzzySearchSupportAssociationByClassName(Map<String, SupportAssociation> map,
                                                                                         String className) {
    Map<String, SupportAssociation> result = new HashMap<>();
    if (map == null) {
      return null;
    } else {
      result = map.values()
        .stream()
        .filter(e ->
          (e.getLeftOriginalClassName().equals(className) || e.getRightOriginalClassName().equals(className)))
        .collect(Collectors.toMap(e -> (String) e.getName(), e -> e));
    }
    return result;
  }

  /**
   * get all inheritance path for each top class by backtracking
   */
  public List<List<String>> getAllInheritancePath4SupportClass(SupportClass supportClass,
                                                               MutableGraph<String> inheritanceGraph) {
    String root = supportClass.getName();
    List<List<String>> pathList = new ArrayList<>();
    getAllInheritancePath4SupportClassHelper(root, new LinkedList<>(), pathList, inheritanceGraph);
    return pathList;
  }

  /**
   * backtracking helper
   */
  private void getAllInheritancePath4SupportClassHelper(String root,
                                                        LinkedList<String> path,
                                                        List<List<String>> pathList,
                                                        MutableGraph<String> inheritanceGraph) {
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
        getAllInheritancePath4SupportClassHelper(parentNode, newPath, pathList, inheritanceGraph);
      }
    }
  }

  /**
   * getting all bottom class in inheritance graph
   */
  public static Set<String> getAllBottomSupportClassNode(MutableGraph<String> inheritanceGraph) {
    Set<String> result = new HashSet<>();
    inheritanceGraph.nodes().forEach(s -> {
      if (inheritanceGraph.predecessors(s).isEmpty()) {
        result.add(s);
      }
    });
    return result;
  }

  /**
   * getting inherited SupportClass name by given supportClass name
   */
  public static Set<String> getInheritedClassSet(MutableGraph<String> inheritanceGraph, String supportClassName) {
    Set<String> result = new HashSet<>();
    result.add(supportClassName);
    Deque<String> currentSupportClassNameQueue = new LinkedList<>();
    currentSupportClassNameQueue.offer(supportClassName);
    while (!inheritanceGraph.predecessors(currentSupportClassNameQueue.peek()).isEmpty()) {
      inheritanceGraph.predecessors(currentSupportClassNameQueue.poll()).forEach(e -> {
        result.add(e);
        currentSupportClassNameQueue.offer(e);
      });
    }
    return result;
  }

  /**
   * getting super SupportClass name by given supportClass name
   */
  public static Set<String> getSuperClassSet(MutableGraph<String> inheritanceGraph, String supportClassName) {
    Set<String> result = new HashSet<>();
    result.add(supportClassName);
    Deque<String> currentSupportClassNameQueue = new LinkedList<>();
    currentSupportClassNameQueue.offer(supportClassName);
    while (!inheritanceGraph.successors(currentSupportClassNameQueue.peek()).isEmpty()) {
      inheritanceGraph.successors(currentSupportClassNameQueue.poll()).forEach(e -> {
        result.add(e);
        currentSupportClassNameQueue.offer(e);
      });
    }
    return result;
  }


  /**
   * return all subclasses about given SupportClass expect abstract class and interface
   */
  public static List<SupportClass> getAllSimpleSubClasses4SupportClass(SupportClass supportClass,
                                                                       MutableGraph<String> inheritanceGraph,
                                                                       Map<String, SupportClass> supportClassGroup) {
    List<SupportClass> result = new LinkedList<>();
    inheritanceGraph.predecessors(supportClass.getName()).forEach(e -> {
      if (supportClassGroup.get(e).getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_CLASS) {
        result.add(supportClassGroup.get(e));
      }
    });
    return result;
  }

  /**
   * generate the list of SupportRefSetAssociation
   * each original association has one SupportRefSetAssociation object
   */
  public static List<SupportRefSetAssociation> createSupportRefSetAssociation(Map<String, SupportAssociation> supportAssociationGroup,
                                                                              MutableGraph<String> inheritanceGraph) {

    List<SupportRefSetAssociation> refSetAssociationList = new ArrayList<>();

    List<SupportAssociation> originalSupportAssocList = supportAssociationGroup.values()
      .stream()
      .filter(e -> e.getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC)
      .collect(Collectors.toList());

    originalSupportAssocList.forEach(originalAssoc -> {
      Set<SupportClass> leftRefSet = new HashSet<>();
      leftRefSet.add(originalAssoc.getSupportLeftClass());
      Set<SupportClass> rightRefSet = new HashSet<>();
      rightRefSet.add(originalAssoc.getSupportRightClass());
      String leftRoleName = originalAssoc.getSupportLeftClassRoleName();
      String rightRoleName = originalAssoc.getSupportRightClassRoleName();
      SupportGroup.SupportAssociationDirection direction = originalAssoc.getSupportDirection();

      List<SupportAssociationPack> matchedAssocList =
        fuzzySearchSupportAssociationBySupportAssociationWithRoleNameAndDirection(supportAssociationGroup, originalAssoc);
      Set<String> leftInheritedClassSet =
        getInheritedClassSet(inheritanceGraph, originalAssoc.getSupportLeftClass().getName());
      Set<String> rightInheritedClassSet =
        getInheritedClassSet(inheritanceGraph, originalAssoc.getSupportRightClass().getName());
      matchedAssocList.forEach(e -> {
        if (!e.isReverse()) {
          SupportAssociation inheritedAssoc = e.getSupportAssociation();
          if (leftInheritedClassSet.contains(inheritedAssoc.getSupportLeftClass().getName()) &&
            rightInheritedClassSet.contains(inheritedAssoc.getSupportRightClass().getName())) {
            leftRefSet.add(inheritedAssoc.getSupportLeftClass());
            rightRefSet.add(inheritedAssoc.getSupportRightClass());
          }
        } else {
          SupportAssociation inheritedAssoc = e.getSupportAssociation();
          if (leftInheritedClassSet.contains(inheritedAssoc.getSupportRightClass().getName()) &&
            rightInheritedClassSet.contains(inheritedAssoc.getSupportLeftClass().getName())) {
            leftRefSet.add(inheritedAssoc.getSupportRightClass());
            rightRefSet.add(inheritedAssoc.getSupportLeftClass());
          }
        }
      });

      refSetAssociationList.add(
        new SupportRefSetAssociation(leftRefSet, leftRoleName, direction, rightRoleName, rightRefSet, originalAssoc));
    });

    return refSetAssociationList;
  }

  /**
   * set the left side class name into ASTCDAssociation
   */
  public static ASTCDAssociation editASTCDAssociationLeftSideBySupportClass(ASTCDAssociation original,
                                                                            SupportClass supportClass) {
    ASTCDAssociation edited = original.deepClone();
    edited.getLeft().getMCQualifiedType().setMCQualifiedName(
      MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
        .addParts(supportClass.getOriginalClassName())
        .build());
    return edited;
  }

  /**
   * set the right side class name into ASTCDAssociation
   */
  public static ASTCDAssociation editASTCDAssociationRightSideBySupportClass(ASTCDAssociation original,
                                                                             SupportClass supportClass) {
    ASTCDAssociation edited = original.deepClone();
    edited.getRight().getMCQualifiedType().setMCQualifiedName(
      MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
        .addParts(supportClass.getOriginalClassName())
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

  /**
   * exchange left side and right side of SupportAssociation
   */
  public static SupportAssociation reverseSupportAssociation(SupportAssociation currentAssoc,
                                                             ASTCDAssocDir reversedAstcdAssocDir) {
    SupportAssociation reversedAssoc;
    try {
      reversedAssoc = currentAssoc.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    SupportClass currentLeftClass = currentAssoc.getSupportLeftClass();
    SupportClass currentRightClass = currentAssoc.getSupportRightClass();

    ASTCDAssociation reversedASTCDAssociation = reversedAssoc.getEditedElement();
    reversedASTCDAssociation = editASTCDAssociationLeftSideBySupportClass(reversedASTCDAssociation, currentRightClass);
    reversedASTCDAssociation = editASTCDAssociationRightSideBySupportClass(reversedASTCDAssociation, currentLeftClass);
    reversedASTCDAssociation.setCDAssocDir(reversedAstcdAssocDir);

    reversedAssoc.setEditedElement(reversedASTCDAssociation);
    reversedAssoc.setSupportLeftClass(currentRightClass);
    reversedAssoc.setSupportRightClass(currentLeftClass);

    return reversedAssoc;
  }
}
