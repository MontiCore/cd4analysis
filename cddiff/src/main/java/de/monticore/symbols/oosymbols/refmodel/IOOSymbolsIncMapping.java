/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsIncMapping;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

// NOTE: Could be generated
public interface IOOSymbolsIncMapping extends IBasicSymbolsIncMapping, IOOSymbolsLocalIncMapping {
  
  /**
   * Returns the scope that is used to symbols of the reference model.
   *
   * @return the scope for reference symbols
   */
  IOOSymbolsScope getReferenceScope();
  
  /**
   * Returns the scope that is used to symbols of the concrete model.
   *
   * @return the scope for concrete symbols
   */
  IOOSymbolsScope getConcreteScope();
  
  /**
   * Compute the key for a symbol that is used to identify it in the incarnation mapping.<br>
   * <br>
   * We cannot use {@link ISymbol#getFullName()} in every case. For example, methods can be
   * overloaded (same name, different parameters) and thus have the same full name.
   * In such cases, the key must be computed differently.
   */
  String computeSymbolKey(ISymbol symbol);
  
  /**
   * Returns the local incarnation mapping for the given context symbol.<br>
   * This is the global incarnation mapping restricted by the bindings holding for the given
   * context symbol.
   *
   * @param contextSymbol the context symbol for which the mapping should be returned
   * @return the local incarnation mapping for the given context symbol
   */
  IOOSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol);
  
  /**
   * Returns the local incarnation mapping for the given scope.<br>
   * This is the global incarnation mapping restricted by the bindings holding for the given scope.
   *
   * @param scope the scope for which the mapping should be returned
   * @return the local incarnation mapping for the given scope
   */
  IOOSymbolsLocalIncMapping getScopedMapping(IScope scope);
  
  /**
   * Returns the bindings holding at the context symbol represented by the given key.<br>
   *
   * @param contextSymbolKey the key representing the context symbol for which the bindings should
   * be returned
   * @return the scoped bindings for the given context symbol key
   *
   * @see #computeSymbolKey(ISymbol)
   */
  IOOSymbolsBindings getScopedBindings(String contextSymbolKey);
  
  /**
   * Returns the bindings holding at the given context symbol.
   *
   * @param contextSymbol the context symbol for which the bindings should be returned
   * @return the scoped bindings for the given context symbol
   */
  IOOSymbolsBindings getScopedBindings(ISymbol contextSymbol);
  
  /**
   * Returns the bindings holding at the given scope.
   *
   * @param scope the scope for which the bindings should be returned
   * @return the scoped bindings for the given scope
   */
  IOOSymbolsBindings getScopedBindings(IScope scope);
  
}
