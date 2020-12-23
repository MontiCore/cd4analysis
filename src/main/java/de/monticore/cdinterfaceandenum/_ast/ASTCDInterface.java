/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdinterfaceandenum._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.Collections;
import java.util.List;

public class ASTCDInterface extends ASTCDInterfaceTOP {
  private final CD4CodeFullPrettyPrinter printer = CD4CodeMill.cD4CodePrettyPrinter();

  @Override
  public void setSpannedScope(ICDBasisScope spannedScope) {
    super.setSpannedScope((IOOSymbolsScope) spannedScope);
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
    if (!isPresentCDExtendUsage()) {
      return Collections.emptyList();
    }
    return getCDExtendUsage().getSuperclassList();
  }

  /**
   * Prints the interfaces
   *
   * @return String representation of the interfaces
   */
  @Override
  public String printInterfaces() {
    if (!isPresentCDExtendUsage()) {
      return PrettyPrintUtil.EMPTY_STRING;
    }
    printer.getPrinter().clearBuffer();
    printer.getTraverser().traverse(getCDExtendUsage());
    return printer.getPrinter().getContent();
  }
}
