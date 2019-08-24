/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.prettyprint;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import de.monticore.cd.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._ast.ASTValue;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

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
    CDPrettyPrinterDelegator printer = new CDPrettyPrinterDelegator(new IndentPrinter());
    if (modifier.isPresent()) {
      return printer.prettyprint(modifier.get());
    }
    return EMPTY_STRING;
  }
  
  /**
   * Same as <pre>{@code printModifier(Optional<ASTModifier> modifier)}</pre>
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
      List<ASTMCImportStatement> importStatements) {
    
    return Collections2.transform(importStatements,
        new Function<ASTMCImportStatement, String>() {

          @Override
          public String apply(ASTMCImportStatement arg0) {
            return arg0.getQName();
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
  public String printType(ASTMCType type) {
    return new CDPrettyPrinterDelegator().prettyprint(type);
  }
  
  public String printType(ASTMCReturnType type) {
    return new CDPrettyPrinterDelegator().prettyprint(type); //TODO BasicGenericsTypesPrinter
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
                return printType(arg0.getMCType()) + " " + arg0.getName();
              }
              
            }));
  }
  
  /**
   * Prints the throws declaration for methods, constructors, etc.
   * 
   * @param exceptionList a list of all qualified exceptions
   * @return a string list of all exceptions
   */
  public String printThrowsDecl(List<ASTMCQualifiedName> exceptionList) {
    StringBuilder str = new StringBuilder();
    
    if (!exceptionList.isEmpty()) {
      str.append("throws ");
    }
    
    return str.append(
        Joiner.on(",").join(
            Collections2.transform(exceptionList,
                new Function<ASTMCQualifiedName, String>() {
                  
                  @Override
                  public String apply(ASTMCQualifiedName arg0) {
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
  public String printReferenceList(List<ASTMCObjectType> extendsList) {
    checkNotNull(extendsList);
    
    return Joiner.on(",").join(
        Collections2.transform(extendsList,
            new Function<ASTMCObjectType, String>() {
              
              @Override
              public String apply(ASTMCObjectType arg0) {
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
      CDPrettyPrinterDelegator p = new CDPrettyPrinterDelegator();
      output = p.prettyprint(value.get()).trim().intern();
    }
    
    return output;
  }
}
