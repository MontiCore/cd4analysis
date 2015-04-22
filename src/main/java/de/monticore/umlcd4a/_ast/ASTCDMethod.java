package de.monticore.umlcd4a._ast;

import de.monticore.types.TypesPrinter;
import de.monticore.types._ast.ASTQualifiedNameList;
import de.monticore.types._ast.ASTReturnType;
import de.monticore.umlcd4a.prettyprint.AstPrinter;

public class ASTCDMethod extends ASTCDMethodTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDMethod() {
  }
  
  protected ASTCDMethod(ASTModifier modifier,
      ASTReturnType returnType,
      String name,
      ASTCDParameterList cDParameters,
      ASTQualifiedNameList exceptions)
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
    return printer.printCDParametersDecl(getCDParameters());
  }
  
  /**
   * Prints the throws declaration for methods and constructors.
   * 
   * @return a string list of all exceptions
   */
  public String printThrowsDecl() {
    return printer.printThrowsDecl(getExceptions());
  }
}
