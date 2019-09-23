/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

public class ASTCDConstructor extends ASTCDConstructorTOP {

  private CD4CodePrinter printer = new CD4CodePrinter();

  protected ASTCDConstructor() {
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
    } else if (modifier.isPrivate()) {
      modifierStr.append(" private ");
    } else if (modifier.isProtected()) {
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
