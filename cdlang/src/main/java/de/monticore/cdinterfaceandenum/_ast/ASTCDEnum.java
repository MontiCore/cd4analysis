/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._prettyprint.CDInterfaceAndEnumFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.ArrayList;
import java.util.List;

public class ASTCDEnum extends ASTCDEnumTOP {
  protected final CDInterfaceAndEnumFullPrettyPrinter printer =
      new CDInterfaceAndEnumFullPrettyPrinter(new IndentPrinter());

  @Override
  public void setSpannedScope(ICDBasisScope spannedScope) {
    super.setSpannedScope((IOOSymbolsScope) spannedScope);
  }

  @Override
  public List<ASTMCObjectType> getSuperclassList() {
    return new ArrayList<ASTMCObjectType>();
  }

  @Override
  public String printSuperclasses() {
    return "";
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
