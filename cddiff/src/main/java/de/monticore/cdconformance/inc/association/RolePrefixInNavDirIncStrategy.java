package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchCDAssocsBySrcNameAndTgtRole;
import de.monticore.cdmatcher.MatchingStrategy;

public class RolePrefixInNavDirIncStrategy extends MatchCDAssocsBySrcNameAndTgtRole {

  public RolePrefixInNavDirIncStrategy(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide srcElem, ASTCDAssocSide tgtElem) {
    if (srcElem.isPresentCDRole() && tgtElem.isPresentCDRole()) {
      return srcElem.getCDRole().getName().startsWith(tgtElem.getCDRole().getName());
    }
    return false;
  }
}
