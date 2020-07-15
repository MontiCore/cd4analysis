/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Collections;
import java.util.List;

public class ASTCDClass extends ASTCDClassTOP {
  private CD4CodePrettyPrinter printer = CD4CodeMill.cD4CodePrettyPrinter();

  @Override
  public List<ASTMCObjectType> getSuperclassList() {
    if (!isPresentCDExtendUsage()) {
      return Collections.emptyList();
    }
    return getCDExtendUsage().getSuperclasList();
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
    printer.traverse(getCDExtendUsage());
    return printer.getPrinter().getContent();
  }

  @Override
  public List<ASTMCObjectType> getInterfaceList() {
    if (!isPresentCDInterfaceUsage()) {
      return Collections.emptyList();
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
    printer.traverse(getCDInterfaceUsage());
    return printer.getPrinter().getContent();
  }
}
