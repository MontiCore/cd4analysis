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

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  /**
   * Fuzzy search for DiffAssociationGroup by ClassName
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
   * getting all top class in inheritance graph
   */
  public static Set<String> getAllBottomNode(MutableGraph<String> inheritanceGraph) {
    Set<String> result = new HashSet<>();
    inheritanceGraph.nodes().forEach(s -> {
      if (inheritanceGraph.predecessors(s).isEmpty()) {
        result.add(s);
      }
    });
    return result;
  }

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
   */
  public static List<DiffRefSetAssociation> createDiffRefSetAssociation(Map<String, DiffAssociation> diffAssociationGroup) {
    Map<String, Map<String, List<DiffAssociation>>> groupResult = diffAssociationGroup
      .values()
      .stream()
      .collect(Collectors.groupingBy(DiffAssociation::getDiffLeftClassRoleName,
        Collectors.groupingBy(DiffAssociation::getDiffRightClassRoleName)));

    List<DiffRefSetAssociation> refSetAssociationList = new ArrayList<>();

    groupResult.forEach((leftRoleName, v) ->
      v.forEach((rightRoleName, list) -> {
        Set<DiffClass> leftRefSet = new HashSet<>();
        Set<DiffClass> rightRefSet = new HashSet<>();
        list.forEach(e -> {
          if (!leftRefSet.stream().anyMatch(s -> s.getOriginalClassName().equals(e.getDiffLeftClass().getOriginalClassName()))) {
            leftRefSet.add(e.getDiffLeftClass());
          }
          if (!rightRefSet.stream().anyMatch(s -> s.getOriginalClassName().equals(e.getDiffRightClass().getOriginalClassName()))) {
            rightRefSet.add(e.getDiffRightClass());
          }
        });
        refSetAssociationList.add(new DiffRefSetAssociation(leftRefSet, leftRoleName, rightRoleName, rightRefSet));
      }));

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
