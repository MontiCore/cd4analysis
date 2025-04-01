package de.monticore.cdconcretization.association;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cddiff.CDDiffUtil;
import java.util.HashSet;
import java.util.Set;

public class DefaultAssocCompleter implements IAssociationCompleter {

  private final ASTCDCompilationUnit ccd;

  private final IAssocSideCompleter assocSideCompleter;

  public DefaultAssocCompleter(ASTCDCompilationUnit ccd, IAssocSideCompleter assocSideCompleter) {
    this.ccd = ccd;
    this.assocSideCompleter = assocSideCompleter;
  }

  @Override
  public void completeAssociation(
      ASTCDAssociation cAssoc, ASTCDAssociation rAssoc, AssocMatchDirection matchDirection)
      throws CompletionException {
    completeAssociationName(cAssoc, rAssoc);

    switch (matchDirection) {
      case SAME_DIRECTION:
        completeAssocNavigability(cAssoc, rAssoc);
        assocSideCompleter.completeAssocSide(cAssoc.getLeft(), rAssoc.getLeft());
        assocSideCompleter.completeAssocSide(cAssoc.getRight(), rAssoc.getRight());
        break;
      case REVERSE_DIRECTION:
        // If the match is in reverse, complete the association properties for alternating sides.
        completeAssocNavigabilityReverse(cAssoc, rAssoc);
        assocSideCompleter.completeAssocSide(cAssoc.getLeft(), rAssoc.getRight());
        assocSideCompleter.completeAssocSide(cAssoc.getRight(), rAssoc.getLeft());
        break;
    }

    // Handle potential role name conflicts in a post-processing step
    renameRoleIfConflicting(cAssoc);
  }

  private void renameRoleIfConflicting(ASTCDAssociation assoc) throws CompletionException {
    /* Wenn es eine andere Assoziation mit gleichem Rollennamen gibt
           und der Typ auf der gegenüberliegenden Seite gleich / Subtyp / Supertyp ist,
           dann ändere den entsprechenden Rollennamen für assoc!
    */
    boolean renamed = false;

    // Check and rename conflicts on the right side
    if (assoc.getRight().isPresentCDRole()) {
      renamed =
          checkAndRenameConflict(
              assoc,
              assoc.getRight().getCDRole().getName(),
              assoc.getLeftQualifiedName().getQName(),
              assoc.getRightQualifiedName().getQName(),
              true);
    }

    // Check and rename conflicts on the left side
    if (assoc.getLeft().isPresentCDRole()) {
      renamed =
          checkAndRenameConflict(
              assoc,
              assoc.getLeft().getCDRole().getName(),
              assoc.getRightQualifiedName().getQName(),
              assoc.getLeftQualifiedName().getQName(),
              false);
    }
  }

  private boolean checkAndRenameConflict(
      ASTCDAssociation assoc,
      String roleName,
      String oppositeQName,
      String currentQName,
      boolean isRightSide)
      throws CompletionException {

    boolean renamed = false;

    ASTCDType oppositeType = ConcretizationHelper.getAssocTypeByQName(ccd, oppositeQName);

    // Create a set to store the full hierarchy (all supertypes and subtypes) of the opposite type.
    Set<ASTCDType> typeFullHierarchy = new HashSet<>();
    typeFullHierarchy.add(oppositeType);
    typeFullHierarchy.addAll(CDDiffUtil.getAllSuperTypes(oppositeType, ccd.getCDDefinition()));
    typeFullHierarchy.addAll(CDDiffUtil.getAllStrictSubTypes(oppositeType, ccd.getCDDefinition()));

    // Iterate over all associations in the current class diagram to detect potential conflicts.
    for (ASTCDAssociation otherAssoc : ccd.getCDDefinition().getCDAssociationsList()) {
      if (otherAssoc == assoc) continue; // Skip the current association itself.

      // Resolve the type on the opposite side of the other association being compared.

      boolean rename = false;

      // Check right role and left type of the other association
      if (otherAssoc.getRight().isPresentCDRole()) {
        ASTCDType leftType = ConcretizationHelper.getAssocLeftType(ccd, otherAssoc);
        String rightRole = otherAssoc.getRight().getCDRole().getName();
        rename = roleName.equals(rightRole) && typeFullHierarchy.contains(leftType);
      }

      // Check left role and right type of the other association
      if (otherAssoc.getLeft().isPresentCDRole()) {
        ASTCDType rightType = ConcretizationHelper.getAssocRightType(ccd, otherAssoc);
        String leftRole = otherAssoc.getLeft().getCDRole().getName();
        rename = roleName.equals(leftRole) && typeFullHierarchy.contains(rightType);
      }

      // Check if the role name matches and if the types are either the same or within the same type
      // hierarchy.
      if (rename) {

        // Create a new role name by appending the name of the current type to the original role
        // name.
        String newRoleName = roleName + "_" + currentQName;

        // Set the new role name on the correct side (left or right) of the current association.
        if (isRightSide) {
          assoc.getRight().getCDRole().setName(newRoleName);
        } else {
          assoc.getLeft().getCDRole().setName(newRoleName);
        }

        renamed = true;
      }
    }

    return renamed;
  }

  private void completeAssocNavigability(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // if cAssoc has complementary navigation it becomes bidirectional, else copy navigation of
    // rAssoc
    if ((cAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableLeft())
        || (cAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableRight())
        || rAssoc.getCDAssocDir().isBidirectional()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDBiDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDLeftToRightDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDRightToLeftDirBuilder().build());
    }
    // else
    // unspecified or overspecifiedf by cAssoc, so do nothing
  }

  private void completeAssocNavigabilityReverse(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // if cAssoc has complementary navigation it becomes bidirectional, else copy navigation of
    // rAssoc
    if ((cAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableRight())
        || (cAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableLeft())
        || rAssoc.getCDAssocDir().isBidirectional()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDBiDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDRightToLeftDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDLeftToRightDirBuilder().build());
    }
    // else
    // unspecified or overspecifiedf by cAssoc, so do nothing
  }

  private void completeAssociationName(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // Check and complete association name
    if (!cAssoc.isPresentName() && rAssoc.isPresentName()) {
      if (ccd.getCDDefinition().getCDAssociationsList().stream()
          .noneMatch(
              assoc -> (assoc.isPresentName() && assoc.getName().equals(rAssoc.getName())))) {
        cAssoc.setName(rAssoc.getName());
      }
    }
  }
}
