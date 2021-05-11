/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cocos;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that only interfaces are implemented.
 */
abstract public class ImplementOnlyInterfaces {

  /**
   * Actual check that the class's interfaces are really interfaces.
   *
   * @param node the node to check.
   */
  public void check(ASTCDClass node) {
    OOTypeSymbol symbol = node.getSymbol();

    if (!node.isPresentCDInterfaceUsage()) {
      return;
    }
    final List<ASTMCObjectType> interfaceList = node.getCDInterfaceUsage().getInterfaceList();
    interfaceList.stream().map(s ->
        symbol.getEnclosingScope()
            .resolveOOType(s.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter())))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(e -> !e.isIsInterface())
        .forEach(e ->
            Log.error(String.format(
                "0xCDCF4: Class %s cannot extend %s %s. A class may only extend classes.",
                node.getName(),
                CDMill.cDTypeKindPrinter().print(e),
                e.getName()),
                node.get_SourcePositionStart())
        );
  }

  /**
   * Actual check that the enums interfaces are really interfaces.
   *
   * @param node the node to check.
   */
  public void check(ASTCDEnum node) {
    OOTypeSymbol symbol = node.getSymbol();
    if (!node.isPresentCDInterfaceUsage()) {
      return;
    }
    symbol.streamSuperTypes().filter(i -> !CoCoHelper.isInterface(i.getTypeInfo())).forEach(e ->
        Log.error(String.format(
            "0xCDCF5: The %s %s cannot implement %s %s. Only interfaces may be implemented.",
            CDMill.cDTypeKindPrinter().print(node),
            symbol.getName(),
            CDMill.cDTypeKindPrinter().print(e.getTypeInfo()),
            e.getTypeInfo().getName()),
            node.get_SourcePositionStart())
    );
  }

  /**
   * Actual check that the node's interfaces are really interfaces.
   *
   * @param node the node to check.
   */
  public void check(ASTCDInterface node) {
    OOTypeSymbol symbol = node.getSymbol();
    if (!node.isPresentCDExtendUsage()) {
      return;
    }
    symbol.streamSuperTypes().filter(i -> !CoCoHelper.isInterface(i.getTypeInfo())).forEach(e ->
        Log.error(String.format(
            "0xCDCF6: The %s %s cannot extend %s %s. Only interfaces may be extended.",
            CDMill.cDTypeKindPrinter().print(node),
            symbol.getName(),
            CDMill.cDTypeKindPrinter().print(e.getTypeInfo()),
            e.getTypeInfo().getName()),
            node.get_SourcePositionStart())
    );
  }
}
