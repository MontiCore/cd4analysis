/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.ITypeCompleter;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import java.util.Set;

/** Completes the details of types (attributes, methods, etc.) in a CD. */
public class TypeDetailsCDCompleter extends AbstractCDCompleter {
  
  private final ITypeCompleter typeDetailsCompleter;
  
  public TypeDetailsCDCompleter(ITypeCompleter typeDetailsCompleter) {
    this.typeDetailsCompleter = typeDetailsCompleter;
  }
  
  @Override
  public void complete(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD,
      CDCompletionContext context) throws CompletionException {
    ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy = context.getTypeIncStrategy();
    // complete member incarnations
    for (ASTCDClass cClass : concreteCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cClass)) {
        TypeCompletionContext typeCompletionContext = new DefaultTypeCompletionContext(context,
            cClass, rType);
        context.getIncarnationMapping().addBinding(cClass.getSymbol(), rType.getSymbol(), cClass
            .getSymbol());
        typeDetailsCompleter.completeType(cClass, rType, typeCompletionContext);
      }
    }
    
    for (ASTCDInterface cInterface : concreteCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cInterface)) {
        TypeCompletionContext typeCompletionContext = new DefaultTypeCompletionContext(context,
            cInterface, rType);
        context.getIncarnationMapping().addBinding(cInterface.getSymbol(), rType.getSymbol(),
            cInterface.getSymbol());
        typeDetailsCompleter.completeType(cInterface, rType, typeCompletionContext);
      }
    }
    for (ASTCDEnum cEnum : concreteCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cEnum)) {
        TypeCompletionContext typeCompletionContext = new DefaultTypeCompletionContext(context,
            cEnum, rType);
        context.getIncarnationMapping().addBinding(cEnum.getSymbol(), rType.getSymbol(), cEnum
            .getSymbol());
        typeDetailsCompleter.completeType(cEnum, rType, typeCompletionContext);
      }
    }
    super.complete(concreteCD, referenceCD, context);
  }
  
  static class DefaultTypeCompletionContext implements TypeCompletionContext {
    
    private final CDCompletionContext parentContext;
    private final ASTCDType concreteType;
    private final ASTCDType referenceType;
    private final CDAttributeMatchingStrategy attributeIncStrategy;
    private final CDMethodMatchingStrategy methodIncStrategy;
    
    DefaultTypeCompletionContext(CDCompletionContext parentContext, ASTCDType concreteType,
        ASTCDType referenceType) {
      this.parentContext = parentContext;
      this.concreteType = concreteType;
      this.referenceType = referenceType;
      
      // TODO this is not save for multi threading
      attributeIncStrategy = parentContext.getAttributeIncStrategy();
      attributeIncStrategy.setReferenceType(referenceType);
      methodIncStrategy = parentContext.getMethodIncStrategy();
      methodIncStrategy.setReferenceType(referenceType);
    }
    
    @Override
    public ASTCDCompilationUnit getConcreteCD() { return parentContext.getConcreteCD(); }
    
    @Override
    public ASTCDCompilationUnit getReferenceCD() { return parentContext.getReferenceCD(); }
    
    @Override
    public String getMappingName() { return parentContext.getMappingName(); }
    
    @Override
    public String getUnderspecifiedPlaceholderTypeName() {
      return parentContext.getUnderspecifiedPlaceholderTypeName();
    }
    
    @Override
    public boolean isForEachNameAdaptationEnabled() {
      return parentContext.isForEachNameAdaptationEnabled();
    }
    
    @Override
    public Set<CDConfParameter> getConformanceParams() {
      return parentContext.getConformanceParams();
    }
    
    @Override
    public ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategy() {
      return parentContext.getTypeIncStrategy();
    }
    
    @Override
    public ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes() {
      return parentContext.getTypeIncStrategyMatchingSubTypes();
    }
    
    @Override
    public ExternalCandidatesMatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
      return parentContext.getAssociationIncStrategy();
    }
    
    // === TypeCompletionContext specific ===
    
    @Override
    public ASTCDType getConcreteType() { return concreteType; }
    
    @Override
    public ASTCDType getReferenceType() { return referenceType; }
    
    @Override
    public CDAttributeMatchingStrategy getAttributeIncStrategy() { return attributeIncStrategy; }
    
    @Override
    public CDMethodMatchingStrategy getMethodIncStrategy() { return methodIncStrategy; }
    
    @Override
    public Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType) {
      return getIncarnationMapping().getIncarnations(getConcreteType().getSpannedScope(),
          referenceType);
    }
    
    @Override
    public Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute) {
      return getIncarnationMapping().getIncarnations(getConcreteType().getSpannedScope(),
          referenceAttribute);
    }
    
    @Override
    public Set<ASTCDMethod> getMethodIncarnations(ASTCDMethod referenceMethod) {
      return getIncarnationMapping().getIncarnations(getConcreteType().getSpannedScope(),
          referenceMethod);
    }
    
    @Override
    public MCTypeMatcher getMCTypeMatcher() { return parentContext.getMCTypeMatcher(); }
    
    @Override
    public CDIncarnationMapping getIncarnationMapping() {
      return parentContext.getIncarnationMapping();
    }
    
  }
  
}
