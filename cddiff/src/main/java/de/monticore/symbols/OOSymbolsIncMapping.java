package de.monticore.symbols;

import de.monticore.symbols.basicsymbols.BasicSymbolsIncMapping;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

// NOTE: Could be generated
public interface OOSymbolsIncMapping extends BasicSymbolsIncMapping, OOSymbolsLocalIncMapping {

  /**
   * Returns the scope that is used to symbols of the reference model.
   *
   * @return the scope for reference symbols
   */
  IOOSymbolsScope getReferenceScope();

  /**
   * Returns the scope that is used to symbols of the concrete model.
   *
   * @return the scope for concrete symbols
   */
  IOOSymbolsScope getConcreteScope();

  String computeSymbolKey(ISymbol symbol);

  OOSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol);

  OOSymbolsLocalIncMapping getScopedMapping(IScope scope);

  OOSymbolsBindings getScopedBindings(String contextSymbolKey);

  OOSymbolsBindings getScopedBindings(ISymbol contextSymbol);

  OOSymbolsBindings getScopedBindings(IScope scope);
}
