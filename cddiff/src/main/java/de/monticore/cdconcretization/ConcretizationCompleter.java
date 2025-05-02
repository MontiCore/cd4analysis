package de.monticore.cdconcretization;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.association.DefaultAssocCompleter;
import de.monticore.cdconcretization.association.DefaultAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssociationCompleter;
import de.monticore.cdconcretization.cd.type.ForEachTypeInCDCompleter;
import de.monticore.cdconcretization.type.attribute.AbstractAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.BaseAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.ForEachAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.IAttributeInTypeCompleter;
import de.monticore.cdconcretization.cd.*;
import de.monticore.cdconcretization.cd.MissingAssociationsCDCompleter;
import de.monticore.cdconcretization.cd.type.AbstractTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.BaseTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.ITypeInCDCompleter;
import de.monticore.cdconcretization.type.*;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.conf.attribute.CompAttributeChecker;
import de.monticore.cdconformance.conf.attribute.EqNameAttributeChecker;
import de.monticore.cdconformance.conf.attribute.STNamedAttributeChecker;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;
import de.monticore.cdmatcher.MatchCDTypesToSubTypes;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScope;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tool for automatic completion of a concrete class diagram (CD) such that it conforms to a given
 * reference CD. The completion process is implemented using multiple modular completer
 * implementations for each element kind in a CD. This class is the facade with easy to use
 * configuration parameters and a single method to perform the completion:
 * {@link #completeCD(ASTCDCompilationUnit, ASTCDCompilationUnit)}).
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
    CDCompletionContext context =
        new DefaultCompletionContext(concreteCD, referenceCD, mapping, conformanceParams);

    ITypeInCDCompleter typeInCDCompleter =
        new ChainBuilder<AbstractTypeInCDCompleter>()
            .add(new ForEachTypeInCDCompleter())
            .add(new BaseTypeInCDCompleter())
            .build();

    IAttributeInTypeCompleter attributeInType =
        new ChainBuilder<AbstractAttributeInTypeCompleter>()
            // TODO add name stereotype support here
            .add(new ForEachAttributeInTypeCompleter())
            .add(new BaseAttributeInTypeCompleter())
            .build();

    ITypeCompleter typeCompleter =
        new ChainBuilder<AbstractTypeCompleter>()
            .add(new ClassModifierCompleter())
            .add(new TypeAttributesCompleter(attributeInType))
            // TODO add method completer here
            .add(new DefaultEnumConstantsCompleter())
            .build();

    IAssocSideCompleter assocSideCompleter = new DefaultAssocSideCompleter();
    IAssociationCompleter assocCompleter =
        new DefaultAssocCompleter(concreteCD, assocSideCompleter);

    ChainBuilder<AbstractCDCompleter> completerChainBuilder =
        new ChainBuilder<AbstractCDCompleter>()
            .add(new ImportsCompleter())
            .add(new MissingTypesCDCompleter(typeInCDCompleter))
            .add(new InheritanceCompleter())
            .add(new TypeDetailsCDCompleter(typeCompleter))
            .add(new ExistingAssociationsCDCompleter(assocCompleter))
            .add(new MissingAssociationsCDCompleter(assocCompleter));

    // add configurable,optional steps
    if (removeRedundancies) {
      completerChainBuilder.add(new RemoveRedundanciesCompletionStep());
    }
    if (reorderElements) {
      completerChainBuilder.add(new ReorderElementsCompletionStep());
    }
    if (checkConformance) {
      completerChainBuilder.add(
          new ConformanceCheckCompletionStep(mapping, "Completion result is not conform"));
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
    private final CompTypeIncStrategy typeIncStrategy;
    private final CompTypeIncStrategy typeIncStrategyMatchingSubTypes;
    private final CompAssocIncStrategy assocIncStrategy;
    private final TypeIncarnationHelper typeIncarnationHelper;

    private final ScopedIncarnationBindings scopedIncarnationBindings =
        new ScopedIncarnationBindings();

    public DefaultCompletionContext(
        ASTCDCompilationUnit concreteCD,
        ASTCDCompilationUnit referenceCD,
        String mapping,
        Set<CDConfParameter> conformanceParams) {
      this.concreteCD = concreteCD;
      this.referenceCD = referenceCD;
      this.mapping = mapping;
      this.conformanceParams = conformanceParams;

      typeIncStrategy = new CompTypeIncStrategy(referenceCD, mapping);
      assocIncStrategy = new CompAssocIncStrategy(referenceCD, mapping);

      // TODO better way to share this logic between conformance and concretization!
      typeIncarnationHelper =
          new TypeIncarnationHelper(underspecifiedPlaceholderTypeName, typeIncStrategy);

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

      // 'typeIncStrategyMatchingSubTypes' matches types which are an incarnation of a reference type
      // themselves or have a subclass which is an incarnation of the reference type.
      // This strategy is only used when matching associations. If we want to allow the concrete CD to
      // define associations in superclasses of the actual type incarnation, we have to pass this type
      // matching strategy to the association matching strategies. This allows supertypes to 'act' as
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
      typeIncStrategyMatchingSubTypes = new CompTypeIncStrategy(referenceCD, mapping);
      typeIncStrategyMatchingSubTypes.addIncStrategy(typeIncStrategy);
      if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
        typeIncStrategyMatchingSubTypes.addIncStrategy(
            new MatchCDTypesToSubTypes(typeIncStrategy, concreteCD, referenceCD));
      }

      if (conformanceParams.contains(CDConfParameter.SRC_TARGET_ASSOC_MAPPING)) {
        if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
          assocIncStrategy.addIncStrategy(
              new RolePrefixInNavDirIncStrategy(
                  typeIncStrategyMatchingSubTypes, concreteCD, referenceCD));
          assocIncStrategy.addIncStrategy(
              new RolePrefixIfPresentIncStrategy(
                  typeIncStrategyMatchingSubTypes, concreteCD, referenceCD));
        } else {
          assocIncStrategy.addIncStrategy(
              new RolePrefixInNavDirIncStrategy(typeIncStrategy, concreteCD, referenceCD));
          assocIncStrategy.addIncStrategy(
              new RolePrefixIfPresentIncStrategy(typeIncStrategy, concreteCD, referenceCD));
        }
      }
    }

    @Override
    public ASTCDCompilationUnit getConcreteCD() {
      return concreteCD;
    }

    @Override
    public ASTCDCompilationUnit getReferenceCD() {
      return referenceCD;
    }

    @Override
    public String getMappingName() {
      return mapping;
    }

    @Override
    public String getUnderspecifiedPlaceholderTypeName() {
      return underspecifiedPlaceholderTypeName;
    }

    @Override
    public boolean isForEachNameAdaptationEnabled() {
      return forEachNameAdaptationEnabled;
    }

    @Override
    public Set<CDConfParameter> getConformanceParams() {
      return conformanceParams;
    }

    @Override
    public CompTypeIncStrategy getTypeIncStrategy() {
      return typeIncStrategy;
    }

    @Override
    public CompTypeIncStrategy getTypeIncStrategyMatchingSubTypes() {
      return typeIncStrategyMatchingSubTypes;
    }

    @Override
    public MatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
      return assocIncStrategy;
    }

    @Override
    public MatchingStrategy<ASTCDAttribute> createAttributeIncStrategy(
        ASTCDType concreteType, ASTCDType referenceType) {
      CompAttributeChecker attributeIncStrategy = new CompAttributeChecker(
              mapping, typeIncarnationHelper);
      if (conformanceParams.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
        attributeIncStrategy.addIncStrategy(new STNamedAttributeChecker(
                mapping, typeIncarnationHelper));
      }
      if (conformanceParams.contains(CDConfParameter.NAME_MAPPING)) {
        attributeIncStrategy.addIncStrategy(new EqNameAttributeChecker(
                mapping, typeIncarnationHelper));
      }
      attributeIncStrategy.setConcreteType(concreteType);
      attributeIncStrategy.setReferenceType(referenceType);
      return attributeIncStrategy;
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
      Optional<Collection<TypeSymbol>> typeIncarnationsOpt =
          scopedIncarnationBindings.getScopedTypeIncarnations(scope, referenceType.getSymbol());
      if (typeIncarnationsOpt.isPresent()) {
        // map symbols back to AST nodes
        return typeIncarnationsOpt.get().stream()
            .map(SymbolUtil::cdTypeFromTypeSymbol)
            .collect(Collectors.toSet());
      } else {
        // 2. Find all incarnations using the usual incarnation strategies
        return ConcretizationHelper.getCDTypes(getConcreteCD()).stream()
            .filter(type -> getTypeIncStrategy().isMatched(type, referenceType))
            .collect(Collectors.toSet());
      }
    }

    @Override
    public Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute) {
      return getAttributeIncarnations(concreteCD.getEnclosingScope(), referenceAttribute);
    }

    @Override
    public Set<ASTCDAttribute> getAttributeIncarnations(
        IScope scope, ASTCDAttribute referenceAttribute) {
      Optional<Collection<FieldSymbol>> fieldIncarnationsOpt =
          scopedIncarnationBindings.getScopedFieldIncarnations(
              scope, referenceAttribute.getSymbol());
      if (fieldIncarnationsOpt.isPresent()) {
        // map symbols back to AST nodes
        return fieldIncarnationsOpt.get().stream()
            .map(SymbolUtil::cdAttributeFromFieldSymbol)
            .collect(Collectors.toSet());
      } else {
        // 2. Find all incarnations using the usual incarnation strategies
        ASTCDType attributeDeclaringType =
            (ASTCDType) referenceAttribute.getSymbol().getEnclosingScope().getAstNode();

        return getTypeIncarnations(scope, attributeDeclaringType).stream()
            .flatMap(
                (cAttributeDeclaringType) -> {
                  MatchingStrategy<ASTCDAttribute> attributeIncStrategy =
                      createAttributeIncStrategy(cAttributeDeclaringType, attributeDeclaringType);
                  return cAttributeDeclaringType.getCDAttributeList().stream()
                      .filter(
                          attributeIncarnation ->
                              attributeIncStrategy.isMatched(
                                  attributeIncarnation, referenceAttribute));
                })
            .collect(Collectors.toSet());
      }
    }
  }
}
