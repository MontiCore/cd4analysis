package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Optional;
import java.util.Set;

// NOTE: Could be generated
public interface BasicSymbolsBindings {

  BasicSymbolsBindings copy();

  Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol);

  Set<Binding<TypeSymbol>> getTypeBindings();

  void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException;

  boolean isConflictingTypeBinding(Binding<TypeSymbol> binding);

  Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol);

  Set<Binding<VariableSymbol>> getVariableBindings();

  void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException;

  /*
   * TODO Think about removing isConflicting... methods form interface
   *  as it is only used internally in the "generated" implementation. The primary us case is
   * that it can be overridden by handcoded subclasses to add special semantics
   */

  boolean isConflictingVariableBinding(Binding<VariableSymbol> binding);

  Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol);

  Set<Binding<FunctionSymbol>> getFunctionBindings();

  void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException;

  boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding);

  void addAll(BasicSymbolsBindings bindings) throws BindingConflictException;
  // NOTE: this is the method we can use when adding all bindings from the original incarnation
  // mapping to the current context (e.g., if we select an incarnation X of R then also add all bindings B1,...Bk at context symbol X

  boolean isConflicting(BasicSymbolsBindings otherBindings);
}
