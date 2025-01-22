package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchCDAssocsBySrcNameAndTgtRole;
import de.monticore.cdmatcher.MatchingStrategy;

public class RolePrefixIfPresentIncStrategy extends MatchCDAssocsBySrcNameAndTgtRole {

  public RolePrefixIfPresentIncStrategy(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }

  @Override
  protected boolean check(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    // associations are <- and -> or -> and <-
    boolean inverseNavigation =
        tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
                == !srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            && tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
                == !srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            && !(srcElem.getCDAssocDir().isBidirectional()
                || tgtElem.getCDAssocDir().isBidirectional());

    return checkReference(
            srcElem.getLeftQualifiedName().getQName(), tgtElem.getLeftQualifiedName().getQName())
        && checkRole(srcElem.getRight(), tgtElem.getRight())
        && checkReference(
            srcElem.getRightQualifiedName().getQName(), tgtElem.getRightQualifiedName().getQName())
        && checkRole(srcElem.getLeft(), tgtElem.getLeft())
        && !inverseNavigation;
  }

  @Override
  protected boolean checkReverse(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {

    boolean inverseNavigation =
        tgtElem.getCDAssocDir().isDefinitiveNavigableRight()
                == !srcElem.getCDAssocDir().isDefinitiveNavigableLeft()
            && tgtElem.getCDAssocDir().isDefinitiveNavigableLeft()
                == !srcElem.getCDAssocDir().isDefinitiveNavigableRight()
            && !(srcElem.getCDAssocDir().isBidirectional()
                || tgtElem.getCDAssocDir().isBidirectional());

    return checkReference(
            srcElem.getLeftQualifiedName().getQName(), tgtElem.getRightQualifiedName().getQName())
        && checkRole(srcElem.getRight(), tgtElem.getLeft())
        && checkReference(
            srcElem.getRightQualifiedName().getQName(), tgtElem.getLeftQualifiedName().getQName())
        && checkRole(srcElem.getLeft(), tgtElem.getRight())
        && !inverseNavigation;
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    if (srcElem.isPresentCDRole() && tgtElem.isPresentCDRole()) {
      return srcElem.getCDRole().getName().startsWith(tgtElem.getCDRole().getName());
    }
    return !tgtElem.isPresentCDRole();
  }
}
