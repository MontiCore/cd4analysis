/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.class2mc.Java2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.ISynthesize;

import java.util.Optional;

/**
 * contains all types, which are basic Java types
 */
public class BuiltInTypes {
  public static void addBuiltInTypes(ICDBasisGlobalScope globalScope, Optional<ISynthesize> synthesizeSymType) {
    if (globalScope.getTypeSymbols().isEmpty()) {
      BasicSymbolsMill.initializePrimitives();

      final Java2MCResolver javaTypeResolver = new Java2MCResolver(globalScope);
      synthesizeSymType.ifPresent(javaTypeResolver::setSynthesizeSymType);
      globalScope.addAdaptedOOTypeSymbolResolver(javaTypeResolver);
      globalScope.addAdaptedTypeSymbolResolver(javaTypeResolver);
    }
  }
}
