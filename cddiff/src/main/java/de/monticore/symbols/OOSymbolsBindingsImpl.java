package de.monticore.symbols;

import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.Bindings;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

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
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) {
    // 1. enforce OO specific constraints
    // TODO check for conflicts with existing bindings OOTypeSymbol, FieldSymbol, MethodSymbol
    super.addOOTypeBinding(binding);
  }

  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) {
    // 1. enforce OO specific constraints
    // TODO check for declaring type conflicts with existing bindings OOTypeSymbol
    // 2. call BasicSymbolsBindings.addTypeBinding (already checks conflicts with existing TypeSymbol)
    super.addFieldBinding(binding);
  }

  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) {
    // 1. enforce OO specific constraints
    createTypeBindingForMethodBinding(binding);
    // TODO check for declaring type conflicts with existing bindings OOTypeSymbol
    super.addMethodBinding(binding);
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
}
