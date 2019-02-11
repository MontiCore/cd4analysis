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

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.symboltable.CDAssociationSymbol;
import de.monticore.cd.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that derived associations have the same cardinality
 *
 * @author Michael von Wenckstern
 */
public class AssociationNoConflictWithDerivedCardinalities implements
    CD4AnalysisASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
    if (!a.isDerived())
      return; // if it is not derived, than a role name conflict is detected by 0xCD4A33
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

    String roleName = assocSym.getDerivedName();

    // inherited
    // do not check read-only, as it is checked by AssociationNoConflictWithCardinalities
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getInheritedAssociations().stream()
            .filter(a -> !a.isReadOnly() && a.getDerivedName().equals(roleName))
            .filter(a -> a != assocSym)
            .filter(a -> a.getTargetCardinality().getMin() != assocSym.getTargetCardinality().getMin()
              || a.getTargetCardinality().getMin() != assocSym.getTargetCardinality().getMin())
            .findAny();

    if (conflictingAssoc.isPresent()) {
      Log.error(
              String
                      .format(
                              "0xC4A37 The target cardinality (%s .. %s) of the derived (inherited) association `%s` does not math the target cardinality (%s .. %s) of the association `%s`",
                              assocSym.getTargetCardinality().getMin(),
                              String.valueOf(assocSym.getTargetCardinality().getMax()).replace("-1", "*"),
                              assocSym.getAstNode().isPresent() ? assocSym.getAstNode().get() : assocSym,
                              conflictingAssoc.get().getTargetCardinality().getMin(),
                              String.valueOf(conflictingAssoc.get().getTargetCardinality().getMax()).replace("-1", "*"),
                              conflictingAssoc.get().getAstNode().isPresent() ? conflictingAssoc.get().getAstNode().get() : conflictingAssoc.get()
                              ),
              assoc.get_SourcePositionStart());
      return true;
    }
    return false;

  }
}
