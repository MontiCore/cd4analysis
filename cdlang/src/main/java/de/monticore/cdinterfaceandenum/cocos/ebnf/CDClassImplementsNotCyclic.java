/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/** Checks that there are no cycles in the the class hierarchy. */
public class CDClassImplementsNotCyclic implements CDBasisASTCDClassCoCo {

  // TODO SVa: braucht man das überhaupt, wird das nicht schon im cycle von interfaces geprüft?

  /** @param node class to check. */
  @Override
  public void check(ASTCDClass node) {
      findCycle(node.getSymbol(), new HashSet<>(), new HashSet<>());
  }

  /**
   * Recursively performs a depth-first search to find inheritance cycles (diamond-safe).
   */
  private boolean findCycle(TypeSymbol symbol, Set<TypeSymbol> visiting, Set<TypeSymbol> fullyVisited) {
    visiting.add(symbol);

    for (SymTypeExpression superType : symbol.getSuperTypesList()) {
      TypeSymbol superSymbol = superType.getTypeInfo();

      if (visiting.contains(superSymbol)) {
        Log.error(String.format(
          "0xCDC09: The %s %s introduces an inheritance cycle. Inheritance must not be cyclic.",
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
