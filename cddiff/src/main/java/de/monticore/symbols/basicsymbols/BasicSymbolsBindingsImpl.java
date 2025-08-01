package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.types.check.SymTypeExpression;

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

  /**
   * Returns the type bindings that are implied by a function binding.
   * This includes:
   * 1. The parameter types of the function
   * 2. The return type of the function if it is not void
   * 3. Possible type parameters if one of the types is a generic type
   *
   * @param binding the function binding
   * @return a set of type bindings that are implied by the function binding
   */
  @Override
  public BasicSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding) throws BindingConflictException {
    // 1. Parameter types
    // TODO We can get issues here depending on how we map concrete parameters to ref parameters
    //   -> should we introduce method parameters to CDIncarnationMapping ?
    // for now, we assume reference and concrete function have the same number of parameters
    List<VariableSymbol> refParams =  binding.getReferenceElement().getParameterList();
    BasicSymbolsBindings bindings = new BasicSymbolsBindingsImpl();
    for (int i=0; i<refParams.size(); i++) {
      SymTypeExpression refParamType = refParams.get(i).getType();
      final int currentParamIndex = i;
      Set<SymTypeExpression> conParamTypes = binding.getConcreteElements().stream()
              .map(f -> f.getParameterList().get(currentParamIndex).getType())
              .collect(Collectors.toSet());
      bindings.addAll(getImpliedTypeBindings(refParamType,conParamTypes, binding.isStrict()));
    }

    // 2. Return type
    if (!binding.getReferenceElement().getType().isVoidType()) {
      SymTypeExpression refReturnType = binding.getReferenceElement().getType();
      Set<SymTypeExpression> conReturnTypes = binding.getConcreteElements().stream()
              .map(FunctionSymbolTOP::getType)
              .collect(Collectors.toSet());
      bindings.addAll(getImpliedTypeBindings(refReturnType, conReturnTypes, binding.isStrict()));
    }
    return bindings;
  }

  /**
   * Returns bindings implied by a variable binding.<br>
   * his includes:
   *  1. The type of the variable itself
   *  2. Possible type parameters if the type is a generic type
   *
   * @param binding the variable binding
   * @return the bindings implied by the variable binding
   */
  @Override
  public BasicSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding) throws BindingConflictException {
    SymTypeExpression refType = binding.getReferenceElement().getType();
    Set<SymTypeExpression> conTypes = binding.getConcreteElements().stream()
            .map(VariableSymbolTOP::getType)
            .collect(Collectors.toSet());
    return getImpliedTypeBindings(refType, conTypes, binding.isStrict());
  }

  /**
   * Returns type bindings that are implied by the binding between the given reference and concrete
   * types. The types are given as SymTypeExpressions so we can handle generic types and return
   * bindings for possibly (deeply) nested type parameters.
   *
   * @param refSymType the reference type
   * @param conSymTypes the concrete types that are bound to the reference type
   * @param strict if true, the returned bindings are strict
   * @return a set of type bindings
   * @throws BindingConflictException if the implied bindings conflict with each other.
   *    This indicates an implementation error
   */
  protected BasicSymbolsBindings getImpliedTypeBindings(
          SymTypeExpression refSymType,
          Set<SymTypeExpression> conSymTypes,
          boolean strict)
          throws BindingConflictException {
    BasicSymbolsBindings bindings = new BasicSymbolsBindingsImpl();
    // 1. handle the type itself
    TypeSymbol refType = refSymType.getTypeInfo();
    Set<TypeSymbol> conTypes = conSymTypes.stream()
            .map(SymTypeExpression::getTypeInfo)
            .collect(Collectors.toSet());
    if (strict) {
      bindings.addTypeBinding(Binding.createStrict(refType, conTypes.stream().findFirst().orElseThrow()));
    } else {
      bindings.addTypeBinding(Binding.createAggregate(refType, conTypes));
    }
    // 2. handle type parameters if the type is a generic type
    if (refSymType.isGenericType()) {
      List<SymTypeExpression> refArguments = refSymType.asGenericType().getArgumentList();
      // Assumption: if reference type is generic concrete is generic as well and has the same
      // number of parameters
      for (int i=0; i<refArguments.size(); i++) {
        SymTypeExpression refArgumentType = refArguments.get(i);
        final int currentParamIndex = i;
        Set<SymTypeExpression> conParamTypes = conSymTypes.stream()
                .map(t -> t.asGenericType().getArgument(currentParamIndex))
                .collect(Collectors.toSet());
        bindings.addAll(getImpliedTypeBindings(refArgumentType, conParamTypes, strict));
      }
    }
    return bindings;
  }
}
