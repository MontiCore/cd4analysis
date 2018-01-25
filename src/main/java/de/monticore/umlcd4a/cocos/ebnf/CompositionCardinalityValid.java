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
import de.monticore.umlcd4a.cd4analysis._ast.ASTCardinality;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the cardinality of compositions is not larger than one. Note that
 * we expect all compositions to have navigation direction "->", "--" or "<->"
 * where the composit is always on the left side. So this CoCo only checks the
 * cardinality of the left side.
 *
 * @author Robert Heim
 */
public class CompositionCardinalityValid implements
    CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.isComposition()) {
      ASTCardinality cardinality = null;
      if (assoc.isLeftCardinalityPresent()) {
        cardinality = assoc.getLeftCardinality();
      }
      else {
        // default cardinality is 1 for compositions.
        cardinality = CD4AnalysisMill.cardinalityBuilder().one(true).build();
      }
      
      boolean isCardinalityValid = cardinality.isOne() | cardinality.isOptional();
      
      if (!isCardinalityValid) {
        Log.error(String.format(
            "0xC4A18 The composition %s has an invalid cardinality %s larger than one.",
            CD4ACoCoHelper.printAssociation(assoc),
            CD4ACoCoHelper.printCardinality(cardinality)),
            assoc.get_SourcePositionStart());
      }
    }
  }
}
