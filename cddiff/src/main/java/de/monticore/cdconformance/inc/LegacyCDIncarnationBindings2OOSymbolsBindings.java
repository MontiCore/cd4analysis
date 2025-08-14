/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import com.google.common.collect.SetMultimap;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdconcretization.util.MethodSignatureString;
import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsBindings;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsBindings;
import de.monticore.symbols.oosymbols.refmodel.OOSymbolsBindings;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Deprecated
public class LegacyCDIncarnationBindings2OOSymbolsBindings implements IOOSymbolsBindings {
  
  private final CDIncarnationBindings cdIncarnationBindings;
  private final ISymbol contextSymbol;
  private final IScope scope;
  private final String contextSymbolKey;
  
  public LegacyCDIncarnationBindings2OOSymbolsBindings(CDIncarnationBindings cdIncarnationBindings,
      ISymbol contextSymbol, IScope scope, String contextSymbolKey) {
    this.cdIncarnationBindings = cdIncarnationBindings;
    this.contextSymbol = contextSymbol;
    this.scope = scope;
    this.contextSymbolKey = contextSymbolKey;
  }
  
  @Override
  public IOOSymbolsBindings copy() {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    Set<TypeSymbol> conElements;
    if (contextSymbol != null) {
      conElements = cdIncarnationBindings.getBindings(contextSymbol, typeSymbol);
    }
    else if (scope != null) {
      conElements = cdIncarnationBindings.getBindings(scope, typeSymbol);
    }
    else {
      throw new IllegalStateException("No context symbol or scope provided for binding lookup.");
    }
    return createBinding(typeSymbol, conElements);
  }
  
  private <T extends ISymbol> Optional<Binding<T>> createBinding(T refSymbol,
      Set<T> concreteSymbols) {
    if (concreteSymbols.isEmpty()) {
      return Optional.empty();
    }
    else if (concreteSymbols.size() == 1) {
      return Optional.of(Binding.createStrict(refSymbol, concreteSymbols.iterator().next()));
    }
    else {
      // TODO ??
      throw new IllegalStateException("Unsupported binding to multiple incarnations ");
    }
  }
  
  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    SetMultimap<String, TypeSymbol> bindings;
    if (contextSymbol != null) {
      bindings = cdIncarnationBindings.getTypeBindings(contextSymbol);
    }
    else if (scope != null) {
      bindings = cdIncarnationBindings.getTypeBindings(scope);
    }
    else {
      throw new IllegalStateException("No context symbol or scope provided for binding lookup.");
    }
    return bindings.asMap().entrySet().stream().map(entry -> {
      if (entry.getValue().size() != 1) {
        throw new IllegalStateException("Expected exactly one TypeSymbol for key: " + entry.getKey()
            + ", but found: " + entry.getValue().size());
      }
      return Binding.createStrict(resolveTypeSymbolByKey(entry.getKey()), entry.getValue().stream()
          .findFirst().orElseThrow());
    }).collect(Collectors.toSet());
  }
  
  @Override
  public Set<Binding<FieldSymbol>> getFieldBindings() {
    SetMultimap<String, FieldSymbol> bindings;
    if (contextSymbol != null) {
      bindings = cdIncarnationBindings.getFieldBindings(contextSymbol);
    }
    else if (scope != null) {
      bindings = cdIncarnationBindings.getFieldBindings(scope);
    }
    else {
      throw new IllegalStateException("No context symbol or scope provided for binding lookup.");
    }
    return bindings.asMap().entrySet().stream().map(entry -> {
      if (entry.getValue().size() != 1) {
        throw new IllegalStateException("Expected exactly one TypeSymbol for key: " + entry.getKey()
            + ", but found: " + entry.getValue().size());
      }
      return Binding.createStrict(resolveFieldSymbolByKey(entry.getKey()), entry.getValue().stream()
          .findFirst().orElseThrow());
    }).collect(Collectors.toSet());
  }
  
  protected TypeSymbol resolveTypeSymbolByKey(String symbolKey) {
    return CD4CodeMill.globalScope().resolveType(symbolKey).orElseThrow();
  }
  
  protected FieldSymbol resolveFieldSymbolByKey(String symbolKey) {
    return CD4CodeMill.globalScope().resolveField(symbolKey).orElseThrow();
  }
  
  protected MethodSymbol resolveMethodSymbolByKey(String symbolKey) {
    return MethodSignatureString.resolveMethodSignature(CD4CodeMill.globalScope(), symbolKey)
        .orElseThrow();
  }
  
  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    return false;
  }
  
  @Override
  public IOOSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding)
      throws BindingConflictException {
    return new OOSymbolsBindings();
  }
  
  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    throw new UnsupportedOperationException("no variable symbols supported yet");
  }
  
  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    if (functionSymbol instanceof MethodSymbol) {
      return getBinding((MethodSymbol) functionSymbol).map(Binding::cast);
    }
    else {
      throw new UnsupportedOperationException("Only method symbols supported yet");
    }
  }
  
  @Override
  public Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol) {
    return getBinding(typeSymbol).map(Binding::cast);
  }
  
  @Override
  public Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol) {
    Set<FieldSymbol> conElements;
    if (contextSymbol != null) {
      conElements = cdIncarnationBindings.getBindings(contextSymbol, fieldSymbol);
    }
    else if (scope != null) {
      conElements = cdIncarnationBindings.getBindings(scope, fieldSymbol);
    }
    else {
      throw new IllegalStateException("No context symbol or scope provided for binding lookup.");
    }
    return createBinding(fieldSymbol, conElements);
  }
  
  @Override
  public Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol) {
    Set<MethodSymbol> conElements;
    if (contextSymbol != null) {
      conElements = cdIncarnationBindings.getBindings(contextSymbol, methodSymbol);
    }
    else if (scope != null) {
      conElements = cdIncarnationBindings.getBindings(scope, methodSymbol);
    }
    else {
      throw new IllegalStateException("No context symbol or scope provided for binding lookup.");
    }
    return createBinding(methodSymbol, conElements);
  }
  
  @Override
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflictingFieldBinding(Binding<FieldSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflictingMethodBinding(Binding<MethodSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public IOOSymbolsBindings getOOTypeImpliedBindings(Binding<OOTypeSymbol> binding)
      throws BindingConflictException {
    return new OOSymbolsBindings();
  }
  
  @Override
  public IOOSymbolsBindings getFieldImpliedBindings(Binding<FieldSymbol> binding)
      throws BindingConflictException {
    return new OOSymbolsBindings();
  }
  
  @Override
  public IOOSymbolsBindings getMethodImpliedBindings(Binding<MethodSymbol> binding)
      throws BindingConflictException {
    return new OOSymbolsBindings();
  }
  
  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public IOOSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException {
    return new OOSymbolsBindings();
  }
  
  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public IOOSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException {
    return new OOSymbolsBindings();
  }
  
  @Override
  public void addAll(IOOSymbolsBindings bindings) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflicting(IOOSymbolsBindings otherBindings) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public void addAll(IBasicSymbolsBindings bindings) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public boolean isConflicting(IBasicSymbolsBindings otherBindings) {
    throw new UnsupportedOperationException("read only");
  }
  
  @Override
  public Set<Binding<OOTypeSymbol>> getOOTypeBindings() {
    return getTypeBindings().stream().filter(binding -> binding
        .getReferenceElement() instanceof OOTypeSymbol).map(Binding::<OOTypeSymbol> cast).collect(
            Collectors.toSet());
  }
  
  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() {
    return getMethodBindings().stream().map(Binding::<FunctionSymbol> cast).collect(Collectors
        .toSet());
  }
  
  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() {
    return getFieldBindings().stream().map(Binding::<VariableSymbol> cast).collect(Collectors
        .toSet());
  }
  
  @Override
  public Set<Binding<MethodSymbol>> getMethodBindings() {
    SetMultimap<String, MethodSymbol> bindings;
    if (contextSymbol != null) {
      bindings = cdIncarnationBindings.getMethodBindings(contextSymbol);
    }
    else if (scope != null) {
      bindings = cdIncarnationBindings.getMethodBindings(scope);
    }
    else {
      throw new IllegalStateException("No context symbol or scope provided for binding lookup.");
    }
    return bindings.asMap().entrySet().stream().map(entry -> {
      if (entry.getValue().size() != 1) {
        throw new IllegalStateException("Expected exactly one TypeSymbol for key: " + entry.getKey()
            + ", but found: " + entry.getValue().size());
      }
      return Binding.createStrict(resolveMethodSymbolByKey(entry.getKey()), entry.getValue()
          .stream().findFirst().orElseThrow());
    }).collect(Collectors.toSet());
  }
  
}
