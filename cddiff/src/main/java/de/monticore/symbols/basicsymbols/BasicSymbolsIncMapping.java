package de.monticore.symbols.basicsymbols;

import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

public interface BasicSymbolsIncMapping extends BasicSymbolsLocalIncMapping {

  String computeSymbolKey(ISymbol symbol);

  //BasicSymbolsLocalIncMapping getFullMapping(); // TODO extend local mapping vs getter?

  BasicSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol);

  BasicSymbolsLocalIncMapping getScopedMapping(IScope scope);

  BasicSymbolsBindings getScopedBindings(String contextSymbolKey);

  BasicSymbolsBindings getScopedBindings(ISymbol contextSymbol);

  BasicSymbolsBindings getScopedBindings(IScope scope);
}
