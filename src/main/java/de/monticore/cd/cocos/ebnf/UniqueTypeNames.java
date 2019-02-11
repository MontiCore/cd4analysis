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

import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Checks uniqueness among the names of classes, interfaces, and enums.
 * 
 * @author Robert Heim
 */
public class UniqueTypeNames implements CD4AnalysisASTCDDefinitionCoCo {
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    
    Collection<ASTCDType> types = new ArrayList<>();
    types.addAll(cdDefinition.getCDClassList());
    types.addAll(cdDefinition.getCDEnumList());
    types.addAll(cdDefinition.getCDInterfaceList());
    
    Collection<String> usedNames = new HashSet<String>();
    for (ASTCDType type : types) {
      String name = type.getName();
      if (usedNames.contains(name)) {
        Log.error(
            String
                .format(
                    "0xC4A04 The name %s is used several times. Classes, interfaces and enumerations may not use the same names.",
                    name),
            type.get_SourcePositionStart());
      }
      usedNames.add(name);
    }
  }
}
