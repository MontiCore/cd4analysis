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

import java.util.Optional;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.prettyprint.AstPrinter;

public class ASTCDClass extends ASTCDClassTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDClass() {
  }
  
  protected ASTCDClass(
      Optional<ASTModifier> modifier,
      String name,
      Optional<ASTReferenceType> superclass,
      java.util.List<de.monticore.types.types._ast.ASTReferenceType> interfaces,
      java.util.List<de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute> cDAttributes,
      java.util.List<de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor> cDConstructors,
      java.util.List<de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod> cDMethods) {
    super(modifier, name, superclass, interfaces, cDAttributes,
        cDConstructors, cDMethods);
  }
  
  /**
   * Prints the superclass
   * 
   * @return String representation of the superclass
   */
  public String printSuperClass() {
    if (!isPresentSuperclass()) {
      return EMPTY_STRING;
    }
    return TypesPrinter.printType(getSuperclass());
  }
  
  public String printModifier() {
    return super.printModifier();
  }
  
  public String printAnnotation() {
    if (isPresentModifier()) {
      if (getModifier().isPresentStereotype()) {
        StringBuffer sb = new StringBuffer();
        for (ASTCDStereoValue s: getModifier().getStereotype().values) {
          sb.append(s.getName());
          sb.append("\n");
        }
        return sb.toString();
      }
    }
    return "";
  }
  
  /**
   * Prints the interfaces
   * 
   * @return String representation of the interfaces
   */
  public String printInterfaces() {
    return printer.printReferenceList(getInterfaceList());
  }
  
}
