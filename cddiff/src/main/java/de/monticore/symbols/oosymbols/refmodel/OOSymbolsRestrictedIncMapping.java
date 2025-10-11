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
import java.util.stream.Collectors;

// NOTE: Could be generated
public class OOSymbolsRestrictedIncMapping implements IOOSymbolsLocalIncMapping {
  
  protected final IOOSymbolsIncMapping originalMapping;
  protected final IOOSymbolsBindings bindings;
  protected final BasicSymbolsRestrictedIncMapping basicSymbolsRestrictedIncMapping;
  
  public OOSymbolsRestrictedIncMapping(IOOSymbolsIncMapping originalMapping,
      IOOSymbolsBindings bindings) {
    this.originalMapping = originalMapping;
    this.bindings = bindings;
    this.basicSymbolsRestrictedIncMapping = new BasicSymbolsRestrictedIncMapping(originalMapping,
        bindings);
  }
  
  @Override
  public Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol) {
    return IncMappingUtils.getRestrictIncarnations(originalMapping::getIncarnations,
        bindings::getBinding, bindings::isConflictingOOTypeBinding, typeSymbol).stream().filter(
            incarnation -> {
              /*
               * This filtering fulfills two purposes:
               * 1. a workaround for adaptation during CD concretization:
               *    We need this additional filtering because there may be bindings attached to
               *    the incarnation that may conflict with the current bindings, even if the
               *    incarnation itself does not conflict! -> this is what we solve in reference
               *    adaptation by creating variants and dropping them if they lead to conflicts.
               *    --> Therefore, as explained before, we should reuse the adaptation framework
               *    during CD concretization as well to properly construct all valid variants,
               *    e.g., of an ASTCDMethod in case of multi incarnation.
               * 2. it filters out incarnations early, which would be dropped anyway later on
               *    by the adaptation framework.
               */
              return !originalMapping.getScopedBindings(incarnation).isConflicting(bindings);
            }).collect(Collectors.toSet());
  }
  
  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return IncMappingUtils.getRestrictIncarnations(originalMapping::getIncarnations,
        bindings::getBinding, bindings::isConflictingFieldBinding, fieldSymbol).stream().filter(
            incarnation -> !originalMapping.getScopedBindings(incarnation).isConflicting(bindings))
        .collect(Collectors.toSet());
  }
  
  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return IncMappingUtils.getRestrictIncarnations(originalMapping::getIncarnations,
        bindings::getBinding, bindings::isConflictingMethodBinding, methodSymbol).stream().filter(
            incarnation -> !originalMapping.getScopedBindings(incarnation).isConflicting(bindings))
        .collect(Collectors.toSet());
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
