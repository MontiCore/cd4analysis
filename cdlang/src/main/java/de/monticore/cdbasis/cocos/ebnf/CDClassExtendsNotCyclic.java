/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;

/** Checks that there are no cycles in the class hierarchy. */
public class CDClassExtendsNotCyclic implements CDBasisASTCDClassCoCo {

  /** @param node class to check. */
  @Override
  public void check(ASTCDClass node) {
    findInheritanceCycleDepthFirst(node, node.getSymbol(), new HashSet<>());
  }

  protected void findInheritanceCycleDepthFirst(
      ASTCDClass origin, TypeSymbol next, Set<String> visitedTypes) {
    if (visitedTypes.contains(next.getFullName())) {
      Log.error(
          String.format(
              "0xCDC07: The %s %s introduces an inheritance cycle for class %s. Inheritance must not be cyclic.",
              CDMill.cDTypeKindPrinter(true).print(next),
              next.getName(),
              origin.getSymbol().getFullName()),
          origin.get_SourcePositionStart());
      return;
    }
    visitedTypes.add(next.getFullName());
    next.getSuperClassesOnly()
        .forEach(
            s -> {
              if (s.hasTypeInfo()) {
                findInheritanceCycleDepthFirst(
                    origin, s.getTypeInfo(), new HashSet<>(visitedTypes));
              } else {
                Log.error(
                    "0xE822B: Can not find symbol for superclass " + s.print(),
                    origin.get_SourcePositionStart(),
                    origin.get_SourcePositionEnd());
              }
            });
  }
}
