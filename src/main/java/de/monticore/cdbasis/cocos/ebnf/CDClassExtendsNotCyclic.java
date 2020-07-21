/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Checks that there are no cycles in the the class hierarchy.
 */
public class CDClassExtendsNotCyclic implements CDBasisASTCDClassCoCo {

  /**
   * @param node class to check.
   */
  @Override
  public void check(ASTCDClass node) {
    Set<String> visitedTypes = new HashSet<>();
    Stack<OOTypeSymbol> typesToVisit = new Stack<>();

    typesToVisit.push(node.getSymbol());

    while (!typesToVisit.isEmpty()) {
      final OOTypeSymbol nextSymbol = typesToVisit.pop();
      if (visitedTypes.contains(nextSymbol.getName())) {
        Log.error(String.format(
            "0xCDC07: The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.", CDMill.cDTypeKindPrinter().print(nextSymbol),
            nextSymbol.getName()));
        return;
      }
      visitedTypes.add(nextSymbol.getName());
      nextSymbol.getSuperClassesOnly().forEach(s -> typesToVisit.push(s.getTypeInfo()));
    }
  }
}
