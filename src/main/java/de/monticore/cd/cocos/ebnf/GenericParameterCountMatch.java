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

import de.monticore.types.BasicGenericsTypesPrinter;
import de.monticore.types.TypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._cocos.MCCollectionTypesASTMCGenericTypeCoCo;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._cocos.TypesASTSimpleReferenceTypeCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that references to generic types uses a correct parameter count (w.r.t
 * the generics definition).
 *
 * @author Robert Heim
 */
public class GenericParameterCountMatch implements MCCollectionTypesASTMCGenericTypeCoCo {

  @Override
  public void check(ASTMCGenericType type) {
    // note that generics cannot be defined within C4A and only three default
    // default types use generics (Optional, List, Set) and they all have
    // exactly one type parameter.
    List<ASTMCTypeArgument> args = type.getMCTypeArgumentList();
    if (!args.isEmpty()) {
      String typeName = BasicGenericsTypesPrinter.printType(type);
      check(typeName, args);
    }
  }
  
  private void check(String typeName, List<ASTMCTypeArgument> typeArguments) {
    // note that "no type arguments" is checked by coco GenericTypeHasParameters
    if (!typeArguments.isEmpty()) {
      String typeWithoutGenerics = typeName;
      if (typeName.indexOf('<') > 0) {
        typeWithoutGenerics = typeName.substring(0, typeName.indexOf('<'));
      }

      int actualCount = typeArguments.size();
      int expectedCount = 1;
      if (typeName.startsWith("Map")) {
        expectedCount = 2;
      }
      if (expectedCount != actualCount) {
        Log.error(String.format(
            "0xC4A31 Generic type %s has %d type-parameter, but %d where given ('%s').",
            typeWithoutGenerics,
            expectedCount,
            actualCount,
            typeName),
            typeArguments.get(0).get_SourcePositionStart());
      }
    }
  }
}
