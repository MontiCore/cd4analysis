/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsBindings;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the bindings at a certain context symbol in a model.
 */
public class OOSymbolScopedBindings implements IOOSymbolsBindings {
  
  /** The context symbol at which these bindings hold. (primarily for debugging) */
  private final String contextSymbolKey;
  
  /**
   * The internal bindings instance used to represent bindings at the context symbol represented by
   * this instance.
   */
  private final IOOSymbolsBindings localBindings;
  
  /** Bindings inherited from enclosing scope of the context symbol */
  private final IOOSymbolsBindings inheritedBindings;
  
  public OOSymbolScopedBindings(String contextSymbolKey, IOOSymbolsBindings localBindings,
      IOOSymbolsBindings inheritedBindings) {
    this.contextSymbolKey = contextSymbolKey;
    this.localBindings = localBindings;
    this.inheritedBindings = inheritedBindings;
  }
  
  @Override
  public IOOSymbolsBindings copy() {
    IOOSymbolsBindings copy = localBindings.copy();
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
  public Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol) {
    return localBindings.getBinding(typeSymbol).or(() -> inheritedBindings.getBinding(typeSymbol));
  }
  
  @Override
  public Set<Binding<OOTypeSymbol>> getOOTypeBindings() {
    Set<Binding<OOTypeSymbol>> result = new LinkedHashSet<>();
    result.addAll(localBindings.getOOTypeBindings());
    result.addAll(inheritedBindings.getOOTypeBindings());
    return result;
  }
  
  @Override
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException {
    localBindings.addOOTypeBinding(binding);
  }
  
  @Override
  public boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding) {
    return localBindings.isConflictingOOTypeBinding(binding) || inheritedBindings
        .isConflictingOOTypeBinding(binding);
  }
  
  @Override
  public IOOSymbolsBindings getOOTypeImpliedBindings(Binding<OOTypeSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings result = localBindings.getOOTypeImpliedBindings(binding);
    result.addAll(inheritedBindings.getOOTypeImpliedBindings(binding));
    return result;
  }
  
  @Override
  public Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol) {
    return localBindings.getBinding(fieldSymbol).or(() -> inheritedBindings.getBinding(
        fieldSymbol));
  }
  
  @Override
  public Set<Binding<FieldSymbol>> getFieldBindings() {
    Set<Binding<FieldSymbol>> result = new LinkedHashSet<>();
    result.addAll(localBindings.getFieldBindings());
    result.addAll(inheritedBindings.getFieldBindings());
    return result;
  }
  
  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addFieldBinding(binding);
  }
  
  @Override
  public boolean isConflictingFieldBinding(Binding<FieldSymbol> binding) {
    return localBindings.isConflictingFieldBinding(binding) || inheritedBindings
        .isConflictingFieldBinding(binding);
  }
  
  @Override
  public IOOSymbolsBindings getFieldImpliedBindings(Binding<FieldSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings result = localBindings.getFieldImpliedBindings(binding);
    result.addAll(inheritedBindings.getFieldImpliedBindings(binding));
    return result;
  }
  
  @Override
  public Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol) {
    return localBindings.getBinding(methodSymbol).or(() -> inheritedBindings.getBinding(
        methodSymbol));
  }
  
  @Override
  public Set<Binding<MethodSymbol>> getMethodBindings() {
    Set<Binding<MethodSymbol>> result = new LinkedHashSet<>();
    result.addAll(localBindings.getMethodBindings());
    result.addAll(inheritedBindings.getMethodBindings());
    return result;
  }
  
  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addMethodBinding(binding);
  }
  
  @Override
  public boolean isConflictingMethodBinding(Binding<MethodSymbol> binding) {
    return localBindings.isConflictingMethodBinding(binding) || inheritedBindings
        .isConflictingMethodBinding(binding);
  }
  
  @Override
  public IOOSymbolsBindings getMethodImpliedBindings(Binding<MethodSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings result = localBindings.getMethodImpliedBindings(binding);
    result.addAll(inheritedBindings.getMethodImpliedBindings(binding));
    return result;
  }
  
  @Override
  public void addAll(IOOSymbolsBindings bindings) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addAll(bindings);
  }
  
  @Override
  public boolean isConflicting(IOOSymbolsBindings otherBindings) {
    return localBindings.isConflicting(otherBindings) || inheritedBindings.isConflicting(
        otherBindings);
  }
  
  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    return localBindings.getBinding(typeSymbol).or(() -> inheritedBindings.getBinding(typeSymbol));
  }
  
  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    Set<Binding<TypeSymbol>> result = new LinkedHashSet<>();
    result.addAll(localBindings.getTypeBindings());
    result.addAll(inheritedBindings.getTypeBindings());
    return result;
  }
  
  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException {
    // NOTE: Always add to LOCAL bindings as this instances is shared with the
    // incarnation mapping where we obtained this instance from.
    localBindings.addTypeBinding(binding);
  }
  
  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    return localBindings.isConflictingTypeBinding(binding) || inheritedBindings
        .isConflictingTypeBinding(binding);
  }
  
  @Override
  public IOOSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings result = localBindings.getTypeImpliedBindings(binding);
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
    Set<Binding<VariableSymbol>> result = new LinkedHashSet<>();
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
    return localBindings.isConflictingVariableBinding(binding) || inheritedBindings
        .isConflictingVariableBinding(binding);
  }
  
  @Override
  public IOOSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings result = localBindings.getVariableImpliedBindings(binding);
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
    Set<Binding<FunctionSymbol>> result = new LinkedHashSet<>();
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
  public IOOSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings result = localBindings.getFunctionImpliedBindings(binding);
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
