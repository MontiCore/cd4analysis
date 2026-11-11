/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.refmodel.Bindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.Set;

/**
 * Basic implementation of {@link IBasicSymbolsBindings}.<br>
 * <br>
 * <b>NOTE:</b> This class is intended to be GENERATED in the future! Therefore, only apply changes
 * which are systematic and can be automatically derived from the language grammar.<br>
 * <br>
 * Developers can customize the semantic relations between different symbols, e.g. by overriding
 * the {@code getXXXImpliedBindings}.
 */
/*
 * TODO If this class is generated, remove the 'Base' suffix as MontiCore will automatically name
 *   it TOP in case there is a handwritten implementation.
 */
public class BasicSymbolsBindingsBase implements IBasicSymbolsBindings {
  
  private final Bindings<TypeSymbol> typeBindings;
  private final Bindings<VariableSymbol> variableBindings;
  private final Bindings<FunctionSymbol> functionBindings;
  
  protected BasicSymbolsBindingsBase(Bindings<TypeSymbol> typeBindings,
      Bindings<VariableSymbol> variableBindings, Bindings<FunctionSymbol> functionBindings) {
    this.typeBindings = typeBindings;
    this.variableBindings = variableBindings;
    this.functionBindings = functionBindings;
  }
  
  public BasicSymbolsBindingsBase() {
    this(new Bindings<>(), new Bindings<>(), new Bindings<>());
  }
  
  @Override
  public IBasicSymbolsBindings copy() {
    return new BasicSymbolsBindings(new Bindings<>(typeBindings), new Bindings<>(variableBindings),
        new Bindings<>(functionBindings));
  }
  
  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    return typeBindings.get(typeSymbol);
  }
  
  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() { return typeBindings.getAll(); }
  
  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException {
    if (isConflictingTypeBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    typeBindings.add(binding);
    addAll(getTypeImpliedBindings(binding));
  }
  
  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    try {
      return typeBindings.conflictsWith(binding) || isConflicting(getTypeImpliedBindings(binding));
    }
    catch (BindingConflictException e) {
      Log.error("The bindings implied by " + binding + " conflict with each other! "
          + "Either there is an issue in the model or you should check the implementation of "
          + "BasicSymbolBindings.getTypeImpliedBindings", e);
      return true;
    }
  }
  
  @Override
  public IBasicSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding)
      throws BindingConflictException {
    // default implementation returns an empty set
    return new BasicSymbolsBindings();
  }
  
  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    return variableBindings.get(variableSymbol);
  }
  
  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() { return variableBindings.getAll(); }
  
  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException {
    if (isConflictingVariableBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    variableBindings.add(binding);
    addAll(getVariableImpliedBindings(binding));
  }
  
  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    try {
      return variableBindings.conflictsWith(binding) || isConflicting(getVariableImpliedBindings(
          binding));
    }
    catch (BindingConflictException e) {
      Log.error("The bindings implied by " + binding + " conflict with each other! "
          + "Either there is an issue in the model or you should check the implementation of "
          + "BasicSymbolBindings.getVariableImpliedBindings", e);
      return true;
    }
  }
  
  @Override
  public IBasicSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException {
    // default implementation returns an empty set
    return new BasicSymbolsBindings();
  }
  
  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    return functionBindings.get(functionSymbol);
  }
  
  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() { return functionBindings.getAll(); }
  
  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException {
    if (isConflictingFunctionBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    functionBindings.add(binding);
    addAll(getFunctionImpliedBindings(binding));
  }
  
  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    try {
      return functionBindings.conflictsWith(binding) || isConflicting(getFunctionImpliedBindings(
          binding));
    }
    catch (BindingConflictException e) {
      Log.error("The bindings implied by " + binding + " conflict with each other! "
          + "Either there is an issue in the model or you should check the implementation of "
          + "BasicSymbolBindings.getFunctionImpliedBindings", e);
      return true;
    }
  }
  
  @Override
  public IBasicSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException {
    // default implementation returns an empty set
    return new BasicSymbolsBindings();
  }
  
  @Override
  public void addAll(IBasicSymbolsBindings bindings) throws BindingConflictException {
    if (isConflicting(bindings)) {
      throw new BindingConflictException();
    }
    for (Binding<TypeSymbol> binding : bindings.getTypeBindings()) {
      addTypeBinding(binding);
    }
    for (Binding<VariableSymbol> binding : bindings.getVariableBindings()) {
      addVariableBinding(binding);
    }
    for (Binding<FunctionSymbol> binding : bindings.getFunctionBindings()) {
      addFunctionBinding(binding);
    }
  }
  
  @Override
  public boolean isConflicting(IBasicSymbolsBindings otherBindings) {
    for (Binding<TypeSymbol> binding : otherBindings.getTypeBindings()) {
      if (isConflictingTypeBinding(binding)) {
        return true;
      }
    }
    for (Binding<VariableSymbol> binding : otherBindings.getVariableBindings()) {
      if (isConflictingVariableBinding(binding)) {
        return true;
      }
    }
    for (Binding<FunctionSymbol> binding : otherBindings.getFunctionBindings()) {
      if (isConflictingFunctionBinding(binding)) {
        return true;
      }
    }
    return false;
  }
  
}
