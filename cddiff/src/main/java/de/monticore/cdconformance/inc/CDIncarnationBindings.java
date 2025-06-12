/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

import java.util.Set;

/**
 * Using the incarnation binding, it is possible to restrict the incarnation mapping in a specific
 * scope, e.g. a reference type which has multiple incarnations in the whole model can be mapped to
 * a specific single concrete type in the scope spanned by a certain concrete type. A mapping must
 * always be resolved in the scope of a concrete class diagram (CD) or some sub scope.
 */
public interface CDIncarnationBindings {
  /*
   * TODO Add documentation for all methods in this interface & highlight danger of using addBinding(String, ..., ...)
   *
   */
  
  /**
   * Computes the unique name of a symbol, which is used as key in the bindings maps.<br>
   * <br>
   * Usually, this is the full name of the symbol, but it can be overridden in subclasses
   * to provide a different naming scheme if necessary.
   *
   * @param symbol the symbol for which the unique key is computed
   * @return the unique key for the symbol
   */
  String computeSymbolKey(ISymbol symbol);
  
  // --------------------------
  // ----- Type Binding -------
  // --------------------------
  
  /**
   * Adds a type binding for a specific scope. After calling this, within the scope, the reference
   * type is only incarnated by the given concrete types.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, TypeSymbol, Set)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the concrete type spanning the scope
   * @param referenceType
   * @param concreteTypes
   */
  void addBinding(String contextSymbolName, TypeSymbol referenceType,
      Set<TypeSymbol> concreteTypes);
  
  /**
   * Adds a type binding for a specific scope. After calling this, within the scope, the reference
   * type is only incarnated by the given concrete types.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, TypeSymbol, TypeSymbol)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the concrete type spanning the scope
   * @param referenceType
   * @param concreteType
   */
  default void addBinding(String contextSymbolName, TypeSymbol referenceType,
      TypeSymbol concreteType) {
    addBinding(contextSymbolName, referenceType, Set.of(concreteType));
  }
  
  default void addBinding(ISymbol contextSymbol, TypeSymbol referenceElement,
      Set<TypeSymbol> concreteElements) {
    addBinding(computeSymbolKey(contextSymbol), referenceElement, concreteElements);
  }
  
  default void addBinding(ISymbol contextSymbol, TypeSymbol referenceElement,
      TypeSymbol concreteElement) {
    addBinding(contextSymbol, referenceElement, Set.of(concreteElement));
  }
  
  /**
   * Returns all concrete types that are bound to the given reference type in the given scope.<br>
   * <b>Note:</b> If no bindings are found, the method returns an empty set. This does not mean that
   * the reference type has no incarnations at all, but rather that there are no specific bindings.
   *
   * @param contextSymbol the symbol at which the bindings are searched
   * @param referenceElement the reference type for which the bindings are searched
   * @return all concrete types that are bound to the given reference type in the given scope
   */
  Set<TypeSymbol> getBindings(ISymbol contextSymbol, TypeSymbol referenceElement);
  
  /**
   * Returns all concrete types that are bound to the given reference type in the given scope.
   * This is a shortcut for {@link #getBindings(ISymbol, TypeSymbol)} which avoids checking
   * for the next spanning symbol in the scope hierarchy.
   *
   * @param concreteScope the scope in which the bindings are searched
   * @param referenceType the reference type for which the bindings are searched
   * @return all concrete types that are bound to the given reference type in the given scope
   */
  Set<TypeSymbol> getBindings(IScope concreteScope, TypeSymbol referenceType);
  
  // --------------------------
  // ----- Attribute Binding --
  // --------------------------
  
  void addBinding(String contextSymbolName, FieldSymbol referenceElement,
      Set<FieldSymbol> concreteElements);
  
  default void addBinding(String contextSymbolName, FieldSymbol referenceElement,
      FieldSymbol concreteElement) {
    addBinding(contextSymbolName, referenceElement, Set.of(concreteElement));
  }
  
  default void addBinding(ISymbol contextSymbol, FieldSymbol referenceElement,
      Set<FieldSymbol> concreteElements) {
    addBinding(computeSymbolKey(contextSymbol), referenceElement, concreteElements);
  }
  
  default void addBinding(ISymbol contextSymbol, FieldSymbol referenceElement,
      FieldSymbol concreteElement) {
    addBinding(contextSymbol, referenceElement, Set.of(concreteElement));
  }
  
  Set<FieldSymbol> getBindings(ISymbol contextSymbol, FieldSymbol referenceElement);
  
  Set<FieldSymbol> getBindings(IScope concreteScope, FieldSymbol referenceElement);
  
  // --------------------------
  // ----- Method Mapping -----
  // --------------------------
  
  void addBinding(String contextSymbolName, MethodSymbol referenceElement,
      Set<MethodSymbol> concreteElements);
  
  default void addBinding(ISymbol contextSymbol, MethodSymbol referenceElement,
      Set<MethodSymbol> concreteElements) {
    addBinding(computeSymbolKey(contextSymbol), referenceElement, concreteElements);
  }
  
  Set<MethodSymbol> getBindings(ISymbol contextSymbol, MethodSymbol referenceElement);
  
  Set<MethodSymbol> getBindings(IScope scope, MethodSymbol referenceElement);
  
  // -------------------------------
  // ----- Association Mapping -----
  // -------------------------------
  
  void addBinding(String contextSymbolName, CDAssociationSymbol referenceElement,
      Set<CDAssociationSymbol> concreteElements);
  
  default void addBinding(ISymbol contextSymbol, CDAssociationSymbol referenceElement,
      Set<CDAssociationSymbol> concreteElements) {
    addBinding(computeSymbolKey(contextSymbol), referenceElement, concreteElements);
  }
  
}
