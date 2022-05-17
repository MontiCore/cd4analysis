package de.monticore.ow2cw;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CDAssociationHelper {

  public static Collection<ASTCDAssociation> collectOverridingAssociations(
      ASTCDCompilationUnit srcAST, ASTCDCompilationUnit targetAST) {

    CD4CodeMill.scopesGenitorDelegator().createFromAST(srcAST);
    ICD4CodeArtifactScope targetScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(srcAST);

    List<ASTCDAssociation> overrides = new ArrayList<>();
    for (ASTCDAssociation srcAssoc : srcAST.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation targetAssoc : targetAST.getCDDefinition().getCDAssociationsList()) {
        if (overridesAssociation(srcAssoc, targetAssoc, targetScope)
            || overridesAssociationInReverse(srcAssoc, targetAssoc, targetScope)) {
          overrides.add(srcAssoc);
        }
      }
    }

    return overrides;
  }

  public static boolean overridesAssociation(ASTCDAssociation srcAssoc,
      ASTCDAssociation targetAssoc, ICD4CodeArtifactScope scope) {

    String srcName = srcAssoc.getLeftQualifiedName().getQName();
    String targetName = targetAssoc.getLeftQualifiedName().getQName();

    if (!(CDInheritanceHelper.isSuperOf(srcName, targetName, scope)
        || CDInheritanceHelper.isSuperOf(targetName, srcName, scope))) {
      return false;
    }

    srcName = srcAssoc.getRightQualifiedName().getQName();
    targetName = targetAssoc.getRightQualifiedName().getQName();

    if (!(CDInheritanceHelper.isSuperOf(srcName, targetName, scope)
        || CDInheritanceHelper.isSuperOf(targetName, srcName, scope))) {
      return false;
    }

    return matchLeftRoleNames(srcAssoc, targetAssoc) && matchRightRoleNames(srcAssoc, targetAssoc);

  }

  public static boolean overridesAssociationInReverse(ASTCDAssociation srcAssoc,
      ASTCDAssociation targetAssoc, ICD4CodeArtifactScope scope) {

    String srcName = srcAssoc.getLeftQualifiedName().getQName();
    String targetName = targetAssoc.getRightQualifiedName().getQName();

    if (!(CDInheritanceHelper.isSuperOf(srcName, targetName, scope)
        || CDInheritanceHelper.isSuperOf(targetName, srcName, scope))) {
      return false;
    }

    srcName = srcAssoc.getRightQualifiedName().getQName();
    targetName = targetAssoc.getLeftQualifiedName().getQName();

    if (!(CDInheritanceHelper.isSuperOf(srcName, targetName, scope)
        || CDInheritanceHelper.isSuperOf(targetName, srcName, scope))) {
      return false;
    }

    return matchLeft2RightRoleNames(srcAssoc, targetAssoc) && matchRight2LeftRoleNames(srcAssoc,
        targetAssoc);

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
   * World allows specification: unspecified -> uni-directional -> bi-directional
   */
  public static void updateDir2Match(Collection<ASTCDAssociation> sources,
      Collection<ASTCDAssociation> targets) {
    for (ASTCDAssociation src : sources) {
      for (ASTCDAssociation target : targets) {
        if (strictMatch(src, target)) {
          if ((!target.getCDAssocDir().isBidirectional()) && src.getCDAssocDir()
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
          if ((!target.getCDAssocDir().isBidirectional()) && src.getCDAssocDir()
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
            else {
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
            else {
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
        return matchRightRoleNames(assoc1, assoc2);
      }
    }

    if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft() && assoc2.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      if (assoc1.getRightQualifiedName()
          .getQName()
          .equals(assoc2.getRightQualifiedName().getQName())) {
        return matchLeftRoleNames(assoc1, assoc2);
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
        return matchRight2LeftRoleNames(assoc1, assoc2);
      }
    }

    if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft() && assoc2.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      if (assoc1.getRightQualifiedName()
          .getQName()
          .equals(assoc2.getLeftQualifiedName().getQName())) {
        return matchLeft2RightRoleNames(assoc1, assoc2);
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

    return matchLeftRoleNames(assoc1, assoc2) && matchRightRoleNames(assoc1, assoc2);
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

    return matchLeft2RightRoleNames(assoc1, assoc2) && matchRight2LeftRoleNames(assoc1, assoc2);
  }

  public static boolean matchLeftRoleNames(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    String roleName1;
    String roleName2;

    // check left role names
    if (assoc1.getLeft().isPresentCDRole()) {
      roleName1 = assoc1.getLeft().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getLeftQualifiedName().getQName().toLowerCase();
    }

    if (assoc2.getLeft().isPresentCDRole()) {
      roleName2 = assoc2.getLeft().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getLeftQualifiedName().getQName().toLowerCase();
    }

    return roleName1.equals(roleName2);
  }

  public static boolean matchRightRoleNames(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    String roleName1;
    String roleName2;

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName().toLowerCase();
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName().toLowerCase();
    }

    return roleName1.equals(roleName2);
  }

  private static boolean matchLeft2RightRoleNames(ASTCDAssociation assoc1,
      ASTCDAssociation assoc2) {
    String roleName1;
    String roleName2;

    // check left role names
    if (assoc1.getLeft().isPresentCDRole()) {
      roleName1 = assoc1.getLeft().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getLeftQualifiedName().getQName().toLowerCase();
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName().toLowerCase();
    }

    return roleName1.equals(roleName2);
  }

  private static boolean matchRight2LeftRoleNames(ASTCDAssociation assoc1,
      ASTCDAssociation assoc2) {
    String roleName1;
    String roleName2;

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName().toLowerCase();
    }

    if (assoc2.getLeft().isPresentCDRole()) {
      roleName2 = assoc2.getLeft().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getLeftQualifiedName().getQName().toLowerCase();
    }

    return roleName1.equals(roleName2);
  }

}
