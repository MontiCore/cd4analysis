package de.monticore.refmodels;

import de.monticore.refadaptation.Binding;
import de.monticore.symboltable.ISymbol;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
   * @param getIncarnations function to retrieve all incarnations of a symbol
   * @param getBinding function to retrieve the binding for a symbol
   * @param refSymbol the reference symbol for which the incarnations should be retrieved
   * @return a set of incarnations that are allowed for the given reference symbol
   *
   * @param <T> the kind of symbol
   */
  public static <T extends ISymbol> Set<T> getRestrictIncarnations(
          Function<T, Set<T>> getIncarnations,
          Function<T, Optional<Binding<T>>> getBinding,
          T refSymbol) {
    return getBinding.apply(refSymbol)
            .map(binding -> {
              if (binding.isStrict()) {
                return binding.getConcreteElements();
              } else {
                return getIncarnations.apply(refSymbol);
              }
            })
            .orElseGet(() -> getIncarnations.apply(refSymbol));
  }
}
