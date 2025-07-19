package de.monticore.refadaptation;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

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

  /**
   * Adds a binding to the set of bindings if there is no conflict with an existing binding.
   *
   * @param binding the binding to add
   * @throws BindingConflictException if there is a conflict with an existing binding.
   */
  public void add(Binding<T> binding) throws BindingConflictException {
    Preconditions.checkNotNull(binding);
    String key = computeKeyFunction.apply(binding.getReferenceElement());
    Binding<T> existingBinding = bindings.get(key);
    if (existingBinding != null) {
      bindings.put(key, existingBinding.mergeOrThrowConflict(binding));
    } else {
      bindings.put(key, binding);
    }
  }

  public boolean conflictsWith(Binding<T> binding) {
    return getConflictingBinding(binding).isPresent();
  }

  protected Optional<Binding<T>> getConflictingBinding(Binding<T> binding) {
    String key = computeKeyFunction.apply(binding.getReferenceElement());
    return Optional.ofNullable(bindings.get(key))
        .filter(existingBinding -> existingBinding.conflictsWith(binding));
  }

  protected void throwIfConflict(Binding<T> newBinding) throws BindingConflictException {
    Optional<Binding<T>> conflictingBinding = getConflictingBinding(newBinding);
    if (conflictingBinding.isPresent()) {
      Log.debug("Existing binding conflicts with new binding: " + conflictingBinding.get() + " - " + newBinding,
          Bindings.class.getName());
      throw new BindingConflictException(newBinding);
    }
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
