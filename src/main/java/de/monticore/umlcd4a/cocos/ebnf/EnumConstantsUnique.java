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

import java.util.Collection;
import java.util.HashSet;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks uniqueness among the enum constants.
 * 
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class EnumConstantsUnique implements CD4AnalysisASTCDEnumCoCo {
  
  @Override
  public void check(ASTCDEnum node) {
    Collection<String> usedNames = new HashSet<String>();
    for (ASTCDEnumConstant constant : node.getCDEnumConstantList()) {
      String name = constant.getName();
      if (usedNames.contains(name)) {
        Log.error(String.format("0xC4A06 Duplicate enum constant: %s.", name),
            constant.get_SourcePositionStart());
      }
      usedNames.add(name);
    }
  }
}
