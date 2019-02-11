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

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.symboltable.CDTypeSymbol;
import de.monticore.cd.symboltable.references.CDTypeSymbolReference;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that classes do only extend other classes.
 * 
 * @author Robert Heim
 */
public class ClassExtendsOnlyClasses implements CD4AnalysisASTCDClassCoCo {
  
  @Override
  public void check(ASTCDClass clazz) {
    CDTypeSymbol symbol = (CDTypeSymbol) clazz.getSymbol();
    Optional<CDTypeSymbolReference> optSuperType = symbol.getSuperClass();
    if (optSuperType.isPresent()) {
      CDTypeSymbol superType = optSuperType.get();
      if (!superType.isClass()) {
        Log.error(String.format(
            "0xC4A08 Class %s cannot extend %s %s. A class may only extend classes.",
            clazz.getName(),
            superType.isInterface()
                ? "interface"
                : "enum", superType.getName()),
            clazz.get_SourcePositionStart());
      }
    }
  }
}
