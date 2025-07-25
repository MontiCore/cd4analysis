package de.monticore.symbols.basicsymbols;

import de.monticore.refmodels.IncMappingUtils;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Set;

public class BasicSymbolsRestrictedIncMapping implements BasicSymbolsLocalIncMapping {

  private final BasicSymbolsLocalIncMapping originalMapping;
  private final BasicSymbolsBindings bindings;

  public BasicSymbolsRestrictedIncMapping(BasicSymbolsLocalIncMapping originalMapping, BasicSymbolsBindings bindings) {
    this.originalMapping = originalMapping;
    this.bindings = bindings;
  }

  @Override
  public Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
        originalMapping::getIncarnations,
        bindings::getBinding,
        typeSymbol
    );
  }

  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            variableSymbol
    );
  }

  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            functionSymbol
    );
  }
}
