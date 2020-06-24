/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos;

import de.monticore.cd.prettyprint.CDTypeKindPrinter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that only interfaces are implemented.
 */
abstract public class ImplementOnlyInterfaces {

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
    symbol.getSuperTypeList().stream().filter(i -> i.getTypeInfo().isIsInterface()).forEach(e ->
        Log.error(String.format(
            "0xCDCF5: The %s %s cannot implement %s %s. Only interfaces may be implemented.",
            new CDTypeKindPrinter().print(node),
            symbol.getName(),
            new CDTypeKindPrinter().print(e.getTypeInfo()),
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
    symbol.getSuperTypeList().stream().filter(i -> i.getTypeInfo().isIsInterface()).forEach(e ->
        Log.error(String.format(
            "0xC4A10: The %s %s cannot extend %s %s. Only interfaces may be extended.",
            new CDTypeKindPrinter().print(node),
            symbol.getName(),
            new CDTypeKindPrinter().print(e.getTypeInfo()),
            e.getTypeInfo().getName()),
            node.get_SourcePositionStart())
    );
  }
}
