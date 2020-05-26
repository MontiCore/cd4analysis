/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that role names do not conflict with attributes in source types nor
 * with inherited attributes derived from inherited association names (inherited
 * associations whose role names conflict are checked in another coco with more
 * detailed error message).
 *
 */
public class AssociationRoleNameNoConflictWithAttribute implements CD4AnalysisASTCDAssociationCoCo {
  
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
        err = check(leftType.get(), a.isPresentRightRole(), a);
      }
      if (rightType.isPresent() && !err
          && (a.isRightToLeft() || a.isBidirectional() || a.isUnspecified())) {
        check(rightType.get(), a.isPresentLeftRole(), a);
      }
    }
  }
  
  /**
   * Does the actual check.
   *
   * @param sourceType source of the assoc under test
   * @param roleNameDefined optional role name of the target type
   * @param assoc association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(CDTypeSymbol sourceType, boolean roleNameDefined, ASTCDAssociation assoc) {
    CDAssociationSymbol assocSym = (CDAssociationSymbol) assoc.getSymbol();
    String automaticallyIntroduced = roleNameDefined
        ? ""
        : AUTOMATICALLY_INTRODUCED;
    
    String roleName = assocSym.getDerivedName();
    String targetType = assocSym.getTargetType().getName();
    
    // attributes
    Optional<CDFieldSymbol> conflictingAttribute = sourceType.getAllVisibleFields().stream()
        .filter(f -> f.getName().equals(roleName))
        .findAny();
    
    if (conflictingAttribute.isPresent()) {
      error(automaticallyIntroduced,
          roleName,
          targetType,
          assoc,
          conflictingAttribute.get().getEnclosingScope().getSpanningSymbol().getName());
      return true;
    }
    
    // automatically introduced attributes from other assocs of the source type.
    // we exclude role names because its an own coco with a more detailed error
    // message
    
    // own
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getAssociations().stream()
        .filter(a -> a.isPresentAssocName() && !a.isPresentTargetRole())
        .filter(a -> a.getDerivedName().equals(roleName))
        .filter(a -> a != assocSym)
        .findAny();
    if (!conflictingAssoc.isPresent()) {
      // inherited
      conflictingAssoc = sourceType.getInheritedAssociations().stream()
          .filter(a -> a.isPresentAssocName() && !a.isPresentTargetRole())
          .filter(a -> a.getDerivedName().equals(roleName))
          .findAny();
    }
    if (conflictingAssoc.isPresent()) {
      error(automaticallyIntroduced,
          roleName,
          targetType,
          assoc,
          conflictingAssoc.get().getSourceType().getName());
      return true;
    }
    return false;
  }
  
  private void error(String automaticallyIntroduced, String roleName, String targetType,
                     ASTCDAssociation assoc, String conflictingAttributeName) {
    Log.error(String
        .format(
            "0xC4A27 The%s role name %s of class %s for association %s conflicts with an attribute in %s.",
            automaticallyIntroduced,
            roleName,
            targetType,
            CD4ACoCoHelper.printAssociation(assoc),
            conflictingAttributeName,
            assoc.get_SourcePositionStart()));
    
  }
}
