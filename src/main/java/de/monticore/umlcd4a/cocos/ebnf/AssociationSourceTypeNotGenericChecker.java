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

import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

public class AssociationSourceTypeNotGenericChecker implements
    CD4AnalysisASTCDDefinitionCoCo {
  
  public void check(ASTCDDefinition cdDefinition) {
    
    List<ASTCDAssociation> assocList = cdDefinition.getCDAssociations();
    for (ASTCDAssociation assoc : assocList) {
      
      if (assoc.isLeftToRight() || assoc.isBidirectional()) {
        ASTQualifiedName leftType = assoc.getLeftReferenceName();
        
        printErrorOnGeneric(leftType, assoc);
      }
      
      if (assoc.isRightToLeft()) {
        ASTQualifiedName rightType = assoc.getRightReferenceName();
        printErrorOnGeneric(rightType, assoc);
      }
    }
  }
  
  private void printErrorOnGeneric(ASTQualifiedName sourceType,
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
