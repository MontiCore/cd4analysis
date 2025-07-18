package de.monticore.symbols.basicsymbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Optional;
import java.util.Set;

public class BasicSymbolsBindingsImplTOP implements BasicSymbolsBindings {

  // TODO -> This could be a generated TOP class and developers can override e.g., addFieldBinding to additionally check if it conflicts with type bindings!
  //      -> That's detailed semantic a generator cannot know about!
  //      -> other example: addFunctionBinding but a parameter type of the function is already bound to something else
  //        -> maybe throw exception in that case so adaptation / variant logic can handle ti and drop the variant

  protected final Bindings<TypeSymbol> typeBindings;
  protected final Bindings<VariableSymbol> variableBindings;
  protected final Bindings<FunctionSymbol> functionBindings;


  protected BasicSymbolsBindingsImplTOP(Bindings<TypeSymbol> typeBindings,
                                        Bindings<VariableSymbol> variableBindings,
                                        Bindings<FunctionSymbol> functionBindings) {
    this.typeBindings = typeBindings;
    this.variableBindings = variableBindings;
    this.functionBindings = functionBindings;
  }

  public BasicSymbolsBindingsImplTOP() {
    this(new Bindings<>(), new Bindings<>(), new Bindings<>());
  }

  @Override
  public BasicSymbolsBindings copy() {
    return new BasicSymbolsBindingsImplTOP(
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
    functionBindings.add(binding);
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
