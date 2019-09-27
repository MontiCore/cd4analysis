/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that only interfaces are implemented.
 *
 * @author Robert Heim
 */
abstract public class ImplementOnlyInterfaces {
  
  /**
   * Actual check that the node's interfaces are really interfaces.
   *
   * @param type depending on the node type that is checked: class or enum
   * @param node the node to check.
   */
  public void check(String type, ASTCDType node) {
    CDTypeSymbol symbol = node.getSymbol();
    for (CDTypeSymbol superType : symbol.getCdInterfaceList()) {
      if (!superType.isIsInterface()) {
        Log.error(String.format(
            "0xC4A10 The %s %s cannot implement %s %s. Only interfaces may be implemented.", type,
            symbol.getName(),
            superType.isIsClass()
                ? "class"
                : "enum", superType.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
