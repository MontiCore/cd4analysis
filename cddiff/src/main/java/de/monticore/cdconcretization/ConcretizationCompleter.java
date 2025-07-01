/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.association.DefaultAssocCompleter;
import de.monticore.cdconcretization.association.DefaultAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssociationCompleter;
import de.monticore.cdconcretization.cd.AbstractCDCompleter;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.cdconcretization.cd.ConformanceCheckCompletionStep;
import de.monticore.cdconcretization.cd.ExistingAssociationsCDCompleter;
import de.monticore.cdconcretization.cd.ImportsCompleter;
import de.monticore.cdconcretization.cd.InheritanceCompleter;
import de.monticore.cdconcretization.cd.MissingAssociationsCDCompleter;
import de.monticore.cdconcretization.cd.MissingTypesCDCompleter;
import de.monticore.cdconcretization.cd.RemoveRedundanciesCompletionStep;
import de.monticore.cdconcretization.cd.ReorderElementsCompletionStep;
import de.monticore.cdconcretization.cd.TypeDetailsCDCompleter;
import de.monticore.cdconcretization.cd.type.AbstractTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.BaseTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.ForEachTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.ITypeInCDCompleter;
import de.monticore.cdconcretization.type.AbstractTypeCompleter;
import de.monticore.cdconcretization.type.ClassModifierCompleter;
import de.monticore.cdconcretization.type.DefaultEnumConstantsCompleter;
import de.monticore.cdconcretization.type.ITypeCompleter;
import de.monticore.cdconcretization.type.TypeAttributesCompleter;
import de.monticore.cdconcretization.type.TypeMethodsCompleter;
import de.monticore.cdconcretization.type.attribute.AbstractAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.BaseAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.ForEachAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.IAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.method.AbstractMethodInTypeCompleter;
import de.monticore.cdconcretization.type.method.BaseMethodInTypeCompleter;
import de.monticore.cdconcretization.type.method.ForEachMethodCompleter;
import de.monticore.cdconcretization.type.method.IMethodInTypeCompleter;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.inc.CompIncStrategy;
import de.monticore.cdconformance.inc.association.EqNameAssocIncStrategy;
import de.monticore.cdconformance.inc.association.RolePrefixIfPresentIncStrategy;
import de.monticore.cdconformance.inc.association.RolePrefixInNavDirIncStrategy;
import de.monticore.cdconformance.inc.association.STNamedAssocIncStrategy;
import de.monticore.cdconformance.inc.attribute.CompAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.EqNameAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.STAttributeIncStrategy;
import de.monticore.cdconformance.inc.method.CompMethodIncStrategy;
import de.monticore.cdconformance.inc.method.EqNameMethodIncStrategy;
import de.monticore.cdconformance.inc.method.EqSignatureMethodIncStrategy;
import de.monticore.cdconformance.inc.method.STMethodIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.CDSynDiffMatches;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.booleanMatching.MatchCDTypesToSubType;
import de.monticore.cdmatcher.caching.StructureCache;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScope;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tool for automatic completion of a concrete class diagram (CD) such that it conforms to a given
 * reference CD. The completion process is implemented using multiple modular completer
 * implementations for each element kind in a CD. This class is the facade with easy to use
 * configuration parameters and a single method to perform the completion: {@link
 * #completeCD(ASTCDCompilationUnit, ASTCDCompilationUnit)}).
 */
public class ConcretizationCompleter {

  private final String mapping;

  /**
   * If true, the conformance checker is used to check the conformance of the concretization result.
   */
  private boolean checkConformance = true;

  /**
   * If true, redundant attributes, methods etc. introduced by the completer are removed from the
   * concretization result, even if they were part of the concrete CD input.
   */
  private boolean removeRedundancies = true;

  /** If true, the elements in the concretization result are reordered for consistent results. */
  private boolean reorderElements = true;

  /**
   * If true, the name of the parameter element is replaced with its incarnation name in reference
   * elements annotated with 'forEach'.
   */
  private boolean forEachNameAdaptationEnabled = true;

  /**
   * Name of the placeholder type that is used to mark underspecified types in the reference CD. See
   * {@link UnderspecifiedPlaceholderType}.
   */
  private String underspecifiedPlaceholderTypeName =
      UnderspecifiedPlaceholderType.DEFAULT_TYPE_NAME;

  protected Set<CDConfParameter> conformanceParams;

  public ConcretizationCompleter(String mapping, Set<CDConfParameter> conformanceParams) {
    this.mapping = mapping;
    this.conformanceParams = conformanceParams;
  }

  /**
   * Completes the given concrete CD such that it conforms to a given reference CD.
   *
   * @param concreteCD the concrete CD to be completed
   * @param referenceCD the reference CD to be used for completion
   * @throws CompletionException if the concrete CD cannot be completed to conform to the reference.
   */
  public void completeCD(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {

    /*
     * Basically we do a couple of dependency initialization here. We create a chain of completers
     * that are responsible for completing the different aspects of the CD. These completers are then used
     * to perform the actual concretization.
     */
    CDCompletionContext context = new DefaultCompletionContext(concreteCD, referenceCD, mapping,
        conformanceParams);

    ITypeInCDCompleter typeInCDCompleter = new ChainBuilder<AbstractTypeInCDCompleter>().add(
        new ForEachTypeInCDCompleter()).add(new BaseTypeInCDCompleter()).build();

    IAttributeInTypeCompleter attributeInType = new ChainBuilder<AbstractAttributeInTypeCompleter>()
        .add(new ForEachAttributeInTypeCompleter()).add(new BaseAttributeInTypeCompleter()).build();

    IMethodInTypeCompleter methodInTypeCompleter = new ChainBuilder<AbstractMethodInTypeCompleter>()
        .add(new ForEachMethodCompleter()).add(new BaseMethodInTypeCompleter()).build();

    ITypeCompleter typeCompleter = new ChainBuilder<AbstractTypeCompleter>().add(
        new ClassModifierCompleter()).add(new TypeAttributesCompleter(attributeInType)).add(
            new TypeMethodsCompleter(methodInTypeCompleter)).add(
                new DefaultEnumConstantsCompleter()).build();

    IAssocSideCompleter assocSideCompleter = new DefaultAssocSideCompleter();
    IAssociationCompleter assocCompleter = new DefaultAssocCompleter(concreteCD,
        assocSideCompleter);

    ChainBuilder<AbstractCDCompleter> completerChainBuilder =
        new ChainBuilder<AbstractCDCompleter>().add(new ImportsCompleter()).add(
            new MissingTypesCDCompleter(typeInCDCompleter)).add(new InheritanceCompleter()).add(
                new TypeDetailsCDCompleter(typeCompleter)).add(new ExistingAssociationsCDCompleter(
                    assocCompleter)).add(new MissingAssociationsCDCompleter(assocCompleter));

    // add configurable,optional steps
    if (removeRedundancies) {
      completerChainBuilder.add(new RemoveRedundanciesCompletionStep());
    }
    if (reorderElements) {
      completerChainBuilder.add(new ReorderElementsCompletionStep());
    }
    if (checkConformance) {
      completerChainBuilder.add(new ConformanceCheckCompletionStep(mapping, conformanceParams,
          "Completion result is not conform"));
    }

    // perform the actual concretization
    completerChainBuilder.build().complete(concreteCD, referenceCD, context);
  }

  /**
   * Configures if the conformance checker should be used to check the conformance of the
   * concretization result.
   */
  public void setCheckConformance(boolean checkConformance) {
    this.checkConformance = checkConformance;
  }

  /**
   * Changes the default name of the placeholder type, which is {@link
   * UnderspecifiedPlaceholderType#DEFAULT_TYPE_NAME}.<br>
   * This MUST be called if you want to use a different name for the placeholder type.
   *
   * @param underspecifiedPlaceholderTypeName the new name of the placeholder type
   */
  public void setUnderspecifiedPlaceholderTypeName(String underspecifiedPlaceholderTypeName) {
    this.underspecifiedPlaceholderTypeName = underspecifiedPlaceholderTypeName;
  }

  public void setForEachNameAdaptationEnabled(boolean forEachNameAdaptationEnabled) {
    this.forEachNameAdaptationEnabled = forEachNameAdaptationEnabled;
  }

  /***
   * Provides default configurations for the matching strategies used in the concretization process.
   */
  class DefaultCompletionContext implements CDCompletionContext {

    private final ASTCDCompilationUnit concreteCD;
    private final ASTCDCompilationUnit referenceCD;
    private final String mapping;
    private final Set<CDConfParameter> conformanceParams;
    private final CompIncStrategy<ASTCDType> typeIncStrategy;
    private final CompIncStrategy<ASTCDType> typeIncStrategyMatchingSubTypes;
    private final CompIncStrategy<ASTCDAssociation> assocIncStrategy;
    private final MCTypeMatcher mcTypeMatcher;

    private final ScopedIncarnationBindings scopedIncarnationBindings =
        new ScopedIncarnationBindings();

    public DefaultCompletionContext(ASTCDCompilationUnit concreteCD,
        ASTCDCompilationUnit referenceCD, String mapping, Set<CDConfParameter> conformanceParams) {
      this.concreteCD = concreteCD;
      this.referenceCD = referenceCD;
      this.mapping = mapping;
      this.conformanceParams = conformanceParams;
      Set<ASTCDType> refTypes = CDDiffUtil.getAllTypesFromCD(referenceCD);
      Set<ASTCDAssociation> refAssocs = CDDiffUtil.getAllAssocsFromCD(referenceCD);

      StructureCache structureCache = new StructureCache();
      CDSynDiffMatches.setupStructureCache(concreteCD, structureCache);
      CDSynDiffMatches.setupStructureCache(referenceCD, structureCache);

      typeIncStrategy = new CompIncStrategy<>(refTypes);
      assocIncStrategy = new CompIncStrategy<>(refAssocs);

      mcTypeMatcher = new MCTypeMatcher(underspecifiedPlaceholderTypeName, typeIncStrategy);

      /*
       * We configure the matching strategies depending on the conformance checker parameter as we
       * want to have the same matching behavior during concretization as the conformance checker.
       */
      if (conformanceParams.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
        typeIncStrategy.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
        assocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(referenceCD, mapping));
      }
      if (conformanceParams.contains(CDConfParameter.NAME_MAPPING)) {
        typeIncStrategy.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));
        assocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(referenceCD, mapping));
      }

      // 'typeIncStrategyMatchingSubTypes' matches types which are an incarnation of a reference
      // type
      // themselves or have a subclass which is an incarnation of the reference type.
      // This strategy is only used when matching associations. If we want to allow the concrete CD
      // to
      // define associations in superclasses of the actual type incarnation, we have to pass this
      // type
      // matching strategy to the association matching strategies. This allows supertypes to 'act'
      // as
      // incarnation of the reference type in context of a specific association.
      // For example in the following concrete CD, A is a valid incarnation of A in the reference CD
      // because A is a subclass of X, which has an association towards B.
      //
      // classdiagram Concrete {
      //   class X;
      //   class A extends X;
      //   X -> B;
      // }
      //
      // classdiagram Reference {
      //   class A;
      //   class B;
      //   A -> B;
      // }
      typeIncStrategyMatchingSubTypes = new CompIncStrategy<>(refTypes);
      typeIncStrategyMatchingSubTypes.addIncStrategy(typeIncStrategy);
      if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
        typeIncStrategyMatchingSubTypes.addIncStrategy(new MatchCDTypesToSubType(typeIncStrategy, structureCache));
      }

      if (conformanceParams.contains(CDConfParameter.SRC_TARGET_ASSOC_MAPPING)) {
        if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
          assocIncStrategy.addIncStrategy(new RolePrefixInNavDirIncStrategy(
              typeIncStrategyMatchingSubTypes, structureCache));
          assocIncStrategy.addIncStrategy(new RolePrefixIfPresentIncStrategy(
              typeIncStrategyMatchingSubTypes, structureCache));
        }
        else {
          assocIncStrategy.addIncStrategy(new RolePrefixInNavDirIncStrategy(typeIncStrategy, structureCache));
          assocIncStrategy.addIncStrategy(new RolePrefixIfPresentIncStrategy(typeIncStrategy, structureCache));
        }
      }
    }

    @Override
    public ASTCDCompilationUnit getConcreteCD() { return concreteCD; }

    @Override
    public ASTCDCompilationUnit getReferenceCD() { return referenceCD; }

    @Override
    public String getMappingName() { return mapping; }

    @Override
    public String getUnderspecifiedPlaceholderTypeName() {
      return underspecifiedPlaceholderTypeName;
    }

    @Override
    public boolean isForEachNameAdaptationEnabled() { return forEachNameAdaptationEnabled; }

    @Override
    public Set<CDConfParameter> getConformanceParams() { return conformanceParams; }

    @Override
    public CompIncStrategy<ASTCDType> getTypeIncStrategy() { return typeIncStrategy; }

    @Override
    public CompIncStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes() {
      return typeIncStrategyMatchingSubTypes;
    }

    @Override
    public ExternalCandidatesMatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
      return assocIncStrategy;
    }

    @Override
    public ExternalCandidatesMatchingStrategy<ASTCDAttribute> createAttributeIncStrategy(
        ASTCDType referenceType) {
      CompAttributeIncStrategy attributeIncStrategy = new CompAttributeIncStrategy();
      if (conformanceParams.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
        attributeIncStrategy.addIncStrategy(new STAttributeIncStrategy(mapping));
      }
      if (conformanceParams.contains(CDConfParameter.NAME_MAPPING)) {
        attributeIncStrategy.addIncStrategy(new EqNameAttributeIncStrategy());
      }
      attributeIncStrategy.setReferenceType(referenceType);
      return attributeIncStrategy;
    }

    @Override
    public ExternalCandidatesMatchingStrategy<ASTCDMethod> createMethodIncStrategy(
        ASTCDType referenceType) {
      CompMethodIncStrategy methodIncStrategy = new CompMethodIncStrategy();
      if (conformanceParams.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
        methodIncStrategy.addIncStrategy(new STMethodIncStrategy(mapping));
      }
      if (conformanceParams.contains(CDConfParameter.NAME_MAPPING)) {
        if (conformanceParams.contains(CDConfParameter.METHOD_OVERLOADING)) {
          methodIncStrategy.addIncStrategy(new EqSignatureMethodIncStrategy(mcTypeMatcher,
              conformanceParams.contains(CDConfParameter.STRICT_PARAMETER_ORDER)));
        }
        else {
          methodIncStrategy.addIncStrategy(new EqNameMethodIncStrategy());
        }
      }
      methodIncStrategy.setReferenceType(referenceType);
      return methodIncStrategy;
    }

    @Override
    public ScopedIncarnationBindings getScopedIncarnationBindings() {
      return scopedIncarnationBindings;
    }

    @Override
    public Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType) {
      return getTypeIncarnations(concreteCD.getEnclosingScope(), referenceType);
    }

    @Override
    public Set<ASTCDType> getTypeIncarnations(IScope scope, ASTCDType referenceType) {
      // TODO improve readability...
      // 1. check for scoped incarnation bindings
      Optional<Collection<TypeSymbol>> typeIncarnationsOpt = scopedIncarnationBindings
          .getScopedTypeIncarnations(scope, referenceType.getSymbol());
      if (typeIncarnationsOpt.isPresent()) {
        // map symbols back to AST nodes
        return typeIncarnationsOpt.get().stream().map(SymbolUtil::cdTypeFromTypeSymbol).collect(
            Collectors.toSet());
      }
      else {
        // 2. Find all incarnations using the usual incarnation strategies
        return ConcretizationHelper.getCDTypes(getConcreteCD()).stream().filter(
            type -> getTypeIncStrategy().isMatched(type, referenceType)).collect(Collectors
                .toSet());
      }
    }

    @Override
    public Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute) {
      return getAttributeIncarnations(concreteCD.getEnclosingScope(), referenceAttribute);
    }

    @Override
    public Set<ASTCDAttribute> getAttributeIncarnations(IScope scope,
        ASTCDAttribute referenceAttribute) {
      Optional<Collection<FieldSymbol>> fieldIncarnationsOpt = scopedIncarnationBindings
          .getScopedFieldIncarnations(scope, referenceAttribute.getSymbol());
      if (fieldIncarnationsOpt.isPresent()) {
        // map symbols back to AST nodes
        return fieldIncarnationsOpt.get().stream().map(SymbolUtil::cdAttributeFromFieldSymbol)
            .collect(Collectors.toSet());
      }
      else {
        // 2. Find all incarnations using the usual incarnation strategies
        ASTCDType attributeDeclaringType = (ASTCDType) referenceAttribute.getSymbol()
            .getEnclosingScope().getAstNode();

        return getTypeIncarnations(scope, attributeDeclaringType).stream().flatMap((
            cAttributeDeclaringType) -> {
          BooleanMatchingStrategy<ASTCDAttribute> attributeIncStrategy = createAttributeIncStrategy(
              attributeDeclaringType);
          return cAttributeDeclaringType.getCDAttributeList().stream().filter(
              attributeIncarnation -> attributeIncStrategy.isMatched(attributeIncarnation,
                  referenceAttribute));
        }).collect(Collectors.toSet());
      }
    }

  }

}
