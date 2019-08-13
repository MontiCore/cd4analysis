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

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that interfaces do only extend other interfaces.
 * 
 * @author Robert Heim
 */
public class InterfaceExtendsOnlyInterfaces implements CD4AnalysisASTCDInterfaceCoCo {
  
  @Override
  public void check(ASTCDInterface iface) {
    CDTypeSymbol symbol = iface.getCDTypeSymbol();
    for (CDTypeSymbol superType : symbol.getCdInterfaces()) {
      if (!superType.isInterface()) {
        Log.error(String.format(
            "0xC4A09 Interface %s cannot extend %s %s. An interface may only extend interfaces.",
            iface.getName(),
            superType.isClass()
                ? "class"
                : "enum", superType.getName()),
            iface.get_SourcePositionStart());
      }
    }
  }
}
