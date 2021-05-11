/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.class2mc.Java2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.types.check.ISynthesize;

import java.util.Optional;

/**
 * contains all types, which are basic Java types
 */
public class BuiltInTypes {
  public static void addBuiltInTypes(IOOSymbolsGlobalScope globalScope) {
    if (globalScope.getTypeSymbols().isEmpty()) {
      BasicSymbolsMill.initializePrimitives();

      final Java2MCResolver javaTypeResolver = new Java2MCResolver(globalScope);
      globalScope.addAdaptedOOTypeSymbolResolver(javaTypeResolver);
      globalScope.addAdaptedTypeSymbolResolver(javaTypeResolver);
    }
  }
}
