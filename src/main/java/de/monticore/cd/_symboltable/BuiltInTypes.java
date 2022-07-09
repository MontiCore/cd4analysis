/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.class2mc.Class2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * contains all types, which are basic Java types
 */
public class BuiltInTypes {
  public static void addBuiltInTypes(IOOSymbolsGlobalScope globalScope) {
    if (globalScope.getTypeSymbols().isEmpty()) {
      BasicSymbolsMill.initializePrimitives();

      setUpCD4AType("List", "T");
      setUpCD4AType("Optional", "T");
      setUpCD4AType("Set", "T");
      setUpCD4AType("Map", "K", "V");

      final Class2MCResolver resolver = new Class2MCResolver();
      globalScope.addAdaptedTypeSymbolResolver(
          (boolean foundSymbols, String name, de.monticore.symboltable.modifiers.AccessModifier modifier, java.util.function.Predicate<de.monticore.symbols.basicsymbols._symboltable.TypeSymbol> predicate) ->
              new ArrayList<>(resolver.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test)));
      globalScope.addAdaptedOOTypeSymbolResolver((boolean foundSymbols, String name, de.monticore.symboltable.modifiers.AccessModifier modifier, java.util.function.Predicate<de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol> predicate) ->
      new ArrayList<>(resolver.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test)));

    }
  }

  protected static void setUpCD4AType(String name, String ... args) {
    ICD4AnalysisScope spanningScope = CD4AnalysisMill.scope();
    OOTypeSymbol genType = CD4AnalysisMill.oOTypeSymbolBuilder()
      .setSpannedScope(spanningScope)
      .setName(name)
      .setEnclosingScope(CD4AnalysisMill.globalScope())
      .build();
    CD4AnalysisMill.globalScope().add(genType);
    Arrays.stream(args).forEach(a ->
      spanningScope.add(CD4AnalysisMill.typeVarSymbolBuilder()
        .setName(a)
        .setSpannedScope(CD4AnalysisMill.scope())
        .setEnclosingScope(spanningScope)
        .build()));
  }

}
