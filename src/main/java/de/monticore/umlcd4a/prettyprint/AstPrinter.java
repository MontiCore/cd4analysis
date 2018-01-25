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

package de.monticore.umlcd4a.prettyprint;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTValue;

/******************************************************************************
 * AST specific helper to print AST nodes.
 *****************************************************************************/
public class AstPrinter {
  
  public static final String EMPTY_STRING = "";
  
  /**
   * Print the string of a ASTModifier type, e.g. abstract private final static
   * 
   * @param modifier the ASTModifier object
   * @return a string, e.g. abstract private final static
   */
  public String printModifier(Optional<ASTModifier> modifier) {
    checkNotNull(modifier);
    
    StringBuilder modifierStr = new StringBuilder();
    
    if (modifier.isPresent()) {
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
    }
    
    return modifierStr.toString();
  }
  
  /**
   * Same as <code>printModifier(Optional<ASTModifier> modifier)</code>
   */
  public String printModifier(ASTModifier modifier) {
    return printModifier(Optional.ofNullable(modifier));
  }
  
  /**
   * Converts a list of import statements to a string list.
   * 
   * @param importStatements the list of import statements
   * @return a string list of all import statements
   */
  public Collection<String> printImportList(
      List<ASTImportStatement> importStatements) {
    
    return Collections2.transform(importStatements,
        new Function<ASTImportStatement, String>() {
          
          @Override
          public String apply(ASTImportStatement arg0) {
            return Joiner.on(".").skipNulls().join(arg0.getImportList());
          }
          
        });
  }
  
  /**
   * Converts a list of enum constants to a string list of enum constants
   * 
   * @param enumConstants list of enum constants
   * @return a string list of enum constants
   */
  public String printEnumConstants(List<ASTCDEnumConstant> enumConstants) {
    
    checkNotNull(enumConstants);
    
    return Joiner.on(",").join(
        
        Collections2.transform(enumConstants,
            new Function<ASTCDEnumConstant, String>() {
              
              @Override
              public String apply(ASTCDEnumConstant arg0) {
                return arg0.getName();
              }
              
            }
            
            ));
  }
  
  /**
   * Prints an ASTType
   * 
   * @param type an ASTType
   * @return String representation of the ASTType
   */
  public String printType(ASTType type) {
    return TypesPrinter.printType(type);
  }
  
  public String printType(ASTReturnType type) {
    return TypesPrinter.printReturnType(type);
  }
  
  /**
   * Prints the parameter declarations that can be used in methods,
   * constructors, etc.
   * 
   * @param parameterList a list of all parameters
   * @return a string list of parameter declarations, e.g. type name
   */
  public String printCDParametersDecl(List<ASTCDParameter> parameterList) {
    checkNotNull(parameterList);
    
    return Joiner.on(",").join(
        Collections2.transform(parameterList,
            new Function<ASTCDParameter, String>() {
              
              @Override
              public String apply(ASTCDParameter arg0) {
                return printType(arg0.getType()) + " " + arg0.getName();
              }
              
            }));
  }
  
  /**
   * Prints the throws declaration for methods, constructors, etc.
   * 
   * @param exceptionList a list of all qualified exceptions
   * @return a string list of all exceptions
   */
  public String printThrowsDecl(List<ASTQualifiedName> exceptionList) {
    StringBuilder str = new StringBuilder();
    
    if (!exceptionList.isEmpty()) {
      str.append("throws ");
    }
    
    return str.append(
        Joiner.on(",").join(
            Collections2.transform(exceptionList,
                new Function<ASTQualifiedName, String>() {
                  
                  @Override
                  public String apply(ASTQualifiedName arg0) {
                    return Joiner.on(".").join(arg0.getPartList());
                  }
                  
                }))).toString();
  }
  
  /**
   * Prints a list of extends declarations.
   * 
   * @param extendsList a list of extends declarations
   * @return a string list of all extends declarations
   */
  public String printReferenceList(List<ASTReferenceType> extendsList) {
    checkNotNull(extendsList);
    
    return Joiner.on(",").join(
        Collections2.transform(extendsList,
            new Function<ASTReferenceType, String>() {
              
              @Override
              public String apply(ASTReferenceType arg0) {
                return printType(arg0);
              }
            }));
  }
  
  /**
   * Prints a value of an attribute
   * 
   * @param value the ASTValue object
   * @return a string representing the ASTValue
   */
  public String printValue(Optional<ASTValue> value) {
    checkNotNull(value);
    String output = "";
    if (value.isPresent()) {
      IndentPrinter iPrinter = new IndentPrinter();
      CDPrettyPrinterConcreteVisitor p = new CDPrettyPrinterConcreteVisitor(iPrinter);
      output = p.prettyprint(value.get()).trim().intern();
    }
    
    return output;
  }
}
