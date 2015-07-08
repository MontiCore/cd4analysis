package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that interfaces do only extend other interfaces.
 * 
 * @author Robert Heim
 */
public class InterfaceExtendsOnlyInterfaces implements CD4AnalysisASTCDInterfaceCoCo {
  
  @Override
  public void check(ASTCDInterface iface) {
    CDTypeSymbol symbol = (CDTypeSymbol) iface.getSymbol().get();
    for (CDTypeSymbol superType : symbol.getInterfaces()) {
      if (!superType.isInterface()) {
        Log.error(String.format(
            "0xC4A09 Interface %s cannot extend %s %s. An interface may only extend interfaces.",
            iface.getName(),
            superType.isClass()
                ? "class"
                : "enum", superType.getName()),
            iface.get_SourcePositionStart());
      }
    }
  }
}
