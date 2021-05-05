/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.ArrayList;
import java.util.List;

public class ASTCDClass extends ASTCDClassTOP {
  private final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter();

  @Override
  public List<ASTMCObjectType> getSuperclassList() {
    if (!isPresentCDExtendUsage()) {
      return new ArrayList<ASTMCObjectType>();
    }
    return getCDExtendUsage().getSuperclassList();
  }

  /**
   * Prints the superclass
   *
   * @return String representation of the superclasses
   */
  @Override
  public String printSuperclasses() {
    if (!isPresentCDExtendUsage()) {
      return PrettyPrintUtil.EMPTY_STRING;
    }
    printer.getPrinter().clearBuffer();
    printer.getTraverser().traverse(getCDExtendUsage());
    return printer.getPrinter().getContent();
  }

  @Override
  public List<ASTMCObjectType> getInterfaceList() {
    if (!isPresentCDInterfaceUsage()) {
      return new ArrayList<ASTMCObjectType>();
    }
    return getCDInterfaceUsage().getInterfaceList();
  }

  /**
   * Prints the interfaces
   *
   * @return String representation of the interfaces
   */
  @Override
  public String printInterfaces() {
    if (!isPresentCDInterfaceUsage()) {
      return PrettyPrintUtil.EMPTY_STRING;
    }
    printer.getPrinter().clearBuffer();
    printer.getTraverser().traverse(getCDInterfaceUsage());
    return printer.getPrinter().getContent();
  }
}
