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

package de.monticore.umlcd4a.cocos.others;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that association names start lower-case.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class AssociationNavigationHasCardinality implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    if ((a.isRightToLeft() || a.isBidirectional()) && !a.isPresentLeftCardinality()) {
      Log.error(
              String.format("0xC4A38 Association `%s` has left navigation arrow (<-), but no left cardinality.", a),
              a.get_SourcePositionStart());
    }
    if ((a.isLeftToRight() || a.isBidirectional()) && !a.isPresentRightCardinality()) {
      Log.error(
              String.format("0xC4A38 Association `%s` has right navigation arrow (->), but no right cardinality.", a),
              a.get_SourcePositionStart());
    }
  }
}
