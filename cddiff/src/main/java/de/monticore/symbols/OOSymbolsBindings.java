package de.monticore.symbols;

import de.monticore.refadaptation.Binding;
import de.monticore.refadaptation.BindingConflictException;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
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
public interface OOSymbolsBindings extends BasicSymbolsBindings {

  OOSymbolsBindings copy();

  Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol);
  Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol);
  Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol);

  void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException;

  void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException;

  void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException;

  boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding);
  boolean isConflictingFieldBinding(Binding<FieldSymbol> binding);
  boolean isConflictingMethodBinding(Binding<MethodSymbol> binding);

  Set<Binding<OOTypeSymbol>> getOOTypeBindings();

  Set<Binding<FieldSymbol>> getFieldBindings();

  Set<Binding<MethodSymbol>> getMethodBindings();

  void addAll(OOSymbolsBindings bindings) throws BindingConflictException;

  boolean isConflicting(OOSymbolsBindings otherBindings);
}
