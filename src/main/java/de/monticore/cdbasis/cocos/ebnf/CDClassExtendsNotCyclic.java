/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/** Checks that there are no cycles in the the class hierarchy. */
public class CDClassExtendsNotCyclic implements CDBasisASTCDClassCoCo {

  /** @param node class to check. */
  @Override
  public void check(ASTCDClass node) {
    Set<String> visitedTypes = new HashSet<>();
    Stack<TypeSymbol> typesToVisit = new Stack<>();

    typesToVisit.push(node.getSymbol());

    while (!typesToVisit.isEmpty()) {
      final TypeSymbol nextSymbol = typesToVisit.pop();
      if (visitedTypes.contains(nextSymbol.getFullName())) {
        Log.error(
            String.format(
                "0xCDC07: The %s %s introduces an inheritance cycle for class %s. Inheritance must not be cyclic.",
                CDMill.cDTypeKindPrinter(true).print(nextSymbol),
                nextSymbol.getName(),
                node.getSymbol().getFullName()),
            node.get_SourcePositionStart());
        return;
      }
      visitedTypes.add(nextSymbol.getFullName());
      nextSymbol.getSuperClassesOnly().forEach(s -> {
        if(s.hasTypeInfo()) {
          typesToVisit.push(s.getTypeInfo());
        }else{
          Log.error("0xE822B: Can not find symbol for superclass " + s.print(),
            node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      });
    }
  }
}
