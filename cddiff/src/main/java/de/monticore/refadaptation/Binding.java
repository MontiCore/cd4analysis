package de.monticore.refadaptation;

import java.util.Set;

/**
 * TODO document
 *
 * A {@link Binding} instance is immutable!
 *
 * @param <T>
 */
public class Binding<T> {
  public enum Kind {
    STRICT,       // only these incarnations are allowed
    AGGREGATE     // these incarnations are required but others may also be present
  }

  private final T referenceElement;
  private final Set<T> concreteElements;
  private final Kind kind;

  protected Binding(T referenceElement, Set<T> concreteElements, Kind kind) {
    this.referenceElement = referenceElement;
    this.concreteElements = Set.copyOf(concreteElements);
    this.kind = kind;
  }

  public static <T> Binding<T> createStrict(T referenceElement, T concreteElement) {
    return new Binding<>(referenceElement, Set.of(concreteElement), Kind.STRICT);
  }

  public static <T> Binding<T> createAggregate(T referenceElement, Set<T> concreteElements) {
    return new Binding<>(referenceElement, concreteElements, Kind.AGGREGATE);
  }

  public T getReferenceElement() {
    return referenceElement;
  }

  public Set<T> getConcreteElements() {
    return concreteElements;
  }

  public T getStrictConcreteElement() {
    if (!isStrict() || concreteElements.size() != 1) {
      throw new IllegalStateException("This binding is not strict or does not have exactly one concrete element.");
    }
    return concreteElements.iterator().next();
  }

  public Kind getKind() {
    return kind;
  }

  public boolean isStrict() {
    return kind == Kind.STRICT;
  }

  public boolean isAggregate() {
    return kind == Kind.AGGREGATE;
  }

  public boolean conflictsWith(Binding<T> other) {
    if (!referenceElement.equals(other.referenceElement)) return false;

    if (this.isStrict() && other.isStrict()) {
      return !this.concreteElements.equals(other.concreteElements);
    }

    if (this.isStrict() || other.isStrict()) {
      return !this.concreteElements.containsAll(other.concreteElements) &&
              !other.concreteElements.containsAll(this.concreteElements);
    }

    // Aggregates are compatible
    return false;
  }

  public <O> Binding<O> cast() {
    @SuppressWarnings("unchecked")
    Binding<O> casted = (Binding<O>) this;
    return casted;
  }

  @Override
  public String toString() {
    return kind + " binding of " + referenceElement + " to " + concreteElements;
  }
}