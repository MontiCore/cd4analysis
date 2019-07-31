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

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._cocos.MCCollectionTypesASTMCGenericTypeCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that references to generic types use at least one parameter.
 *
 * @author Robert Heim
 */
public class GenericTypeHasParameters implements MCCollectionTypesASTMCGenericTypeCoCo {
  
  @Override
  public void check(ASTMCGenericType type) {
    List<ASTMCTypeArgument> args = type.getMCTypeArgumentList();
    if (!args.isEmpty()) {
      String typeName = new AstPrinter().printType(type);
      check(typeName, args);
    }
  }
  
  private void check(String typeName, List<ASTMCTypeArgument> typeArguments) {
    if (typeArguments.isEmpty()) {
      Log.error(
          String
              .format(
                  "0xC4A30 Generic type %s has no type-parameter. References to generic types must be parametrized.",
                  typeName),
          typeArguments.get(0).get_SourcePositionStart());
    }
  }
}
