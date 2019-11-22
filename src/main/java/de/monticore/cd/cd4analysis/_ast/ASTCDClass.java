/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.cd.prettyprint.CDPrettyPrinterDelegator;

import static de.monticore.cd.prettyprint.AstPrinter.EMPTY_STRING;

public class ASTCDClass extends ASTCDClassTOP {

  private CD4CodePrinter printer = new CD4CodePrinter();

  protected ASTCDClass() {
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
    return new CDPrettyPrinterDelegator().prettyprint(getSuperclass());
  }

  public String printAnnotation() {
    if (isPresentModifier()) {
      if (getModifier().isPresentStereotype()) {
        StringBuffer sb = new StringBuffer();
        for (ASTCDStereoValue s : getModifier().getStereotype().values) {
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
