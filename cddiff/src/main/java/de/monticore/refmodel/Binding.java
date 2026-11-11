/* (c) https://github.com/MontiCore/monticore */
package de.monticore.refmodel;

import de.monticore.symboltable.ISymbol;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A binding fixes a reference symbol to a concrete symbol or a set of concrete symbols. The
 * concrete symbols MUST be incarnations of the reference symbol!<br>
 * In context of incarnation mappings, this means that although there are multiple incarnations
 * of a reference symbol, only a certain set of incarnations is allowed to be used.<br>
 * <br>
 * If a STRICT binding is attached to an adaptation variant during reference artifact adaptation,
 * this variant can only be combined with variants having the EXACT same binding.<br>
 * <br>
 * TODO defined aggregate bindings properly!
 *
 * A {@link Binding} instance is immutable!
 *
 * @param <T> the kind of symbol this binding refers to.
 */
public class Binding<T extends ISymbol> {
  
  /*
   * TODO rework the definition of STRICT vs. AGGREGATE:
   *  - Can there be STRICT bindings with multiple concrete elements?
   *  - Can there be AGGREGATE bindings with a single concrete element?
   *  - Is there a different kind of binding that only limits the concrete elements to a certain
   *    set of incarnations?
   */
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
  
  public static <T extends ISymbol> Binding<T> createStrict(T referenceElement, T concreteElement) {
    return new Binding<>(referenceElement, Set.of(concreteElement), Kind.STRICT);
  }
  
  public static <T extends ISymbol> Binding<T> createAggregate(T referenceElement,
      Set<T> concreteElements) {
    return new Binding<>(referenceElement, concreteElements, Kind.AGGREGATE);
  }
  
  public T getReferenceElement() { return referenceElement; }
  
  public Set<T> getConcreteElements() { return concreteElements; }
  
  public T getStrictConcreteElement() {
    if (!isStrict() || concreteElements.size() != 1) {
      throw new IllegalStateException(
          "This binding is not strict or does not have exactly one concrete element.");
    }
    return concreteElements.iterator().next();
  }
  
  public Kind getKind() { return kind; }
  
  public boolean isStrict() { return kind == Kind.STRICT; }
  
  public boolean isAggregate() { return kind == Kind.AGGREGATE; }
  
  public boolean conflictsWith(Binding<T> other, Function<ISymbol, String> computeKeyFun) {
    /*
     * TODO review if this still makes any sense?! -> or simplify:
     *  - strict: exactly one concrete element -> rename to FIXED ?
     *  -> aggregate -> exactly multiple elements ??
     */
    // NOTE: We have to apply the computeKeyFun here to avoid having two symbol instances representing
    // the same symbol but not being equal.
    // e.g. in some cases there seem to be multiple java.util.Set instances
    String thisReferenceKey = computeKeyFun.apply(referenceElement);
    String otherReferenceKey = computeKeyFun.apply(other.referenceElement);
    if (!thisReferenceKey.equals(otherReferenceKey)) {
      return false;
    }
    
    Set<String> thisConcreteKeys = concreteElements.stream().map(computeKeyFun).collect(Collectors
        .toSet());
    Set<String> otherConcreteKeys = other.concreteElements.stream().map(computeKeyFun).collect(
        Collectors.toCollection(LinkedHashSet::new));
    if (this.isStrict() && other.isStrict()) {
      return !thisConcreteKeys.equals(otherConcreteKeys);
    }
    
    if (this.isStrict() || other.isStrict()) {
      return !thisConcreteKeys.containsAll(otherConcreteKeys) && !otherConcreteKeys.containsAll(
          thisConcreteKeys);
    }
    
    // Aggregates are compatible
    return false;
  }
  
  public Binding<T> mergeOrThrowConflict(Binding<T> other, Function<ISymbol, String> computeKeyFun)
      throws BindingConflictException {
    // TODO adjust implementation for aggregate bindings
    Set<String> thisConcreteKeys = concreteElements.stream().map(computeKeyFun).collect(Collectors
        .toSet());
    Set<String> otherConcreteKeys = other.concreteElements.stream().map(computeKeyFun).collect(
        Collectors.toCollection(LinkedHashSet::new));
    if (this.isStrict() && other.isStrict()) {
      if (thisConcreteKeys.equals(otherConcreteKeys)) {
        return this; // No conflict, return the existing binding
      }
      else {
        throw new BindingConflictException(other);
      }
    }
    else if (this.isAggregate() || other.isAggregate()) {
      // TODO check if this makes sense?!
      Set<T> mergedConcreteElements = Set.copyOf(this.concreteElements);
      mergedConcreteElements.addAll(other.concreteElements);
      return new Binding<>(this.referenceElement, mergedConcreteElements, Kind.AGGREGATE);
    }
    else {
      throw new BindingConflictException(other);
    }
  }
  
  /**
   * <b>WARNING: use with care!</b><br>
   * <br>
   * Casts this binding to a binding of a different symbol type.<br>
   * This is a workaround for limitations of the Java type system, which does not allow
   * to, e.g. cast a {@code Binding<FieldSymbol>} to a {@code Binding<VariableSymbol>} even though
   * {@code FieldSymbol} extends {@code VariableSymbol}.
   * <br>
   * Instead of writing an explicit <i>unchecked</i> cast producing a warning everytime, we provide
   * this helper method.
   *
   * @return a binding of the specified type
   * @param <O> the type to cast teh symbols to, must extend {@link ISymbol}
   */
  public <O extends ISymbol> Binding<O> cast() {
    @SuppressWarnings("unchecked")
    Binding<O> casted = (Binding<O>) this;
    return casted;
  }
  
  @Override
  public String toString() {
    return kind + " binding of " + referenceElement + " to " + concreteElements;
  }
  
}
