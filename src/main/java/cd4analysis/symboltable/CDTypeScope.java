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
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PRIVATE;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PROTECTED;

public class CDTypeScope extends BaseScope {

  public CDTypeScope(Optional<ScopeManipulationApi> enclosingScope) {
    super(enclosingScope, true);
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
      // To resolve symbols of super types, they must at least be protected.
      resolvedSymbol = resolveInSuperTypes(name, kind, PROTECTED);
    }

    return resolvedSymbol;
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    Optional<T> resolvedSymbol = super.resolve(name, kind, modifier);

    if (!resolvedSymbol.isPresent()) {
      resolvedSymbol = resolveInSuperTypes(name, kind, modifier);
    }

    return resolvedSymbol;
  }

  private <T extends Symbol> Optional<T> resolveInSuperTypes(String name, SymbolKind kind, AccessModifier modifier) {
    Optional<T> resolvedSymbol = Optional.absent();

    CDTypeSymbol spanningSymbol = (CDTypeSymbol) getSpanningSymbol().get();

    // resolve in super class
    if (spanningSymbol.getSuperClass().isPresent()) {
      resolvedSymbol = resolveInSuperType(name, kind, modifier, spanningSymbol.getSuperClass().get());
    }

    // resolve in interfaces
    if (!resolvedSymbol.isPresent()) {
      for (CDTypeSymbol interfaze : spanningSymbol.getInterfaces()) {
        resolvedSymbol = resolveInSuperType(name, kind, modifier, interfaze);

        // Stop as soon as symbol is found in an interface. Note that the other option is to
        // search in all interfaces and throw an ambiguous exception if more than one symbol is
        // found. => TODO PN discuss it!
        if (resolvedSymbol.isPresent()) {
          break;
        }
      }
    }

    return resolvedSymbol;
  }

  private <T extends Symbol> Optional<T> resolveInSuperType(String name, SymbolKind kind,
      AccessModifier modifier, CDTypeSymbol superType) {

    Log.trace("Continue in scope of super class " + superType.getName(), CDTypeScope.class.getSimpleName());
    // Private symbols cannot be resolved from the super class. So, the modifier must at
    // least be protected when searching in the super class scope
    AccessModifier modifierForSuperClass = (modifier == PRIVATE) ? PROTECTED : modifier;

    return superType.getSpannedScope().resolve(name, kind, modifierForSuperClass);
  }

  @Override
  public Optional<? extends Symbol> resolve(SymbolPredicate predicate) {
    Optional<? extends Symbol> resolvedSymbol = super.resolve(predicate);

    // TODO PN resolve in super types?

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
