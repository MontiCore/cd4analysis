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
import de.monticore.cd.cd4analysis._ast.ASTCDEnumTOP;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._ast.ASTTImplements;

import java.util.Optional;

public class ASTCDEnum extends ASTCDEnumTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDEnum() {
  }
  
  protected ASTCDEnum(
      java.util.List<ASTCDAttribute> cDAttributes,
      Optional<ASTModifier> modifier,
      String name,
      Optional<ASTTImplements> r__implements,
      java.util.List<de.monticore.types.mcbasictypes._ast.ASTMCObjectType> interfaces,
      java.util.List<de.monticore.cd.cd4analysis._ast.ASTCDEnumConstant> cDEnumConstants,
      java.util.List<ASTCDConstructor> cDConstructors,
      java.util.List<ASTCDMethod> cDMethods) {
    super(cDAttributes, modifier, r__implements, interfaces, cDEnumConstants, cDConstructors,
        cDMethods, name);
  }
  
  public String printModifier() {
    return super.printModifier();
  }
  
  public String printEnumConstants() {
    return printer.printEnumConstants(getCDEnumConstantList());
  }
  
  public String printInterfaces() {
    return printer.printReferenceList(getInterfaceList());
  }
}
