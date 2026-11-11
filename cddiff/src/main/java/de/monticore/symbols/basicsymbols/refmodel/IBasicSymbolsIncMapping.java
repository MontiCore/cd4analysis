/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

/**
 * Represents the incarnation mapping between a reference model and a concrete model for the symbols
 * defined by the "BasicSymbols" language.<br>
 * This interface not only provides access to all incarnation of a certain reference model but
 * knows a set of "bindings" that restrict the incarnations in context of a certain symbol
 * or scope.<br>
 *
 * @see IBasicSymbolsBindings
 * @see Binding
 */
public interface IBasicSymbolsIncMapping extends IBasicSymbolsLocalIncMapping {
  
  /*
   * Although an incarnation mapping is always defined for a single reference model and a single
   * concrete model, this does not necessarily mean that there is only a single concrete and
   * reference artifact! A "model" could also be composed of multiple artifacts defining distinct
   * symbols.
   * Therefore, we have no "getReferenceModel()" or "getConcreteModel()" methods here.
   */
  
  /**
   * Returns the scope that is used to symbols of the reference model.
   *
   * @return the scope for reference symbols
   */
  IBasicSymbolsScope getReferenceScope();
  
  /**
   * Returns the scope that is used to symbols of the concrete model.
   *
   * @return the scope for concrete symbols
   */
  IBasicSymbolsScope getConcreteScope();
  
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
  IBasicSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol);
  
  /**
   * Returns the local incarnation mapping for the given scope.<br>
   * This is the global incarnation mapping restricted by the bindings holding for the given scope.
   *
   * @param scope the scope for which the mapping should be returned
   * @return the local incarnation mapping for the given scope
   */
  IBasicSymbolsLocalIncMapping getScopedMapping(IScope scope);
  
  /**
   * Returns the <b>LOCAL</b> bindings holding at the context symbol represented by the given
   * key.<br>
   * <b>NOTE:</b> This method does not return bindings from enclosing scopes of the symbol, as no
   * scope information is available when using only the symbol key!<br>
   * <br>
   * If the returned bindings instance is changed, the changes <b>do affect</b> the actual
   * state of the incarnation mapping!
   *
   * @param contextSymbolKey the key representing the context symbol for which the bindings should
   * be returned
   * @return the scoped bindings for the given context symbol key
   *
   * @see #computeSymbolKey(ISymbol)
   */
  IBasicSymbolsBindings getLocalOnlyBindings(String contextSymbolKey);
  
  /**
   * Returns <b>all</b> bindings holding at the given context symbol, <b>including the bindings
   * inherited from enclosing scopes!</b><br>
   * <br>
   * If the returned bindings instance is changed, the changes <b>do affect</b> the actual
   * state of the incarnation mapping!
   *
   * @param contextSymbol the context symbol for which the bindings should be returned
   * @return the scoped bindings for the given context symbol
   */
  IBasicSymbolsBindings getScopedBindings(ISymbol contextSymbol);
  
  /**
   * Returns <b>all</b> bindings holding at the given scope, <b>including the bindings
   * inherited from enclosing scopes!</b><br>
   * <br>
   * If the returned bindings instance is changed, the changes <b>do affect</b> the actual
   * state of the incarnation mapping!
   *
   * @param scope the scope for which the bindings should be returned
   * @return the scoped bindings for the given scope
   */
  IBasicSymbolsBindings getScopedBindings(IScope scope);
  
}
