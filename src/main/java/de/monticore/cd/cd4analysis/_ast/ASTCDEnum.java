/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CD4CodePrinter;

import java.util.Optional;

public class ASTCDEnum extends ASTCDEnumTOP {

  private CD4CodePrinter printer = new CD4CodePrinter();

  protected ASTCDEnum() {
  }

  protected ASTCDEnum(
      java.util.List<ASTCDAttribute> cDAttributes,
      Optional<ASTModifier> modifier,
      Optional<ASTTImplements> r__implements,
      java.util.List<de.monticore.types.mcbasictypes._ast.ASTMCObjectType> interfaces,
      java.util.List<de.monticore.cd.cd4analysis._ast.ASTCDEnumConstant> cDEnumConstants,
      java.util.List<ASTCDConstructor> cDConstructors,
      java.util.List<ASTCDMethod> cDMethods,
      String name) {
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
