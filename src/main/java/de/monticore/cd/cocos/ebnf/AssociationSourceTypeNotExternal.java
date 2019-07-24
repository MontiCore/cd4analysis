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
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.symboltable.CDTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks if the source of an association is defined within the classdiagram
 * itself.
 *
 * @author Robert Heim
 */
public class AssociationSourceTypeNotExternal implements
    CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.isLeftToRight() || assoc.isBidirectional()) {
      ASTMCQualifiedName leftType = assoc.getLeftReferenceName();
      if (isExternal(leftType, assoc)) {
        error(assoc);
      }
    }
    
    if (assoc.isRightToLeft() || assoc.isBidirectional()) {
      ASTMCQualifiedName rightType = assoc.getRightReferenceName();
      if (isExternal(rightType, assoc)) {
        error(assoc);
      }
    }
    
    if (assoc.isUnspecified()) {
      ASTMCQualifiedName leftType = assoc.getLeftReferenceName();
      ASTMCQualifiedName rightType = assoc.getRightReferenceName();
      // not both can be external, but one is ok
      if (isExternal(leftType, assoc) && isExternal(rightType, assoc)) {
        error(assoc);
      }
    }
  }
  
  private void error(ASTCDAssociation assoc) {
    String assocString = "";
    if (assoc.isPresentName()) {
      assocString = assoc.getName();
    }
    else {
      assocString = CD4ACoCoHelper.printAssociation(assoc);
    }
    Log.error(
        String
            .format(
                "0xC4A22 Association %s is invalid, because an association's source may not be an external type.",
                assocString),
        assoc.get_SourcePositionStart());
  }
  
  private boolean isExternal(ASTMCQualifiedName sourceType, ASTCDAssociation assoc) {
    Optional<CDTypeSymbol> sourceSym = assoc.getEnclosingScope()
        .resolve(sourceType.toString(), CDTypeSymbol.KIND);
    if (sourceSym.isPresent()) {
      return false;
    }
    return true;
  }
  
}