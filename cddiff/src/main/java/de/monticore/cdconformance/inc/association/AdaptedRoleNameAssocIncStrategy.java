/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchCDAssocsBySrcTypeAndTgtRole;

import java.util.Optional;

/**
 * Matches associations by source type and target role, accepting role names that are derived via
 * implicit name adaptation from the reference role name.<br>
 * <br>
 * For example, if the reference role is {@code assignedTasks} and the concrete type {@code Ticket}
 * incarnates the reference type {@code Task}, then the concrete role {@code assignedTickets} is
 * accepted as an incarnation of {@code assignedTasks}, because
 * {@link NameUtil#adaptTemplatedName(String, String, String) adaptTemplatedName("assignedTasks",
 * "Task", "Ticket")} produces {@code assignedTickets}.
 */
public class AdaptedRoleNameAssocIncStrategy extends MatchCDAssocsBySrcTypeAndTgtRole {
  
  public AdaptedRoleNameAssocIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }
  
  @Override
  protected boolean checkRole(ASTCDAssocSide concrete, ASTCDAssocSide reference) {
    Optional<ASTCDType> conType = resolveConcreteCDTyp(concrete.getMCQualifiedType()
        .getMCQualifiedName().getQName());
    Optional<ASTCDType> refType = resolveReferenceCDTyp(reference.getMCQualifiedType()
        .getMCQualifiedName().getQName());
    
    if (conType.isPresent() && refType.isPresent() && typeMatcher.isMatched(conType.get(), refType
        .get())) {
      if (reference.isPresentCDRole() && concrete.isPresentCDRole()) {
        String refRoleName = reference.getCDRole().getName();
        String conRoleName = concrete.getCDRole().getName();
        return NameUtil.adaptTemplatedName(refRoleName, refType.get().getName(), conType.get()
            .getName()).map(adapted -> adapted.equals(conRoleName)).orElse(false);
      }
    }
    return false;
  }
  
}
