package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicSymbolBindingsImpl implements BasicSymbolsBindings {

  // TODO maybe even handcode one class as TOP and other with actual name to show what could be generated

  // TODO pretty boring class ... just delegates to Bindings -> NOOOOOOO ->NO see blow & document dsing decision
  //   -> maybe just delegate from? -> NO
  //   -> This could be a generated TOP class and developers can override e.g., addFieldBinding to additionally check if it conflicts with type bindings!
  //      -> That's detailed semantic a generator cannot know about!
  //     -> other example: addFunctionBinding but a parameter type of the function is already bound to something else
  //        -> maybe throw exception in that case so adaptation / variant logic can handle ti and drop the variant

  protected final Bindings<TypeSymbol> typeBindings;
  protected final Bindings<VariableSymbol> variableBindings;
  protected final Bindings<FunctionSymbol> functionBindings;


  protected BasicSymbolBindingsImpl(Bindings<TypeSymbol> typeBindings,
                                 Bindings<VariableSymbol> variableBindings,
                                 Bindings<FunctionSymbol> functionBindings) {
    this.typeBindings = typeBindings;
    this.variableBindings = variableBindings;
    this.functionBindings = functionBindings;
  }

  public BasicSymbolBindingsImpl() {
    this(new Bindings<>(), new Bindings<>(), new Bindings<>());
  }

  @Override
  public BasicSymbolsBindings copy() {
    return new BasicSymbolBindingsImpl(
        new Bindings<>(typeBindings),
        new Bindings<>(variableBindings),
        new Bindings<>(functionBindings)
    );
  }

  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    return typeBindings.get(typeSymbol);
  }

  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    return typeBindings.getAll();
  }

  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) {
    typeBindings.add(binding);
  }

  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    return variableBindings.get(variableSymbol);
  }

  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() {
    return variableBindings.getAll();
  }

  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) {
    // TODO add checks to enforce no conflict with type bindings
    variableBindings.add(binding);
  }

  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    return functionBindings.get(functionSymbol);
  }

  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() {
    return functionBindings.getAll();
  }

  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) {
    // TODO If we throw some kind of exception here, would need a "transaction cocnept" for adding bindings > i.e. only add type bindings if the function can be added without conflicts
    // TODO add checks to enforce no conflict with type bindings -> should be solved by adding type bindings first
    addTypeBindingsForFunctionReturnType(binding);
    addTypeBindingsForFunctionParameterTypes(binding);
    functionBindings.add(binding);
  }

  protected void addTypeBindingsForFunctionReturnType(Binding<FunctionSymbol> binding) {
    TypeSymbol refReturnType = binding.getReferenceElement().getType().getTypeInfo();
    Set<TypeSymbol> concReturnTypes = binding.getConcreteElements().stream()
            .map(f -> f.getType().getTypeInfo())
            .collect(Collectors.toSet());
    Binding<TypeSymbol> typeBinding;
    if (binding.isStrict()) {
      typeBinding = Binding.createStrict(refReturnType, concReturnTypes.stream().findFirst().orElseThrow());
    } else {
      typeBinding = Binding.createAggregate(refReturnType, concReturnTypes);
    }
    addTypeBinding(typeBinding);
  }

  protected void addTypeBindingsForFunctionParameterTypes(Binding<FunctionSymbol> binding) {
    // TODO We can get issues here depending on how we map concrete parameters to ref parameters
    //   -> should we introduce method parameters to CDIncarnationMapping ?
    // for now, we assume reference and concrete function have the same number of parameters
    List<VariableSymbol> refParams =  binding.getReferenceElement().getParameterList();
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
      addTypeBinding(typeBinding);
    }

  }

  @Override
  public void addAll(BasicSymbolsBindings bindings) {
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
}
