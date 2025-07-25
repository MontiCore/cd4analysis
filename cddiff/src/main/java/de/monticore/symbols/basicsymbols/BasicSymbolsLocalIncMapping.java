package de.monticore.symbols.basicsymbols;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Set;

public interface BasicSymbolsLocalIncMapping {

  Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol);

  Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol);

  Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol);
}
