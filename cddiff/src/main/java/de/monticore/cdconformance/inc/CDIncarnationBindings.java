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
  
  // --------------------------
  // ----- Type Binding -------
  // --------------------------
  
  /**
   * Adds a type binding for a specific scope. After calling this, within the scope, the reference
   * type is only incarnated by the given concrete types.
   *
   * @param contextSymbolName the name of the concrete type spanning the scope
   * @param referenceType
   * @param concreteTypes
   */
  void addBinding(String contextSymbolName, TypeSymbol referenceType,
      Set<TypeSymbol> concreteTypes);
  
  default void addBinding(String contextSymbolName, TypeSymbol referenceElement,
      TypeSymbol concreteElement) {
    addBinding(contextSymbolName, referenceElement, Set.of(concreteElement));
  }
  
  default void addBinding(ISymbol contextSymbol, TypeSymbol referenceElement,
      Set<TypeSymbol> concreteElements) {
    addBinding(contextSymbol.getFullName(), referenceElement, concreteElements);
  }
  
  default void addBinding(ISymbol contextSymbol, TypeSymbol referenceElement,
      TypeSymbol concreteElement) {
    addBinding(contextSymbol.getFullName(), referenceElement, Set.of(concreteElement));
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
    addBinding(contextSymbol.getFullName(), referenceElement, concreteElements);
  }
  
  default void addBinding(ISymbol contextSymbol, FieldSymbol referenceElement,
      FieldSymbol concreteElement) {
    addBinding(contextSymbol.getFullName(), referenceElement, Set.of(concreteElement));
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
    addBinding(contextSymbol.getFullName(), referenceElement, concreteElements);
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
    addBinding(contextSymbol.getFullName(), referenceElement, concreteElements);
  }
  
}
