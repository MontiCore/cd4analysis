package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDType;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that only interfaces are implemented.
 * 
 * @author Robert Heim
 */
abstract public class ImplementOnlyInterfaces {
  
  public static final String ERROR_CODE = "0xC4A10";
  
  public static final String ERROR_MSG_FORMAT = "The %s %s cannot implement %s %s. Only interfaces may be implemented.";
  
  /**
   * Actual check that the node's interfaces are really interfaces.
   * 
   * @param type depending on the node type that is checked: class or enum
   * @param node the node to check.
   */
  public void check(String type, ASTCDType node) {
    CDTypeSymbol symbol = (CDTypeSymbol) node.getSymbol().get();
    for (CDTypeSymbol superType : symbol.getInterfaces()) {
      if (!superType.isInterface()) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, type, symbol.getName(),
                superType.isClass()
                    ? "class"
                    : "enum", superType.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
