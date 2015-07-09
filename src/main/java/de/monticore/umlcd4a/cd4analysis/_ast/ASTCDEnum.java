package de.monticore.umlcd4a.cd4analysis._ast;

import de.monticore.types.types._ast.ASTReferenceTypeList;
import de.monticore.umlcd4a.prettyprint.AstPrinter;

public class ASTCDEnum extends ASTCDEnumTOP {
  
  private AstPrinter printer = new AstPrinter();
  
  protected ASTCDEnum() {
  }
  
  protected ASTCDEnum(
      ASTModifier modifier,
      String name,
      ASTReferenceTypeList interfaces,
      ASTCDEnumConstantList cDEnumConstants,
      ASTCDConstructorList cDConstructors,
      ASTCDMethodList cDMethods) {
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
