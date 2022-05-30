package de.monticore.ow2cw;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CDAssociationHelper {

  /**
   * Collect all associations in srcAST that are super-associations of an associations in targetAST
   */
  public static Collection<ASTCDAssociation> collectSuperAssociations(ASTCDCompilationUnit srcAST,
      ASTCDCompilationUnit targetAST) {

    CD4CodeMill.scopesGenitorDelegator().createFromAST(srcAST);
    ICD4CodeArtifactScope targetScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(srcAST);

    List<ASTCDAssociation> superAssociations = new ArrayList<>();
    for (ASTCDAssociation srcAssoc : srcAST.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation targetAssoc : targetAST.getCDDefinition().getCDAssociationsList()) {
        if (isSuperAssociation(srcAssoc, targetAssoc, targetScope) || isSuperAssociationInReverse(
            srcAssoc, targetAssoc, targetScope)) {
          superAssociations.add(srcAssoc);
        }
      }
    }

    return superAssociations;
  }

  /**
   * Collect all associations in srcAST that are in conflict with associations in targetAST
   */
  public static Collection<ASTCDAssociation> collectConflictingAssociations(
      ASTCDCompilationUnit srcAST, ASTCDCompilationUnit targetAST) {

    CD4CodeMill.scopesGenitorDelegator().createFromAST(srcAST);
    ICD4CodeArtifactScope targetScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(srcAST);

    List<ASTCDAssociation> conflicts = new ArrayList<>();
    for (ASTCDAssociation srcAssoc : srcAST.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation targetAssoc : targetAST.getCDDefinition().getCDAssociationsList()) {
        if (inConflict(srcAssoc, targetAssoc, targetScope)) {
          conflicts.add(srcAssoc);
        }
      }
    }

    return conflicts;
  }

  /**
   * An association srcAssoc is in conflict with another association targetAssoc if their
   * source-classes and target-role-names match in navigable direction, unless srcAssoc is a
   * super-association of targetAssoc
   */
  public static boolean inConflict(ASTCDAssociation srcAssoc, ASTCDAssociation targetAssoc,
      ICD4CodeArtifactScope scope) {
    if (isSuperAssociation(srcAssoc, targetAssoc, scope) || isSuperAssociationInReverse(srcAssoc,
        targetAssoc, scope)) {
      return false;
    }

    if (weakMatch(srcAssoc, targetAssoc) || weakReverseMatch(srcAssoc, targetAssoc)) {
      return true;
    }

    String srcLeft = srcAssoc.getLeftQualifiedName().getQName();
    String targetLeft = targetAssoc.getLeftQualifiedName().getQName();
    String srcRight = srcAssoc.getRightQualifiedName().getQName();
    String targetRight = targetAssoc.getRightQualifiedName().getQName();

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableRight() && CDInheritanceHelper.isSuperOf(srcLeft, targetLeft, scope)
        && CDInheritanceHelper.isSuperOf(targetLeft, srcLeft, scope)) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableLeft() && CDInheritanceHelper.isSuperOf(srcRight, targetRight, scope)
        && CDInheritanceHelper.isSuperOf(targetRight, srcRight, scope)) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableLeft() && CDInheritanceHelper.isSuperOf(srcLeft, targetRight, scope)
        && CDInheritanceHelper.isSuperOf(targetRight, srcLeft, scope)) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableRight() && CDInheritanceHelper.isSuperOf(srcRight, targetLeft, scope)
        && CDInheritanceHelper.isSuperOf(targetLeft, srcRight, scope)) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }

    return false;
  }

  /**
   * An association srcAssoc is a super-association of another association targetAssoc iff
   * srcClass.targetRoleName = srcClass.targetRoleName, srcAssoc.srcClass != targetAssoc.srcClass,
   * srcAssoc.srcClass is superclass of targetAssoc.srcClass, and srcAssoc.targetClass is superclass
   * of targetAssoc.targetClass in navigable direction.
   */
  public static boolean isSuperAssociation(ASTCDAssociation srcAssoc, ASTCDAssociation targetAssoc,
      ICD4CodeArtifactScope scope) {

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      return false;
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      return false;
    }

    if (similarAssociation(srcAssoc, targetAssoc)) {
      return false;
    }

    String srcLeft = srcAssoc.getLeftQualifiedName().getQName();
    String targetLeft = targetAssoc.getLeftQualifiedName().getQName();
    String srcRight = srcAssoc.getRightQualifiedName().getQName();
    String targetRight = targetAssoc.getRightQualifiedName().getQName();

    if (!CDInheritanceHelper.isSuperOf(srcLeft, targetLeft, scope)) {
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(srcRight, targetRight, scope)) {
      return false;
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft()) && matchRoleNames(
        srcAssoc.getRight(), targetAssoc.getRight());

  }

  public static boolean isSuperAssociationInReverse(ASTCDAssociation srcAssoc,
      ASTCDAssociation targetAssoc, ICD4CodeArtifactScope scope) {

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && (!targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableRight())) {
      return false;
    }

    if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      return false;
    }

    if (similarAssociation(srcAssoc, targetAssoc)) {
      return false;
    }

    String srcLeft = srcAssoc.getLeftQualifiedName().getQName();
    String targetLeft = targetAssoc.getLeftQualifiedName().getQName();
    String srcRight = srcAssoc.getRightQualifiedName().getQName();
    String targetRight = targetAssoc.getRightQualifiedName().getQName();

    if (!CDInheritanceHelper.isSuperOf(srcLeft, targetRight, scope)) {
      return false;
    }

    if (!CDInheritanceHelper.isSuperOf(srcRight, targetLeft, scope)) {
      return false;
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }

    if (!srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !targetAssoc.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }

    return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight()) && matchRoleNames(
        srcAssoc.getRight(), targetAssoc.getLeft());

  }

  /**
   * check if assoc1 and assoc2 are similar associations, i.e. reference and role-name match in
   * navigable direction
   */
  public static boolean similarAssociation(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    return weakMatch(assoc1, assoc2) || weakReverseMatch(assoc1, assoc2);
  }

  /**
   * check if assoc1 and assoc2 are the same association, i.e. references AND role names match
   */
  public static boolean sameAssociation(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    return strictMatch(assoc1, assoc2) || strictReverseMatch(assoc1, assoc2);
  }

  /**
   * update directions of underspecified associations in targets to match those in sources Open
   * World allows specification: unspecified -> uni-directional -> bi-directional Closed World only
   * allows: unspecified -> uni-directional / bi-directional
   */
  public static void updateDir2Match(Collection<ASTCDAssociation> sources,
      Collection<ASTCDAssociation> targets, boolean isOpenWorld) {
    for (ASTCDAssociation src : sources) {
      for (ASTCDAssociation target : targets) {
        if (strictMatch(src, target)) {
          if (isOpenWorld && (!target.getCDAssocDir().isBidirectional()) && src.getCDAssocDir()
              .isBidirectional()) {
            target.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
            break;
          }
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            target.setCDAssocDir(src.getCDAssocDir().deepClone());
          }
          break;
        }
        if (strictReverseMatch(src, target)) {
          if (isOpenWorld && (!target.getCDAssocDir().isBidirectional()) && src.getCDAssocDir()
              .isBidirectional()) {
            target.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
            break;
          }
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
            }
            else {
              if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
                target.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
              }
            }
          }
          break;
        }
      }
    }
  }

  /**
   * update directions of underspecified associations in targets to differ to those in sources Open
   * World allows specification: unspecified -> uni-directional -> bi-directional
   */
  public static void updateDir4Diff(Collection<ASTCDAssociation> sources,
      Collection<ASTCDAssociation> targets) {
    for (ASTCDAssociation src : sources) {
      for (ASTCDAssociation target : targets) {
        if (strictMatch(src, target)) {
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
            }
            else if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
            }
          }
          break;
        }
        if (strictReverseMatch(src, target)) {
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
            }
            else if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
              target.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
            }
          }
          break;
        }
      }
    }
  }

  private static boolean weakMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    if (assoc1.getCDAssocDir().isDefinitiveNavigableRight() && assoc2.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      if (assoc1.getLeftQualifiedName()
          .getQName()
          .equals(assoc2.getLeftQualifiedName().getQName())) {
        return matchRoleNames(assoc1.getRight(), assoc2.getRight());
      }
    }

    if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft() && assoc2.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      if (assoc1.getRightQualifiedName()
          .getQName()
          .equals(assoc2.getRightQualifiedName().getQName())) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getLeft());
      }
    }

    return strictMatch(assoc1, assoc2);
  }

  private static boolean weakReverseMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    if (assoc1.getCDAssocDir().isDefinitiveNavigableRight() && assoc2.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      if (assoc1.getLeftQualifiedName()
          .getQName()
          .equals(assoc2.getRightQualifiedName().getQName())) {
        return matchRoleNames(assoc1.getRight(), assoc2.getLeft());
      }
    }

    if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft() && assoc2.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      if (assoc1.getRightQualifiedName()
          .getQName()
          .equals(assoc2.getLeftQualifiedName().getQName())) {
        return matchRoleNames(assoc1.getLeft(), assoc2.getRight());
      }
    }

    return strictReverseMatch(assoc1, assoc2);
  }

  public static boolean strictMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    // check left reference
    if (!assoc1.getLeftQualifiedName()
        .getQName()
        .equals(assoc2.getLeftQualifiedName().getQName())) {
      return false;
    }

    // check right reference
    if (!assoc1.getRightQualifiedName()
        .getQName()
        .equals(assoc2.getRightQualifiedName().getQName())) {
      return false;
    }

    return matchRoleNames(assoc1.getLeft(), assoc2.getLeft()) && matchRoleNames(assoc1.getRight(),
        assoc2.getRight());
  }

  public static boolean strictReverseMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    // check left reference
    if (!assoc1.getLeftQualifiedName()
        .getQName()
        .equals(assoc2.getRightQualifiedName().getQName())) {
      return false;
    }

    // check right reference
    if (!assoc1.getRightQualifiedName()
        .getQName()
        .equals(assoc2.getLeftQualifiedName().getQName())) {
      return false;
    }

    return matchRoleNames(assoc1.getLeft(), assoc2.getRight()) && matchRoleNames(assoc1.getRight(),
        assoc2.getLeft());
  }

  public static boolean matchRoleNames(ASTCDAssocSide side1, ASTCDAssocSide side2) {
    String roleName1;
    String roleName2;

    // check left role names
    if (side1.isPresentCDRole()) {
      roleName1 = side1.getCDRole().getName();
    }
    else {
      roleName1 = side1.getMCQualifiedType().getMCQualifiedName().getQName().toLowerCase();
    }

    if (side2.isPresentCDRole()) {
      roleName2 = side2.getCDRole().getName();
    }
    else {
      roleName2 = side2.getMCQualifiedType().getMCQualifiedName().getQName().toLowerCase();
    }

    return roleName1.equals(roleName2);
  }

}
