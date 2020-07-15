/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.Arrays;
import java.util.List;

/**
 * contains all types, which are basic Java types
 */
public class BuiltInTypes {
  public static final List<String> PRIMITIVE_TYPES = Arrays.asList("char", "int", "double", "float", "long", "boolean");
  public static final List<String> OBJECT_TYPES = Arrays.asList("Character", "Integer", "Double", "Float", "Long", "Boolean", "String");
  public static final List<String> UTIL_TYPES = Arrays.asList("Date", "List", "Optional", "Set", "Map");

  public static void addBuiltInTypes(ITypeSymbolsScope utilTypesScope, List<String> utilTypes, boolean isClass) {
    utilTypes
        .forEach(t -> {
          final TypeSymbolsScope scope = TypeSymbolsMill.typeSymbolsScopeBuilder().build();
          final OOTypeSymbol symbol = TypeSymbolsMill
              .oOTypeSymbolBuilder()
              .setName(t)
              .setEnclosingScope(utilTypesScope)
              .setSpannedScope(scope)
              .setIsPublic(true)
              .setIsClass(isClass)
              .build();

          // TODO SVa: remove when Builder of symbols are fixed
          symbol.setIsPublic(true);
          symbol.setIsClass(isClass);
          symbol.setSpannedScope(scope);

          utilTypesScope.add(
              symbol);
        });
  }
}
