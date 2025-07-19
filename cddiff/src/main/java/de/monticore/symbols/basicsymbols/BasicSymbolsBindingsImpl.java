package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicSymbolsBindingsImpl extends BasicSymbolsBindingsImplTOP {

  protected BasicSymbolsBindingsImpl(Bindings<TypeSymbol> typeBindings,
                                     Bindings<VariableSymbol> variableBindings,
                                     Bindings<FunctionSymbol> functionBindings) {
    super(typeBindings, variableBindings, functionBindings);
  }

  public BasicSymbolsBindingsImpl() {
    super();
  }

  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    // TODO check for conflicts with existing bindings for VariableSymbol, FunctionSymbol!
    return super.isConflictingTypeBinding(binding);
  }

  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException {
    super.addVariableBinding(binding);
    addTypeBinding(getVariableImpliedTypeBinding(binding));
  }

  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    return super.isConflictingVariableBinding(binding)
            && isConflictingTypeBinding(getVariableImpliedTypeBinding(binding));
  }

  /**
   * Returns the type binding that is implied by a variable binding.
   *
   * @param binding the variable binding
   * @return the type binding that is implied by the variable binding
   */
  protected Binding<TypeSymbol> getVariableImpliedTypeBinding(Binding<VariableSymbol> binding) {
    TypeSymbol varTypeSymbol = binding.getReferenceElement().getType().getTypeInfo();
    Set<TypeSymbol> conTypes = binding.getConcreteElements().stream()
            .map(f -> f.getType().getTypeInfo())
            .collect(Collectors.toSet());
    if (binding.isStrict()) {
      return Binding.createStrict(varTypeSymbol, conTypes.stream().findFirst().orElseThrow());
    } else {
      return Binding.createAggregate(varTypeSymbol, conTypes);
    }
  }

  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException {
    /*
     * IMPLEMENTATION NOTE: the superclass method makes sure to check for conflicts and
     * throw an exception if there is a conflict.
     * We only need to make sure to check all constraints, e.g. additional type bindings for
     * the function parameters and return type in 'isConflictingFunctionBinding'
     */
    super.addFunctionBinding(binding);
    for (Binding<TypeSymbol> typeBinding : getFunctionImpliedTypeBindings(binding)) {
      addTypeBinding(typeBinding);
    }
  }

  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    return super.isConflictingFunctionBinding(binding) && getFunctionImpliedTypeBindings(binding).stream()
            .anyMatch(this::isConflictingTypeBinding);
  }

  /**
   * Returns the type bindings that are implied by a function binding.
   * This includes:
   * 1. The parameter types of the function
   * 2. The return type of the function if it is not void
   *
   * @param binding the function binding
   * @return a set of type bindings that are implied by the function binding
   */
  protected Set<Binding<TypeSymbol>> getFunctionImpliedTypeBindings(Binding<FunctionSymbol> binding) {
    // 1. Parameter types
    Set<Binding<TypeSymbol>> bindings = new HashSet<>(getFunctionParameterTypeBindings(binding));

    // 2. Return type
    if (!binding.getReferenceElement().getType().isVoidType()) {
      TypeSymbol refReturnType = binding.getReferenceElement().getType().getTypeInfo();
      Set<TypeSymbol> concReturnTypes = binding.getConcreteElements().stream()
              .map(f -> f.getType().getTypeInfo())
              .collect(Collectors.toSet());
      if (binding.isStrict()) {
        bindings.add(Binding.createStrict(refReturnType, concReturnTypes.stream().findFirst().orElseThrow()));
      } else {
        bindings.add(Binding.createAggregate(refReturnType, concReturnTypes));
      }
    }
    return bindings;
  }

  protected Set<Binding<TypeSymbol>> getFunctionParameterTypeBindings(Binding<FunctionSymbol> binding) {
    // TODO We can get issues here depending on how we map concrete parameters to ref parameters
    //   -> should we introduce method parameters to CDIncarnationMapping ?
    // for now, we assume reference and concrete function have the same number of parameters
    List<VariableSymbol> refParams =  binding.getReferenceElement().getParameterList();
    Set<Binding<TypeSymbol>> bindings = new HashSet<>();
    for (int i=0; i<refParams.size(); i++) {
      TypeSymbol refParamType = refParams.get(i).getType().getTypeInfo();
      final int currentParamIndex = i;
      Set<TypeSymbol> conParamTypes = binding.getConcreteElements().stream()
              .map(f -> f.getParameterList().get(currentParamIndex).getType().getTypeInfo())
              .collect(Collectors.toSet());
      Binding<TypeSymbol> typeBinding;
      if (binding.isStrict()) {
        typeBinding = Binding.createStrict(refParamType, conParamTypes.stream().findFirst().orElseThrow());
      } else {
        typeBinding = Binding.createAggregate(refParamType, conParamTypes);
      }
      bindings.add(typeBinding);
    }
    return bindings;
  }
}
