package de.monticore.symbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindingsImpl;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Optional;
import java.util.Set;

// NOTE: Could be generated
public class OOSymbolsBindingsImplTOP implements OOSymbolsBindings {

  // ========== Symbol bindings from languages extended by OOSymbols ==========

  /**
   * Used to manage basic symbols (required because OOSymbols language extends BasicSymbols).
   */
  private final BasicSymbolsBindings basicSymbolsBindings;

  // ========== OOSymbols specific symbol bindings ==========

  private final Bindings<OOTypeSymbol> ooTypeBindings;
  private final Bindings<FieldSymbol> fieldBindings;
  private final Bindings<MethodSymbol> methodBindings;

  public OOSymbolsBindingsImplTOP() {
    this(new BasicSymbolsBindingsImpl(),
         new Bindings<>(),
         new Bindings<>(),
         new Bindings<>());
  }

  protected OOSymbolsBindingsImplTOP(
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
    return new OOSymbolsBindingsImplTOP(
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
   * IMPLEMENTATION NOTE: We MUST add OOTypeSymbol, FieldSymbol, MethodSymbol to the BasicSymbolsBindings
   * because they are also TypeSymbol, VariableSymbol, FunctionSymbol.
   * This is necessary to ensure that the constraints enforced by BasicSymbolsBindings
   * are also applied to OOTypeSymbol, FieldSymbol, MethodSymbol.
   * e.g., an OOTypeSymbol used as parameter/return type must be considered when checking conflicts
   * in BasicSymbolsBindings.addFunctionBinding.
   * We do not want to duplicate the conflict checking here in OOSymbolsBindingsImpl because
   * this would lead to inconsistencies -> think about developers using TOP mechanism to add
   * constraints to BasicSymbolsBindings.addFunctionBinding -> they would expect them to be
   * respected by a generated implementation of OOSymbolsBindings
   */

  @Override
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException {
    if (isConflictingOOTypeBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    basicSymbolsBindings.addTypeBinding(binding.cast());
    ooTypeBindings.add(binding);
  }

  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException {
    if (isConflictingFieldBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    basicSymbolsBindings.addVariableBinding(binding.cast());
    fieldBindings.add(binding);
  }

  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException {
    if (isConflictingMethodBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    basicSymbolsBindings.addFunctionBinding(binding.cast());
    methodBindings.add(binding);
  }

  @Override
  public boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding) {
    return ooTypeBindings.conflictsWith(binding)
        || basicSymbolsBindings.isConflictingTypeBinding(binding.cast());
  }

  @Override
  public boolean isConflictingFieldBinding(Binding<FieldSymbol> binding) {
    return fieldBindings.conflictsWith(binding)
        || basicSymbolsBindings.isConflictingVariableBinding(binding.cast());
  }

  @Override
  public boolean isConflictingMethodBinding(Binding<MethodSymbol> binding) {
    return methodBindings.conflictsWith(binding)
        || basicSymbolsBindings.isConflictingFunctionBinding(binding.cast());
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
  public void addAll(OOSymbolsBindings bindings) throws BindingConflictException {
    // 1. check for conflicts
    if (isConflicting(bindings)) {
      throw new BindingConflictException();
    }
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

  @Override
  public boolean isConflicting(OOSymbolsBindings bindings) {
    if (basicSymbolsBindings.isConflicting(bindings)) {
      return true;
    }
    // Check for conflicts in OO specific bindings
    for (Binding<OOTypeSymbol> binding : bindings.getOOTypeBindings()) {
      if (isConflictingOOTypeBinding(binding)) {
        return true;
      }
    }
    for (Binding<FieldSymbol> binding : bindings.getFieldBindings()) {
      if (isConflictingFieldBinding(binding)) {
        return true;
      }
    }
    for (Binding<MethodSymbol> binding : bindings.getMethodBindings()) {
      if (isConflictingMethodBinding(binding)) {
        return true;
      }
    }
    return false;
  }

  // ========== Delegate to BasicSymbolsBindings methods ==========

  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    /*
     * IMPL NOTE: We do not need to check if the typeSymbol is an OOTypeSymbol here
     * because we added OOTypeSymbol bindings to the basicSymbolsBindings as well.
     */
    return basicSymbolsBindings.getBinding(typeSymbol);
  }

  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    // IMPL NOTE: Returns OOTypeSymbols as well
    return basicSymbolsBindings.getTypeBindings();
  }

  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof OOTypeSymbol) {
      addOOTypeBinding(binding.cast());
    } else {
      basicSymbolsBindings.addTypeBinding(binding);
    }
  }

  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof OOTypeSymbol) {
      return isConflictingOOTypeBinding(binding.cast());
    } else {
      return basicSymbolsBindings.isConflictingTypeBinding(binding);
    }
  }

  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    /*
     * IMPL NOTE: We do not need to check if the variableSymbol is a FieldSymbols here
     * because we added FieldSymbol bindings to the basicSymbolsBindings as well.
     */
    return basicSymbolsBindings.getBinding(variableSymbol);
  }

  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() {
    // IMPL NOTE: Returns FieldSymbols as well
    return basicSymbolsBindings.getVariableBindings();
  }

  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof FieldSymbol) {
      addFieldBinding(binding.cast());
    } else {
      basicSymbolsBindings.addVariableBinding(binding);
    }
  }

  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof FieldSymbol) {
      return isConflictingFieldBinding(binding.cast());
    } else {
      return basicSymbolsBindings.isConflictingVariableBinding(binding);
    }
  }

  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    /*
     * IMPL NOTE: We do not need to check if the functionSymbol is a MethodSymbol here
     * because we added MethodSymbol bindings to the basicSymbolsBindings as well.
     */
    return basicSymbolsBindings.getBinding(functionSymbol);
  }

  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() {
    // IMPL NOTE: Returns MethodSymbols as well
    return basicSymbolsBindings.getFunctionBindings();
  }

  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof MethodSymbol) {
      addMethodBinding(binding.cast());
    } else {
      basicSymbolsBindings.addFunctionBinding(binding);
    }
  }

  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    // TODO should we check this here?
    if (binding.getReferenceElement() instanceof MethodSymbol) {
      return isConflictingMethodBinding(binding.cast());
    } else {
      return basicSymbolsBindings.isConflictingFunctionBinding(binding);
    }
  }

  @Override
  public void addAll(BasicSymbolsBindings bindings) throws BindingConflictException {
    // TODO should we check this here?
    if (bindings instanceof OOSymbolsBindings) {
      addAll((OOSymbolsBindings) bindings);
    } else {
      basicSymbolsBindings.addAll(bindings);
    }
  }

  @Override
  public boolean isConflicting(BasicSymbolsBindings otherBindings) {
    // TODO should we check this here?
    if (otherBindings instanceof OOSymbolsBindings) {
      return isConflicting((OOSymbolsBindings) otherBindings);
    } else {
      return basicSymbolsBindings.isConflicting(otherBindings);
    }
  }
}
