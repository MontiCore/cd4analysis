package de.monticore.symbols.basicsymbols;

/**
 * Handcoded extension of {@link BasicSymbolsRestrictedIncMappingTOP} to enforce the special
 * semantics of the BasicSymbols language.<br>
 * The incarnations of TypeSymbols influence which VariableSymbols, or FunctionSymbols are valid
 * incarnations in a certain context. This is because a VariableSymbol has a certain type, and a
 * FunctionSymbol has a certain return type & parameter
 * types, which are all TypeSymbols. If on of these types is not incarnated in a context, the
 * VariableSymbol/FunctionSymbol cannot be used as well, i.e. it is not an incarnation.
 */
public class BasicSymbolsRestrictedIncMapping extends BasicSymbolsRestrictedIncMappingTOP {

  public BasicSymbolsRestrictedIncMapping(BasicSymbolsLocalIncMapping originalMapping, BasicSymbolsBindings bindings) {
    super(originalMapping, bindings);
  }

  /*
   * TODO Implement the special restrictions mentioned above
   */
}
