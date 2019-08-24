/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.AstPrinter;
import de.monticore.cd.prettyprint.CDPrettyPrinterDelegator;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Optional;

import static de.monticore.cd.prettyprint.AstPrinter.EMPTY_STRING;

public class ASTCDClass extends ASTCDClassTOP {

  private AstPrinter printer = new AstPrinter();

  protected ASTCDClass() {
  }

  protected ASTCDClass(
      Optional<ASTModifier> modifier,
      Optional<ASTMCObjectType> superclass,
      Optional<ASTTImplements> r__implements,
      java.util.List<ASTMCObjectType> interfaces,
      Optional<ASTCDStereotype> stereotype,
      java.util.List<ASTCDAttribute> cDAttributes,
      java.util.List<ASTCDConstructor> cDConstructors,
      java.util.List<ASTCDMethod> cDMethods,
      String name) {
    super(modifier, superclass, r__implements, interfaces, stereotype, cDAttributes,
        cDConstructors, cDMethods, name);
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
