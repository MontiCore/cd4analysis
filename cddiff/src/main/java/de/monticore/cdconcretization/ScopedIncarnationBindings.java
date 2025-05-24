package de.monticore.cdconcretization;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Using the incarnation binding, it is possible to restrict the incarnation mapping in a specific
 * scope, e.g. a reference type which has multiple incarnations in the whole CD can be mapped to a
 * specific single concrete type in the scope spanned by a certain concrete type. A mapping must
 * always be resolved in the scope of a concrete class diagram (CD) or some sub scope.
 */
public class ScopedIncarnationBindings {

  private static final String LOG_NAME = ScopedIncarnationBindings.class.getName();

  /**
   * The mapping of type incarnations for a specific scope. The key is the name of the concrete type
   * spanning the scope, and the value is the specific incarnation mapping for that scope. If a type
   * is not present in the mapping, it means that the mapping needs to be resolved in the parent
   * scope.
   */
  private final Map<String, Multimap<String, TypeSymbol>> typeBindings = new HashMap<>();

  private final Map<String, Multimap<String, FieldSymbol>> fieldBindings = new HashMap<>();

  public void addTypeBinding(
      String scopeSpanningSymbolName, TypeSymbol referenceType, TypeSymbol concreteTypes) {
    addTypeBinding(scopeSpanningSymbolName, referenceType, Set.of(concreteTypes));
  }

  /**
   * Adds a type binding for a specific scope. After calling this, within the scope, the reference
   * type is only incarnated by the given concrete types.
   *
   * @param scopeSpanningSymbolName the name of the concrete type spanning the scope
   * @param referenceType
   * @param concreteTypes
   */
  public void addTypeBinding(
      String scopeSpanningSymbolName, TypeSymbol referenceType, Set<TypeSymbol> concreteTypes) {
    Multimap<String, TypeSymbol> typeBinding =
        typeBindings.computeIfAbsent(scopeSpanningSymbolName, (k) -> ArrayListMultimap.create());
    // TODO future: make sure the binding we add does not conflict with existing ones
    typeBinding.putAll(referenceType.getFullName(), concreteTypes);
  }

  public void addFieldBinding(
      String scopeSpanningSymbolName, FieldSymbol referenceField, Set<FieldSymbol> concreteFields) {
    Multimap<String, FieldSymbol> fieldBinding =
        fieldBindings.computeIfAbsent(scopeSpanningSymbolName, (k) -> ArrayListMultimap.create());
    // TODO future: make sure the binding we add does not conflict with existing ones
    fieldBinding.putAll(referenceField.getFullName(), concreteFields);
  }

  public Optional<Collection<TypeSymbol>> getScopedTypeIncarnations(
      IScope concreteScope, TypeSymbol referenceType) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty collection
      return Optional.empty();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getScopedTypeIncarnations(concreteScope.getEnclosingScope(), referenceType);
    }
    String spanningSymbolName = concreteScope.getSpanningSymbol().getFullName();
    Log.debug(
        "Checking for type incarnations in scope spanned by: " + spanningSymbolName, LOG_NAME);

    Multimap<String, TypeSymbol> localTypeMapping = typeBindings.get(spanningSymbolName);
    if (localTypeMapping != null && localTypeMapping.containsKey(referenceType.getFullName())) {
      // incarnation is locally defined in that scope
      return Optional.of(localTypeMapping.get(referenceType.getFullName()));
    } else {
      // search higher in the scope hierarchy
      return getScopedTypeIncarnations(concreteScope.getEnclosingScope(), referenceType);
    }
  }

  public Optional<Collection<FieldSymbol>> getScopedFieldIncarnations(
      IScope concreteScope, FieldSymbol referenceField) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty collection
      return Optional.empty();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getScopedFieldIncarnations(concreteScope.getEnclosingScope(), referenceField);
    }
    String spanningSymbolName = concreteScope.getSpanningSymbol().getFullName();
    Log.debug(
        "Checking for field incarnations in scope spanned by: " + spanningSymbolName, LOG_NAME);

    // 1. resolve the type of the field
    TypeSymbol declaringTypeSymbol = SymbolUtil.getDeclaringTypeSymbol(referenceField);
    Optional<Collection<TypeSymbol>> declaringTypeIncarnations =
        getScopedTypeIncarnations(concreteScope, declaringTypeSymbol);

    // 2. resolve the type incarnations of the field
    Multimap<String, FieldSymbol> localFieldMapping = fieldBindings.get(spanningSymbolName);

    Optional<Collection<FieldSymbol>> fieldIncarnations;
    if (localFieldMapping != null
        && localFieldMapping.containsKey(declaringTypeSymbol.getFullName())) {
      // incarnation is locally defined in that scope
      fieldIncarnations = Optional.of(localFieldMapping.get(declaringTypeSymbol.getFullName()));
    } else {
      // search higher in the scope hierarchy
      fieldIncarnations =
          getScopedFieldIncarnations(concreteScope.getEnclosingScope(), referenceField);
    }

    // 3. filter the field incarnations by the declaring type incarnations, if present
    if (declaringTypeIncarnations.isPresent() && fieldIncarnations.isPresent()) {
      return Optional.of(
          fieldIncarnations.get().stream()
              // only return the field incarnations that are declared in one of the type
              // incarnations of this scope
              .filter(
                  field ->
                      declaringTypeIncarnations
                          .get()
                          .contains(SymbolUtil.getDeclaringTypeSymbol(field)))
              .collect(Collectors.toSet()));
    } else {
      // no declaring type incarnations, return all field incarnations
      return fieldIncarnations;
    }
  }
}
