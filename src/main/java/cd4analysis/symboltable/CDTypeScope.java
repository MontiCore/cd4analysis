/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import com.google.common.base.Optional;
import de.monticore.symboltable.BaseScope;
import de.monticore.symboltable.ScopeManipulationApi;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.SymbolPredicate;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import static com.google.common.base.Preconditions.checkArgument;

public class CDTypeScope extends BaseScope {

  public CDTypeScope(Optional<ScopeManipulationApi> enclosingScope) {
    super(enclosingScope, true);
  }

  public CDTypeScope() {
    this(Optional.absent());
  }

  @Override
  public void setSpanningSymbol(ScopeSpanningSymbol symbol) {
    checkArgument(symbol instanceof CDTypeSymbol);
    super.setSpanningSymbol(symbol);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind) {
    Optional<T> resolvedSymbol = super.resolve(name, kind);


    if (!resolvedSymbol.isPresent()) {
      CDTypeSymbol spanningSymbol = (CDTypeSymbol) getSpanningSymbol().get();
      CDTypeSymbol superClass = spanningSymbol.getSuperClass().orNull();

      if (superClass != null) {
        Log.trace("Continue in scope of super class " + superClass.getName(), CDTypeScope.class.getSimpleName());
        resolvedSymbol = superClass.getSpannedScope().resolve(name, kind);
      }
    }

    return resolvedSymbol;
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    Optional<T> resolvedSymbol = super.resolve(name, kind, modifier);

    if (!resolvedSymbol.isPresent()) {
      CDTypeSymbol spanningSymbol = (CDTypeSymbol) getSpanningSymbol().get();
      CDTypeSymbol superClass = spanningSymbol.getSuperClass().orNull();

      if (superClass != null) {
        Log.trace("Continue in scope of super class " + superClass.getName(), CDTypeScope.class.getSimpleName());
        resolvedSymbol = superClass.getSpannedScope().resolve(name, kind, modifier);
      }
    }

    return resolvedSymbol;
  }

  @Override
  public Optional<? extends Symbol> resolve(SymbolPredicate predicate) {
    Optional<? extends Symbol> resolvedSymbol = super.resolve(predicate);


    if (!resolvedSymbol.isPresent()) {
      CDTypeSymbol spanningSymbol = (CDTypeSymbol) getSpanningSymbol().get();
      CDTypeSymbol superClass = spanningSymbol.getSuperClass().orNull();

      if (superClass != null) {
        Log.trace("Continue in scope of super class " + superClass.getName(), CDTypeScope.class.getSimpleName());
        resolvedSymbol = superClass.getSpannedScope().resolve(predicate);
      }
    }

    return resolvedSymbol;
  }
}