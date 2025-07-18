package de.monticore.symbols;

import de.monticore.refadaptation.Binding;
import de.monticore.symbols.basicsymbols.BasicSymbolsBindings;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.Optional;
import java.util.Set;

// NOTE: Could be generated
public interface OOSymbolsBindings extends BasicSymbolsBindings {

  OOSymbolsBindings copy();

  Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol);
  Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol);
  Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol);

  void addOOTypeBinding(Binding<OOTypeSymbol> binding);

  void addFieldBinding(Binding<FieldSymbol> binding);

  void addMethodBinding(Binding<MethodSymbol> binding);

  Set<Binding<OOTypeSymbol>> getOOTypeBindings();

  Set<Binding<FieldSymbol>> getFieldBindings();

  Set<Binding<MethodSymbol>> getMethodBindings();

  void addAll(OOSymbolsBindings bindings);
}
