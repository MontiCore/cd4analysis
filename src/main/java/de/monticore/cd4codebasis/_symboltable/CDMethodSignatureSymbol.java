/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.ArrayList;
import java.util.List;

public class CDMethodSignatureSymbol extends CDMethodSignatureSymbolTOP {
  public CDMethodSignatureSymbol(String name) {
    super(name);
  }

  /**
   * this is overriden, because parameters could be FieldSymbols
   * and there could be no variable symbols present
   */
  @Override
  public List<VariableSymbol> getParameterList() {
    final List<VariableSymbol> variableSymbols = new ArrayList<>(super.getParameterList());
    variableSymbols.addAll(getSpannedScope().getLocalVariableSymbols());
    return variableSymbols;
  }
}
