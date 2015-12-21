package de.monticore.umlcd4a.cd4analysis._ast;

import static de.monticore.umlcd4a.prettyprint.AstPrinter.EMPTY_STRING;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.prettyprint.AstPrinter;

public class ASTCDClass extends ASTCDClassTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDClass() {
  }
  
  protected ASTCDClass(
      ASTModifier modifier,
      String name,
      ASTReferenceType superclass,
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
    if (!superclassIsPresent()) {
      return EMPTY_STRING;
    }
    return TypesPrinter.printType(getSuperclass().get());
  }
  
  public String printModifier() {
    return super.printModifier();
  }
  
  /**
   * Prints the interfaces
   * 
   * @return String representation of the interfaces
   */
  public String printInterfaces() {
    return printer.printReferenceList(getInterfaces());
  }
  
}
