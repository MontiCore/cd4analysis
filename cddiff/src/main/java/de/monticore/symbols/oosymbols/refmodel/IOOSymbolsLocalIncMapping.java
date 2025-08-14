package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsLocalIncMapping;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Set;

// NOTE: Could be generated
public interface IOOSymbolsLocalIncMapping extends IBasicSymbolsLocalIncMapping {

  Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol);

  Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol);

  Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol);
}
