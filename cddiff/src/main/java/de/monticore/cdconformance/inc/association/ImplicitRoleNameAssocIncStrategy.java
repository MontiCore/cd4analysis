/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchCDAssocsBySrcTypeAndTgtRole;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * This strategy matches associations by source type and target role, but takes the implicit role
 * names derived from the target type into account.<br>
 * This means, if one of the role names is not present (concrete/reference) the other role name
 * has to be exactly the implicit role name.
 * If both role names are present, they have to be exactly the implicit role names.
 *
 * For example, lets assume the target type of a reference association is name is "Person",
 * and the role is exactly the implicit role name "person". Further, we assume there is a concrete
 * type "Employee" that is an incarnation of "Person". Then, a concrete association with
 * source/target type "Employee" and role "employee" matches the reference association.
 */
public class ImplicitRoleNameAssocIncStrategy extends MatchCDAssocsBySrcTypeAndTgtRole {
  
  protected String mapping;
  
  public ImplicitRoleNameAssocIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, String mapping) {
    super(typeMatcher, srcCD, tgtCD);
    this.mapping = mapping;
  }
  
  @Override
  protected boolean checkRole(ASTCDAssocSide concrete, ASTCDAssocSide reference) {
    Optional<ASTCDType> conType = resolveConcreteCDTyp(concrete.getMCQualifiedType()
        .getMCQualifiedName().getQName());
    Optional<ASTCDType> refType = resolveReferenceCDTyp(reference.getMCQualifiedType()
        .getMCQualifiedName().getQName());
    
    if (conType.isPresent() && refType.isPresent() && typeMatcher.isMatched(conType.get(), refType
        .get())) {
      String implicitRefRoleName = StringUtils.uncapitalize(reference.getName());
      String implicitConRoleName = StringUtils.uncapitalize(concrete.getName());
      
      if (concrete.isPresentCDRole()) {
        String conRoleName = concrete.getCDRole().getName();
        if (reference.isPresentCDRole()) {
          String refRoleName = reference.getCDRole().getName();
          // If both, the reference and concrete roles are present, both names have to be exactly
          // the implicit role names derived from the target type.
          // (If con type name == ref type name, this implies con role name == ref role name)
          return refRoleName.equals(implicitRefRoleName) && conRoleName.equals(implicitConRoleName);
        }
        else {
          // If no reference role is present, the concrete role must be exactly the implicit role
          // name, if it is present.
          return conRoleName.equals(implicitConRoleName);
        }
      }
      else if (reference.isPresentCDRole()) {
        String refRoleName = reference.getCDRole().getName();
        /*
         * If no concrete role is present, it is still a match if the reference role is exactly
         * the implicit role name.
         * However, the conformance checker will warn the user to provide a concrete role name if
         * there is an explicit reference role name.
         * See BasicAssocConfStrategy.warnExplicitRoleNameMissing.
         * NOTE: We should not warn here, because this strategy is not only used for conformance
         * checking but also during concretization, where we add the missing role name anyway.
          */
        return refRoleName.equals(implicitRefRoleName);
      }
    }
    return false;
  }
  
}
