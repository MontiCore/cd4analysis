/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition.cocos;

import de.monticore.stdefinition._ast.ASTSTDVariable;
import de.monticore.stdefinition._cocos.STDefinitionASTSTDVariableCoCo;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/** Ensures that a variable's fully qualified name does not occur twice. */
public class STDVariableUnique implements STDefinitionASTSTDVariableCoCo {

  @Override
  public void check(ASTSTDVariable node) {
    String name = node.getSymbol().getFullName();
    IBasicSymbolsScope enclosingScope = node.getEnclosingScope();
    List<VariableSymbol> resolved = enclosingScope.resolveVariableMany(name);

    if (resolved.size() != 1) {
      // note: this will Log the same error multiple times,
      // except that they differ by the source position.
      Log.error(
          "0xFDC23 resolved variable "
              + name
              + " "
              + resolved.size()
              + " times, but expected exactly one.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
  }
}
