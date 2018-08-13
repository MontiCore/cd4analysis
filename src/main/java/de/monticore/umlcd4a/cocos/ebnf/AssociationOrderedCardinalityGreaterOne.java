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
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCardinality;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that the cardinality of an ordered association is greater than 1.
 *
 * @author Robert Heim
 */
public class AssociationOrderedCardinalityGreaterOne implements
    CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation assoc) {
    boolean err = false;
    if (assoc.isPresentLeftModifier()
        && isOrdered(assoc.getLeftModifier())) {
      err = check(assoc.getLeftCardinalityOpt(), assoc);
    }
    
    if (!err && assoc.isPresentRightModifier()
        && isOrdered(assoc.getRightModifier())) {
      check(assoc.getRightCardinalityOpt(), assoc);
    }
    
  }
  
  /**
   * Does the check on the given cardinality.
   * 
   * @param card the cardinality under test
   * @param assoc the association under test
   * @return whether ther was a coco error or not
   */
  private boolean check(Optional<ASTCardinality> card, ASTCDAssociation assoc) {
    if (card.isPresent()) {
      if (card.get().isOne() || card.get().isOptional()) {
        Log.error(
            String
                .format(
                    "0xC4A24 Association %s is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.",
                    CD4ACoCoHelper.printAssociation(assoc)),
            assoc.get_SourcePositionStart());
        return true;
      }
    }
    return false;
    
  }
  
  private boolean isOrdered(ASTModifier mod) {
    if (mod.isPresentStereotype()) {
       List<ASTCDStereoValue> list = mod.getStereotype().getValueList();
      for (ASTCDStereoValue l : list) {
        if ("ordered".equals(l.getName())) {
          return true;
        }
      }
    }
    
    return false;
  }
  
}
