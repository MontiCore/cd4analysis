package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsIncMapping;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

// NOTE: Could be generated
public interface IOOSymbolsIncMapping extends IBasicSymbolsIncMapping, IOOSymbolsLocalIncMapping {

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

  IOOSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol);

  IOOSymbolsLocalIncMapping getScopedMapping(IScope scope);

  IOOSymbolsBindings getScopedBindings(String contextSymbolKey);

  IOOSymbolsBindings getScopedBindings(ISymbol contextSymbol);

  IOOSymbolsBindings getScopedBindings(IScope scope);
}
