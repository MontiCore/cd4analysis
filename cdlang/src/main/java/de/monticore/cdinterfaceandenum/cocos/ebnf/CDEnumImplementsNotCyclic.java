/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Checks that there are no inheritance cycles.
 */
public class CDEnumImplementsNotCyclic implements CDInterfaceAndEnumASTCDEnumCoCo {
  
  @Override
  public void check(ASTCDEnum node) {
    findCycle(node.getSymbol(), new LinkedHashSet<>(), new LinkedHashSet<>());
  }
  
  /**
   * Recursively performs a depth-first search to find inheritance cycles (diamond-safe).
   */
  private boolean findCycle(TypeSymbol symbol, Set<TypeSymbol> visiting,
      Set<TypeSymbol> fullyVisited) {
    visiting.add(symbol);
    
    for (SymTypeExpression superType : symbol.getSuperTypesList()) {
      TypeSymbol superSymbol = superType.getTypeInfo();
      
      if (visiting.contains(superSymbol)) {
        Log.error(String.format(
            "0xCDC31: The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.",
            CDMill.cDTypeKindPrinter().print(symbol), symbol.getName()));
        return true;
      }
      
      if (!fullyVisited.contains(superSymbol)) {
        if (findCycle(superSymbol, visiting, fullyVisited)) {
          return true;
        }
      }
    }
    
    visiting.remove(symbol);
    fullyVisited.add(symbol);
    return false;
  }
  
}
