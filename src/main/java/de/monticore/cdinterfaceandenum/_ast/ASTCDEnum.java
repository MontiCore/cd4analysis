/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

import java.util.Collections;
import java.util.List;

public class ASTCDEnum extends ASTCDEnumTOP {
  private CD4CodePrettyPrinter printer = new CD4CodePrettyPrinter();

  @Override
  public void setSpannedScope(ICDBasisScope spannedScope) {
    super.setSpannedScope((ITypeSymbolsScope) spannedScope);
  }

  @Override
  public List<ASTMCObjectType> getSuperclassList() {
    return Collections.emptyList();
  }

  @Override
  public String printSuperclasses() {
    return "";
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
