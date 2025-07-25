package de.monticore.symbols;

import de.monticore.refmodels.IncMappingUtils;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OOSymbolsRestrictedIncMapping implements OOSymbolsLocalIncMapping {

  private final OOSymbolsLocalIncMapping originalMapping;
  private final OOSymbolsBindings bindings;

  public OOSymbolsRestrictedIncMapping(OOSymbolsLocalIncMapping originalMapping, OOSymbolsBindings bindings) {
    this.originalMapping = originalMapping;
    this.bindings = bindings;
  }

  @Override
  public Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            typeSymbol
    );
  }

  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            fieldSymbol
    );
  }

  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            methodSymbol
    );
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
    IBasicSymbolsScope enclosingScope = variableSymbol.getEnclosingScope();
    Optional<TypeSymbol> declaringRefType = Optional.empty();
    Set<TypeSymbol> declaringTypeIncs = null;
    if (enclosingScope.isPresentSpanningSymbol()) {
      ISymbol spanningSymbol = enclosingScope.getSpanningSymbol();
      if (spanningSymbol instanceof TypeSymbol) {
        declaringRefType = Optional.of((TypeSymbol) spanningSymbol);
        declaringTypeIncs = getIncarnations((TypeSymbol) spanningSymbol);
      }
    }
    final Optional<TypeSymbol> finalDeclaringRefType = declaringRefType;
    final Set<TypeSymbol> declaringTypeIncsFinal = declaringTypeIncs;
    return IncMappingUtils.getRestrictIncarnations(
            originalMapping::getIncarnations,
            bindings::getBinding,
            variableSymbol
    ).stream().filter(variable -> {
      if (finalDeclaringRefType.isPresent()) {
        // assumption: if reference variable has declaring type, then all incarnations also have a declaring type
        TypeSymbol concDeclaringType = (TypeSymbol) variable.getEnclosingScope().getSpanningSymbol();
        return declaringTypeIncsFinal.contains(concDeclaringType);
      }
      return true;
    }).collect(Collectors.toSet());
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
