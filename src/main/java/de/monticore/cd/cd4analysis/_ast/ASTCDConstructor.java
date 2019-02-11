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

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.cd.cd4analysis._ast.ASTCDConstructorTOP;
import de.monticore.cd.cd4analysis._ast.ASTModifier;

public class ASTCDConstructor extends ASTCDConstructorTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDConstructor() {
  }
  
  protected ASTCDConstructor(
      ASTModifier modifier,
      String name,
      java.util.List<de.monticore.cd.cd4analysis._ast.ASTCDParameter> cDParameters,
      java.util.List<de.monticore.types.types._ast.ASTQualifiedName> exceptions)
  {
    super(modifier, name, cDParameters, exceptions);
  }
  
  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   * 
   * @return a string, e.g. abstract private final
   */
  public String printModifier() {
    ASTModifier modifier = getModifier();
    
    StringBuilder modifierStr = new StringBuilder();
    if (getModifier().isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.isPublic()) {
      modifierStr.append(" public ");
    }
    else if (modifier.isPrivate()) {
      modifierStr.append(" private ");
    }
    else if (modifier.isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.isStatic()) {
      modifierStr.append(" static ");
    }
    
    return modifierStr.toString();
  }
  
  /**
   * Prints the parameter declarations that can be used in methods and
   * constructors
   * 
   * @return a string list of parameter declarations, e.g. type name
   */
  public String printParametersDecl() {
    return printer.printCDParametersDecl(getCDParameterList());
  }
  
  /**
   * Prints the throws declaration for methods and constructors.
   * 
   * @return a string list of all exceptions
   */
  public String printThrowsDecl() {
    return printer.printThrowsDecl(getExceptionList());
  }
  
}
