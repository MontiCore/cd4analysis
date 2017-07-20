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

import java.util.Optional;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that classes that extended an external class are abstract in case that
 * the external class does not provide an empty constructor.
 * 
 * @author Robert Heim
 */
public class ClassExtendExternalType implements CD4AnalysisASTCDClassCoCo {
  
  @Override
  public void check(ASTCDClass clazz) {
    CDTypeSymbol symbol = (CDTypeSymbol) clazz.getSymbol().get();
    Optional<CDTypeSymbolReference> optSuperType = symbol.getSuperClass();
    if (optSuperType.isPresent()) {
      CDTypeSymbol superType = optSuperType.get();
      if (isExternal(superType)) {
        boolean hasEmptyConstructor = superType.getConstructors()
            .stream()
            .filter(c -> c.getParameters().isEmpty())
            .count() > 0;
        if (!hasEmptyConstructor) {
          Log.error(
              String
                  .format(
                      "0xC4A36 Class %s extends the external class %s, which does not provide an empty constructor and thus %s must be abstract.",
                      clazz.getName(),
                      superType.getName(),
                      clazz.getName()),
              clazz.get_SourcePositionStart());
        }
      }
    }
  }
  
  private boolean isExternal(CDTypeSymbol s) {
    // TODO PN <- RH how to calculate this? s. #1566
    throw new RuntimeException("not implemented");
  }
}
