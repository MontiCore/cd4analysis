/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.inc.CDIncarnationBindings;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.DefaultCDIncarnationBindings;
import de.monticore.cdconformance.inc.DefaultCDIncarnationMapping;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.attribute.CompAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.EqNameAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.STAttributeIncStrategy;
import de.monticore.cdconformance.inc.method.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchCDTypesToSubTypes;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.CDSynDiffMatches;
import de.monticore.cdmatcher.CachedMultiMatches;

import java.util.Set;

public class DefaultCDConformanceContext implements CDConformanceContext {
  
  private final ASTCDCompilationUnit concreteCD;
  private final ASTCDCompilationUnit referenceCD;
  private final String mapping;
  
  private final String underspecifiedPlaceholderTypeName;
  private final Set<CDConfParameter> conformanceParams;
  private final ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy;
  private final ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategyMatchingSubTypes;
  private final ExternalCandidatesMatchingStrategy<ASTCDAssociation> assocIncStrategy;
  
  private final CDAttributeMatchingStrategy attributeIncStrategy;
  private final CDMethodMatchingStrategy methodIncStrategy;
  
  private final MCTypeMatcher mcTypeMatcher;
  
  private final CDIncarnationMapping incarnationMapping;
  
  protected DefaultCDConformanceContext(ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD, String mapping, String underspecifiedPlaceholderTypeName,
      Set<CDConfParameter> conformanceParams,
      ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy,
      ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategyMatchingSubTypes,
      ExternalCandidatesMatchingStrategy<ASTCDAssociation> assocIncStrategy,
      CDAttributeMatchingStrategy attributeIncStrategy, CDMethodMatchingStrategy methodIncStrategy,
      MCTypeMatcher mcTypeMatcher) {
    this.concreteCD = concreteCD;
    this.referenceCD = referenceCD;
    this.mapping = mapping;
    this.underspecifiedPlaceholderTypeName = underspecifiedPlaceholderTypeName;
    this.conformanceParams = conformanceParams;
    this.typeIncStrategy = typeIncStrategy;
    this.typeIncStrategyMatchingSubTypes = typeIncStrategyMatchingSubTypes;
    this.assocIncStrategy = assocIncStrategy;
    this.attributeIncStrategy = attributeIncStrategy;
    this.methodIncStrategy = methodIncStrategy;
    this.mcTypeMatcher = mcTypeMatcher;
    
    CDIncarnationBindings incarnationBinding = new DefaultCDIncarnationBindings();
    // TODO provide bindings impl supporting method overloading
    incarnationMapping = new DefaultCDIncarnationMapping(concreteCD, typeIncStrategy,
        attributeIncStrategy, methodIncStrategy, assocIncStrategy, incarnationBinding);
  }
  
  public static CDConformanceContext create(ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD, String mapping, String underspecifiedPlaceholderTypeName,
      Set<CDConfParameter> conformanceParams) {
    CompTypeIncStrategy compTypeIncStrategy = new CompTypeIncStrategy(referenceCD, mapping);
    CompAssocIncStrategy compAssocIncStrategy = new CompAssocIncStrategy(referenceCD, mapping);
    CompAttributeIncStrategy compAttributeIncStrategy = new CompAttributeIncStrategy();
    CompMethodIncStrategy compMethodIncStrategy = new CompMethodIncStrategy();
    
    MCTypeMatcher mcTypeMatcher = new MCTypeMatcher(underspecifiedPlaceholderTypeName,
        compTypeIncStrategy);
    
    /*
     * We configure the matching strategies depending on the conformance checker parameter as we
     * want to have the same matching behavior during concretization as the conformance checker.
     */
    if (conformanceParams.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
      compTypeIncStrategy.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
      compAssocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(referenceCD, mapping));
      compAttributeIncStrategy.addIncStrategy(new STAttributeIncStrategy(mapping));
      compMethodIncStrategy.addIncStrategy(new STMethodIncStrategy(mapping));
    }
    if (conformanceParams.contains(CDConfParameter.NAME_MAPPING)) {
      compTypeIncStrategy.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));
      compAssocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(referenceCD, mapping));
      compAttributeIncStrategy.addIncStrategy(new EqNameAttributeIncStrategy());
      if (conformanceParams.contains(CDConfParameter.METHOD_OVERLOADING)) {
        compMethodIncStrategy.addIncStrategy(new EqSignatureMethodIncStrategy(mcTypeMatcher,
            conformanceParams.contains(CDConfParameter.STRICT_PARAMETER_ORDER)));
      }
      else {
        compMethodIncStrategy.addIncStrategy(new EqNameMethodIncStrategy());
      }
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
    CompTypeIncStrategy compSubTypeIncStrategy = new CompTypeIncStrategy(referenceCD, mapping);
    compSubTypeIncStrategy.addIncStrategy(compTypeIncStrategy);
    if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
      compSubTypeIncStrategy.addIncStrategy(new MatchCDTypesToSubTypes(compTypeIncStrategy,
          concreteCD, referenceCD));
    }
    
    if (conformanceParams.contains(CDConfParameter.SRC_TARGET_ASSOC_MAPPING)) {
      if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
        compAssocIncStrategy.addIncStrategy(new RolePrefixInNavDirIncStrategy(
            compSubTypeIncStrategy, concreteCD, referenceCD));
        compAssocIncStrategy.addIncStrategy(new RolePrefixIfPresentIncStrategy(
            compSubTypeIncStrategy, concreteCD, referenceCD));
      }
      else {
        compAssocIncStrategy.addIncStrategy(new RolePrefixInNavDirIncStrategy(compTypeIncStrategy,
            concreteCD, referenceCD));
        compAssocIncStrategy.addIncStrategy(new RolePrefixIfPresentIncStrategy(compTypeIncStrategy,
            concreteCD, referenceCD));
      }
    }
    
    return new DefaultCDConformanceContext(concreteCD, referenceCD, mapping,
        underspecifiedPlaceholderTypeName, conformanceParams, compTypeIncStrategy,
        compSubTypeIncStrategy, compAssocIncStrategy, compAttributeIncStrategy,
        compMethodIncStrategy, mcTypeMatcher);
  }
  
  /**
   * Creates a cached context optimizing performance. Caches the results of matching strategies
   * to avoid recomputation during conformance checks.<br>
   * <br>
   * NOTE: Currently, supports caching for type and association matching strategies.
   *
   * @param concreteCD the concrete CD
   * @param referenceCD the reference CD
   * @param mapping the mapping name
   * @param underspecifiedPlaceholderTypeName the name of the underspecified placeholder type
   * @param conformanceParams the conformance parameters to use
   * @return a CDConformanceContext with cached matching strategies
   */
  public static CDConformanceContext createCached(ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD, String mapping, String underspecifiedPlaceholderTypeName,
      Set<CDConfParameter> conformanceParams) {
    CDConformanceContext context = create(concreteCD, referenceCD, mapping,
        underspecifiedPlaceholderTypeName, conformanceParams);
    return createCached(context);
  }
  
  /**
   * Creates a cached conformance context from the given context.
   *
   * @param context the original conformance context to cache
   * @return a new CDConformanceContext with cached matching strategies
   */
  public static CDConformanceContext createCached(CDConformanceContext context) {
    // we compute and cache all type matches to optimize performanceAdd commentMore actions
    Set<ASTCDType> concTypes = CDDiffUtil.getAllTypesFromCD(context.getConcreteCD());
    CachedMultiMatches<ASTCDType> cachedTypeIncStrategy = new CachedMultiMatches<>(CDSynDiffMatches
        .computeMultiMatching(concTypes, context.getTypeIncStrategy()));
    MCTypeMatcher mcTypeMatcher = new MCTypeMatcher(context.getUnderspecifiedPlaceholderTypeName(),
        cachedTypeIncStrategy);
    
    CachedMultiMatches<ASTCDType> cachedSubtypeIncStrategy = new CachedMultiMatches<>(
        CDSynDiffMatches.computeMultiMatching(concTypes, context
            .getTypeIncStrategyMatchingSubTypes()));
    
    // we compute and cache all association matches to optimize performanceAdd commentMore actions
    Set<ASTCDAssociation> concAssocs = CDDiffUtil.getAllAssocsFromCD(context.getConcreteCD());
    CachedMultiMatches<ASTCDAssociation> cachedAssocIncStrategy = new CachedMultiMatches<>(
        CDSynDiffMatches.computeMultiMatching(concAssocs, context.getAssociationIncStrategy()));
    
    return new DefaultCDConformanceContext(context.getConcreteCD(), context.getReferenceCD(),
        context.getMappingName(), context.getUnderspecifiedPlaceholderTypeName(), context
            .getConformanceParams(), cachedTypeIncStrategy, cachedSubtypeIncStrategy,
        cachedAssocIncStrategy, context.getAttributeIncStrategy(), context.getMethodIncStrategy(),
        mcTypeMatcher);
  }
  
  @Override
  public ASTCDCompilationUnit getConcreteCD() { return concreteCD; }
  
  @Override
  public ASTCDCompilationUnit getReferenceCD() { return referenceCD; }
  
  @Override
  public String getMappingName() { return mapping; }
  
  @Override
  public String getUnderspecifiedPlaceholderTypeName() { return underspecifiedPlaceholderTypeName; }
  
  @Override
  public Set<CDConfParameter> getConformanceParams() { return conformanceParams; }
  
  @Override
  public ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategy() {
    return typeIncStrategy;
  }
  
  @Override
  public ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes() {
    return typeIncStrategyMatchingSubTypes;
  }
  
  @Override
  public ExternalCandidatesMatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
    return assocIncStrategy;
  }
  
  @Override
  public CDAttributeMatchingStrategy getAttributeIncStrategy() { return attributeIncStrategy; }
  
  @Override
  public CDMethodMatchingStrategy getMethodIncStrategy() { return methodIncStrategy; }
  
  @Override
  public MCTypeMatcher getMCTypeMatcher() { return mcTypeMatcher; }
  
  @Override
  public CDIncarnationMapping getIncarnationMapping() { return incarnationMapping; }
  
}
