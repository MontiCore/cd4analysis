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
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
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
   * @param role       optional role name of the target type
   * @param assoc      association under test
   * @return whether there was a CoCo error or not.
   */
  private boolean check(CDTypeSymbol sourceType, Optional<String> role, ASTCDAssociation assoc) {
    CDAssociationSymbol assocSym = (CDAssociationSymbol) assoc.getSymbol();

    String targetType = assocSym.getTargetType().getName();
    String automaticallyIntroduced = role.isPresent()
            ? ""
            : AUTOMATICALLY_INTRODUCED;

    // association sourceType (sourceRoleName) -> (targetRoleName) targetType;
    // what needs to be unique is: targetType.sourceRoleName to navigate to sourceType from targetType and
    //                             sourceType.targetRoleName to navigate to targetType from sourceType
    String roleName = assocSym.getDerivedName(); // is always target role name

    List<CDAssociationSymbol> conflictingAssoc2 = assocSym.getSourceType().getAllAssociations().stream()
            .filter(a -> a != assocSym && (a.getTargetRole().isPresent() || !a.getAssocName().isPresent()))
            .filter(a -> a.getDerivedName().equals(roleName))
            .collect(Collectors.toList());

    for (CDAssociationSymbol conflicting : conflictingAssoc2) {
      String conflictingRoleNameAuto = conflicting.getTargetRole().isPresent()
              ? ""
              : AUTOMATICALLY_INTRODUCED;
      String conflictingRoleName = conflicting.getDerivedName();

      boolean isReadOnly = false;
      List<String> superTypes = null;
      Optional<CDTypeSymbol> targetTypeSymbol = assoc.getEnclosingScope()
              .<CDTypeSymbol>resolve(targetType, CDTypeSymbol.KIND);
      if (targetTypeSymbol.isPresent()) {
        isReadOnly = conflicting.isReadOnly();
        superTypes = targetTypeSymbol.get().getSuperTypesTransitive().stream().map(type -> type.getFullName()).collect(Collectors.toList());
      }
      if (isReadOnly && superTypes.contains(conflicting.getTargetType().getFullName())) {
        Log.info(String.format("Association `%s` overwrites read-only association `%s`",
                assoc, conflicting.getAstNode().isPresent() ? conflicting.getAstNode().get() : conflicting),
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
                                        .getAstNode().get())
                        ),
                assoc.get_SourcePositionStart());
        return true;
      }
    }
    return false;
  }

  // true for handled case
  private boolean checkTargetTypesAreSameAndSourceTypesAreSuperTypes(CDAssociationSymbol assocSym, ASTCDAssociation assoc, CDAssociationSymbol conflicting) {
    if (assocSym.getTargetType().getFullName().equals(conflicting.getTargetType().getFullName())) {
      if (assocSym.getSourceType().hasSuperTypeByFullName(conflicting.getSourceType().getFullName())) {
        if (assocSym.isDerived()) {
          Log.info(String.format("Derived association `%s` is inherited from association `%s` (source type `%s` extends/implements source type `%s`)",
                  assoc, conflicting.getAstNode().isPresent() ? conflicting.getAstNode().get() : conflicting,
                  assocSym.getSourceType().getName(), conflicting.getSourceType().getName()),
                  this.getClass().getSimpleName());
          return true;
        } else {
          Log.error(String.format("0xC4A33 Association `%s` has same target role name and source type extends source type of association `%s`. So the \"inherited\" association `%s` should be a derived association.",
                  assoc, conflicting.getAstNode().isPresent() ? conflicting.getAstNode().get() : conflicting, assoc),
                  assoc.get_SourcePositionStart());
          return true;
        }
      } else if (conflicting.getSourceType().hasSuperTypeByFullName(assocSym.getSourceType().getFullName())) {
        if (conflicting.isDerived()) {
          Log.info(String.format("Derived association `%s` is inherited from association `%s` (source type `%s` extends/implements source type `%s`)",
                  conflicting.getAstNode().isPresent() ? conflicting.getAstNode().get() : conflicting, assoc,
                  conflicting.getSourceType().getName(), assocSym.getSourceType().getName()),
                  this.getClass().getSimpleName());
          return true;
        } else {
          Log.error(String.format("0xC4A33 Association `%s` has same target role name and source type extends source type of association `%s`. So the \"inherited\" association `%s` should be a derived association.",
                  conflicting.getAstNode().isPresent() ? conflicting.getAstNode().get() : conflicting, assoc, conflicting.getAstNode().isPresent() ? conflicting.getAstNode().get() : conflicting),
                  assoc.get_SourcePositionStart());
          return true;
        }
      }
    }
    return false;
  }
}
