/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconformance.inc.CDIncarnationBindings;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.DefaultCDIncarnationBindings;
import de.monticore.cdconformance.inc.DefaultCDIncarnationMapping;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.attribute.CompAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.EqNameAttributeIncStrategy;
import de.monticore.cdconformance.inc.attribute.STAttributeIncStrategy;
import de.monticore.cdconformance.inc.method.CompMethodIncStrategy;
import de.monticore.cdconformance.inc.method.EqNameMethodIncStrategy;
import de.monticore.cdconformance.inc.method.EqSignatureMethodIncStrategy;
import de.monticore.cdconformance.inc.method.STMethodIncStrategy;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchCDTypesToSubTypes;

import java.util.Set;

public class DefaultCDConformanceContext implements CDConformanceContext {
  
  private final ASTCDCompilationUnit concreteCD;
  private final ASTCDCompilationUnit referenceCD;
  private final String mapping;
  
  private final String underspecifiedPlaceholderTypeName;
  private final Set<CDConfParameter> conformanceParams;
  private final CompTypeIncStrategy typeIncStrategy;
  private final CompTypeIncStrategy typeIncStrategyMatchingSubTypes;
  private final CompAssocIncStrategy assocIncStrategy;
  
  private final CompAttributeIncStrategy attributeIncStrategy;
  private final CompMethodIncStrategy methodIncStrategy;
  
  private final MCTypeMatcher mcTypeMatcher;
  
  private final CDIncarnationMapping incarnationMapping;
  
  public DefaultCDConformanceContext(ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD, String mapping, String underspecifiedPlaceholderTypeName,
      Set<CDConfParameter> conformanceParams) {
    this.concreteCD = concreteCD;
    this.referenceCD = referenceCD;
    this.mapping = mapping;
    this.underspecifiedPlaceholderTypeName = underspecifiedPlaceholderTypeName;
    this.conformanceParams = conformanceParams;
    
    typeIncStrategy = new CompTypeIncStrategy(referenceCD, mapping);
    assocIncStrategy = new CompAssocIncStrategy(referenceCD, mapping);
    attributeIncStrategy = new CompAttributeIncStrategy();
    methodIncStrategy = new CompMethodIncStrategy();
    
    mcTypeMatcher = new MCTypeMatcher(underspecifiedPlaceholderTypeName, typeIncStrategy);
    
    /*
     * We configure the matching strategies depending on the conformance checker parameter as we
     * want to have the same matching behavior during concretization as the conformance checker.
     */
    if (conformanceParams.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
      typeIncStrategy.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
      assocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(referenceCD, mapping));
      attributeIncStrategy.addIncStrategy(new STAttributeIncStrategy(mapping));
      methodIncStrategy.addIncStrategy(new STMethodIncStrategy(mapping));
    }
    if (conformanceParams.contains(CDConfParameter.NAME_MAPPING)) {
      typeIncStrategy.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));
      assocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(referenceCD, mapping));
      attributeIncStrategy.addIncStrategy(new EqNameAttributeIncStrategy());
      if (conformanceParams.contains(CDConfParameter.METHOD_OVERLOADING)) {
        methodIncStrategy.addIncStrategy(new EqSignatureMethodIncStrategy(mcTypeMatcher,
            conformanceParams.contains(CDConfParameter.STRICT_PARAMETER_ORDER)));
      }
      else {
        methodIncStrategy.addIncStrategy(new EqNameMethodIncStrategy());
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
    typeIncStrategyMatchingSubTypes = new CompTypeIncStrategy(referenceCD, mapping);
    typeIncStrategyMatchingSubTypes.addIncStrategy(typeIncStrategy);
    if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
      typeIncStrategyMatchingSubTypes.addIncStrategy(new MatchCDTypesToSubTypes(typeIncStrategy,
          concreteCD, referenceCD));
    }
    
    if (conformanceParams.contains(CDConfParameter.SRC_TARGET_ASSOC_MAPPING)) {
      if (conformanceParams.contains(CDConfParameter.INHERITANCE)) {
        assocIncStrategy.addIncStrategy(new RolePrefixInNavDirIncStrategy(
            typeIncStrategyMatchingSubTypes, concreteCD, referenceCD));
        assocIncStrategy.addIncStrategy(new RolePrefixIfPresentIncStrategy(
            typeIncStrategyMatchingSubTypes, concreteCD, referenceCD));
      }
      else {
        assocIncStrategy.addIncStrategy(new RolePrefixInNavDirIncStrategy(typeIncStrategy,
            concreteCD, referenceCD));
        assocIncStrategy.addIncStrategy(new RolePrefixIfPresentIncStrategy(typeIncStrategy,
            concreteCD, referenceCD));
      }
    }
    
    CDIncarnationBindings incarnationBinding = new DefaultCDIncarnationBindings();
    // TODO provide bidnigns impl supporting method overloading
    
    incarnationMapping = new DefaultCDIncarnationMapping(concreteCD, typeIncStrategy,
        attributeIncStrategy, methodIncStrategy, assocIncStrategy, incarnationBinding);
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
  public CompTypeIncStrategy getTypeIncStrategy() { return typeIncStrategy; }
  
  @Override
  public CompTypeIncStrategy getTypeIncStrategyMatchingSubTypes() {
    return typeIncStrategyMatchingSubTypes;
  }
  
  @Override
  public ExternalCandidatesMatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
    return assocIncStrategy;
  }
  
  @Override
  public CDAttributeMatchingStrategy getAttributeIncStrategy() { return attributeIncStrategy; }
  
  @Override
  public CompMethodIncStrategy getMethodIncStrategy() { return methodIncStrategy; }
  
  @Override
  public MCTypeMatcher getMCTypeMatcher() { return mcTypeMatcher; }
  
  @Override
  public CDIncarnationMapping getIncarnationMapping() { return incarnationMapping; }
  
}
