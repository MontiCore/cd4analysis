/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.cocos;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symtabdefinition._ast.ASTSTDVariable;
import de.monticore.symtabdefinition._cocos.SymTabDefinitionASTSTDVariableCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/** Ensures that a variable's fully qualified name does not occur twice. */
public class STDVariableUnique implements SymTabDefinitionASTSTDVariableCoCo {

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
