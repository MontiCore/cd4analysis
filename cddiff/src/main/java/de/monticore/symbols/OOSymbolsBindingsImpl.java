package de.monticore.symbols;

import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OOSymbolsBindingsImpl extends OOSymbolsBindingsImplTOP {

  public OOSymbolsBindingsImpl() {
    super();
  }

  protected OOSymbolsBindingsImpl(
          BasicSymbolsBindings basicSymbolsBindings,
          Bindings<OOTypeSymbol> ooTypeBindings,
          Bindings<FieldSymbol> fieldBindings,
          Bindings<MethodSymbol> methodBindings) {
    super(basicSymbolsBindings, ooTypeBindings, fieldBindings, methodBindings);
  }

  @Override
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException {
    // 1. enforce OO specific constraints
    // TODO check for conflicts with existing bindings, FieldSymbol, MethodSymbol
    super.addOOTypeBinding(binding);
  }

  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException {
    super.addFieldBinding(binding);
    addTypeBinding(getFieldImpliedTypeBinding(binding));
  }

  @Override
  public boolean isConflictingFieldBinding(Binding<FieldSymbol> binding) {
    return super.isConflictingFieldBinding(binding)
            || isConflictingTypeBinding(getFieldImpliedTypeBinding(binding));
  }

  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException {
    super.addMethodBinding(binding);
    addTypeBinding(getMethodImpliedTypeBinding(binding));
  }

  @Override
  public boolean isConflictingMethodBinding(Binding<MethodSymbol> binding) {
    return super.isConflictingMethodBinding(binding)
            || isConflictingTypeBinding(getMethodImpliedTypeBinding(binding));
  }

  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException {
    super.addVariableBinding(binding);
    for (Binding<TypeSymbol> typeBinding : getVariableImpliedTypeBinding(binding)) {
      addTypeBinding(typeBinding);
    }
  }

  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    return super.isConflictingVariableBinding(binding) || getVariableImpliedTypeBinding(binding)
            .stream().anyMatch(this::isConflictingTypeBinding);
  }

  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException {
    super.addFunctionBinding(binding);
    for (Binding<TypeSymbol> typeBinding : getFunctionImpliedTypeBinding(binding)) {
      addTypeBinding(typeBinding);
    }
  }

  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    return super.isConflictingFunctionBinding(binding) || getFunctionImpliedTypeBinding(binding).stream()
            .anyMatch(this::isConflictingTypeBinding);
  }

  /**
   * Returns the type binding that is implied by a method binding, i.e. a binding for the
   * declaring type.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the method binding
   */
  protected Binding<TypeSymbol> getMethodImpliedTypeBinding(Binding<MethodSymbol> binding) {
    return getImpliedTypeBinding(binding);
  }

  /**
   * Returns the type binding that is implied by a field binding, i.e. a binding for the
   * declaring type.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the field binding
   */
  protected Binding<TypeSymbol> getFieldImpliedTypeBinding(Binding<FieldSymbol> binding) {
    return getImpliedTypeBinding(binding);
  }

  /**
   * Returns the type binding that is implied by a variable binding, i.e. a binding for the
   * declaring type.<br>
   * A VariableSymbol instance might still represent a field of a CDType, although
   * it is not a FieldSymbol. Therefore, we try to get a declaring type by the heuristic in
   * {@link #getDeclaringTypeIfPresent(ISymbol)}.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the field binding
   */
  protected Set<Binding<TypeSymbol>> getVariableImpliedTypeBinding(Binding<VariableSymbol> binding) {
    Optional<TypeSymbol> declaringTypeOpt = getDeclaringTypeIfPresent(binding.getReferenceElement());
    if (declaringTypeOpt.isPresent()) {
      return Set.of(getImpliedTypeBinding(binding));
    } else {
      return Collections.emptySet();
    }
  }

  /**
   * Returns the type binding that is implied by a function binding, i.e. a binding for the
   * declaring type.<br>
   * A VariableSymbol instance might still represent a field of a CDType, although
   * it is not a FieldSymbol. Therefore, we try to get a declaring type by the heuristic in
   * {@link #getDeclaringTypeIfPresent(ISymbol)}.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the method binding
   */
  protected Set<Binding<TypeSymbol>> getFunctionImpliedTypeBinding(Binding<FunctionSymbol> binding) {
    Optional<TypeSymbol> declaringTypeOpt = getDeclaringTypeIfPresent(binding.getReferenceElement());
    if (declaringTypeOpt.isPresent()) {
      return Set.of(getImpliedTypeBinding(binding));
    } else {
      return Collections.emptySet();
    }
  }

  /**
   * Returns the type binding for the declaring typ of a method/field binding for the
   * declaring type.
   *
   * @param binding the binding for the method/field
   * @return
   * @param <T>
   */
  protected <T extends ISymbol> Binding<TypeSymbol> getImpliedTypeBinding(Binding<T> binding) {
    TypeSymbol declaringRefType = SymbolUtil.getDeclaringTypeSymbol(binding.getReferenceElement());
    Set<TypeSymbol> declaringTypeIncs = binding.getConcreteElements().stream()
            .map(SymbolUtil::getDeclaringTypeSymbol)
            .collect(Collectors.toSet());
    if (binding.isStrict()) {
      return Binding.createStrict(declaringRefType, declaringTypeIncs.stream().findFirst().orElseThrow());
    } else {
      return Binding.createAggregate(declaringRefType, declaringTypeIncs);
    }
  }

  /**
   * Returns the declaring type of the given symbol, if it exists, e.g. the type in which an
   * attribute is declared.
   *
   * @param symbol the symbol for which to get the declaring type
   * @return the declaring type if it exists, otherwise an empty Optional
   */
  protected Optional<TypeSymbol> getDeclaringTypeIfPresent(ISymbol symbol) {
    // TODO Get declaring type via spanning symbol of enclosing scope vs. resolve qualifier from symbol full name
    if (symbol.getEnclosingScope().isPresentSpanningSymbol()) {
      ISymbol spanningSymbol = symbol.getEnclosingScope().getSpanningSymbol();
      if (spanningSymbol instanceof TypeSymbol) {
        return Optional.of((TypeSymbol) spanningSymbol);
      }
    }
    return Optional.empty();
  }
}
