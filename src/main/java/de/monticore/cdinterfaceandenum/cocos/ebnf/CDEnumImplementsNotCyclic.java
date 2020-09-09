/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Checks that there are no inheritance cycles.
 */
public class CDEnumImplementsNotCyclic
    implements CDInterfaceAndEnumASTCDEnumCoCo {

  /**
   *
   */
  @Override
  public void check(ASTCDEnum node) {
    Set<String> visitedTypes = new HashSet<>();
    Stack<TypeSymbol> typesToVisit = new Stack<>();

    typesToVisit.push(node.getSymbol());

    while (!typesToVisit.isEmpty()) {
      final TypeSymbol symbol = typesToVisit.pop();
      if (visitedTypes.contains(symbol.getName())) {
        Log.error(String.format(
            "0xCDC31: The %s %s introduces an inheritance cycle. Inheritance may not be cyclic.", CDMill.cDTypeKindPrinter().print(symbol),
            symbol.getName()));
        return;
      }
      visitedTypes.add(symbol.getName());
      symbol.getInterfaceList().forEach(s -> typesToVisit.push(s.getTypeInfo()));
    }
  }
}
