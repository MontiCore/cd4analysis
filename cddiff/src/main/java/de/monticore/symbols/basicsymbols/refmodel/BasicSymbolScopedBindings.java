/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the bindings at a certain context symbol in a model.
 */
public class BasicSymbolScopedBindings implements IBasicSymbolsBindings {
  
  /** The context symbol at which these bindings hold. (primarily for debugging) */
  private final String contextSymbolKey;
  
  /** Bindings inherited from enclosing scope of the context symbol */
  private final IBasicSymbolsBindings inheritedBindings;
  
  /**
   * The internal bindings instance used to represent bindings at the context symbol represented by
   * this instance.
   */
  private final IBasicSymbolsBindings localBindings;
  
  public BasicSymbolScopedBindings(String contextSymbolKey, IBasicSymbolsBindings inheritedBindings,
      IBasicSymbolsBindings localBindings) {
    this.contextSymbolKey = contextSymbolKey;
    this.inheritedBindings = inheritedBindings;
    this.localBindings = localBindings;
  }
  
  @Override
  public IBasicSymbolsBindings copy() {
    IBasicSymbolsBindings copy = localBindings.copy();
    try {
      copy.addAll(inheritedBindings);
    }
    catch (BindingConflictException e) {
      throw new RuntimeException(
          "Unexpected conflicting bindings when merging local with inherited bindings", e);
    }
    return copy;
  }
  
  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    return localBindings.getBinding(typeSymbol).or(() -> inheritedBindings.getBinding(typeSymbol));
  }
  
  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    Set<Binding<TypeSymbol>> result = new HashSet<>();
    result.addAll(localBindings.getTypeBindings());
    result.addAll(inheritedBindings.getTypeBindings());
    return result;
  }
  
  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException {
    localBindings.addTypeBinding(binding);
  }
  
  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    return localBindings.isConflictingTypeBinding(binding) || inheritedBindings
        .isConflictingTypeBinding(binding);
  }
  
  @Override
  public IBasicSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding)
      throws BindingConflictException {
    IBasicSymbolsBindings result = localBindings.getTypeImpliedBindings(binding);
    result.addAll(inheritedBindings.getTypeImpliedBindings(binding));
    return result;
  }
  
  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    return localBindings.getBinding(variableSymbol).or(() -> inheritedBindings.getBinding(
        variableSymbol));
  }
  
  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() {
    Set<Binding<VariableSymbol>> result = new HashSet<>();
    result.addAll(localBindings.getVariableBindings());
    result.addAll(inheritedBindings.getVariableBindings());
    return result;
  }
  
  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addVariableBinding(binding);
  }
  
  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    return false;
  }
  
  @Override
  public IBasicSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException {
    IBasicSymbolsBindings result = localBindings.getVariableImpliedBindings(binding);
    result.addAll(inheritedBindings.getVariableImpliedBindings(binding));
    return result;
  }
  
  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    return localBindings.getBinding(functionSymbol).or(() -> inheritedBindings.getBinding(
        functionSymbol));
  }
  
  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() {
    Set<Binding<FunctionSymbol>> result = new HashSet<>();
    result.addAll(localBindings.getFunctionBindings());
    result.addAll(inheritedBindings.getFunctionBindings());
    return result;
  }
  
  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addFunctionBinding(binding);
  }
  
  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    return localBindings.isConflictingFunctionBinding(binding) || inheritedBindings
        .isConflictingFunctionBinding(binding);
  }
  
  @Override
  public IBasicSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException {
    IBasicSymbolsBindings result = localBindings.getFunctionImpliedBindings(binding);
    result.addAll(inheritedBindings.getFunctionImpliedBindings(binding));
    return result;
  }
  
  @Override
  public void addAll(IBasicSymbolsBindings bindings) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addAll(bindings);
  }
  
  @Override
  public boolean isConflicting(IBasicSymbolsBindings otherBindings) {
    return localBindings.isConflicting(otherBindings) || inheritedBindings.isConflicting(
        otherBindings);
  }
  
}
