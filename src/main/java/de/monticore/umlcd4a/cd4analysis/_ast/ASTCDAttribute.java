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

package de.monticore.umlcd4a.cd4analysis._ast;

import static de.monticore.umlcd4a.prettyprint.AstPrinter.EMPTY_STRING;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.prettyprint.AstPrinter;

public class ASTCDAttribute extends ASTCDAttributeTOP
    implements ASTCD4AnalysisNode {
  
  protected ASTCDAttribute() {
  }
  
  protected ASTCDAttribute(
      ASTModifier modifier,
      ASTType type,
      String name,
      ASTValue value) {
    super(modifier, type, name, value);
  }
  
  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   * 
   * @return a string, e.g. abstract private final 
   */
  public String printModifier() {
    if (!modifierIsPresent()) {
      return EMPTY_STRING;
    }
    
    StringBuilder modifierStr = new StringBuilder();
    if (modifier.get().isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.get().isPublic()) {
      modifierStr.append(" public ");
    }
    else if (modifier.get().isPrivate()) {
      modifierStr.append(" private ");
    }
    else if (modifier.get().isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.get().isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.get().isStatic()) {
      modifierStr.append(" static ");
    }
    
    return modifierStr.toString();
  }
  
  /**
   * Prints a value of an attribute
   * 
   * @return a string representing the ASTValue
   */
  public String printValue() {
    if (!valueIsPresent()) {
      return EMPTY_STRING;
    }
    
    return (new AstPrinter().printValue(value));
  }
  
  /**
   * Prints an attribute type
   * 
   * @return String representation of the ASTType
   */
  public String printType() {
    return TypesPrinter.printType(type);
  }
}
