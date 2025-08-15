/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.refmodel.Bindings;
import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;

import java.util.Set;
import java.util.stream.Collectors;

public class OOSymbolsBindings extends OOSymbolsBindingsTOP {
  
  public OOSymbolsBindings() {
    super();
  }
  
  protected OOSymbolsBindings(IBasicSymbolsBindings basicSymbolsBindings,
      Bindings<OOTypeSymbol> ooTypeBindings, Bindings<FieldSymbol> fieldBindings,
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
  public IOOSymbolsBindings getMethodImpliedBindings(Binding<MethodSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings bindings = super.getMethodImpliedBindings(binding);
    bindings.addTypeBinding(getDeclaringTypeBinding(binding));
    return bindings;
  }
  
  @Override
  public IOOSymbolsBindings getFieldImpliedBindings(Binding<FieldSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings bindings = super.getFieldImpliedBindings(binding);
    bindings.addTypeBinding(getDeclaringTypeBinding(binding));
    return bindings;
  }
  
  @Override
  public IOOSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings bindings = super.getVariableImpliedBindings(binding);
    /* A VariableSymbol instance might still represent a field of a CDType, although
     * it is not a FieldSymbol. Therefore, we try to get a declaring type by the heuristic in
     * {@link #getDeclaringTypeIfPresent(ISymbol)}.
     */
    if (isDeclaringTypeSymbolPresent(binding.getReferenceElement())) {
      bindings.addTypeBinding(getDeclaringTypeBinding(binding));
    }
    return bindings;
  }
  
  @Override
  public IOOSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException {
    IOOSymbolsBindings bindings = super.getFunctionImpliedBindings(binding);
    /* A FunctionSymbol instance might still represent a method of a CDType, although
     * it is not a MethodSymbol. Therefore, we try to get a declaring type by the heuristic in
     * {@link #getDeclaringTypeIfPresent(ISymbol)}.
     */
    if (isDeclaringTypeSymbolPresent(binding.getReferenceElement())) {
      bindings.addTypeBinding(getDeclaringTypeBinding(binding));
    }
    return bindings;
  }
  
  /**
   * Returns the type binding for the declaring type of a method/field binding.
   *
   * @param binding the binding for the method/field
   * @return the binding for the declaring type of the method/field
   * @param <T> the type of the symbol
   */
  protected <T extends ISymbol> Binding<TypeSymbol> getDeclaringTypeBinding(Binding<T> binding) {
    TypeSymbol declaringRefType = SymbolUtil.getDeclaringTypeSymbol(binding.getReferenceElement());
    Set<TypeSymbol> declaringTypeIncs = binding.getConcreteElements().stream().map(
        SymbolUtil::getDeclaringTypeSymbol).collect(Collectors.toSet());
    if (binding.isStrict()) {
      return Binding.createStrict(declaringRefType, declaringTypeIncs.stream().findFirst()
          .orElseThrow());
    }
    else {
      return Binding.createAggregate(declaringRefType, declaringTypeIncs);
    }
  }
  
  /**
   * Checks if the given symbol is declared in a type symbol, i.e., if the spanning symbol of the
   * enclosing scope of the symbol is a {@link TypeSymbol}.
   *
   * @param symbol the symbol to check
   * @return true if the symbol is declared in a type symbol, false otherwise
   */
  protected boolean isDeclaringTypeSymbolPresent(ISymbol symbol) {
    return symbol.getEnclosingScope().isPresentSpanningSymbol() && symbol.getEnclosingScope()
        .getSpanningSymbol() instanceof TypeSymbol;
  }
  
}
