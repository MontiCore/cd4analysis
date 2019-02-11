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

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._cocos.TypesASTSimpleReferenceTypeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that references to generic types use at least one parameter.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class GenericTypeHasParameters implements TypesASTSimpleReferenceTypeCoCo {
  
  @Override
  public void check(ASTSimpleReferenceType type) {
    Optional<ASTTypeArguments> args = type.getTypeArgumentsOpt();
    if (args.isPresent()) {
      String typeName = TypesPrinter.printType(type);
      check(typeName, args.get());
    }
  }
  
  private void check(String typeName, ASTTypeArguments typeArguments) {
    if (typeArguments.getTypeArgumentList().isEmpty()) {
      Log.error(
          String
              .format(
                  "0xC4A30 Generic type %s has no type-parameter. References to generic types must be parametrized.",
                  typeName),
          typeArguments.get_SourcePositionStart());
    }
  }
}
