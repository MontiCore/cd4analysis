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
import de.se_rwth.commons.logging.Log;

/**
 * Checks that role names start lower-case.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class AssociationRoleNameLowerCase implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation assoc) {
    boolean err = false;
    if (assoc.isPresentLeftRole()) {
      err = check(assoc.getLeftRole(), assoc);
    }
    if (!err && assoc.isPresentRightRole()) {
      check(assoc.getRightRole(), assoc);
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param roleName name under test
   * @param assoc association under test
   * @return whether there was an error or not
   */
  private boolean check(String roleName, ASTCDAssociation assoc) {
    if (roleName.isEmpty()) {
      return false;
    }
    if (!Character.isLowerCase(roleName.charAt(0))) {
      Log.error(String.format("0xC4A17 Role %s of association %s must start in lower-case.",
          roleName, CD4ACoCoHelper.printAssociation(assoc)),
          assoc.get_SourcePositionStart());
      return true;
    }
    return false;
  }
  
}
