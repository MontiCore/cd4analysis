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

import de.monticore.umlcd4a.prettyprint.AstPrinter;

public class ASTCDEnum extends ASTCDEnumTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDEnum() {
  }
  
  protected ASTCDEnum(
      ASTModifier modifier,
      String name,
      java.util.List<de.monticore.types.types._ast.ASTReferenceType> interfaces,
      java.util.List<de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant> cDEnumConstants,
      java.util.List<de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor> cDConstructors,
      java.util.List<de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod> cDMethods) {
    super(modifier, name, interfaces, cDEnumConstants, cDConstructors,
        cDMethods);
  }
  
  public String printModifier() {
    return super.printModifier();
  }
  
  public String printEnumConstants() {
    return printer.printEnumConstants(getCDEnumConstants());
  }
  
  public String printInterfaces() {
    return printer.printReferenceList(getInterfaces());
  }
}
