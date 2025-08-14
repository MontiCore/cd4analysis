package de.monticore.symbols.basicsymbols.refmodel;

import de.monticore.refmodel.IncMappingUtils;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Set;

// NOTE: Can be generated.
public class BasicSymbolsRestrictedIncMapping implements IBasicSymbolsLocalIncMapping {

  protected final IBasicSymbolsLocalIncMapping originalMapping;
  protected final IBasicSymbolsBindings bindings;

  public BasicSymbolsRestrictedIncMapping(IBasicSymbolsLocalIncMapping originalMapping, IBasicSymbolsBindings bindings) {
    this.originalMapping = originalMapping;
    this.bindings = bindings;
  }

  @Override
  public Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
        originalMapping::getIncarnations,
        bindings::getBinding,
        bindings::isConflictingTypeBinding,
        typeSymbol
    );
  }

  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            bindings::isConflictingVariableBinding,
            variableSymbol
    );
  }

  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            bindings::isConflictingFunctionBinding,
            functionSymbol
    );
  }
}
