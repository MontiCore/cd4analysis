package de.monticore.cdconcretization;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symboltable.modifiers.AccessModifier;

/**
 * Utility class to add an underspecified placeholder type to the global scope.<br>
 * This type can be used to mark attribute types, method return types, or parameter types as
 * underspecified in the reference CD. The placeholder type can be cast to any other
 * type. It must only be used in the reference CD and only in context of templating, e.g. using
 * the 'forEach' stereotype.
 */
public class UnderspecifiedPlaceholderType {

  /**
   * The default name of the underspecified placeholder type.<br>
   * used when calling {@link #addPlaceholderType(IOOSymbolsGlobalScope)}.
   */
  public static final String DEFAULT_TYPE_NAME = "any";

  private UnderspecifiedPlaceholderType() {
  }

  /**
   * Adds the placeholder type to the global scope that can be used to mark attribute types, method
   * return types, or parameter types as underspecified in the reference CD.<br>
   * <b>Important:</b> This <b>must</b> be called before parsing the CD models. Otherwise, the 'any'
   * type is unknown! <br>
   *
   * @param globalScope the global scope to add the placeholder type to
   */
  public static void addPlaceholderType(IOOSymbolsGlobalScope globalScope) {
    addPlaceholderType(globalScope, DEFAULT_TYPE_NAME);
  }

  public static void addPlaceholderType(IOOSymbolsGlobalScope globalScope, String typeName) {
    globalScope.add(createPlaceholderTypeSymbol(typeName));
  }

  /**
   * Creates a type symbol for an underspecified placeholder type.<br>
   * Prefer using {@link #addPlaceholderType(IOOSymbolsGlobalScope)} to add the type to
   * the global scope.
   *
   * @param name the name of the type
   * @return the type symbol
   */
  private static TypeSymbol createPlaceholderTypeSymbol(String name) {
    return CD4CodeMill.typeSymbolBuilder()
            .setName(name)
            .setEnclosingScope(CD4CodeMill.globalScope())
            .setFullName(name)
            .setSpannedScope(CD4CodeMill.scope())
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .build();
  }
}
