package de.monticore.ow2cw;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;

import java.util.Collection;

public class CDAssociationMatcher {

  /**
   * check if assoc1 and assoc2 are the same association i.e. references AND role names match
   */
  public static boolean sameAssociation(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    return strictMatch(assoc1, assoc2) || reverseMatch(assoc1, assoc2);
  }

  /**
   * update directions of underspecified associations in sources to match those in targets
   */
  public static void updateDir2Match(Collection<ASTCDAssociation> sources,
      Collection<ASTCDAssociation> targets) {
    for (ASTCDAssociation src : sources) {
      for (ASTCDAssociation target : targets) {
        if (strictMatch(src, target)) {
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            target.setCDAssocDir(src.getCDAssocDir().deepClone());
          }
          break;
        }
        if (reverseMatch(src, target)) {
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isBidirectional()) {
              target.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
              break;
            }
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
              break;
            }
            if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
              target.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
            }
          }
          break;
        }
      }
    }
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
      roleName2 = assoc2.getLeftQualifiedName().getQName().toLowerCase();;
    }

    if (!roleName1.equals(roleName2)) {
      return false;
    }

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName().toLowerCase();;
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName().toLowerCase();;
    }

    return roleName1.equals(roleName2);
  }

  public static boolean reverseMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
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

    String roleName1;
    String roleName2;

    // check left role names
    if (assoc1.getLeft().isPresentCDRole()) {
      roleName1 = assoc1.getLeft().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getLeftQualifiedName().getQName().toLowerCase();;
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName().toLowerCase();;
    }

    if (!roleName1.equals(roleName2)) {
      return false;
    }

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName().toLowerCase();;
    }

    if (assoc2.getLeft().isPresentCDRole()) {
      roleName2 = assoc2.getLeft().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getLeftQualifiedName().getQName().toLowerCase();;
    }

    return roleName1.equals(roleName2);
  }

}
