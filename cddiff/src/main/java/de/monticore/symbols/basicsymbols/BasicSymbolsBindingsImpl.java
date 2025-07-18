package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

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
  public void addVariableBinding(Binding<VariableSymbol> binding) {
    // TODO add checks to enforce no conflict with type bindings
    super.addVariableBinding(binding);
  }

  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) {
    // TODO If we throw some kind of exception here, would need a "transaction cocnept" for adding bindings > i.e. only add type bindings if the function can be added without conflicts
    // TODO add checks to enforce no conflict with type bindings -> should be solved by adding type bindings first
    addTypeBindingsForFunctionReturnType(binding);
    addTypeBindingsForFunctionParameterTypes(binding);
    super.addFunctionBinding(binding);
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
}
