/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that role names do not conflict with other role names where the source
 * types has other outgoing associations (which might be inherited).
 *
 * @author Robert Heim
 */
public class AssociationRoleNameNoConflictWithOtherRoleNames implements
    CD4AnalysisASTCDAssociationCoCo {

  public static final String AUTOMATICALLY_INTRODUCED = " automatically introduced";

  @Override
  public void check(ASTCDAssociation a) {
    if (!a.isPresentName()) {
      Optional<CDTypeSymbol> leftType = a.getEnclosingScope()
              .resolveCDType(a.getLeftReferenceName().toString());
      Optional<CDTypeSymbol> rightType = a.getEnclosingScope()
              .resolveCDType(a.getRightReferenceName().toString());
      boolean err = false;
      // source type might be external (in this case we do nothing)
      if (leftType.isPresent() && (a.isLeftToRight() || a.isBidirectional() || a.isUnspecified())) {
        err = check( a.isPresentRightRole(), a);
      }
      if (rightType.isPresent() && !err
              && (a.isRightToLeft() || a.isBidirectional() || a.isUnspecified())) {
          check(a.isPresentLeftRole(), a);
      }
    }
  }

  /**
   * Does the actual check.
   *
   * @param roleNameDefined defines whether the role name was automatically introduced or not
   * @param assoc      association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(boolean roleNameDefined, ASTCDAssociation assoc) {
    CDAssociationSymbol assocSym = assoc.getSymbol();

    String targetType = assocSym.getTargetType().getName();
    String automaticallyIntroduced = roleNameDefined
            ? ""
            : AUTOMATICALLY_INTRODUCED;

    // association sourceType (sourceRoleName) -> (targetRoleName) targetType;
    // what needs to be unique is: targetType.sourceRoleName to navigate to sourceType from targetType and
    //                             sourceType.targetRoleName to navigate to targetType from sourceType
    String roleName = assocSym.getDerivedName(); // is always target role name

    List<CDAssociationSymbol> conflictingAssoc2 = assocSym.getSourceType().getLoadedSymbol().getAllAssociations().stream()
            .filter(a -> a != assocSym && (a.isPresentTargetRole() || !a.isPresentAssocName()))
            .filter(a -> a.getDerivedName().equals(roleName))
            .collect(Collectors.toList());

    for (CDAssociationSymbol conflicting : conflictingAssoc2) {
      String conflictingRoleNameAuto = conflicting.isPresentTargetRole()
              ? ""
              : AUTOMATICALLY_INTRODUCED;
      String conflictingRoleName = conflicting.getDerivedName();

      boolean isReadOnly = false;
      List<String> superTypes = null;
      Optional<CDTypeSymbol> targetTypeSymbol = assoc.getEnclosingScope().resolveCDType(targetType);
      if (targetTypeSymbol.isPresent()) {
        isReadOnly = conflicting.isReadOnly();
        superTypes = targetTypeSymbol.get().getSuperTypesTransitive().stream().map(type -> type.getFullName()).collect(Collectors.toList());
      }
      if (isReadOnly && superTypes.contains(conflicting.getTargetType().getLoadedSymbol().getFullName())) {
        Log.info(String.format("Association `%s` overwrites read-only association `%s`",
                assoc, conflicting.isPresentAstNode() ? conflicting.getAstNode() : conflicting),
                this.getClass().getSimpleName());
      } else if (!checkTargetTypesAreSameAndSourceTypesAreSuperTypes(assocSym, assoc, conflicting)) {
        Log.error(
                String
                        .format(
                                "0xC4A28 The%s role name %s of class %s for association %s conflicts with the%s role name %s for association %s.",
                                automaticallyIntroduced,
                                roleName,
                                targetType,
                                CD4ACoCoHelper.printAssociation(assoc),
                                conflictingRoleNameAuto,
                                conflictingRoleName,
                                CD4ACoCoHelper.printAssociation((ASTCDAssociation) conflicting
                                        .getAstNode())
                        ),
                assoc.get_SourcePositionStart());
        return true;
      }
    }
    return false;
  }

  // true for handled case
  private boolean checkTargetTypesAreSameAndSourceTypesAreSuperTypes(CDAssociationSymbol assocSym, ASTCDAssociation assoc, CDAssociationSymbol conflicting) {
    if (assocSym.getTargetType().getLoadedSymbol().getFullName().equals(conflicting.getTargetType().getLoadedSymbol().getFullName())) {
      if (assocSym.getSourceType().getLoadedSymbol().hasSuperTypeByFullName(conflicting.getSourceType().getLoadedSymbol().getFullName())) {
        if (assocSym.isDerived()) {
          Log.info(String.format("Derived association `%s` is inherited from association `%s` (source type `%s` extends/implements source type `%s`)",
                  assoc, conflicting.isPresentAstNode()? conflicting.getAstNode() : conflicting,
                  assocSym.getSourceType().getName(), conflicting.getSourceType().getName()),
                  this.getClass().getSimpleName());
          return true;
        } else {
          Log.error(String.format("0xC4A33 Association `%s` has same target role name and source type extends source type of association `%s`. So the \"inherited\" association `%s` should be a derived association.",
                  assoc, conflicting.isPresentAstNode() ? conflicting.getAstNode() : conflicting, assoc),
                  assoc.get_SourcePositionStart());
          return true;
        }
      } else if (conflicting.getSourceType().getLoadedSymbol().hasSuperTypeByFullName(assocSym.getSourceType().getLoadedSymbol().getFullName())) {
        if (conflicting.isDerived()) {
          Log.info(String.format("Derived association `%s` is inherited from association `%s` (source type `%s` extends/implements source type `%s`)",
                  conflicting.isPresentAstNode() ? conflicting.getAstNode() : conflicting, assoc,
                  conflicting.getSourceType().getName(), assocSym.getSourceType().getName()),
                  this.getClass().getSimpleName());
          return true;
        } else {
          Log.error(String.format("0xC4A33 Association `%s` has same target role name and source type extends source type of association `%s`. So the \"inherited\" association `%s` should be a derived association.",
                  conflicting.isPresentAstNode() ? conflicting.getAstNode() : conflicting, assoc, conflicting.isPresentAstNode()
                  ? conflicting.getAstNode(): conflicting),
                  assoc.get_SourcePositionStart());
          return true;
        }
      }
    }
    return false;
  }
}
