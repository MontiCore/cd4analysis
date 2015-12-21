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
