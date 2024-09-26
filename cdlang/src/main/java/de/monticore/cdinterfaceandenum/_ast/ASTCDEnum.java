/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum._ast;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._prettyprint.CDInterfaceAndEnumFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ASTCDEnum extends ASTCDEnumTOP {
  protected final CDInterfaceAndEnumFullPrettyPrinter printer =
      new CDInterfaceAndEnumFullPrettyPrinter(new IndentPrinter());

  @Override
  public void setSpannedScope(ICDBasisScope spannedScope) {
    super.setSpannedScope((IOOSymbolsScope) spannedScope);
  }

  @Override
  public List<ASTMCObjectType> getSuperclassList() {
    return Collections.emptyList(); // empty unmodifiable list
  }

  @Override
  public String printSuperclasses() {
    return "";
  }

  @Override
  public List<ASTMCObjectType> getInterfaceList() {
    if (!isPresentCDInterfaceUsage()) {
      return Collections.emptyList(); // empty unmodifiable list
    }
    return getCDInterfaceUsage().getInterfaceList();
  }

  /**
   * Prints the name of the interfaces as a comma-separated string
   *
   * @return String representation of the interfaces
   */
  @Override
  public String printInterfaces() {
    if (!isPresentCDInterfaceUsage()) {
      return PrettyPrintUtil.EMPTY_STRING;
    }
    return getCDInterfaceUsage().getInterfaceList().stream()
        .map(ASTMCObjectType::printType)
        .collect(Collectors.joining(","));
  }
}
