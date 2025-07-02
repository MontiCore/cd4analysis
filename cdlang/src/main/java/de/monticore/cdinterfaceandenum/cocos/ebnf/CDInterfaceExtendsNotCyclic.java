/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Checks that there are no inheritance cycles.
 */
public class CDInterfaceExtendsNotCyclic implements CDInterfaceAndEnumASTCDInterfaceCoCo {
  
  @Override
  public void check(ASTCDInterface node) {
    findCycle(node.getSymbol(), new HashSet<>(), new HashSet<>());
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
            "0xCDC32: The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.",
            CDMill.cDTypeKindPrinter().print(symbol), symbol.getName()));
        return true;
      }
      
      if (!fullyVisited.contains(superSymbol)) {
        if (findCycle(superSymbol, visiting, fullyVisited)) {
          return true;
        }
      }
    }
    
    // Backtracking
    visiting.remove(symbol);
    fullyVisited.add(symbol);
    
    return false;
  }
  
}
