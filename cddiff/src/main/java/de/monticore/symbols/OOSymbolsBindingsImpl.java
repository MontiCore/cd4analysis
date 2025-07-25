package de.monticore.symbols;

import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols.BasicSymbolBindingsImpl;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OOSymbolsBindingsImpl implements OOSymbolsBindings {

  // ========== Symbol bindings from languages extended by OOSymbols ==========

  /**
   * Used to manage basic symbols (required because OOSymbols language extends BasicSymbols).
   */
  private final BasicSymbolsBindings basicSymbolsBindings;

  // ========== OOSymbols specific symbol bindings ==========

  private final Bindings<OOTypeSymbol> ooTypeBindings;
  private final Bindings<FieldSymbol> fieldBindings;
  private final Bindings<MethodSymbol> methodBindings;

  public OOSymbolsBindingsImpl() {
    this(new BasicSymbolBindingsImpl(),
         new Bindings<>(),
         new Bindings<>(),
         new Bindings<>());
  }

  protected OOSymbolsBindingsImpl(
          BasicSymbolsBindings basicSymbolsBindings,
          Bindings<OOTypeSymbol> ooTypeBindings,
          Bindings<FieldSymbol> fieldBindings,
          Bindings<MethodSymbol> methodBindings) {
    this.basicSymbolsBindings = basicSymbolsBindings;
    this.ooTypeBindings = ooTypeBindings;
    this.fieldBindings = fieldBindings;
    this.methodBindings = methodBindings;
  }


  @Override
  public OOSymbolsBindings copy() {
    return new OOSymbolsBindingsImpl(
        basicSymbolsBindings.copy(),
        new Bindings<>(ooTypeBindings),
        new Bindings<>(fieldBindings),
        new Bindings<>(methodBindings)
    );
  }

  @Override
  public Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol) {
    return ooTypeBindings.get(typeSymbol);
  }

  @Override
  public Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol) {
    return fieldBindings.get(fieldSymbol);
  }

  @Override
  public Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol) {
    return methodBindings.get(methodSymbol);
  }

  /*
   * NOTE: We MUST add OOTypeSymbol, FieldSymbol, MethodSymbol to the BasicSymbolsBindings
   * because they are also TypeSymbol, VariableSymbol, FunctionSymbol.
   * This is necessary to ensure that the constraints enforced by BasicSymbolsBindings
   * are also applied to OOTypeSymbol, FieldSymbol, MethodSymbol.
   * e.g., an OOTypeSymbol used as parameter/return type must be considered when checkign conflicts
   * in BasicSymbolsBindings.addFunctionBinding.
   * We do not want to duplicate the conflict checking here in OOSymbolsBindingsImpl because
   * this would lead to inconsistencies -> think about developers using TOP mechanism to add
   * constraints to BasicSymbolsBindings.addFunctionBinding -> they would expect them to be
   * respected by a generated implementation of OOSymbolsBindings
   */

  // TODO throw BindingConflictException
  @Override
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) {
    // 1. enforce OO specific constraints
    // TODO check for conflicts with existing bindings OOTypeSymbol, FieldSymbol, MethodSymbol
    // 2. call BasicSymbolsBindings.addTypeBinding
    basicSymbolsBindings.addTypeBinding(binding.cast());
    // 3. if that is successful add to ooTypeBindings
    ooTypeBindings.add(binding);
  }

  // TODO throw BindingConflictException
  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) {
    // 1. enforce OO specific constraints
    // TODO check for declaring type conflicts with existing bindings OOTypeSymbol
    // 2. call BasicSymbolsBindings.addTypeBinding (already checks conflicts with existing TypeSymbol)
    basicSymbolsBindings.addVariableBinding(binding.cast());
    // 3. if that is successful add to ooTypeBindings
    fieldBindings.add(binding);
  }

  // TODO throw BindingConflictException
  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) {
    // 1. enforce OO specific constraints
    createTypeBindingForMethodBinding(binding);
    // TODO check for declaring type conflicts with existing bindings OOTypeSymbol
    // 2. call BasicSymbolsBindings.addFunctionBinding (already checks conflicts with existing TypeSymbol)
    basicSymbolsBindings.addFunctionBinding(binding.cast());
    // 3. if that is successful add to ooTypeBindings
    methodBindings.add(binding);
  }

  protected void createTypeBindingForMethodBinding(Binding<MethodSymbol> binding) {
    TypeSymbol declaringRefType = SymbolUtil.getDeclaringTypeSymbol(binding.getReferenceElement());
    Set<TypeSymbol> declaringTypeIncs = binding.getConcreteElements().stream()
            .map(SymbolUtil::getDeclaringTypeSymbol)
            .collect(Collectors.toSet());
    Binding<TypeSymbol> typeBinding;
    if (binding.isStrict()) {
      typeBinding = Binding.createStrict(declaringRefType, declaringTypeIncs.stream().findFirst().orElseThrow());
    } else {
      typeBinding = Binding.createAggregate(declaringRefType, declaringTypeIncs);
    }
    addTypeBinding(typeBinding);
  }

  @Override
  public Set<Binding<OOTypeSymbol>> getOOTypeBindings() {
    return ooTypeBindings.getAll();
  }

  @Override
  public Set<Binding<FieldSymbol>> getFieldBindings() {
    return fieldBindings.getAll();
  }

  @Override
  public Set<Binding<MethodSymbol>> getMethodBindings() {
    return methodBindings.getAll();
  }

  @Override
  public void addAll(OOSymbolsBindings bindings) {
    // Add all basic symbols bindings
    basicSymbolsBindings.addAll(bindings);

    // Add all OO specific bindings
    for (Binding<OOTypeSymbol> binding : bindings.getOOTypeBindings()) {
      addOOTypeBinding(binding);
    }
    for (Binding<FieldSymbol> binding : bindings.getFieldBindings()) {
      addFieldBinding(binding);
    }
    for (Binding<MethodSymbol> binding : bindings.getMethodBindings()) {
      addMethodBinding(binding);
    }
  }

  // ========== Delegate to BasicSymbolsBindings methods ==========

  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    // even if it is an OOTypeSymbol, we added it to the basic symbols bindings to it is
    return basicSymbolsBindings.getBinding(typeSymbol);
  }

  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    return basicSymbolsBindings.getTypeBindings();
  }

  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof OOTypeSymbol) {
      addOOTypeBinding(binding.cast());
    } else {
      basicSymbolsBindings.addTypeBinding(binding);
    }
  }

  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    return basicSymbolsBindings.getBinding(variableSymbol);
  }

  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() {
    return basicSymbolsBindings.getVariableBindings();
  }

  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof FieldSymbol) {
      addFieldBinding(binding.cast());
    } else {
      basicSymbolsBindings.addVariableBinding(binding);
    }
  }

  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    return basicSymbolsBindings.getBinding(functionSymbol);
  }

  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() {
    return basicSymbolsBindings.getFunctionBindings();
  }

  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof MethodSymbol) {
      addMethodBinding(binding.cast());
    } else {
      basicSymbolsBindings.addFunctionBinding(binding);
    }
  }

  @Override
  public void addAll(BasicSymbolsBindings bindings) {
    // TODO should we check this here?
    if (bindings instanceof OOSymbolsBindings) {
      addAll((OOSymbolsBindings) bindings);
    } else {
      basicSymbolsBindings.addAll(bindings);
    }
  }
}
