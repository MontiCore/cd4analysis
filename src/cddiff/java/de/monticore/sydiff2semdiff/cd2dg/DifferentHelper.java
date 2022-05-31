package de.monticore.sydiff2semdiff.cd2dg;

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

  public static DifferentGroup.DiffClassKind distinguishASTCDTypeHelper(ASTCDType astcdType) {
    if (astcdType.getClass().equals(ASTCDClass.class)) {
      if (astcdType.getModifier().isAbstract()) {
        return DifferentGroup.DiffClassKind.DIFF_ABSTRACT_CLASS;
      }
      else {
        return DifferentGroup.DiffClassKind.DIFF_CLASS;
      }
    }
    else if (astcdType.getClass().equals(ASTCDEnum.class)) {
      return DifferentGroup.DiffClassKind.DIFF_ENUM;
    }
    else {
      return DifferentGroup.DiffClassKind.DIFF_INTERFACE;
    }
  }

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

  public static DifferentGroup.DiffAssociationDirection distinguishAssociationDirectionHelper(ASTCDAssociation astcdAssociation) {
    Boolean left = astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft();
    Boolean right = astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight();
    Boolean bidirectional = astcdAssociation.getCDAssocDir().isBidirectional();
    if (!left && right && !bidirectional) {
      return DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT;
    }
    else if (left && !right && !bidirectional) {
      return DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT;
    }
    else if (left && right && bidirectional) {
      return DifferentGroup.DiffAssociationDirection.BIDIRECTIONAL;
    }
    else {
      return DifferentGroup.DiffAssociationDirection.UNDEFINED;
    }
  }

  public static DifferentGroup.DiffAssociationCardinality distinguishLeftAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().getCDCardinality().isOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isOpt()) {
      return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isAtLeastOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
    }
    else {
      return DifferentGroup.DiffAssociationCardinality.MORE;
    }
  }

  public static DifferentGroup.DiffAssociationCardinality distinguishRightAssociationCardinalityHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().getCDCardinality().isOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isOpt()) {
      return DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isAtLeastOne()) {
      return DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE;
    }
    else {
      return DifferentGroup.DiffAssociationCardinality.MORE;
    }
  }

  public static String getLeftClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().isPresentCDRole()) {
      return astcdAssociation.getLeft().getCDRole().getName();
    }
    else {
      return astcdAssociation.getLeftQualifiedName().getQName().toLowerCase();
    }
  }

  public static String getRightClassRoleNameHelper(ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().isPresentCDRole()) {
      return astcdAssociation.getRight().getCDRole().getName();
    }
    else {
      return astcdAssociation.getRightQualifiedName().getQName().toLowerCase();
    }
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  public static Map<String, DiffAssociation> parseMapForFilter(Map<String, DiffAssociation> map, String filters) {
    if (map == null) {
      return null;
    }
    else {
      map = map.entrySet().stream().filter((e) -> e.getKey().contains(filters)).collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));
    }
    return map;
  }

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
        refSetAssociationList.add(new DiffRefSetAssociation(leftRefSet, leftRoleName,rightRoleName, rightRefSet));
      }));

    return refSetAssociationList;
  }

  public static ASTCDAssociation editASTCDAssociationLeftSideByDiffClass(ASTCDAssociation original, DiffClass diffClass) {
    ASTCDAssociation edited = original.deepClone();
    edited.getLeft().getMCQualifiedType().setMCQualifiedName(
      MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
        .addParts(diffClass.getOriginalClassName())
        .build());
    return edited;
  }

  public static ASTCDAssociation editASTCDAssociationRightSideByDiffClass(ASTCDAssociation original, DiffClass diffClass) {
    ASTCDAssociation edited = original.deepClone();
    edited.getRight().getMCQualifiedType().setMCQualifiedName(
      MCBasicTypesMillForCD4Analysis.mCQualifiedNameBuilder()
        .addParts(diffClass.getOriginalClassName())
        .build());
    return edited;
  }

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
