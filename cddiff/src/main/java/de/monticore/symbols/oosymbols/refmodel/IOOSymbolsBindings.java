package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Optional;
import java.util.Set;

// NOTE: Could be generated
/**
 * Represents a <i>consistent</i> set of bindings for the symbols defined by the "OOSymbols"
 * language. These are:
 * <ul>
 *   <li>{@link OOTypeSymbol}</li>
 *   <li>{@link FieldSymbol}</li>
 *   <li>{@link MethodSymbol}</li>
 * </ul>
 * Consistent means that no binding conflicts with another binding in this set.
 */
public interface IOOSymbolsBindings extends IBasicSymbolsBindings {

  IOOSymbolsBindings copy();

  Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol);
  Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol);
  Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol);

  void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException;

  void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException;

  void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException;

  boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding);
  boolean isConflictingFieldBinding(Binding<FieldSymbol> binding);
  boolean isConflictingMethodBinding(Binding<MethodSymbol> binding);

  IOOSymbolsBindings getOOTypeImpliedBindings(Binding<OOTypeSymbol> binding) throws BindingConflictException;
  IOOSymbolsBindings getFieldImpliedBindings(Binding<FieldSymbol> binding) throws BindingConflictException;
  IOOSymbolsBindings getMethodImpliedBindings(Binding<MethodSymbol> binding) throws BindingConflictException;

  // redefine methods from BasicSymbolsBindings for more precise return types


  @Override
  IOOSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding) throws BindingConflictException;

  @Override
  IOOSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding) throws BindingConflictException;

  @Override
  IOOSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding) throws BindingConflictException;

  Set<Binding<OOTypeSymbol>> getOOTypeBindings();

  Set<Binding<FieldSymbol>> getFieldBindings();

  Set<Binding<MethodSymbol>> getMethodBindings();

  void addAll(IOOSymbolsBindings bindings) throws BindingConflictException;

  boolean isConflicting(IOOSymbolsBindings otherBindings);
}
