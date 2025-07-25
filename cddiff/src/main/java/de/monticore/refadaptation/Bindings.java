package de.monticore.refadaptation;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * NOTE: This is a helper class for IMPLEMENTATION
 *
 * TOO decide if this should be a type used in interfaces or only an internal helper
 *
 * TODO Think about using this instead of Set<Binding>. then we have a datastructure
 * that can always enforce consistency of the bindings.
 * -> disadvantage: this class is mutable (we dont want to expose mutable data structures). And for
 * read-only Set<Binding> is still perfectly fine
 *
 * @param <T>
 */
public class Bindings<T extends ISymbol> {

  private final Map<String, Binding<T>> bindings = new HashMap<>();
  private final Function<ISymbol, String> computeKeyFunction;

  public Bindings(Function<ISymbol, String> computeKeyFunction) {
    this.computeKeyFunction = computeKeyFunction;
  }

  public Bindings() {
    this(Bindings::computeDefaultKey);
  }

  public Bindings(Bindings<T> other) {
    this.computeKeyFunction = other.computeKeyFunction;
    this.bindings.putAll(other.bindings);
  }

  public static String computeDefaultKey(ISymbol symbol) {
    return symbol.getFullName();
  }

  // TODO add compatibility checks etc

  // TODO throws BindingConflictException
  public void add(Binding<T> binding) {
    // TODO check again
    Preconditions.checkNotNull(binding);
    String key = computeKeyFunction.apply(binding.getReferenceElement());
    bindings.put(key, binding);
  }

  public Optional<Binding<T>> get(T refElement) {
    Preconditions.checkNotNull(refElement);
    String key = computeKeyFunction.apply(refElement);
    return Optional.ofNullable(bindings.get(key));
  }

  public Set<Binding<T>> getAll() {
    return Set.copyOf(bindings.values());
  }
}
