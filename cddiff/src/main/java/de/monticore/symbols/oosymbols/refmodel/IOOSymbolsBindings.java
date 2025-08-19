/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Optional;
import java.util.Set;

// NOTE: Could be generated
/**
 * Represents a <i>consistent</i> set of bindings for the symbols defined by the "OOSymbols"
 * language. These are:
 * <ul>
 * <li>{@link OOTypeSymbol}</li>
 * <li>{@link FieldSymbol}</li>
 * <li>{@link MethodSymbol}</li>
 * </ul>
 * Consistent means that no binding conflicts with another binding in this set.
 */
public interface IOOSymbolsBindings extends IBasicSymbolsBindings {
  
  /**
   * Creates a copy of this instance.<br>
   * This is useful as we may not know the actual implementation class,e specially because of
   * language composition.
   *
   * @return a new instance with the same bindings
   */
  IOOSymbolsBindings copy();
  
  /**
   * Returns the binding for the given {@link OOTypeSymbol} if it exists in this set.
   *
   * @param typeSymbol the reference type symbol to look for
   * @return the binding, if it exists, or an empty Optional not
   */
  Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol);
  
  /**
   * Returns the binding for the given {@link FieldSymbol} if it exists in this set.
   *
   * @param fieldSymbol the reference field symbol to look for
   * @return the binding, if it exists, or an empty Optional not
   */
  Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol);
  
  /**
   * Returns the binding for the given {@link MethodSymbol} if it exists in this set.
   *
   * @param methodSymbol the reference method symbol to look for
   * @return the binding, if it exists, or an empty Optional not
   */
  Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol);
  
  /**
   * Adds a binding for a {@link OOTypeSymbol} to this set of bindings. Further, all bindings
   * implied by the binding are added as well (see {@link #getOOTypeImpliedBindings(Binding)}).
   *
   * @param binding the binding to add
   * @throws BindingConflictException if the binding conflicts with existing bindings
   *
   * @see #isConflictingOOTypeBinding(Binding)
   * @see #getOOTypeImpliedBindings(Binding)
   */
  void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException;
  
  /**
   * Adds a binding for a {@link FieldSymbol} to this set of bindings. Further, all bindings
   * implied by the binding are added as well (see {@link #getFieldImpliedBindings(Binding)}).
   *
   * @param binding the binding to add
   * @throws BindingConflictException if the binding conflicts with existing bindings
   *
   * @see #isConflictingFieldBinding(Binding)
   * @see #getFieldImpliedBindings(Binding)
   */
  void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException;
  
  /**
   * Adds a binding for a {@link MethodSymbol} to this set of bindings. Further, all bindings
   * implied by the binding are added as well (see {@link #getMethodImpliedBindings(Binding)}).
   *
   * @param binding the binding to add
   * @throws BindingConflictException if the binding conflicts with existing bindings
   *
   * @see #isConflictingMethodBinding(Binding)
   * @see #getMethodImpliedBindings(Binding)
   */
  void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException;
  
  /**
   * Checks if the given {@link OOTypeSymbol} binding or any implied binding conflicts with
   * existing bindings.
   *
   * @param binding the binding to check
   * @return true if there is a conflict, false otherwise
   *
   * @see #getOOTypeImpliedBindings(Binding)
   */
  boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding);
  
  /**
   * Checks if the given {@link FieldSymbol} binding or any implied binding conflicts with
   * existing bindings.
   *
   * @param binding the binding to check
   * @return true if there is a conflict, false otherwise
   *
   * @see #getFieldImpliedBindings(Binding)
   */
  boolean isConflictingFieldBinding(Binding<FieldSymbol> binding);
  
  /**
   * Checks if the given {@link MethodSymbol} binding or any implied binding conflicts with
   * existing bindings.
   *
   * @param binding the binding to check
   * @return true if there is a conflict, false otherwise
   *
   * @see #getMethodImpliedBindings(Binding)
   */
  boolean isConflictingMethodBinding(Binding<MethodSymbol> binding);
  
  /**
   * Returns the bindings that are implied by a type binding.
   *
   * @param binding the type binding
   * @return the bindings implied by the type binding
   * @throws BindingConflictException if the implied bindings conflict with each other
   */
  IOOSymbolsBindings getOOTypeImpliedBindings(Binding<OOTypeSymbol> binding)
      throws BindingConflictException;
  
  /**
   * Returns the bindings that are implied by a field binding.<br>
   * e.g. a field binding implies:
   * 1. A binding for the declaring type of the field
   * 2. all other implied bindings as defined in {@link IBasicSymbolsBindings}
   *
   * @param binding the type binding
   * @return the bindings implied by the type binding
   * @throws BindingConflictException if the implied bindings conflict with each other
   */
  IOOSymbolsBindings getFieldImpliedBindings(Binding<FieldSymbol> binding)
      throws BindingConflictException;
  
  /**
   * Returns the bindings that are implied by a method binding.<br>
   * e.g. a field binding implies:
   * 1. A binding for the declaring type of the field
   * 2. all other implied bindings as defined in {@link IBasicSymbolsBindings}
   *
   * @param binding the type binding
   * @return the bindings implied by the type binding
   * @throws BindingConflictException if the implied bindings conflict with each other
   */
  IOOSymbolsBindings getMethodImpliedBindings(Binding<MethodSymbol> binding)
      throws BindingConflictException;
  
  // redefine methods from BasicSymbolsBindings for more precise return types
  
  @Override
  IOOSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding)
      throws BindingConflictException;
  
  @Override
  IOOSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException;
  
  @Override
  IOOSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException;
  
  /**
   * Returns all {@link OOTypeSymbol} bindings in this set.
   *
   * @return a set of all type bindings
   */
  Set<Binding<OOTypeSymbol>> getOOTypeBindings();
  
  /**
   * Returns all {@link FieldSymbol} bindings in this set.
   *
   * @return a set of all type bindings
   */
  Set<Binding<FieldSymbol>> getFieldBindings();
  
  /**
   * Returns all {@link MethodSymbol} bindings in this set.
   *
   * @return a set of all type bindings
   */
  Set<Binding<MethodSymbol>> getMethodBindings();
  
  /**
   * Adds all bindings from the given {@link IOOSymbolsBindings} instance to this instance.
   *
   * @param bindings the bindings to add
   * @throws BindingConflictException if any binding to add conflicts with existing bindings
   */
  void addAll(IOOSymbolsBindings bindings) throws BindingConflictException;
  
  /**
   * Checks if any binding in the given {@link IOOSymbolsBindings} instance conflicts with
   * the bindings in this instance.
   *
   * @param otherBindings the other bindings to check for conflicts
   * @return true if there is a conflict, false otherwise
   */
  boolean isConflicting(IOOSymbolsBindings otherBindings);
  
}
