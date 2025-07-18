package de.monticore.symbols.basicsymbols;

import de.monticore.refmodels.IncMappingUtils;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Set;

// NOTE: Can be generated.
public class BasicSymbolsRestrictedIncMappingTOP implements BasicSymbolsLocalIncMapping {

  protected final BasicSymbolsLocalIncMapping originalMapping;
  protected final BasicSymbolsBindings bindings;

  public BasicSymbolsRestrictedIncMappingTOP(BasicSymbolsLocalIncMapping originalMapping, BasicSymbolsBindings bindings) {
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
