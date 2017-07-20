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

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that only interfaces are implemented.
 * 
 * @author Robert Heim
 */
abstract public class ImplementOnlyInterfaces {
  
  /**
   * Actual check that the node's interfaces are really interfaces.
   * 
   * @param type depending on the node type that is checked: class or enum
   * @param node the node to check.
   */
  public void check(String type, ASTCDType node) {
    CDTypeSymbol symbol = (CDTypeSymbol) node.getSymbol().get();
    for (CDTypeSymbol superType : symbol.getInterfaces()) {
      if (!superType.isInterface()) {
        Log.error(String.format(
            "0xC4A10 The %s %s cannot implement %s %s. Only interfaces may be implemented.", type,
            symbol.getName(),
            superType.isClass()
                ? "class"
                : "enum", superType.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
