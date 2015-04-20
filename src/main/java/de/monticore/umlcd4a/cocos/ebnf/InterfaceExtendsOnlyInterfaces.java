package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that interfaces do only extend other interfaces.
 * 
 * @author Robert Heim
 */
public class InterfaceExtendsOnlyInterfaces implements CD4AnalysisASTCDInterfaceCoCo {
  
  public static final String ERROR_CODE = "0xC4A09";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s cannot extend %s %s. An interface may only extend interfaces.";
  
  @Override
  public void check(ASTCDInterface iface) {
    CDTypeSymbol symbol = (CDTypeSymbol) iface.getSymbol().get();
    for (CDTypeSymbol superType : symbol.getInterfaces()) {
      if (!superType.isInterface()) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, iface.getName(),
                superType.isClass()
                    ? "class"
                    : "enum", superType.getName()),
            iface.get_SourcePositionStart());
      }
    }
  }
}
