/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Arrays;
import java.util.List;

/**
 * contains all types, which are basic Java types
 */
public class BuiltInTypes {
  public static final String SCOPE_NAME = "BuiltInTypes";
  public static final List<String> PRIMITIVE_TYPES = Arrays.asList("char", "int", "double", "float", "long", "boolean", "short", "byte", "void");
  public static final List<String> OBJECT_TYPES = Arrays.asList("Character", "Integer", "Double", "Float", "Long", "Boolean", "Short", "Byte", "Void", "Number", "String");
  public static final List<String> UTIL_TYPES = Arrays.asList("Date", "List", "Optional", "Set", "Map");

  public static void addBuiltInOOTypes(IOOSymbolsScope utilTypesScope, List<String> utilTypes, boolean isClass) {
    utilTypes
        .forEach(t -> {
          final IOOSymbolsScope scope = OOSymbolsMill.scope();
          final OOTypeSymbol typeSymbol = OOSymbolsMill
              .oOTypeSymbolBuilder()
              .setName(t)
              .setEnclosingScope(utilTypesScope)
              .setSpannedScope(scope)
              .setIsPublic(true)
              .setIsClass(isClass)
              .build();
          utilTypesScope.add(typeSymbol);
          utilTypesScope.add((TypeSymbol)typeSymbol);
        });
  }

  public static void addBuiltInTypes(IOOSymbolsScope utilTypesScope, List<String> utilTypes) {
    utilTypes
        .forEach(t -> {
          final IBasicSymbolsScope scope = BasicSymbolsMill.scope();
          utilTypesScope.add(OOSymbolsMill
              .typeSymbolBuilder()
              .setName(t)
              .setEnclosingScope(utilTypesScope)
              .setSpannedScope(scope)
              .build());
        });
  }
}
