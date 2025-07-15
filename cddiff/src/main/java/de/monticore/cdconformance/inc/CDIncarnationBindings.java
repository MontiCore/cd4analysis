/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import com.google.common.collect.SetMultimap;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

import java.util.Set;

/**
 * Using incarnation bindings, it is possible to restrict the incarnation mapping for a specific
 * part of the concrete model. A <i>binding</i> is a mapping from a reference element to a subset of
 * its incarnations. This mapping is attached to a concrete symbol and restricts the incarnation
 * mapping for this specific symbol, and if it is a scope spanning symbol (e.g., a type) for all
 * elements in the spanned scope.<br>
 * <br>
 * For example, assuming there are multiple incarnations of a type <code>DataClass</code>, in a
 * concrete model, we can attach a binding <code>DataClass=Employee</code> to the class
 * <code>EmployeeBuilder</code> using
 * <pre>
 * bindings.addBinding("Con.EmployeeBuilder", dataClassSym, employeeSym)
 * </pre>
 * If we now query the bindings of the type <code>DataClass</code> in context of
 * <code>EmployeeBuilder</code> we get only <code>Employee</code> as incarnation:
 * <pre>
 * Set&lt;TypeSymbol&gt; concreteTypes = bindings.getBindings(employeeBuilderSym, dataClassSym);
 * // concreteTypes == {employeeSym}
 * </pre>
 * If we query the binding for some other symbol nested in the scope spanned by
 * <code>EmployeeBuilder</code>, e.g., <code>EmployeeBuilder.build()</code>, we still get
 * <code>Employee</code> as incarnation:
 * <pre>
 * CDMethodSymbol buildMethodSymbol = employeeBuilderSym.getSpannedScope().resolveMethod("build")
 * concreteTypes = bindings.getBindings(buildMethodSymbol, dataClassSym);
 * // concreteTypes == {employeeSym}
 * </pre>
 *
 * <b>Note:</b> There are always two variants of the <code>addBinding</code> method for each element
 * kind. One taking a "context symbol" and one taking a raw "context symbol name". Prefer using
 * the first variant, as it is more type-safe and avoids manually computing the key that is used
 * internally to store the bindings. The second variant need to use the exact full qualified symbol
 * name, and even worse, if the {@link #computeSymbolKey(ISymbol)} method is overridden in a
 * subclass, it may not even be possible to use the symbol name as key anymore.<br>
 * However, in some cases it is necessary to add bindings for symbols which are not yet part of the
 * symbol table, e.g., during concretization of an incomplete model. In this case, implementations
 * must make sure to use the correct key for the contexts symbol.
 */
public interface CDIncarnationBindings {
  
  /**
   * Computes the unique name of a symbol, which is used as key in the bindings maps.<br>
   * <br>
   * Usually, this is the full name of the symbol, but it can be overridden in subclasses
   * to provide a different naming scheme if necessary.<br>
   * <br>
   * <b>Note: If you override this method, you must ensure that you use the correct raw keys
   * when using the <code>addBinding(String, ...)</code> variants!</b>
   *
   * @param symbol the symbol for which the unique key is computed
   * @return the unique key for the symbol
   */
  String computeSymbolKey(ISymbol symbol);
  
  // --------------------------
  // ----- Type Binding -------
  // --------------------------
  
  /**
   * Adds a type binding for the given context symbol.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, TypeSymbol, Set)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the context symbol
   * @param referenceType the reference type for which the bindings are added
   * @param concreteTypes the concrete types to which the reference type is bound to in the context
   */
  void addBinding(String contextSymbolName, TypeSymbol referenceType,
      Set<TypeSymbol> concreteTypes);
  
  /**
   * Adds a type binding for the given context symbol.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, TypeSymbol, TypeSymbol)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the context symbol
   * @param referenceType the reference type for which a binding is added
   * @param concreteType the concrete type to which the reference type is bound to in the context
   */
  default void addBinding(String contextSymbolName, TypeSymbol referenceType,
      TypeSymbol concreteType) {
    addBinding(contextSymbolName, referenceType, Set.of(concreteType));
  }
  
  /**
   * Adds a type binding for the given context symbol.
   *
   * @param contextSymbol the symbol at which the binding is added
   * @param referenceType the reference type for which the binding is added
   * @param concreteTypes the concrete types to which the reference type is bound to in the context
   */
  default void addBinding(ISymbol contextSymbol, TypeSymbol referenceType,
      Set<TypeSymbol> concreteTypes) {
    addBinding(computeSymbolKey(contextSymbol), referenceType, concreteTypes);
  }
  
  /**
   * Adds a type binding for the given context symbol.
   *
   * @param contextSymbol the symbol at which the binding is added
   * @param referenceTypes the reference type for which a binding is added
   * @param concreteElements the concrete type to which the reference type is bound to in the
   * context
   */
  default void addBinding(ISymbol contextSymbol, TypeSymbol referenceTypes,
      TypeSymbol concreteElements) {
    addBinding(contextSymbol, referenceTypes, Set.of(concreteElements));
  }
  
  /**
   * Returns all concrete types that are bound to the given reference type at the given context
   * symbol.<br>
   * <b>Note:</b> If no bindings are found, the method returns an empty set. This does not mean that
   * the reference type has no incarnations at all, but rather that there are no specific bindings.
   *
   * @param contextSymbol the symbol at which the bindings are searched
   * @param referenceType the reference type for which the bindings are searched
   * @return all concrete types that are bound to the given reference type in the given scope
   */
  Set<TypeSymbol> getBindings(ISymbol contextSymbol, TypeSymbol referenceType);
  
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
  
  /**
   * Adds a field binding for the given context symbol.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, FieldSymbol, Set)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the context symbol
   * @param referenceField the reference field for which the bindings are added
   * @param concreteElements the concrete fields to which the reference field is bound to in the
   * context
   */
  void addBinding(String contextSymbolName, FieldSymbol referenceField,
      Set<FieldSymbol> concreteElements);
  
  /**
   * Adds a field binding for the given context symbol.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, FieldSymbol, FieldSymbol)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the context symbol
   * @param referenceElement the reference field for which a binding is added
   * @param concreteField the concrete field to which the reference field is bound to in the context
   */
  default void addBinding(String contextSymbolName, FieldSymbol referenceElement,
      FieldSymbol concreteField) {
    addBinding(contextSymbolName, referenceElement, Set.of(concreteField));
  }
  
  /**
   * Adds a field binding for the given context symbol.
   *
   * @param contextSymbol the symbol at which the binding is added
   * @param referenceField the reference field for which the bindings are added
   * @param concreteFields the concrete fields to which the reference field is bound to in the
   * context
   */
  default void addBinding(ISymbol contextSymbol, FieldSymbol referenceField,
      Set<FieldSymbol> concreteFields) {
    addBinding(computeSymbolKey(contextSymbol), referenceField, concreteFields);
  }
  
  /**
   * Adds a field binding for the given context symbol.
   *
   * @param contextSymbol the symbol at which the binding is added
   * @param referenceField the reference field for which a binding is added
   * @param concreteField the concrete field to which the reference field is bound to in the context
   */
  default void addBinding(ISymbol contextSymbol, FieldSymbol referenceField,
      FieldSymbol concreteField) {
    addBinding(contextSymbol, referenceField, Set.of(concreteField));
  }
  
  /**
   * Returns all concrete fields that are bound to the given reference field at the given context
   * symbol.<br>
   * <b>Note:</b> If no bindings are found, the method returns an empty set. This does not mean that
   * the reference field has no incarnations at all, but rather that there are no specific bindings.
   *
   * @param contextSymbol the symbol at which the bindings are searched
   * @param referenceField the reference field for which the bindings are searched
   * @return all concrete fields that are bound to the given reference field in the given scope
   */
  Set<FieldSymbol> getBindings(ISymbol contextSymbol, FieldSymbol referenceField);
  
  /**
   * Returns all concrete fields that are bound to the given reference field in the given scope.
   * This is a shortcut for {@link #getBindings(ISymbol, FieldSymbol)} which avoids checking
   * for the next spanning symbol in the scope hierarchy.
   *
   * @param concreteScope the scope in which the bindings are searched
   * @param referenceField the reference field for which the bindings are searched
   * @return all concrete fields that are bound to the given reference field in the given scope
   */
  Set<FieldSymbol> getBindings(IScope concreteScope, FieldSymbol referenceField);
  
  // --------------------------
  // ----- Method Mapping -----
  // --------------------------
  
  /**
   * Adds a method binding for the given context symbol.<br>
   * <br>
   * <b>NOTE: Prefer using {@link #addBinding(ISymbol, MethodSymbol, Set)}</b>. In case
   * {@link #computeSymbolKey(ISymbol)} is overridden, you may not be able to use the symbol name
   * as key anymore!
   *
   * @param contextSymbolName the name of the context symbol
   * @param referenceMethod the reference method for which the bindings are added
   * @param concreteMethods the concrete methods to which the reference method is bound to in the
   * context
   */
  void addBinding(String contextSymbolName, MethodSymbol referenceMethod,
      Set<MethodSymbol> concreteMethods);
  
  /**
   * Adds a method binding for the given context symbol.
   *
   * @param contextSymbol the symbol at which the binding is added
   * @param referenceMethod the reference method for which a binding is added
   * @param concreteMethods the concrete methods to which the reference method is bound to in the
   * context
   */
  default void addBinding(ISymbol contextSymbol, MethodSymbol referenceMethod,
      Set<MethodSymbol> concreteMethods) {
    addBinding(computeSymbolKey(contextSymbol), referenceMethod, concreteMethods);
  }
  
  /**
   * Adds a method binding for the given context symbol.
   *
   * @param contextSymbol the symbol at which the binding is added
   * @param referenceMethod the reference method for which a binding is added
   * @param concreteMethod the concrete method to which the reference method is bound to in the
   * context
   */
  default void addBinding(ISymbol contextSymbol, MethodSymbol referenceMethod,
      MethodSymbol concreteMethod) {
    addBinding(contextSymbol, referenceMethod, Set.of(concreteMethod));
  }
  
  /**
   * Returns all concrete methods that are bound to the given reference method at the given context
   * symbol.<br>
   * <b>Note:</b> If no bindings are found, the method returns an empty set. This does not mean that
   * the reference method has no incarnations at all, but rather that there are no specific
   * bindings.
   *
   * @param contextSymbol the symbol at which the bindings are searched
   * @param referenceMethod the reference method for which the bindings are searched
   * @return all concrete methods that are bound to the given reference method in the given scope
   */
  Set<MethodSymbol> getBindings(ISymbol contextSymbol, MethodSymbol referenceMethod);
  
  /**
   * Returns all concrete methods that are bound to the given reference method in the given scope.
   * This is a shortcut for {@link #getBindings(ISymbol, MethodSymbol)} which avoids checking
   * for the next spanning symbol in the scope hierarchy.
   *
   * @param concreteScope the scope in which the bindings are searched
   * @param referenceMethod the reference method for which the bindings are searched
   * @return all concrete methods that are bound to the given reference method in the given scope
   */
  Set<MethodSymbol> getBindings(IScope concreteScope, MethodSymbol referenceMethod);
  
  // -------------------------------
  // ----- Association Mapping -----
  // -------------------------------
  
  void addBinding(String contextSymbolName, CDAssociationSymbol refAssociation,
      Set<CDAssociationSymbol> conAssociations);
  
  default void addBinding(ISymbol contextSymbol, CDAssociationSymbol refAssociation,
      Set<CDAssociationSymbol> conAssociations) {
    addBinding(computeSymbolKey(contextSymbol), refAssociation, conAssociations);
  }

  SetMultimap<String, TypeSymbol> getTypeBindings(IScope concreteScope);

  SetMultimap<String, TypeSymbol> getTypeBindings(ISymbol contextSymbol);

  SetMultimap<String, FieldSymbol> getFieldBindings(IScope concreteScope);

  SetMultimap<String, FieldSymbol> getFieldBindings(ISymbol contextSymbol);

  SetMultimap<String, MethodSymbol> getMethodBindings(IScope concreteScope);

  SetMultimap<String, MethodSymbol> getMethodBindings(ISymbol contextSymbol);
}
