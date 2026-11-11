/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols.refmodel;

import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.refmodel.Bindings;
import de.monticore.symbols.basicsymbols.refmodel.IBasicSymbolsBindings;
import de.monticore.symbols.basicsymbols.refmodel.BasicSymbolsBindings;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.Set;

/**
 * Basic implementation of {@link IOOSymbolsBindings}.<br>
 * <br>
 * <b>NOTE:</b> This class is intended to be GENERATED in the future! Therefore, only apply changes
 * which are systematic and can be automatically derived from the language grammar.<br>
 * <br>
 * Developers can customize the semantic relations between different symbols, e.g. by overriding
 * the {@code getXXXImpliedBindings}.
 */
/*
 * TODO If this class is generated, remove the 'Base' suffix as MontiCore will automatically name
 *   it TOP in case there is a handwritten implementation.
 */
public class OOSymbolsBindingsBase implements IOOSymbolsBindings {
  
  // ========== Symbol bindings from languages extended by OOSymbols ==========
  
  /**
   * Used to manage basic symbols (required because OOSymbols language extends BasicSymbols).
   */
  private final IBasicSymbolsBindings basicSymbolsBindings;
  
  // ========== OOSymbols specific symbol bindings ==========
  
  private final Bindings<OOTypeSymbol> ooTypeBindings;
  private final Bindings<FieldSymbol> fieldBindings;
  private final Bindings<MethodSymbol> methodBindings;
  
  public OOSymbolsBindingsBase() {
    this(new BasicSymbolsBindings(), new Bindings<>(), new Bindings<>(), new Bindings<>());
  }
  
  protected OOSymbolsBindingsBase(IBasicSymbolsBindings basicSymbolsBindings,
      Bindings<OOTypeSymbol> ooTypeBindings, Bindings<FieldSymbol> fieldBindings,
      Bindings<MethodSymbol> methodBindings) {
    this.basicSymbolsBindings = basicSymbolsBindings;
    this.ooTypeBindings = ooTypeBindings;
    this.fieldBindings = fieldBindings;
    this.methodBindings = methodBindings;
  }
  
  @Override
  public IOOSymbolsBindings copy() {
    return new OOSymbolsBindings(basicSymbolsBindings.copy(), new Bindings<>(ooTypeBindings),
        new Bindings<>(fieldBindings), new Bindings<>(methodBindings));
  }
  
  @Override
  public Optional<Binding<OOTypeSymbol>> getBinding(OOTypeSymbol typeSymbol) {
    return ooTypeBindings.get(typeSymbol);
  }
  
  @Override
  public Optional<Binding<FieldSymbol>> getBinding(FieldSymbol fieldSymbol) {
    return fieldBindings.get(fieldSymbol);
  }
  
  @Override
  public Optional<Binding<MethodSymbol>> getBinding(MethodSymbol methodSymbol) {
    return methodBindings.get(methodSymbol);
  }
  
  /*
   * IMPLEMENTATION NOTE: We MUST add OOTypeSymbol, FieldSymbol, MethodSymbol to the BasicSymbolsBindings
   * because they are also TypeSymbol, VariableSymbol, FunctionSymbol.
   * This is necessary to ensure that the constraints enforced by BasicSymbolsBindings
   * are also applied to OOTypeSymbol, FieldSymbol, MethodSymbol.
   * e.g., an OOTypeSymbol used as parameter/return type must be considered when checking conflicts
   * in BasicSymbolsBindings.addFunctionBinding.
   * We do not want to duplicate the conflict checking here in OOSymbolsBindingsImpl because
   * this would lead to inconsistencies -> think about developers using TOP mechanism to add
   * constraints to BasicSymbolsBindings.addFunctionBinding -> they would expect them to be
   * respected by a generated implementation of OOSymbolsBindings
   */
  
  @Override
  public void addOOTypeBinding(Binding<OOTypeSymbol> binding) throws BindingConflictException {
    if (isConflictingOOTypeBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    basicSymbolsBindings.addTypeBinding(binding.cast());
    ooTypeBindings.add(binding);
    addAll(getOOTypeImpliedBindings(binding));
  }
  
  @Override
  public void addFieldBinding(Binding<FieldSymbol> binding) throws BindingConflictException {
    if (isConflictingFieldBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    basicSymbolsBindings.addVariableBinding(binding.cast());
    fieldBindings.add(binding);
    addAll(getFieldImpliedBindings(binding));
  }
  
  @Override
  public void addMethodBinding(Binding<MethodSymbol> binding) throws BindingConflictException {
    if (isConflictingMethodBinding(binding)) {
      throw new BindingConflictException(binding);
    }
    basicSymbolsBindings.addFunctionBinding(binding.cast());
    methodBindings.add(binding);
    addAll(getMethodImpliedBindings(binding));
  }
  
  @Override
  public boolean isConflictingOOTypeBinding(Binding<OOTypeSymbol> binding) {
    try {
      return ooTypeBindings.conflictsWith(binding) || isConflicting(getOOTypeImpliedBindings(
          binding)) || basicSymbolsBindings.isConflictingTypeBinding(binding.cast());
    }
    catch (BindingConflictException e) {
      Log.error("The bindings implied by " + binding + " conflict with each other! "
          + "Either there is an issue in the model or you should check the implementation of "
          + "OOSymbolBindings.getOOTypeImpliedBindings", e);
      return true;
    }
  }
  
  @Override
  public boolean isConflictingFieldBinding(Binding<FieldSymbol> binding) {
    try {
      return fieldBindings.conflictsWith(binding) || isConflicting(getFieldImpliedBindings(binding))
          || basicSymbolsBindings.isConflictingVariableBinding(binding.cast());
    }
    catch (BindingConflictException e) {
      Log.error("The bindings implied by " + binding + " conflict with each other! "
          + "Either there is an issue in the model or you should check the implementation of "
          + "OOSymbolBindings.getFieldImpliedBindings", e);
      return true;
    }
  }
  
  @Override
  public boolean isConflictingMethodBinding(Binding<MethodSymbol> binding) {
    try {
      return methodBindings.conflictsWith(binding) || isConflicting(getMethodImpliedBindings(
          binding)) || basicSymbolsBindings.isConflictingFunctionBinding(binding.cast());
    }
    catch (BindingConflictException e) {
      Log.error("The bindings implied by " + binding + " conflict with each other! "
          + "Either there is an issue in the model or you should check the implementation of "
          + "OOSymbolBindings.getMethodImpliedBindings", e);
      return true;
    }
  }
  
  @Override
  public IOOSymbolsBindings getOOTypeImpliedBindings(Binding<OOTypeSymbol> binding)
      throws BindingConflictException {
    // Default implementation returns an empty set
    return new OOSymbolsBindings();
  }
  
  @Override
  public IOOSymbolsBindings getFieldImpliedBindings(Binding<FieldSymbol> binding)
      throws BindingConflictException {
    // Default implementation returns an empty set
    return new OOSymbolsBindings();
  }
  
  @Override
  public IOOSymbolsBindings getMethodImpliedBindings(Binding<MethodSymbol> binding)
      throws BindingConflictException {
    // Default implementation returns an empty set
    return new OOSymbolsBindings();
  }
  
  @Override
  public Set<Binding<OOTypeSymbol>> getOOTypeBindings() { return ooTypeBindings.getAll(); }
  
  @Override
  public Set<Binding<FieldSymbol>> getFieldBindings() { return fieldBindings.getAll(); }
  
  @Override
  public Set<Binding<MethodSymbol>> getMethodBindings() { return methodBindings.getAll(); }
  
  @Override
  public void addAll(IOOSymbolsBindings bindings) throws BindingConflictException {
    // 1. check for conflicts
    if (isConflicting(bindings)) {
      throw new BindingConflictException();
    }
    // Add all basic symbols bindings
    basicSymbolsBindings.addAll(bindings);
    
    // Add all OO specific bindings
    for (Binding<OOTypeSymbol> binding : bindings.getOOTypeBindings()) {
      addOOTypeBinding(binding);
    }
    for (Binding<FieldSymbol> binding : bindings.getFieldBindings()) {
      addFieldBinding(binding);
    }
    for (Binding<MethodSymbol> binding : bindings.getMethodBindings()) {
      addMethodBinding(binding);
    }
  }
  
  @Override
  public boolean isConflicting(IOOSymbolsBindings bindings) {
    if (basicSymbolsBindings.isConflicting(bindings)) {
      return true;
    }
    // Check for conflicts in OO specific bindings
    for (Binding<OOTypeSymbol> binding : bindings.getOOTypeBindings()) {
      if (isConflictingOOTypeBinding(binding)) {
        return true;
      }
    }
    for (Binding<FieldSymbol> binding : bindings.getFieldBindings()) {
      if (isConflictingFieldBinding(binding)) {
        return true;
      }
    }
    for (Binding<MethodSymbol> binding : bindings.getMethodBindings()) {
      if (isConflictingMethodBinding(binding)) {
        return true;
      }
    }
    return false;
  }
  
  // ========== Delegate to BasicSymbolsBindings methods ==========
  
  @Override
  public Optional<Binding<TypeSymbol>> getBinding(TypeSymbol typeSymbol) {
    /*
     * IMPL NOTE: We do not need to check if the typeSymbol is an OOTypeSymbol here
     * because we added OOTypeSymbol bindings to the basicSymbolsBindings as well.
     */
    return basicSymbolsBindings.getBinding(typeSymbol);
  }
  
  @Override
  public Set<Binding<TypeSymbol>> getTypeBindings() {
    // IMPL NOTE: Returns OOTypeSymbols as well
    return basicSymbolsBindings.getTypeBindings();
  }
  
  @Override
  public void addTypeBinding(Binding<TypeSymbol> binding) throws BindingConflictException {
    if (binding.getReferenceElement() instanceof OOTypeSymbol) {
      addOOTypeBinding(binding.cast());
    }
    else {
      if (isConflictingTypeBinding(binding)) {
        throw new BindingConflictException(binding);
      }
      basicSymbolsBindings.addTypeBinding(binding.cast());
      addAll(getTypeImpliedBindings(binding));
    }
  }
  
  @Override
  public boolean isConflictingTypeBinding(Binding<TypeSymbol> binding) {
    if (binding.getReferenceElement() instanceof OOTypeSymbol) {
      return isConflictingOOTypeBinding(binding.cast());
    }
    else {
      try {
        return basicSymbolsBindings.isConflictingTypeBinding(binding) || isConflicting(
            getTypeImpliedBindings(binding));
      }
      catch (BindingConflictException e) {
        Log.error("The bindings implied by " + binding + " conflict with each other! "
            + "Either there is an issue in the model or you should check the implementation of "
            + "OOSymbolBindings.getTypeImpliedBindings", e);
        return true;
      }
    }
  }
  
  @Override
  public IOOSymbolsBindings getTypeImpliedBindings(Binding<TypeSymbol> binding)
      throws BindingConflictException {
    if (binding.getReferenceElement() instanceof OOTypeSymbol) {
      return getOOTypeImpliedBindings(binding.cast());
    }
    else {
      IOOSymbolsBindings ooSymbolsBindings = new OOSymbolsBindings();
      ooSymbolsBindings.addAll(basicSymbolsBindings.getTypeImpliedBindings(binding));
      return ooSymbolsBindings;
    }
  }
  
  @Override
  public Optional<Binding<VariableSymbol>> getBinding(VariableSymbol variableSymbol) {
    /*
     * IMPL NOTE: We do not need to check if the variableSymbol is a FieldSymbols here
     * because we added FieldSymbol bindings to the basicSymbolsBindings as well.
     */
    return basicSymbolsBindings.getBinding(variableSymbol);
  }
  
  @Override
  public Set<Binding<VariableSymbol>> getVariableBindings() {
    // IMPL NOTE: Returns FieldSymbols as well
    return basicSymbolsBindings.getVariableBindings();
  }
  
  @Override
  public void addVariableBinding(Binding<VariableSymbol> binding) throws BindingConflictException {
    if (binding.getReferenceElement() instanceof FieldSymbol) {
      addFieldBinding(binding.cast());
    }
    else {
      if (isConflictingVariableBinding(binding)) {
        throw new BindingConflictException(binding);
      }
      basicSymbolsBindings.addVariableBinding(binding.cast());
      addAll(getVariableImpliedBindings(binding));
    }
  }
  
  @Override
  public boolean isConflictingVariableBinding(Binding<VariableSymbol> binding) {
    if (binding.getReferenceElement() instanceof FieldSymbol) {
      return isConflictingFieldBinding(binding.cast());
    }
    else {
      try {
        return basicSymbolsBindings.isConflictingVariableBinding(binding) || isConflicting(
            getVariableImpliedBindings(binding));
      }
      catch (BindingConflictException e) {
        Log.error("The bindings implied by " + binding + " conflict with each other! "
            + "Either there is an issue in the model or you should check the implementation of "
            + "OOSymbolBindings.getVariableImpliedBindings", e);
        return true;
      }
    }
  }
  
  @Override
  public IOOSymbolsBindings getVariableImpliedBindings(Binding<VariableSymbol> binding)
      throws BindingConflictException {
    if (binding.getReferenceElement() instanceof FieldSymbol) {
      return getFieldImpliedBindings(binding.cast());
    }
    else {
      IOOSymbolsBindings ooSymbolsBindings = new OOSymbolsBindings();
      ooSymbolsBindings.addAll(basicSymbolsBindings.getVariableImpliedBindings(binding));
      return ooSymbolsBindings;
    }
  }
  
  @Override
  public Optional<Binding<FunctionSymbol>> getBinding(FunctionSymbol functionSymbol) {
    /*
     * IMPL NOTE: We do not need to check if the functionSymbol is a MethodSymbol here
     * because we added MethodSymbol bindings to the basicSymbolsBindings as well.
     */
    return basicSymbolsBindings.getBinding(functionSymbol);
  }
  
  @Override
  public Set<Binding<FunctionSymbol>> getFunctionBindings() {
    // IMPL NOTE: Returns MethodSymbols as well
    return basicSymbolsBindings.getFunctionBindings();
  }
  
  @Override
  public void addFunctionBinding(Binding<FunctionSymbol> binding) throws BindingConflictException {
    if (binding.getReferenceElement() instanceof MethodSymbol) {
      addMethodBinding(binding.cast());
    }
    else {
      if (isConflictingFunctionBinding(binding)) {
        throw new BindingConflictException(binding);
      }
      basicSymbolsBindings.addFunctionBinding(binding.cast());
      addAll(getFunctionImpliedBindings(binding));
    }
  }
  
  @Override
  public boolean isConflictingFunctionBinding(Binding<FunctionSymbol> binding) {
    if (binding.getReferenceElement() instanceof MethodSymbol) {
      return isConflictingMethodBinding(binding.cast());
    }
    else {
      try {
        return basicSymbolsBindings.isConflictingFunctionBinding(binding) || isConflicting(
            getFunctionImpliedBindings(binding));
      }
      catch (BindingConflictException e) {
        Log.error("The bindings implied by " + binding + " conflict with each other! "
            + "Either there is an issue in the model or you should check the implementation of "
            + "OOSymbolBindings.getFunctionImpliedBindings", e);
        return true;
      }
    }
  }
  
  @Override
  public IOOSymbolsBindings getFunctionImpliedBindings(Binding<FunctionSymbol> binding)
      throws BindingConflictException {
    if (binding.getReferenceElement() instanceof MethodSymbol) {
      return getMethodImpliedBindings(binding.cast());
    }
    else {
      IOOSymbolsBindings ooSymbolsBindings = new OOSymbolsBindings();
      ooSymbolsBindings.addAll(basicSymbolsBindings.getFunctionImpliedBindings(binding));
      return ooSymbolsBindings;
    }
  }
  
  @Override
  public void addAll(IBasicSymbolsBindings bindings) throws BindingConflictException {
    if (bindings instanceof IOOSymbolsBindings) {
      addAll((IOOSymbolsBindings) bindings);
    }
    else {
      basicSymbolsBindings.addAll(bindings);
    }
  }
  
  @Override
  public boolean isConflicting(IBasicSymbolsBindings otherBindings) {
    if (otherBindings instanceof IOOSymbolsBindings) {
      return isConflicting((IOOSymbolsBindings) otherBindings);
    }
    else {
      return basicSymbolsBindings.isConflicting(otherBindings);
    }
  }
  
}
