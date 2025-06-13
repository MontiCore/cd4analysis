/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdconcretization.CDRefSymbolHandlerDelegator;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Visitor that derives incarnation bindings for CD elements that incarnate reference elements
 * annotated with the "forEach" stereotype. Each concrete element that incarnates a reference
 * element annotated with "forEach" is expected to have a binding for the forEach-parameter element
 * used in the reference model.
 * The binding of a forEach-parameter element is derived using different strategies:
 * <ol>
 * <li><b>Templated name:</b> Checking the concrete name against a regex pattern built form the
 * reference name. e.g., parameter element <code>DataClass</code> is bound to <code>Employee/code>
 * if the reference name is <code>DataClassBuilder</code> and the concrete name is
 * <code>EmployeeBuilder</code>.
 * </li>
 * <li><b>Name suffix:</b> If the parameter incarnation name was added as suffix to the reference
 * element name. e.g., if the reference name is <code>Builder</code> and the concrete name is
 * <code>Builder_Employee</code>, we know that the parameter element <code>DataClass</code> is
 * bound to <code>Employee</code> in this context.
 * </li>
 * <li><b>Binding hints:</b> If all incarnations in the subtree starting from the concrete element
 * are the same for the forEach-parameter element, we can use the this incarnation as binding.
 * </li>
 * <li><b>Fallback:</b> If there is only one incarnation of the forEach-parameter element anyway,
 * this incarnation must be the binding.</li>
 * </ol>
 *
 * See {@link #deriveBinding(ISymbol, String, ISymbol, Optional, Function, Predicate, Supplier)} for
 * the core logic of deriving a binding for a forEach-parameter element.
 */
public class ForEachBindingDerivingVisitor implements CDBasisVisitor2, CD4CodeBasisVisitor2 {
  
  private static final String LOG_NAME = ForEachBindingDerivingVisitor.class.getName();
  
  private final CDIncarnationMapping incMapping;
  
  /** Indicates if an invalid binding was found during the visit. */
  private boolean foundInvalidBinding = false;
  
  /** Indicates if a binding could not be derived for a forEach-parameter element. */
  private boolean missingBinding = false;
  
  public ForEachBindingDerivingVisitor(CDIncarnationMapping incMapping) {
    this.incMapping = incMapping;
  }
  
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
    traverser.add4CD4CodeBasis(this);
  }
  
  public boolean hasFoundInvalidBinding() {
    return foundInvalidBinding;
  }
  
  public boolean hasMissingBinding() {
    return missingBinding;
  }
  
  @Override
  public void visit(ASTCDType concreteType) {
    deriveBindings(concreteType.getEnclosingScope(), concreteType, concreteType.getSymbol(),
        incMapping.getReferenceElements(concreteType), ASTCDType::getModifier, ASTCDType::getName);
  }
  
  @Override
  public void visit(ASTCDAttribute concreteAttribute) {
    deriveBindings(concreteAttribute.getEnclosingScope(), concreteAttribute, concreteAttribute
        .getSymbol(), incMapping.getReferenceElements(concreteAttribute),
        ASTCDAttribute::getModifier, ASTCDAttribute::getName);
  }
  
  @Override
  public void visit(ASTCDMethod concreteMethod) {
    deriveBindings(concreteMethod.getEnclosingScope(), concreteMethod, concreteMethod.getSymbol(),
        incMapping.getReferenceElements(concreteMethod), ASTCDMethod::getModifier,
        ASTCDMethod::getName);
  }
  
  /**
   * Derives a binding for each reference element incarnated by the concrete element which is
   * annotated with the "forEach" stereotype.
   *
   * @param enclosingScope the scope in which the concrete element is defined
   * @param concreteElement the concrete element for which bindings are derived
   * @param concreteSymbol the symbol of the concrete element
   * @param referenceElements the reference elements that the concrete element incarnates
   * @param getModifierFun a function to get the modifier of the reference element
   * @param <T> the type of the reference elements, e.g., ASTCDType, ASTCDAttribute, ASTCDMethod
   */
  protected <T extends ASTCDBasisNode> void deriveBindings(ICDBasisScope enclosingScope,
      ASTCDBasisNode concreteElement, ISymbol concreteSymbol, Set<T> referenceElements,
      Function<T, ASTModifier> getModifierFun, Function<T, String> getName) {
    BindingHintsVisitor bindingHintsVisitor = null;
    
    // we are only interested in elements where the reference element is annotated with "forEach"
    for (T refElement : referenceElements) {
      Optional<String> forEachRefElement = StereotypeUtil.getForEachStereotypeValue(getModifierFun
          .apply(refElement), "forEach stereotype without value");
      if (forEachRefElement.isPresent()) {
        if (bindingHintsVisitor == null) {
          // we only need to collect binding hints once for each concrete element
          bindingHintsVisitor = new BindingHintsVisitor(incMapping);
          bindingHintsVisitor.collectHints(concreteElement);
        }
        
        CDRefSymbolHandlerDelegator delegator = new CDRefSymbolHandlerDelegator();
        final BindingHintsVisitor finalBindingHintsVisitor = bindingHintsVisitor;
        // TODO maybe refactor and return BindingHints result object from the visitor instead of passing the visitor to the derive methods
        
        delegator.setTypeHandler((refParamType) -> deriveTypeBinding(enclosingScope, concreteSymbol,
            getName.apply(refElement), refParamType, finalBindingHintsVisitor));
        delegator.setAttributeHandler((refParamAttribute) -> deriveAttributeBinding(enclosingScope,
            concreteSymbol, getName.apply(refElement), refParamAttribute,
            finalBindingHintsVisitor));
        // TODO support method & association references
        try {
          delegator.resolveSymbol((ICDBasisScope) refElement.getEnclosingScope(), forEachRefElement
              .get(), concreteElement.get_SourcePositionStart());
        }
        catch (CompletionException e) {
          // TODO refactor
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  /**
   * Derives a type binding for the given annotated element.
   *
   * @param concreteScope the scope in which the concrete element is defined
   * @param concreteSymbol the symbol of the concrete element
   * @param referenceElementName the name of the reference element
   * @param referenceParamType the forEach-parameter reference type
   * @param bindingHints the binding hints collected from the concrete element
   */
  private void deriveTypeBinding(ICDBasisScope concreteScope, ISymbol concreteSymbol,
      String referenceElementName, ASTCDType referenceParamType, BindingHintsVisitor bindingHints) {
    if (!incMapping.getBindings(concreteSymbol, referenceParamType.getSymbol()).isEmpty()) {
      // there is already a binding for the parameter type, no need to derive one
      // see STBindingDerivingVisitor
      return;
    }
    Optional<CDTypeSymbol> derivedBinding = deriveBinding(concreteSymbol, referenceElementName,
        referenceParamType.getSymbol(), bindingHints.getUniqueTypeIncarnation(referenceParamType)
            .map(ASTCDType::getSymbol), concreteScope::resolveCDType, (incarnation) -> incMapping
                .getReferenceElements(incarnation.getAstNode()).contains(referenceParamType),
        () -> incMapping.getIncarnations(concreteSymbol.getEnclosingScope(), referenceParamType)
            .stream().map(ASTCDType::getSymbol).collect(Collectors.toSet()));
    if (derivedBinding.isPresent()) {
      incMapping.addBinding(concreteSymbol, referenceParamType.getSymbol(), derivedBinding.get());
    }
  }
  
  /**
   * Derives a binding for a forEach-parameter attribute.
   *
   * @param concreteScope the scope in which the concrete element is defined
   * @param annotatedSymbol the symbol of the concrete element
   * @param referenceElementName the name of the reference element
   * @param referenceParamAttr the reference parameter attribute
   * @param bindingHints the binding hints collected from the concrete element
   */
  private void deriveAttributeBinding(ICDBasisScope concreteScope, ISymbol annotatedSymbol,
      String referenceElementName, ASTCDAttribute referenceParamAttr,
      BindingHintsVisitor bindingHints) {
    if (!incMapping.getBindings(annotatedSymbol, referenceParamAttr.getSymbol()).isEmpty()) {
      // there is already a binding for the parameter attribute, no need to derive one
      // see STBindingDerivingVisitor
      return;
    }
    Optional<FieldSymbol> derivedBinding = deriveBinding(annotatedSymbol, referenceElementName,
        referenceParamAttr.getSymbol(), bindingHints.getUniqueAttributeIncarnation(
            referenceParamAttr).map(ASTCDAttribute::getSymbol), (name) -> resolveFieldIncarnation(
                concreteScope, referenceParamAttr.getSymbol(), name), (incarnation) -> incMapping
                    .getReferenceElements(SymbolUtil.cdAttributeFromFieldSymbol(incarnation))
                    .contains(referenceParamAttr), () -> incMapping.getIncarnations(annotatedSymbol
                        .getEnclosingScope(), referenceParamAttr).stream().map(
                            ASTCDAttribute::getSymbol).collect(Collectors.toSet()));
    if (derivedBinding.isPresent()) {
      incMapping.addBinding(annotatedSymbol, referenceParamAttr.getSymbol(), derivedBinding.get());
    }
  }
  
  // TODO add more docs /general desciption
  /**
   * Usually there is only one declaring type incarnation in the current scope because it was
   * bound by another forEach relation. If the declaring type is not bound, we expect it to
   * be qualified in such a way that it can still be resolved uniquely.
   *
   * @param scope the scope in which we search for incarnations
   * @param referenceField the reference field we search an incarnation for
   * @param incarnationName the name of the incarnation to be resolved
   */
  private Optional<FieldSymbol> resolveFieldIncarnation(ICDBasisScope scope,
      FieldSymbol referenceField, String incarnationName) {
    String incarnationNameQualifier = Names.getQualifier(incarnationName);
    boolean isQualifiedName = !incarnationNameQualifier.isEmpty();
    // 1. if the incarnation name is qualified, we resolve the declaring type directly
    if (isQualifiedName) {
      /*
       * NOTE: resolveField(name) seems to resolve qualified names top-down from the global scope.
       * This can lead to errors if we have the concrete CD and concretized CD (using same symbol
       * names) in the same scope.
       * We can avoid the problem by resolving the qualified name down starting from the artifact
       * scope of the concrete CD.
       */
      return SymbolUtil.getArtifactScope(scope).resolveFieldDown(incarnationName);
    }
    // 2. if the incarnation name is not qualified, we check all incarnations of the declaring type
    TypeSymbol declaringRefType = SymbolUtil.getDeclaringTypeSymbol(referenceField);
    Set<TypeSymbol> declaringTypeIncarnations = incMapping.getIncarnations(scope, declaringRefType);
    Optional<FieldSymbol> fieldIncarnation = Optional.empty();
    for (TypeSymbol typeIncarnation : declaringTypeIncarnations) {
      ASTCDType cdType = SymbolUtil.cdTypeFromTypeSymbol(typeIncarnation);
      Optional<FieldSymbol> resolvedInc = cdType.getSpannedScope().resolveField(incarnationName);
      if (resolvedInc.isPresent()) {
        if (fieldIncarnation.isPresent() && !fieldIncarnation.get().equals(resolvedInc.get())) {
          Log.warn("Multiple field incarnations found for name '" + incarnationName
              + "' in incarnations of type '" + declaringRefType.getFullName()
              + "'. Cannot resolve unique incarnation.");
          return Optional.empty();
        }
        fieldIncarnation = resolvedInc;
      }
    }
    return fieldIncarnation;
  }
  
  /**
   * Derives a binding for a forEach-parameter element using different strategies:
   * <ol>
   * <li>Template variables replacements in the concrete name</li>
   * <li>Suffix added to the concrete name</li>
   * <li>Binding hints, if they are unambiguous</li>
   * <li>Fallback to the only possible incarnation of the parameter type</li>
   * </ol>
   * This method defines the abstract logic for deriving a binding for a certain model element and
   * delegates element specific resolving and incarnation logic to the provided parameters
   * callbacks. This way we can share the same logic for different elements: types, attributes,
   * methods, and, associations.
   *
   * @param concreteSymbol the concrete element symbol at which we want to derive a binding
   * @param referenceElementName the name of the reference element that is annotated with "forEach"
   * @param referenceParamSymbol the reference forEach-parameter symbol for which we want to derive
   * a binding
   * @param uniqueBindingHint a unique binding hint that can be used to resolve the binding directly
   * @param resolveSymbol a function that resolves a symbol by its name
   * @param isIncarnation a predicate that checks if a symbol is an incarnation of the reference
   * parameter element
   * @param getIncarnations a supplier that provides a set of all incarnations of the reference
   * parameter element
   * @param <S> the type of the reference parameter symbol, e.g., CDTypeSymbol, FieldSymbol
   */
  private <S extends ISymbol> Optional<S> deriveBinding(ISymbol concreteSymbol,
      String referenceElementName, S referenceParamSymbol, Optional<S> uniqueBindingHint,
      Function<String, Optional<S>> resolveSymbol, Predicate<S> isIncarnation,
      Supplier<Set<S>> getIncarnations) {
    // 1. try to derive binding from name template
    Optional<S> paramIncarnationSymbol = deriveBindingFromNameTemplate(concreteSymbol,
        referenceElementName, referenceParamSymbol, resolveSymbol, isIncarnation);
    if (paramIncarnationSymbol.isPresent()) {
      return paramIncarnationSymbol;
    }
    
    // 2. try to derive binding from suffix
    paramIncarnationSymbol = deriveBindingFromSuffix(concreteSymbol, referenceParamSymbol,
        resolveSymbol, isIncarnation);
    if (paramIncarnationSymbol.isPresent()) {
      return paramIncarnationSymbol;
    }
    
    // 3. check unique binding hint
    if (uniqueBindingHint.isPresent()) {
      S binding = uniqueBindingHint.get();
      Log.info("Fallback to unique binding hint @ '" + concreteSymbol.getFullName() + "':  "
          + referenceParamSymbol.getFullName() + "=" + binding.getFullName(), LOG_NAME);
      return uniqueBindingHint;
    }
    
    // 4. check if there is only one incarnation anyway
    Set<S> paramTypeIncarnations = getIncarnations.get();
    if (paramTypeIncarnations.size() == 1) {
      S onlyIncarnation = paramTypeIncarnations.iterator().next();
      // there is only one incarnation of the parameter type, we can use it as binding
      // (in fact it is not even necessary to derive a binding here, but we do it for consistency in conformance checking)
      Log.info("Fallback to only incarnation @ '" + concreteSymbol.getFullName() + "':  "
          + referenceParamSymbol.getFullName() + "=" + onlyIncarnation.getFullName(), LOG_NAME);
      return Optional.of(onlyIncarnation);
    }
    
    // 5. no binding could be derived + multiple incarnations of the parameter type
    Log.warn("The concrete element '" + concreteSymbol.getFullName()
        + "' has no information that can be used to derive a binding for forEach-parameter type '"
        + referenceParamSymbol.getFullName() + "'.");
    missingBinding = true;
    return Optional.empty();
  }
  
  /**
   * Derives a binding for a forEach-parameter symbol based on the name of the annotated symbol
   * and the difference to the reference element name.
   *
   * @param concreteSymbol the concrete element symbol at which we want to derive a binding
   * @param referenceElementName the name of the reference element that is annotated with "forEach"
   * @param referenceParamSymbol the reference forEach-parameter symbol for which we want to derive
   * a binding
   * @param resolveSymbol a function that resolves a symbol by its name
   * @param isIncarnation a predicate that checks if a symbol is an incarnation of the reference
   * parameter element
   * @return the derived binding if successful, otherwise an empty Optional
   * @param <S> the type of the reference parameter symbol, e.g., CDTypeSymbol, FieldSymbol
   */
  private <S extends ISymbol> Optional<S> deriveBindingFromNameTemplate(ISymbol concreteSymbol,
      String referenceElementName, S referenceParamSymbol,
      Function<String, Optional<S>> resolveSymbol, Predicate<S> isIncarnation) {
    String concreteName = concreteSymbol.getName();
    // 1. try to derive binding from regex
    String paramName = referenceParamSymbol.getName();
    Set<String> incarnationNameCandidates = NameUtil.extractTemplateVariableCandidates(
        referenceElementName, paramName, concreteName).stream()
        // the extracted variable part could be an escaped qualified name -> unescape first
        .map(NameUtil::unescapeQualifiedNameFromIdentifier).collect(Collectors.toSet());
    if (!incarnationNameCandidates.isEmpty()) {
      Optional<S> paramIncarnationSymbol = resolveNameVariants(incarnationNameCandidates,
          resolveSymbol);
      if (paramIncarnationSymbol.isPresent()) {
        // we have an incarnation binding!
        Log.info("Derived binding @ '" + concreteSymbol.getFullName() + "':  "
            + referenceParamSymbol.getFullName() + "=" + paramIncarnationSymbol.get().getFullName(),
            LOG_NAME);
        if (isIncarnation.test(paramIncarnationSymbol.get())) {
          return paramIncarnationSymbol;
        }
        else {
          Log.warn("The derived forEach-parameter incarnation '" + paramIncarnationSymbol.get()
              .getFullName() + "' is not an incarnation of '" + referenceParamSymbol.getFullName()
              + "'.");
          foundInvalidBinding = true;
        }
      }
      else {
        Log.warn("The forEach-parameter incarnation name candidates '" + incarnationNameCandidates
            + "' cannot be resolved.");
        missingBinding = true;
      }
    }
    return Optional.empty();
  }
  
  /**
   * Derives a binding for a forEach-parameter symbol based on the suffix of the annotated symbol's
   * name.
   *
   * @param annotatedSymbol the concrete element symbol at which we want to derive a binding
   * @param referenceParamSymbol the reference forEach-parameter symbol for which we want to derive
   * a binding
   * @param resolveSymbol a function that resolves a symbol by its name
   * @param isIncarnation a predicate that checks if a symbol is an incarnation of the reference
   * parameter element
   * @return the derived binding if successful, otherwise an empty Optional
   * @param <S> the type of the reference parameter symbol, e.g., CDTypeSymbol, FieldSymbol
   */
  private <S extends ISymbol> Optional<S> deriveBindingFromSuffix(ISymbol annotatedSymbol,
      S referenceParamSymbol, Function<String, Optional<S>> resolveSymbol,
      Predicate<S> isIncarnation) {
    String concreteName = annotatedSymbol.getName();
    if (concreteName.contains("_")) {
      // the suffix could be an escaped qualified name -> unescape first
      String suffix = NameUtil.unescapeQualifiedNameFromIdentifier(concreteName.substring(
          concreteName.indexOf("_") + 1));
      Optional<S> paramIncarnationSymbol = resolveSymbol.apply(suffix);
      if (paramIncarnationSymbol.isPresent()) {
        S incarnationCandidate = paramIncarnationSymbol.get();
        // we still have to check if it really is an incarnation of the reference parameter type
        if (isIncarnation.test(incarnationCandidate)) {
          return paramIncarnationSymbol;
        }
        else {
          Log.warn("The suffix '" + suffix + "' of the concrete name '" + concreteName
              + "' is not an incarnation of forEach-parameter '" + referenceParamSymbol
                  .getFullName() + "'");
          foundInvalidBinding = true;
        }
      }
      else {
        Log.warn("The suffix '" + suffix + "' of the concrete name '" + concreteName
            + "' cannot be resolved as symbol. Cannot derive binding for forEach-parameter type '"
            + referenceParamSymbol.getFullName() + "'.");
        missingBinding = true;
      }
    }
    return Optional.empty();
  }
  
  /**
   * Tries to resolve a name in different variants.
   *
   * @param nameVariants the name variants to resolve
   * @param resolveFunction a function that takes a name and returns an Optional of the resolved
   * symbol
   * @return an Optional containing the resolved symbol if found, otherwise an empty Optional
   * @param <T> the type of the resolved symbol
   */
  private <T> Optional<T> resolveNameVariants(Set<String> nameVariants,
      Function<String, Optional<T>> resolveFunction) {
    for (String name : nameVariants) {
      Optional<T> resolved = resolveFunction.apply(name);
      if (resolved.isPresent()) {
        return resolved;
      }
    }
    return Optional.empty();
  }
  
}
