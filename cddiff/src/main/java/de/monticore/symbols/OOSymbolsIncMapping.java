package de.monticore.symbols;

import de.monticore.symbols.basicsymbols.BasicSymbolsIncMapping;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

// NOTE: Could be generated
public interface OOSymbolsIncMapping extends BasicSymbolsIncMapping, OOSymbolsLocalIncMapping {

  String computeSymbolKey(ISymbol symbol);

  //OOSymbolsLocalIncMapping getFullMapping(); // TODO extend local mapping vs getter?

  OOSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol);

  OOSymbolsLocalIncMapping getScopedMapping(IScope scope);

  OOSymbolsBindings getScopedBindings(String contextSymbolKey);

  OOSymbolsBindings getScopedBindings(ISymbol contextSymbol);

  OOSymbolsBindings getScopedBindings(IScope scope);
}
