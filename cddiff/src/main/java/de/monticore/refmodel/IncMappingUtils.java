/* (c) https://github.com/MontiCore/monticore */
package de.monticore.refmodel;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for handling incarnation mappings and bindings.<br>
 * Specifically, it is used to reduce duplication in language specific (generated) code because in
 * fact we do not generate any code (yet).
 */
public class IncMappingUtils {
  
  private IncMappingUtils() {
  }
  
  /**
   * Returns the set of incarnations that are allowed for a given reference symbol,
   * taking into account the binding associated with it.
   * If the binding is strict, only the concrete elements of the binding are returned.
   * If not, the function still filters the incarnations such that no conflicting incarnations
   * are returned.<br>
   * <br>
   * For example, a FunctionSymbol has return & parameter types, which are all TypeSymbols. If one
   * of these types is not incarnated in a certain context, the FunctionSymbol cannot be used,
   * i.e. it is not an incarnation as well.
   *
   * @param getIncarnations function to retrieve all incarnations of a symbol
   * @param getBinding function to retrieve the binding for a symbol
   * @param refSymbol the reference symbol for which the incarnations should be retrieved
   * @return a set of incarnations that are allowed for the given reference symbol
   *
   * @param <T> the kind of symbol
   */
  public static <T extends ISymbol> Set<T> getRestrictIncarnations(
      Function<T, Set<T>> getIncarnations, Function<T, Optional<Binding<T>>> getBinding,
      Predicate<Binding<T>> isConflictingBinding, T refSymbol) {
    return getBinding.apply(refSymbol).map(binding -> {
      if (binding.isStrict()) {
        return binding.getConcreteElements();
      }
      else {
        return getIncarnations.apply(refSymbol);
      }
    }).orElseGet(() -> getIncarnations.apply(refSymbol)).stream().filter(incarnation -> {
      Binding<T> binding = Binding.createStrict(refSymbol, incarnation);
      return !isConflictingBinding.test(binding);
    }).collect(Collectors.toSet());
  }
  
}
