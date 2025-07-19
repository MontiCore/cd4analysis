package de.monticore.symbols;

import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;

import java.util.Set;
import java.util.function.Function;
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
            && isConflictingTypeBinding(getFieldImpliedTypeBinding(binding));
  }

  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException {
    super.addMethodBinding(binding);
    addTypeBinding(getMethodImpliedTypeBinding(binding));
  }

  @Override
  public boolean isConflictingMethodBinding(Binding<MethodSymbol> binding) {
    return super.isConflictingMethodBinding(binding)
            && isConflictingTypeBinding(getMethodImpliedTypeBinding(binding));
  }

  /**
   * Returns the type binding that is implied by a method binding, i.e. a binding for the
   * declaring type.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the method binding
   */
  protected Binding<TypeSymbol> getMethodImpliedTypeBinding(Binding<MethodSymbol> binding) {
    return getImpliedTypeBinding(binding, SymbolUtil::getDeclaringTypeSymbol);
  }

  /**
   * Returns the type binding that is implied by a field binding, i.e. a binding for the
   * declaring type.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the field binding
   */
  protected Binding<TypeSymbol> getFieldImpliedTypeBinding(Binding<FieldSymbol> binding) {
    return getImpliedTypeBinding(binding, SymbolUtil::getDeclaringTypeSymbol);
  }

  /**
   * Returns the type binding for the declaring typ of a method/field binding for the
   * declaring type.
   *
   * @param binding the binding for the method/field
   * @param getDeclaringType a function that retrieves the declaring type symbol of an OO symbol
   * @return
   * @param <T>
   */
  protected <T extends ISymbol> Binding<TypeSymbol> getImpliedTypeBinding(
          Binding<T> binding,
          Function<T, TypeSymbol> getDeclaringType) {
    TypeSymbol declaringRefType = getDeclaringType.apply(binding.getReferenceElement());
    Set<TypeSymbol> declaringTypeIncs = binding.getConcreteElements().stream()
            .map(getDeclaringType)
            .collect(Collectors.toSet());
    if (binding.isStrict()) {
      return Binding.createStrict(declaringRefType, declaringTypeIncs.stream().findFirst().orElseThrow());
    } else {
      return Binding.createAggregate(declaringRefType, declaringTypeIncs);
    }
  }
}
