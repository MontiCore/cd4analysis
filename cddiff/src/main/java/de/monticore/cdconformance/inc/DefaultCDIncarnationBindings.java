/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link CDIncarnationBindings} interface, which provides methods to
 * manage and retrieve type, field, and method bindings in a specific scope.
 */
public class DefaultCDIncarnationBindings implements CDIncarnationBindings {
  
  private static final String LOG_NAME = DefaultCDIncarnationBindings.class.getName();
  
  /**
   * The mapping of type incarnations for a specific scope. The key is the name of the concrete type
   * spanning the scope, and the value is the specific incarnation mapping for that scope. If a type
   * is not present in the mapping, it means that the mapping needs to be resolved in the parent
   * scope.
   */
  private final Map<String, SetMultimap<String, TypeSymbol>> typeBindings = new HashMap<>();
  
  private final Map<String, SetMultimap<String, FieldSymbol>> fieldBindings = new HashMap<>();
  
  private final Map<String, SetMultimap<String, MethodSymbol>> methodBindings = new HashMap<>();
  
  @Override
  public String computeSymbolKey(ISymbol symbol) {
    return symbol.getFullName();
  }
  
  @Override
  public void addBinding(String contextSymbolName, TypeSymbol referenceType,
      Set<TypeSymbol> concreteTypes) {
    Multimap<String, TypeSymbol> typeBinding = typeBindings.computeIfAbsent(contextSymbolName,
        k -> HashMultimap.create());
    // TODO future: make sure the binding we add does not conflict with existing ones
    typeBinding.putAll(computeSymbolKey(referenceType), concreteTypes);
  }
  
  @Override
  public void addBinding(String contextSymbolName, FieldSymbol referenceField,
      Set<FieldSymbol> concreteFields) {
    Multimap<String, FieldSymbol> fieldBinding = fieldBindings.computeIfAbsent(contextSymbolName,
        k -> HashMultimap.create());
    // TODO future: make sure the binding we add does not conflict with existing ones
    fieldBinding.putAll(computeSymbolKey(referenceField), concreteFields);
  }
  
  @Override
  public void addBinding(String contextSymbolName, MethodSymbol referenceMethod,
      Set<MethodSymbol> concreteMethods) {
    Multimap<String, MethodSymbol> methodBinding = methodBindings.computeIfAbsent(contextSymbolName,
        k -> HashMultimap.create());
    // TODO future: make sure the binding we add does not conflict with existing ones
    methodBinding.putAll(computeSymbolKey(referenceMethod), concreteMethods);
  }
  
  @Override
  public void addBinding(String contextSymbolName, CDAssociationSymbol refAssociation,
      Set<CDAssociationSymbol> conAssociations) {
    // TODO implement association binding support
    throw new NotImplementedException();
  }
  
  public SetMultimap<String, TypeSymbol> getTypeBindings(IScope concreteScope) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return HashMultimap.create();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getTypeBindings(concreteScope.getEnclosingScope());
    }
    // collect all bindings starting from the spanning symbol of the scope
    return getTypeBindings(concreteScope.getSpanningSymbol());
  }
  
  public SetMultimap<String, TypeSymbol> getTypeBindings(ISymbol contextSymbol) {
    String symbolName = computeSymbolKey(contextSymbol);
    Log.debug("Checking for type incarnations in context of symbol: " + symbolName, LOG_NAME);
    
    SetMultimap<String, TypeSymbol> allTypeBindings = HashMultimap.create();
    SetMultimap<String, TypeSymbol> localTypeMapping = typeBindings.get(symbolName);
    if (localTypeMapping != null) {
      // add the local type mapping to the result
      allTypeBindings.putAll(localTypeMapping);
    }
    // search higher in the scope hierarchy
    allTypeBindings.putAll(getTypeBindings(contextSymbol.getEnclosingScope()));
    return allTypeBindings;
  }
  
  @Override
  public Set<TypeSymbol> getBindings(IScope concreteScope, TypeSymbol referenceType) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return Collections.emptySet();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getBindings(concreteScope.getEnclosingScope(), referenceType);
    }
    // check if there are bindings for the spanning symbol
    return getBindings(concreteScope.getSpanningSymbol(), referenceType);
  }
  
  @Override
  public Set<TypeSymbol> getBindings(ISymbol contextSymbol, TypeSymbol referenceType) {
    String symbolName = computeSymbolKey(contextSymbol);
    Log.debug("Checking for type incarnations in context of symbol: " + symbolName, LOG_NAME);
    
    SetMultimap<String, TypeSymbol> localTypeMapping = typeBindings.get(symbolName);
    String referenceTypeKey = computeSymbolKey(referenceType);
    if (localTypeMapping != null && localTypeMapping.containsKey(referenceTypeKey)) {
      // incarnation is locally defined in that scope
      return localTypeMapping.get(referenceTypeKey);
    }
    else {
      // search higher in the scope hierarchy
      return getBindings(contextSymbol.getEnclosingScope(), referenceType);
    }
  }
  
  @Override
  public Set<FieldSymbol> getBindings(IScope concreteScope, FieldSymbol referenceField) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return Collections.emptySet();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getBindings(concreteScope.getEnclosingScope(), referenceField);
    }
    return getBindings(concreteScope.getSpanningSymbol(), referenceField);
  }
  
  @Override
  public Set<FieldSymbol> getBindings(ISymbol contextSymbol, FieldSymbol referenceField) {
    String symbolName = computeSymbolKey(contextSymbol);
    Log.debug("Checking for field incarnations in scope spanned by: " + symbolName, LOG_NAME);
    
    // 1. resolve the type of the field
    TypeSymbol declaringTypeSymbol = SymbolUtil.getDeclaringTypeSymbol(referenceField);
    Set<TypeSymbol> declaringTypeIncarnations = getBindings(contextSymbol, declaringTypeSymbol);
    
    // 2. resolve the type incarnations of the field
    SetMultimap<String, FieldSymbol> localFieldMapping = fieldBindings.get(symbolName);
    
    Set<FieldSymbol> fieldIncarnations;
    String referenceFieldKey = computeSymbolKey(referenceField);
    if (localFieldMapping != null && localFieldMapping.containsKey(referenceFieldKey)) {
      // incarnation is locally defined in that scope
      fieldIncarnations = localFieldMapping.get(referenceFieldKey);
    }
    else {
      // search higher in the scope hierarchy
      fieldIncarnations = getBindings(contextSymbol.getEnclosingScope(), referenceField);
    }
    
    // 3. filter the field incarnations by the declaring type incarnations, if present
    if (!declaringTypeIncarnations.isEmpty() && !fieldIncarnations.isEmpty()) {
      return fieldIncarnations.stream()
          // only return the field incarnations that are declared in one of the type
          // incarnations of this scope
          .filter(field -> declaringTypeIncarnations.contains(SymbolUtil.getDeclaringTypeSymbol(
              field))).collect(Collectors.toSet());
    }
    else {
      // no declaring type incarnations, return all field incarnations
      return fieldIncarnations;
    }
  }
  
  public SetMultimap<String, FieldSymbol> getFieldBindings(IScope concreteScope) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return HashMultimap.create();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getFieldBindings(concreteScope.getEnclosingScope());
    }
    // collect all bindings starting from the spanning symbol of the scope
    return getFieldBindings(concreteScope.getSpanningSymbol());
  }
  
  public SetMultimap<String, FieldSymbol> getFieldBindings(ISymbol contextSymbol) {
    String symbolName = computeSymbolKey(contextSymbol);
    Log.debug("Checking for type incarnations in context of symbol: " + symbolName, LOG_NAME);
    
    SetMultimap<String, FieldSymbol> allBindings = HashMultimap.create();
    SetMultimap<String, FieldSymbol> localMapping = fieldBindings.get(symbolName);
    if (localMapping != null) {
      // add the local type mapping to the result
      allBindings.putAll(localMapping);
    }
    // search higher in the scope hierarchy
    allBindings.putAll(getFieldBindings(contextSymbol.getEnclosingScope()));
    return allBindings;
  }
  
  @Override
  public Set<MethodSymbol> getBindings(IScope concreteScope, MethodSymbol referenceMethod) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return Collections.emptySet();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getBindings(concreteScope.getEnclosingScope(), referenceMethod);
    }
    return getBindings(concreteScope.getSpanningSymbol(), referenceMethod);
  }
  
  @Override
  public Set<MethodSymbol> getBindings(ISymbol contextSymbol, MethodSymbol referenceMethod) {
    String symbolName = computeSymbolKey(contextSymbol);
    Log.debug("Checking for method incarnations in scope spanned by: " + symbolName, LOG_NAME);
    
    // 1. resolve the declaring type of the method
    TypeSymbol declaringTypeSymbol = SymbolUtil.getDeclaringTypeSymbol(referenceMethod);
    Set<TypeSymbol> declaringTypeIncarnations = getBindings(contextSymbol, declaringTypeSymbol);
    
    // 2. resolve the incarnations of the method
    SetMultimap<String, MethodSymbol> localMethodMapping = methodBindings.get(symbolName);
    
    Set<MethodSymbol> methodIncarnations;
    String referenceMethodKey = computeSymbolKey(referenceMethod);
    if (localMethodMapping != null && localMethodMapping.containsKey(referenceMethodKey)) {
      // incarnation is locally defined in that scope
      methodIncarnations = localMethodMapping.get(referenceMethodKey);
    }
    else {
      // search higher in the scope hierarchy
      methodIncarnations = getBindings(contextSymbol.getEnclosingScope(), referenceMethod);
    }
    
    // 3. filter the method incarnations by the declaring type incarnations, if present
    if (!declaringTypeIncarnations.isEmpty() && !methodIncarnations.isEmpty()) {
      return methodIncarnations.stream()
          // only return the method incarnations that are declared in one of the type
          // incarnations of this scope
          .filter(method -> declaringTypeIncarnations.contains(SymbolUtil.getDeclaringTypeSymbol(
              method))).collect(Collectors.toSet());
    }
    else {
      // no declaring type incarnations, return all method incarnations
      return methodIncarnations;
    }
  }
  
  public SetMultimap<String, MethodSymbol> getMethodBindings(IScope concreteScope) {
    if (concreteScope instanceof IGlobalScope) {
      // no enclosing scope, return empty set
      return HashMultimap.create();
    }
    if (!concreteScope.isPresentSpanningSymbol()) {
      // ignore the scope and jump one level higher
      return getMethodBindings(concreteScope.getEnclosingScope());
    }
    // collect all bindings starting from the spanning symbol of the scope
    return getMethodBindings(concreteScope.getSpanningSymbol());
  }
  
  public SetMultimap<String, MethodSymbol> getMethodBindings(ISymbol contextSymbol) {
    String symbolName = computeSymbolKey(contextSymbol);
    Log.debug("Checking for type incarnations in context of symbol: " + symbolName, LOG_NAME);
    
    SetMultimap<String, MethodSymbol> allBindings = HashMultimap.create();
    SetMultimap<String, MethodSymbol> localMapping = methodBindings.get(symbolName);
    if (localMapping != null) {
      // add the local type mapping to the result
      allBindings.putAll(localMapping);
    }
    // search higher in the scope hierarchy
    allBindings.putAll(getMethodBindings(contextSymbol.getEnclosingScope()));
    return allBindings;
  }
  
}
