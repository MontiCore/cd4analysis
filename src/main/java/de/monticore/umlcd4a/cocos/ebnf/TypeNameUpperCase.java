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

import java.util.ArrayList;
import java.util.Collection;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that type names start upper-case.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class TypeNameUpperCase implements CD4AnalysisASTCDDefinitionCoCo {
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    Collection<ASTCDType> types = new ArrayList<>();
    types.addAll(cdDefinition.getCDClassList());
    check(types, "class");
    types.clear();
    types.addAll(cdDefinition.getCDInterfaceList());
    check(types, "interface");
    types.clear();
    types.addAll(cdDefinition.getCDEnumList());
    check(types, "enum");
  }
  
  /**
   * Does the actual check.
   * 
   * @param types
   * @param kind kind of the types (class, interface, or enum)
   */
  private void check(Collection<ASTCDType> types, String kind) {
    for (ASTCDType cdType : types) {
      if (!Character.isUpperCase(cdType.getName().charAt(0))) {
        Log.error(String.format("0xC4A05 The first character of the %s %s must be upper-case.",
            kind, cdType.getName()),
            cdType.get_SourcePositionStart());
      }
    }
  }
}
