/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.umlcd4a.cocos.ebnf;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.symboltable.Symbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

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
          .resolve(a.getLeftReferenceName().toString(), CDTypeSymbol.KIND);
      Optional<CDTypeSymbol> rightType = a.getEnclosingScope()
          .resolve(a.getRightReferenceName().toString(), CDTypeSymbol.KIND);
      boolean err = false;
      // source type might be external (in this case we do nothing)
      if (leftType.isPresent() && (a.isLeftToRight() || a.isBidirectional() || a.isUnspecified())) {
        err = check(leftType.get(), a.getRightRoleOpt(), a);
      }
      if (rightType.isPresent() && !err
          && (a.isRightToLeft() || a.isBidirectional() || a.isUnspecified())) {
        check(rightType.get(), a.getLeftRoleOpt(), a);
      }
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param sourceType source of the assoc under test
   * @param role optional role name of the target type
   * @param assoc association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(CDTypeSymbol sourceType, Optional<String> role, ASTCDAssociation assoc) {
    CDAssociationSymbol assocSym = (CDAssociationSymbol) assoc.getSymbol();
    String automaticallyIntroduced = role.isPresent()
        ? ""
        : AUTOMATICALLY_INTRODUCED;
    
    String roleName = assocSym.getDerivedName();
    String targetType = assocSym.getTargetType().getName();
    
    // automatically introduced attributes from other assocs with role names of
    // the source type.
    // we exclude assoc names (and do not test for normal attributes) because
    // its an own coco
    
    // own
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getAssociations().stream()
        .filter(a -> a.getSourceRole().isPresent() || !a.getAssocName().isPresent())
        .filter(a -> a.getDerivedName().equals(roleName))
        .filter(a -> a != assocSym)
        .findAny();
    if (!conflictingAssoc.isPresent()) {
      // inherited
      conflictingAssoc = sourceType.getInheritedAssociations().stream()
          .filter(a -> a.getSourceRole().isPresent() || !a.getAssocName().isPresent())
          .filter(a -> a.getDerivedName().equals(roleName))
          .findAny();
    }
    if (conflictingAssoc.isPresent()) {
      String conflictingRoleNameAuto = conflictingAssoc.get().getTargetRole().isPresent()
          ? ""
          : AUTOMATICALLY_INTRODUCED;
      String conflictingRoleName = conflictingAssoc.get().getDerivedName();

      boolean isReadOnly = false;
      List<String> superTypes = null;
      Optional<CDTypeSymbol> targetTypeSymbol = assoc.getEnclosingScope()
              .<CDTypeSymbol>resolve(targetType, CDTypeSymbol.KIND);
      if (targetTypeSymbol.isPresent()) {
        isReadOnly = conflictingAssoc.get().isReadOnly();
        superTypes = targetTypeSymbol.get().getSuperTypesTransitive().stream().map(type -> type.getFullName()).collect(Collectors.toList());
      }
      if (isReadOnly && superTypes.contains(conflictingAssoc.get().getTargetType().getFullName())) {
        Log.info(String.format("Association `%s` overwrites read-only association `%s`",
                assoc, conflictingAssoc.get()),
                "INFO");
      }
      else {
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
                                CD4ACoCoHelper.printAssociation((ASTCDAssociation) conflictingAssoc.get()
                                        .getAstNode().get())
                        ),
                assoc.get_SourcePositionStart());
        return true;
      }
    }
    return false;
  }
}
