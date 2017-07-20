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

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that generics are not nested.
 *
 * @author Robert Heim
 */
public class GenericsNotNested implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute attr) {
    ASTType type = attr.getType();
    String typeStr = TypesPrinter.printType(type);
    if (typeStr.matches("(.*)<(.*)<(.*)")) {
      Log.error(String.format(
          "0xC4A29 Invalid type parameter %s. Generic types may not be nested.", typeStr),
          type.get_SourcePositionStart());
    }
  }
}
