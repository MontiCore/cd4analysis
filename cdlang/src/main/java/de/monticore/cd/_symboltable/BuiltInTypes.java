/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import java.util.ArrayList;
import java.util.Arrays;

/** contains all types, which are basic Java types */
public class BuiltInTypes {
  public static void addBuiltInTypes(IOOSymbolsGlobalScope globalScope) {
    addBuiltInTypes(globalScope, true);
  }

  public static void addBuiltInTypes(
      IOOSymbolsGlobalScope globalScope, boolean withCollectionType) {
    if (globalScope.getTypeSymbols().isEmpty()) {
      BasicSymbolsMill.initializePrimitives();

      if (withCollectionType) {
        setUpCD4AType(globalScope, "List", "T");
        setUpCD4AType(globalScope, "Optional", "T");
        setUpCD4AType(globalScope, "Set", "T");
        setUpCD4AType(globalScope, "Map", "K", "V");
      }

      final OOClass2MCResolver resolver = new OOClass2MCResolver();
      globalScope.addAdaptedTypeSymbolResolver(
          (boolean foundSymbols,
              String name,
              de.monticore.symboltable.modifiers.AccessModifier modifier,
              java.util.function.Predicate<
                      de.monticore.symbols.basicsymbols._symboltable.TypeSymbol>
                  predicate) ->
              new ArrayList<>(
                  resolver.resolveAdaptedOOTypeSymbol(
                      foundSymbols, name, modifier, predicate::test)));
      globalScope.addAdaptedOOTypeSymbolResolver(
          (boolean foundSymbols,
              String name,
              de.monticore.symboltable.modifiers.AccessModifier modifier,
              java.util.function.Predicate<de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol>
                  predicate) ->
              new ArrayList<>(
                  resolver.resolveAdaptedOOTypeSymbol(
                      foundSymbols, name, modifier, predicate::test)));
    }
  }

  protected static void setUpCD4AType(
      IOOSymbolsGlobalScope globalScope, String name, String... args) {
    IOOSymbolsScope spanningScope = OOSymbolsMill.scope();
    OOTypeSymbol genType =
        OOSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(spanningScope)
            .setName(name)
            .setEnclosingScope(globalScope)
            .build();
    globalScope.add(genType);
    Arrays.stream(args)
        .forEach(
            a ->
                spanningScope.add(
                    OOSymbolsMill.typeVarSymbolBuilder()
                        .setName(a)
                        .setSpannedScope(OOSymbolsMill.scope())
                        .setEnclosingScope(spanningScope)
                        .build()));
  }
}
