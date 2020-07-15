/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Checks that there are no cycles in the the class hierarchy.
 */
public class CDClassImplementsNotCyclic implements CDBasisASTCDClassCoCo {

  // TODO SVa: braucht man das überhaupt, wird das nicht schon im cycle von interfaces geprüft?

  /**
   * @param node class to check.
   */
  @Override
  public void check(ASTCDClass node) {
    Set<String> visitedTypes = new HashSet<>();
    Stack<OOTypeSymbol> typesToVisit = new Stack<>();

    typesToVisit.push(node.getSymbol());

    while (!typesToVisit.isEmpty()) {
      final OOTypeSymbol symbol = typesToVisit.pop();
      if (visitedTypes.contains(symbol.getName())) {
        Log.error(String.format(
            "0xCDC09: The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.", CDMill.cDTypeKindPrinter().print(symbol),
            symbol.getName()));
        return;
      }
      visitedTypes.add(symbol.getName());
      symbol.getInterfaceList().forEach(s -> typesToVisit.push(s.getTypeInfo()));
    }
  }
}
