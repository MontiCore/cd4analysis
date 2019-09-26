/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that interfaces do only extend other interfaces.
 * 
 * @author Robert Heim
 */
public class InterfaceExtendsOnlyInterfaces implements CD4AnalysisASTCDInterfaceCoCo {
  
  @Override
  public void check(ASTCDInterface iface) {
    CDTypeSymbol symbol = iface.getCDTypeSymbol();
    for (CDTypeSymbol superType : symbol.getCdInterfaces()) {
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
