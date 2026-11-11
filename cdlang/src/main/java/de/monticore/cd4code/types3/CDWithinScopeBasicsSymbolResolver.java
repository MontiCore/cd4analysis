/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.types3;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.Predicate;

// quick-fix until resolving issues for CD symbols are fixed
@Deprecated
public class CDWithinScopeBasicsSymbolResolver extends OOWithinScopeBasicSymbolsResolver {
  
  private static final String LOG_NAME = CDWithinScopeBasicsSymbolResolver.class.getSimpleName();
  
  public static void init() {
    Log.trace(() -> "Initialize CDWithinScopeBasicsSymbolResolver as within scope resolver",
        LOG_NAME);
    setDelegate(new CDWithinScopeBasicsSymbolResolver());
  }
  
  @Override
  protected Optional<VariableSymbol> resolveVariable(IBasicSymbolsScope enclosingScope, String name,
      AccessModifier accessModifier, Predicate<VariableSymbol> predicate) {
    // we do not log an error if multiple symbols are found (avoid redundant error logging)
    return enclosingScope.resolveVariableMany(name, accessModifier, predicate).stream().findFirst();
  }
  
  @Override
  protected Optional<TypeSymbol> resolveType(IBasicSymbolsScope enclosingScope, String name,
      AccessModifier accessModifier, Predicate<TypeSymbol> typeSymbolPredicate) {
    // we do not log an error if multiple symbols are found (avoid redundant error logging)
    // also we do not resolve type var symbols as type symbols
    return enclosingScope.resolveTypeMany(name, accessModifier, typeSymbolPredicate.and(
        v -> !(v instanceof TypeVarSymbol))).stream().findFirst();
  }
  
  @Override
  protected Optional<TypeVarSymbol> resolveTypeVar(IBasicSymbolsScope enclosingScope, String name,
      AccessModifier accessModifier, Predicate<TypeVarSymbol> predicate) {
    // we do not log an error if multiple symbols are found (avoid redundant error logging)
    return enclosingScope.resolveTypeVarMany(name, accessModifier, predicate).stream().findFirst();
  }
  
}
