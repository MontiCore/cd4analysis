package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import com.google.common.graph.MutableGraph;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.CDQNameHelper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4InheritanceHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4SearchHelper.*;

public class CDWrapper4AssocHelper {

  /**
   * get the corresponding the direction kind of CDAssociationWrapper by ASTCDAssociation
   */
  public static CDAssociationWrapperDirection distinguishAssociationDirectionHelper(
      ASTCDAssociation astcdAssociation) {
    boolean left = astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft();
    boolean right = astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight();
    boolean bidirectional = astcdAssociation.getCDAssocDir().isBidirectional();
    if (!left && right && !bidirectional) {
      return CDAssociationWrapperDirection.LEFT_TO_RIGHT;
    }
    else if (left && !right && !bidirectional) {
      return CDAssociationWrapperDirection.RIGHT_TO_LEFT;
    }
    else if (left && right && bidirectional) {
      return CDAssociationWrapperDirection.BIDIRECTIONAL;
    }
    else {
      return CDAssociationWrapperDirection.UNDEFINED;
    }
  }

  /**
   * get the corresponding the left cardinality kind of CDAssociationWrapper by ASTCDAssociation
   */
  public static CDAssociationWrapperCardinality distinguishLeftAssociationCardinalityHelper(
      ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getLeft().getCDCardinality().isOne()) {
      return CDAssociationWrapperCardinality.ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isOpt()) {
      return CDAssociationWrapperCardinality.ZERO_TO_ONE;
    }
    else if (astcdAssociation.getLeft().getCDCardinality().isAtLeastOne()) {
      return CDAssociationWrapperCardinality.ONE_TO_MORE;
    }
    else {
      return CDAssociationWrapperCardinality.MORE;
    }
  }

  /**
   * get the corresponding the right cardinality kind of CDAssociationWrapper by ASTCDAssociation
   */
  public static CDAssociationWrapperCardinality distinguishRightAssociationCardinalityHelper(
      ASTCDAssociation astcdAssociation) {
    if (astcdAssociation.getRight().getCDCardinality().isOne()) {
      return CDAssociationWrapperCardinality.ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isOpt()) {
      return CDAssociationWrapperCardinality.ZERO_TO_ONE;
    }
    else if (astcdAssociation.getRight().getCDCardinality().isAtLeastOne()) {
      return CDAssociationWrapperCardinality.ONE_TO_MORE;
    }
    else {
      return CDAssociationWrapperCardinality.MORE;
    }
  }

  /**
   * get the left class role name in CDAssociationWrapper
   * if it exists in ASTCDAssociation, then direct return the role name
   * otherwise set the lower case of the left class qualified name as role name
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
   * get the right class role name in CDAssociationWrapper
   * if it exists in ASTCDAssociation, then direct return the role name
   * otherwise set the lower case of the right class qualified name as role name
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
  public static CDAssociationWrapperDirection reverseDirection(CDAssociationWrapperDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return CDAssociationWrapperDirection.RIGHT_TO_LEFT;
      case RIGHT_TO_LEFT:
        return CDAssociationWrapperDirection.LEFT_TO_RIGHT;
      default:
        return direction;
    }
  }

  /**
   * format direction to String
   */
  public static String formatDirection(CDAssociationWrapperDirection direction) {
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
  public static CDAssociationWrapperCardinality cDAssociationWrapperCardinalityHelper(
      CDAssociationWrapperCardinality exist,
      CDAssociationWrapperCardinality current) {
    switch (exist) {
      case ONE:
        switch (current) {
          case ONE:
            return CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDAssociationWrapperCardinality.ONE;
          case ONE_TO_MORE:
            return CDAssociationWrapperCardinality.ONE;
          default:
            return CDAssociationWrapperCardinality.ONE;
        }
      case ZERO_TO_ONE:
        switch (current) {
          case ONE:
            return CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDAssociationWrapperCardinality.ZERO_TO_ONE;
          case ONE_TO_MORE:
            return CDAssociationWrapperCardinality.ONE;
          default:
            return CDAssociationWrapperCardinality.ZERO_TO_ONE;
        }
      case ONE_TO_MORE:
        switch (current) {
          case ONE:
            return CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDAssociationWrapperCardinality.ONE;
          case ONE_TO_MORE:
            return CDAssociationWrapperCardinality.ONE_TO_MORE;
          default:
            return CDAssociationWrapperCardinality.ONE_TO_MORE;
        }
      default:
        switch (current) {
          case ONE:
            return CDAssociationWrapperCardinality.ONE;
          case ZERO_TO_ONE:
            return CDAssociationWrapperCardinality.ZERO_TO_ONE;
          case ONE_TO_MORE:
            return CDAssociationWrapperCardinality.ONE_TO_MORE;
          default:
            return CDAssociationWrapperCardinality.MORE;
        }
    }
  }

  /**
   * return the intersection set for direction of association
   *
   * The return value are "current" or "exist" or "both"
   *  - "current" means using current direction
   *  - "exist" means using exist direction
   *  - "both" means using both directions
   */
  public static String cDAssociationWrapperDirectionHelper(
      CDAssociationWrapperDirection exist,
      CDAssociationWrapperDirection current) {
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
   * generate the role name for ASTCDAssociation
   * if there is no role name in the original ASTCDAssociation
   * then set the lower case of the left/right class name as role name
   */
  public static void generateASTCDAssociationRoleName(
      ASTCDAssociation astcdAssociation) {
    if (!astcdAssociation.getLeft().isPresentCDRole()) {
      String leftRoleName =
          CDQNameHelper.partHandler(astcdAssociation.getLeftReferenceName(), true);
      astcdAssociation.getLeft()
          .setCDRole(CD4AnalysisMill.cDRoleBuilder().setName(leftRoleName).build());
    }
    if (!astcdAssociation.getRight().isPresentCDRole()) {
      String rightRoleName =
          CDQNameHelper.partHandler(astcdAssociation.getRightReferenceName(), true);
      astcdAssociation.getRight()
          .setCDRole(CD4AnalysisMill.cDRoleBuilder().setName(rightRoleName).build());
    }
  }

  /**
   * If an association has no cardinality that means its underspecified
   * and for (static) SemDiff: no cardinality == [*]
   */
  public static void generateASTCDAssociationCardinality(
      ASTCDAssociation astcdAssociation) {
    if (!astcdAssociation.getLeft().isPresentCDCardinality()) {
      astcdAssociation.getLeft().setCDCardinality(CD4AnalysisMill.cDCardMultBuilder().build());
    }
    if (!astcdAssociation.getRight().isPresentCDCardinality()) {
      astcdAssociation.getRight().setCDCardinality(CD4AnalysisMill.cDCardMultBuilder().build());
    }
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
    reversedASTCDAssociation =
        editASTCDAssociationLeftSideByCDTypeWrapper(reversedASTCDAssociation, currentRightClass);
    reversedASTCDAssociation =
        editASTCDAssociationRightSideByCDTypeWrapper(reversedASTCDAssociation, currentLeftClass);
    reversedASTCDAssociation.setCDAssocDir(reversedAstCDAssocDir);

    reversedAssoc.setEditedElement(reversedASTCDAssociation);
    reversedAssoc.setCDWrapperLeftClass(currentRightClass);
    reversedAssoc.setCDWrapperRightClass(currentLeftClass);

    return reversedAssoc;
  }

  /**
   * calculate the intersection set of cardinality of given CDAssociationWrapper
   * by its relevant CDRefSetAssociationWrappers
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

    AtomicReference<CDAssociationWrapperCardinality> intersectedLeftCardinality =
        new AtomicReference<>(originalAssoc.getCDWrapperLeftClassCardinality());
    AtomicReference<CDAssociationWrapperCardinality> intersectedRightCardinality =
        new AtomicReference<>(originalAssoc.getCDWrapperRightClassCardinality());

    cdw.getRefSetAssociationList().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && item.getDirection().equals(originalAssoc.getCDAssociationWrapperDirection())
          && item.getLeftRefSet()
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
   * Return:
   * CDAssociationWrapperCardinalityPack {
   *  "leftCardinality"   :   CDWrapper.CDAssociationWrapperCardinality
   *  "rightCardinality"  :   CDWrapper.CDAssociationWrapperCardinality }
   */
  public static CDAssociationWrapperCardinalityPack intersectCDAssociationWrapperCardinalityHelper(
      CDAssociationWrapper originalAssoc, boolean isReversed,
      CDRefSetAssociationWrapper CDRefSetAssociationWrapper, CDWrapper cdw,
      CDAssociationWrapperCardinality existLeftCardinality,
      CDAssociationWrapperCardinality existRightCardinality) {

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

    AtomicReference<CDAssociationWrapperCardinality> leftResult =
        new AtomicReference<>(existLeftCardinality);
    AtomicReference<CDAssociationWrapperCardinality> rightResult =
        new AtomicReference<>(existRightCardinality);
    Set<String> finalLeftSuperClassSet = leftSuperClassSet;
    Set<String> finalRightSuperClassSet = rightSuperClassSet;

    CDRefSetAssociationWrapper.getLeftRefSet().forEach(leftClass ->
        CDRefSetAssociationWrapper.getRightRefSet().forEach(rightClass -> {
          StringBuilder sb = new StringBuilder();
          sb.append("CDAssociationWrapper_");
          sb.append(leftClass.getOriginalClassName())
              .append("_");
          sb.append(CDRefSetAssociationWrapper.getLeftRoleName())
              .append("_");
          sb.append(formatDirection(CDRefSetAssociationWrapper.getDirection()))
              .append("_");
          sb.append(CDRefSetAssociationWrapper.getRightRoleName())
              .append("_");
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
            reversedSb.append(rightClass.getOriginalClassName())
                .append("_");
            reversedSb.append(CDRefSetAssociationWrapper.getRightRoleName())
                .append("_");
            reversedSb.append(formatDirection(reverseDirection(CDRefSetAssociationWrapper.getDirection())))
                .append("_");
            reversedSb.append(CDRefSetAssociationWrapper.getLeftRoleName())
                .append("_");
            reversedSb.append(leftClass.getOriginalClassName());
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
        }));
    return new CDAssociationWrapperCardinalityPack(leftResult.get(), rightResult.get());
  }

  /**
   * generate the list of CDRefSetAssociationWrapper
   * each original association has one CDRefSetAssociationWrapper object
   */
  public static List<CDRefSetAssociationWrapper> createCDRefSetAssociationWrapper(
      Map<String, CDAssociationWrapper> cDAssociationWrapperGroup,
      MutableGraph<String> inheritanceGraph) {

    List<CDRefSetAssociationWrapper> refSetAssociationList = new ArrayList<>();

    List<CDAssociationWrapper> originalCDAssocWrapperList = cDAssociationWrapperGroup.values()
        .stream()
        .filter(e -> e.getCDWrapperKind() == CDAssociationWrapperKind.CDWRAPPER_ASC)
        .collect(Collectors.toList());

    originalCDAssocWrapperList.forEach(originalAssoc -> {
      Set<CDTypeWrapper> leftRefSet = new HashSet<>();
      leftRefSet.add(originalAssoc.getCDWrapperLeftClass());
      Set<CDTypeWrapper> rightRefSet = new HashSet<>();
      rightRefSet.add(originalAssoc.getCDWrapperRightClass());
      String leftRoleName = originalAssoc.getCDWrapperLeftClassRoleName();
      String rightRoleName = originalAssoc.getCDWrapperRightClassRoleName();
      CDAssociationWrapperDirection direction = originalAssoc.getCDAssociationWrapperDirection();

      List<CDAssociationWrapperPack> matchedAssocList =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithRoleNameAndDirection(
              cDAssociationWrapperGroup, originalAssoc);
      Set<String> leftInheritedClassSet = getInheritedClassSet(inheritanceGraph,
          originalAssoc.getCDWrapperLeftClass().getName());
      Set<String> rightInheritedClassSet = getInheritedClassSet(inheritanceGraph,
          originalAssoc.getCDWrapperRightClass().getName());
      matchedAssocList.forEach(e -> {
        CDAssociationWrapper inheritedAssoc = e.getCDAssociationWrapper();
        if (!e.isReverse()) {
          if (leftInheritedClassSet.contains(inheritedAssoc.getCDWrapperLeftClass().getName())
              && rightInheritedClassSet.contains(inheritedAssoc.getCDWrapperRightClass().getName())) {
            leftRefSet.add(inheritedAssoc.getCDWrapperLeftClass());
            rightRefSet.add(inheritedAssoc.getCDWrapperRightClass());
          }
        }
        else {
          if (leftInheritedClassSet.contains(inheritedAssoc.getCDWrapperRightClass().getName())
              && rightInheritedClassSet.contains(inheritedAssoc.getCDWrapperLeftClass().getName())) {
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

  public static Set<CDTypeWrapper> getTargetClass(CDAssociationWrapper cdAssociationWrapper) {
    Set<CDTypeWrapper> result = new HashSet<>();
    switch (cdAssociationWrapper.getCDAssociationWrapperDirection()) {
      case LEFT_TO_RIGHT:
        result.add(cdAssociationWrapper.getCDWrapperRightClass());
        break;
      case RIGHT_TO_LEFT:
        result.add(cdAssociationWrapper.getCDWrapperLeftClass());
        break;
      case BIDIRECTIONAL:
      default:
        result.add(cdAssociationWrapper.getCDWrapperLeftClass());
        result.add(cdAssociationWrapper.getCDWrapperRightClass());
        break;
    }
    return result;
  }

  /**
   * update CD status for CDAssociationWrapper if it has conflict
   */
  public static void updateCDStatus4CDAssociationWrapper(
      List<CDAssociationWrapperPack> cdAssociationWrapperPacks) {
    cdAssociationWrapperPacks.forEach(e -> e.getCDAssociationWrapper().setStatus(CDStatus.CONFLICTING));
  }
}
