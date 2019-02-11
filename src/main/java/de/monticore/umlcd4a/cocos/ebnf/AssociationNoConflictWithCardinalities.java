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

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that role names do not conflict with other role names where the source
 * types has other outgoing associations (which might be inherited).
 *
 * @author Michael von Wenckstern
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class AssociationNoConflictWithCardinalities implements
    CD4AnalysisASTCDAssociationCoCo {

  @Override
  public void check(ASTCDAssociation a) {
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
    Optional<CDAssociationSymbol> conflictingAssoc = sourceType.getInheritedAssociations().stream()
            .filter(a -> a.isReadOnly() && a.getDerivedName().equals(roleName))
            .filter(a -> a != assocSym)
            .filter(a -> mapStarToMax(a.getTargetCardinality().getMin()) > mapStarToMax(assocSym.getTargetCardinality().getMin()) ||
                    mapStarToMax(a.getTargetCardinality().getMax()) < mapStarToMax(assocSym.getTargetCardinality().getMax()))
            .findAny();

    if (conflictingAssoc.isPresent()) {
      Log.error(
              String
                      .format(
                              "0xC4A32 The target cardinality (%s .. %s) of the inherited read-only association `%s` is not a superset of the target cardinality (%s ..%s) of the association `%s`",
                              conflictingAssoc.get().getTargetCardinality().getMin(),
                              String.valueOf(conflictingAssoc.get().getTargetCardinality().getMax()).replace("-1", "*"),
                              conflictingAssoc.get(),
                              assocSym.getTargetCardinality().getMin(),
                              String.valueOf(assocSym.getTargetCardinality().getMax()).replace("-1", "*"),
                              assocSym
                              ),
              assoc.get_SourcePositionStart());
      return true;
    }
    return false;

  }
  private static int mapStarToMax(int value) {
    return value == -1 ? Integer.MAX_VALUE : value;
  }
}
