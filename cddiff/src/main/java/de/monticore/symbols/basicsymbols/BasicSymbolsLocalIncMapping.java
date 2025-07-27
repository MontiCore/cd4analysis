package de.monticore.symbols.basicsymbols;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Set;

// NOTE: Could be generated
/**
 * Represents a <i>local</i> incarnation mapping for the symbols defined by the "BasicSymbols"
 * language.<br>
 * <i>Local mapping</i> means that the incarnations are only valid in a certain scope of model,
 * or a certain context, e.g. during reference artifact adaptation.<br>
 * The mapping provides incarnations for the following symbols:
 * <ul>
 *   <li>{@link TypeSymbol}</li>
 *   <li>{@link VariableSymbol}</li>
 *   <li>{@link FunctionSymbol}</li>
 * </ul>
 */
public interface BasicSymbolsLocalIncMapping {

  Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol);

  Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol);

  Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol);
}
