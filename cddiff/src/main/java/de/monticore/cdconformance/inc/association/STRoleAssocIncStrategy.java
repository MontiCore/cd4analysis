/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchCDAssocsBySrcTypeAndTgtRole;

public class STRoleAssocIncStrategy extends MatchCDAssocsBySrcTypeAndTgtRole {

  protected String mapping;

  public STRoleAssocIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher, ASTCDCompilationUnit srcCD,
                                ASTCDCompilationUnit tgtCD, String mapping) {
    super(typeMatcher, srcCD, tgtCD);
    this.mapping = mapping;
  }

  @Override
  protected boolean checkRole(ASTCDAssocSide concrete, ASTCDAssocSide reference) {
    if (concrete.getModifier().isPresentStereotype() && concrete.getModifier().getStereotype()
        .contains(mapping) && reference.isPresentCDRole()) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return reference.getCDRole().getName().equals(refName);
    }
    return false;
  }

}
