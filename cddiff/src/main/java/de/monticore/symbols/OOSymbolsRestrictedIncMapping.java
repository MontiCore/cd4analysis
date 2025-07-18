package de.monticore.symbols;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Handcoded extension of {@link OOSymbolsRestrictedIncMappingTOP} to enforce the special semantics
 * of OOSymbols language, The incarnations of TypeSymbols influence which VariableSymbols,
 * FunctionSymbols, and MethodSymbols are valid incarnations in a certain context. Additional,
 * to the constraints introduced in
 * {@link de.monticore.symbols.basicsymbols.BasicSymbolsRestrictedIncMapping}, we aditionally
 * require that the declaring type of a VariableSymbol, FiledSymbol, FunctionSymbol, or MethodSymbol
 * is incarnated.
 */
public class OOSymbolsRestrictedIncMapping extends OOSymbolsRestrictedIncMappingTOP {

  public OOSymbolsRestrictedIncMapping(OOSymbolsLocalIncMapping originalMapping, OOSymbolsBindings bindings) {
    super(originalMapping, bindings);
  }

  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return filterIncarnationsByDeclaringType(
            super.getIncarnations(fieldSymbol),
            fieldSymbol
    );
  }

  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return filterIncarnationsByDeclaringType(
            super.getIncarnations(methodSymbol),
            methodSymbol
    );
  }

  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    return filterIncarnationsByDeclaringType(
        super.getIncarnations(variableSymbol),
        variableSymbol
    );
  }

  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    return filterIncarnationsByDeclaringType(
        super.getIncarnations(functionSymbol),
        functionSymbol
    );
  }

  /**
   * Filters the given set of incarnations by keeping only the incarnations whose declaring type
   * is also incarnated in the current context.
   *
   * @param incarnations the set of incarnations to filter
   * @param refSymbol the reference symbol whose declaring type is used for filtering
   * @return the filtered set of incarnations
   * @param <T> the type of the symbol
   */
  protected <T extends ISymbol> Set<T> filterIncarnationsByDeclaringType(Set<T> incarnations, T refSymbol) {
    final Optional<TypeSymbol> declaringRefType = getDeclaringType(refSymbol);
    final Set<TypeSymbol> declaringTypeIncs = declaringRefType.map(this::getIncarnations)
            .orElseGet(Collections::emptySet);
    if (declaringRefType.isPresent()) {
      return incarnations.stream()
              .filter(isDeclaringTypeIncarnatedPredicate(declaringTypeIncs))
              .collect(Collectors.toSet());
    }
    return incarnations;
  }

  /**
   * Returns a predicate that checks if a symbol's declaring type is in the given set
   * of declaring type incarnations.
   * @param declaringTypeIncarnations the set of declaring type incarnations to check against
   * @return the predicate
   */
  protected <T extends ISymbol> Predicate<T> isDeclaringTypeIncarnatedPredicate(Set<TypeSymbol> declaringTypeIncarnations) {
    return symbol -> {
      Optional<TypeSymbol> declaringType = getDeclaringType(symbol);
      if (declaringType.isEmpty()) {
        Log.warn("The symbol " + symbol.getFullName() + " has no declaring type although the reference type has. This might lead to unexpected behavior.");
        return true; // no declaring type, so we assume it is valid
      }
      return declaringTypeIncarnations.contains(declaringType.get());
    };
  }

  /**
   * Returns the declaring type of the given symbol, if it exists, e.g. the type in which an
   * attribute is declared.
   *
   * @param symbol the symbol for which to get the declaring type
   * @return the declaring type if it exists, otherwise an empty Optional
   */
  protected Optional<TypeSymbol> getDeclaringType(ISymbol symbol) {
    // TODO Get declaring type via spanning symbol of enclosing scope vs. resolve qualifier from symbol full name
    if (symbol.getEnclosingScope().isPresentSpanningSymbol()) {
      ISymbol spanningSymbol = symbol.getEnclosingScope().getSpanningSymbol();
      if (spanningSymbol instanceof TypeSymbol) {
        return Optional.of((TypeSymbol) spanningSymbol);
      }
    }
    return Optional.empty();
  }
}
