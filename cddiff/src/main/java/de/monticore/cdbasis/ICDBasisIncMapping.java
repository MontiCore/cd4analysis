package de.monticore.cdbasis;

import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.IOOSymbolsLocalIncMapping;

import java.util.Set;

public interface ICDBasisIncMapping extends IOOSymbolsLocalIncMapping {

  Set<CDTypeSymbol> getIncarnations(CDTypeSymbol cdTypeSymbol);

  Set<CDMethodSignatureSymbol> getIncarnations(CDMethodSignatureSymbol cdMethodSignatureSymbol);
}
