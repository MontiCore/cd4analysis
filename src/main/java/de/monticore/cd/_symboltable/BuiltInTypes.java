/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.class2mc.Class2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;

import java.util.ArrayList;

/**
 * contains all types, which are basic Java types
 */
public class BuiltInTypes {
  public static void addBuiltInTypes(IOOSymbolsGlobalScope globalScope) {
    if (globalScope.getTypeSymbols().isEmpty()) {
      BasicSymbolsMill.initializePrimitives();

      final Class2MCResolver resolver = new Class2MCResolver();
      globalScope.addAdaptedTypeSymbolResolver(
          (boolean foundSymbols, String name, de.monticore.symboltable.modifiers.AccessModifier modifier, java.util.function.Predicate<de.monticore.symbols.basicsymbols._symboltable.TypeSymbol> predicate) ->
              new ArrayList<>(resolver.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test)));
      globalScope.addAdaptedOOTypeSymbolResolver((boolean foundSymbols, String name, de.monticore.symboltable.modifiers.AccessModifier modifier, java.util.function.Predicate<de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol> predicate) ->
      new ArrayList<>(resolver.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test)));
    }
  }
}
