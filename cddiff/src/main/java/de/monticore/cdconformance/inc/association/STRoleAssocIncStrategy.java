package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchCDAssocsBySrcNameAndTgtRole;
import de.monticore.cdmatcher.MatchingStrategy;

public class STRoleAssocIncStrategy extends MatchCDAssocsBySrcNameAndTgtRole {

  protected String mapping;

  public STRoleAssocIncStrategy(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      String mapping) {
    super(typeMatcher, srcCD, tgtCD);
    this.mapping = mapping;
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide concrete, ASTCDAssocSide reference) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)
        && reference.isPresentCDRole()) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return reference.getCDRole().getName().equals(refName);
    }
    return false;
  }
}
