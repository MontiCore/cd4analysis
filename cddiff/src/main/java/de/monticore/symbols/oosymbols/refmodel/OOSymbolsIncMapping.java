package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.cdconformance.inc.DefaultCDIncarnationBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OOSymbolsIncMapping implements IOOSymbolsIncMapping {

  private static final String LOG_NAME = DefaultCDIncarnationBindings.class.getName();

  private final IOOSymbolsLocalIncMapping globalMapping;
  private final IOOSymbolsScope referenceScope;
  private final IOOSymbolsScope concreteScope;

  /**
   * The mapping of type incarnations for a specific scope. The key is the name of the concrete type
   * spanning the scope, and the value is the specific incarnation mapping for that scope. If a type
   * is not present in the mapping, it means that the mapping needs to be resolved in the parent
   * scope.
   */
  private final Map<String, IOOSymbolsBindings> ooSymbolsBindings = new HashMap<>();

  public OOSymbolsIncMapping(IOOSymbolsLocalIncMapping globalMapping, IOOSymbolsScope referenceScope, IOOSymbolsScope concreteScope) {
    this.globalMapping = globalMapping;
    this.referenceScope = referenceScope;
    this.concreteScope = concreteScope;
  }

  @Override
  public IOOSymbolsScope getReferenceScope() {
    return referenceScope;
  }

  @Override
  public IOOSymbolsScope getConcreteScope() {
    return concreteScope;
  }

  @Override
  public String computeSymbolKey(ISymbol symbol) {
    return symbol.getFullName();
  }

  @Override
  public IOOSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol) {
    return new OOSymbolsRestrictedIncMapping(globalMapping, this.getScopedBindings(contextSymbol));
  }

  @Override
  public IOOSymbolsLocalIncMapping getScopedMapping(IScope scope) {
    return new OOSymbolsRestrictedIncMapping(globalMapping, this.getScopedBindings(scope));
  }

  @Override
  public IOOSymbolsBindings getScopedBindings(ISymbol contextSymbol) {
    String symbolKey = computeSymbolKey(contextSymbol);
    Log.debug("Checking for type incarnations in context of symbol: " + symbolKey, LOG_NAME);
    IOOSymbolsBindings localBindings = getLocalOnlyBindings(symbolKey);
    return new OOSymbolScopedBindings(symbolKey, localBindings,
            this.getScopedBindings(contextSymbol.getEnclosingScope()));
  }

  @Override
  public IOOSymbolsBindings getLocalOnlyBindings(String contextSymbolKey) {
    IOOSymbolsBindings localBindings = ooSymbolsBindings.get(contextSymbolKey);
    if (localBindings == null) {
      // create a new empty set of bindings
      localBindings = new OOSymbolsBindings();
      ooSymbolsBindings.put(contextSymbolKey, localBindings);
    }
    return localBindings;
  }

  @Override
  public IOOSymbolsBindings getScopedBindings(IScope scope) {
    if (scope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return new OOSymbolsBindings();
    }
    if (!scope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return this.getScopedBindings(scope.getEnclosingScope());
    }
    // collect all bindings starting from the spanning symbol of the scope
    return this.getScopedBindings(scope.getSpanningSymbol());
  }

  @Override
  public Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol) {
    return globalMapping.getIncarnations(typeSymbol);
  }

  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return globalMapping.getIncarnations(fieldSymbol);
  }

  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return globalMapping.getIncarnations(methodSymbol);
  }

  @Override
  public Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol) {
    return globalMapping.getIncarnations(typeSymbol);
  }

  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    return globalMapping.getIncarnations(variableSymbol);
  }

  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    return globalMapping.getIncarnations(functionSymbol);
  }
}
