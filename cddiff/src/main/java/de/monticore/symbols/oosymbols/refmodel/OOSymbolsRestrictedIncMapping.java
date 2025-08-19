/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.refmodel.IncMappingUtils;
import de.monticore.symbols.basicsymbols.refmodel.BasicSymbolsRestrictedIncMapping;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Set;

// NOTE: Could be generated
public class OOSymbolsRestrictedIncMapping implements IOOSymbolsLocalIncMapping {
  
  protected final IOOSymbolsLocalIncMapping originalMapping;
  protected final IOOSymbolsBindings bindings;
  protected final BasicSymbolsRestrictedIncMapping basicSymbolsRestrictedIncMapping;
  
  public OOSymbolsRestrictedIncMapping(IOOSymbolsLocalIncMapping originalMapping,
      IOOSymbolsBindings bindings) {
    this.originalMapping = originalMapping;
    this.bindings = bindings;
    this.basicSymbolsRestrictedIncMapping = new BasicSymbolsRestrictedIncMapping(originalMapping,
        bindings);
  }
  
  @Override
  public Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol) {
    return IncMappingUtils.getRestrictIncarnations(originalMapping::getIncarnations,
        bindings::getBinding, bindings::isConflictingOOTypeBinding, typeSymbol);
  }
  
  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return IncMappingUtils.getRestrictIncarnations(originalMapping::getIncarnations,
        bindings::getBinding, bindings::isConflictingFieldBinding, fieldSymbol);
  }
  
  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return IncMappingUtils.getRestrictIncarnations(originalMapping::getIncarnations,
        bindings::getBinding, bindings::isConflictingMethodBinding, methodSymbol);
  }
  
  // ########## Delegate to BasicSymbolsRestrictedIncMapping ##########
  
  @Override
  public Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol) {
    return basicSymbolsRestrictedIncMapping.getIncarnations(typeSymbol);
  }
  
  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    return basicSymbolsRestrictedIncMapping.getIncarnations(variableSymbol);
  }
  
  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    return basicSymbolsRestrictedIncMapping.getIncarnations(functionSymbol);
  }
  
}
