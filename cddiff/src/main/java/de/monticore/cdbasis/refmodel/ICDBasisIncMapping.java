/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.refmodel;

import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsLocalIncMapping;

import java.util.Set;

public interface ICDBasisIncMapping extends IOOSymbolsLocalIncMapping {
  
  Set<CDTypeSymbol> getIncarnations(CDTypeSymbol cdTypeSymbol);
  
  Set<CDMethodSignatureSymbol> getIncarnations(CDMethodSignatureSymbol cdMethodSignatureSymbol);
  
}
