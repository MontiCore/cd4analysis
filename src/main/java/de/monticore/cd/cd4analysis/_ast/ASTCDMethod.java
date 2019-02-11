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
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.cd.cd4analysis._ast.ASTCDMethodTOP;
import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.cd.cd4analysis._ast.ASTModifier;

public class ASTCDMethod extends ASTCDMethodTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDMethod() {
  }
  
  protected ASTCDMethod(ASTModifier modifier,
      ASTReturnType returnType,
      String name,
      java.util.List<de.monticore.cd.cd4analysis._ast.ASTCDParameter> cDParameters,
      java.util.List<de.monticore.types.types._ast.ASTQualifiedName> exceptions)
  {
    super(modifier, returnType, name, cDParameters, exceptions);
  }
  
  /**
   * Prints a return type
   * 
   * @return String representation of the ASTreturnType
   */
  public String printReturnType() {
    return TypesPrinter.printReturnType(getReturnType());
  }
  
  public String printAnnotation() {
    if (getModifier().isPresentStereotype()) {
      StringBuffer sb = new StringBuffer();
      for (ASTCDStereoValue s: getModifier().getStereotype().values) {
        sb.append(s.getName());
        sb.append("\n");
      }
      return sb.toString();
    }
    
    return "";
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
