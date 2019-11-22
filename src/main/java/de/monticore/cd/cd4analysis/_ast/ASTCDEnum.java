/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._ast;

import de.monticore.cd.prettyprint.CD4CodePrinter;

public class ASTCDEnum extends ASTCDEnumTOP {

  private CD4CodePrinter printer = new CD4CodePrinter();

  protected ASTCDEnum() {
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
