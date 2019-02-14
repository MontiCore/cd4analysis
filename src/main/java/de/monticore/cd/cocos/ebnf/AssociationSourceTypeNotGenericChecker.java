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
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class AssociationSourceTypeNotGenericChecker implements
    CD4AnalysisASTCDDefinitionCoCo {
  
  public void check(ASTCDDefinition cdDefinition) {
    
    List<ASTCDAssociation> assocList = cdDefinition.getCDAssociationList();
    for (ASTCDAssociation assoc : assocList) {
      
      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTMCQualifiedName leftType = assoc.getLeftReferenceName();
        
        printErrorOnGeneric(leftType, assoc);
      }
      
      if (assoc.isRightToLeft()) {
        ASTMCQualifiedName rightType = assoc.getRightReferenceName();
        printErrorOnGeneric(rightType, assoc);
      }
    }
  }
  
  private void printErrorOnGeneric(ASTMCQualifiedName sourceType,
      ASTCDAssociation assoc) {
    
    String s = sourceType.toString();
    
    if (s.contains("<")) {
      
      String assocString = CD4ACoCoHelper.printAssociation(assoc);
      Log.error(
          String
              .format(
                  "0xD??? Association %s is invalid, because an association's source may not be a generic type",
                  assocString),
          assoc.get_SourcePositionStart());
      
    }
  }
}
