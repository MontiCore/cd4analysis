package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Optional;
import java.util.Set;

// NOTE: Could be generated
/**
 * Represents a <i>consistent</i> set of bindings for the symbols defined by the "BasicSymbols"
 * language. These are:
 * <ul>
 *   <li>{@link TypeSymbol}</li>
 *   <li>{@link VariableSymbol}</li>
 *   <li>{@link FunctionSymbol}</li>
 * </ul>
 * Consistent means that no binding conflicts with another binding in this set.
 */
public interface BasicSymbolsBindings {

  /**
   * Creates a copy of this instance.<br>
   * This is useful as we may not know the actual implementation class,e specially because of
   * language composition.
   *
   * @return a new instance with the same bindings
   */
  BasicSymbolsBindings copy();

  /**
   * Returns the binding for the given {@link TypeSymbol} if it exists in this set.
   *
   * @param typeSymbol the reference type symbol to look for
   * @return the binding, if it exists, or an empty Optional not
   */
  Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol);

  /**
   * Returns all {@link TypeSymbol} bindings in this set.
   * @return a set of all type bindings
   */
  Set<Binding<TypeSymbol>> getTypeBindings();

  /**
   * Adds a binding for a {@link TypeSymbol} to this set of bindings.
   *
   * @param binding the binding to add
   * @throws BindingConflictException if the binding conflicts with existing bindings
   *
   * @see #isConflictingTypeBinding(Binding)
   */
  void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException;

  /**
   * Checks if the given {@link TypeSymbol} binding conflicts with existing bindings.<br>
   * <br>
   * NOTE: This is not limited to bindings of the same symbol kind, as there may be implications
   * between different kinds of symbols, e.g., a type binding could conflict with a variable binding
   * because it already uses a different type incarnation.
   *
   * @param binding the binding to check
   * @return true if there is a conflict, false otherwise
   */
  boolean isConflictingTypeBinding(Binding<TypeSymbol> binding);

  /**
   * Returns the binding for the given {@link VariableSymbol} if it exists in this set.
   *
   * @param variableSymbol the reference variable symbol to look for
   * @return the binding, if it exists, or an empty Optional not
   */
  Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol);

  /**
   * Returns all {@link VariableSymbol} bindings in this set.
   *
   * @return a set of all variable bindings
   */
  Set<Binding<VariableSymbol>> getVariableBindings();

  /**
   * Adds a binding for a {@link VariableSymbol} to this set of bindings.<br>
   * <br>
   * NOTE: This method might add additional bindings implied by this binding, e.g., a variable
   * binding implies a type binding for the variable's type.
   *
   * @param binding the binding to add
   * @throws BindingConflictException if the binding conflicts with existing bindings
   *
   * @see #isConflictingVariableBinding(Binding)
   */
  void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException;

  /**
   * Checks if the given {@link VariableSymbol} binding conflicts with existing bindings.<br>
   * <br>
   * NOTE: This is not limited to bindings of the same symbol kind, as there may be implications
   * between different kinds of symbols, e.g., a variable binding implies an additional type binding
   * for its type.
   *
   * @param binding the binding to check
   * @return true if there is a conflict, false otherwise
   */
  boolean isConflictingVariableBinding(Binding<VariableSymbol> binding);

  /**
   * Returns the binding for the given {@link FunctionSymbol} if it exists in this set.
   *
   * @param functionSymbol the reference function symbol to look for
   * @return the binding, if it exists, or an empty Optional not
   */
  Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol);

  /**
   * Returns all {@link FunctionSymbol} bindings in this set.
   *
   * @return a set of all function bindings
   */
  Set<Binding<FunctionSymbol>> getFunctionBindings();

  /**
   * Adds a binding for a {@link FunctionSymbol} to this set of bindings.<br>
   * <br>
   * NOTE: This method might add additional bindings implied by this binding, e.g., a function
   * binding implies type bindings for its parameters and return type.
   *
   * @param binding the binding to add
   * @throws BindingConflictException if the binding conflicts with existing bindings
   *
   * @see #isConflictingFunctionBinding(Binding)
   */
  void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException;

  /**
   * Checks if the given {@link FunctionSymbol} binding conflicts with existing bindings.<br>
   * <br>
   * NOTE: This is not limited to bindings of the same symbol kind, as there may be implications
   * between different kinds of symbols, e.g., a function binding implies additional type bindings
   * for its parameters and return type.
   *
   * @param binding the binding to check
   * @return true if there is a conflict, false otherwise
   */
  boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding);

  /**
   * Adds all bindings from the given BasicSymbolsBindings instance to this instance.
   *
   * @param bindings the bindings to add
   * @throws BindingConflictException if any binding to add conflicts with existing bindings
   */
  void addAll(BasicSymbolsBindings bindings) throws BindingConflictException;

  /**
   * Checks if any binding in the given {@link BasicSymbolsBindings} instance conflicts with
   * the bindings in this instance.
   *
   * @param otherBindings the other bindings to check for conflicts
   * @return true if there is a conflict, false otherwise
   */
  boolean isConflicting(BasicSymbolsBindings otherBindings);
}
