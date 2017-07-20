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

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that derived attributes are not initialized.
 *
 * @author Robert Heim
 */
public class TypeNoInitializationOfDerivedAttribute implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPresent()) {
      if (attr.getModifier().get().isDerived()) {
        if (attr.getValue().isPresent()) {
          Log.error(String.format("0xC4A34 Invalid initialization of the derived attribute %s. Derived attributes may not be initialized.", attr.getName()),
              attr.get_SourcePositionStart());
        }
      }
    }
  }
}
