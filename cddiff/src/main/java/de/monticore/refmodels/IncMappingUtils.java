package de.monticore.refmodels;

import de.monticore.refadaptation.Binding;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class IncMappingUtils {

  private IncMappingUtils() {
  }

  public static <T> Set<T> getRestrictIncarnations(
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
