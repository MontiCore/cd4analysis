package de.monticore.cdconcretization.cd;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.AbstractCDCompleter;
import de.monticore.cdconcretization.CompletionContext;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.attribute.TypeCompletionContext;
import de.monticore.cdconcretization.typedetails.ITypeDetailsCompleter;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.conf.attribute.CompAttributeChecker;
import de.monticore.cdconformance.conf.attribute.EqNameAttributeChecker;
import de.monticore.cdconformance.conf.attribute.STNamedAttributeChecker;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.Set;

public class TypeDetailsCDCompleter extends AbstractCDCompleter {

  private final ITypeDetailsCompleter typeDetailsCompleter;

  public TypeDetailsCDCompleter(ITypeDetailsCompleter typeDetailsCompleter) {
    this.typeDetailsCompleter = typeDetailsCompleter;
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException {
    MatchingStrategy<ASTCDType> typeIncStrategy = context.getTypeIncStrategy();
    // complete member incarnations
    for (ASTCDClass cClass : concreteCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cClass)) {
        TypeCompletionContext typeCompletionContext =
            new DefaultTypeCompletionContext(context, cClass, rType);
        typeDetailsCompleter.completeTypeDetails(cClass, rType, typeCompletionContext);
      }
    }
    for (ASTCDInterface cInterface : concreteCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cInterface)) {
        TypeCompletionContext typeCompletionContext =
            new DefaultTypeCompletionContext(context, cInterface, rType);
        typeDetailsCompleter.completeTypeDetails(cInterface, rType, typeCompletionContext);
      }
    }
    for (ASTCDEnum cEnum : concreteCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cEnum)) {
        TypeCompletionContext typeCompletionContext =
            new DefaultTypeCompletionContext(context, cEnum, rType);
        typeDetailsCompleter.completeTypeDetails(cEnum, rType, typeCompletionContext);
      }
    }
    super.complete(concreteCD, referenceCD, context);
  }

  static class DefaultTypeCompletionContext implements TypeCompletionContext {

    private final CompletionContext parentContext;

    private final ASTCDType concreteType;

    private final ASTCDType referenceType;

    private final CompAttributeChecker attributeIncStrategy;

    DefaultTypeCompletionContext(
        CompletionContext parentContext, ASTCDType concreteType, ASTCDType referenceType) {
      this.parentContext = parentContext;
      this.concreteType = concreteType;
      this.referenceType = referenceType;

      attributeIncStrategy = new CompAttributeChecker(parentContext.getMappingName());
      if (parentContext.getConformanceParams().contains(CDConfParameter.STEREOTYPE_MAPPING)) {
        attributeIncStrategy.addIncStrategy(
            new STNamedAttributeChecker(parentContext.getMappingName()));
      }
      if (parentContext.getConformanceParams().contains(CDConfParameter.NAME_MAPPING)) {
        attributeIncStrategy.addIncStrategy(
            new EqNameAttributeChecker(parentContext.getMappingName()));
      }
      attributeIncStrategy.setConcreteType(concreteType);
      attributeIncStrategy.setReferenceType(referenceType);
    }

    @Override
    public ASTCDCompilationUnit getConcreteCD() {
      return parentContext.getConcreteCD();
    }

    @Override
    public ASTCDCompilationUnit getReferenceCD() {
      return parentContext.getReferenceCD();
    }

    @Override
    public String getMappingName() {
      return parentContext.getMappingName();
    }

    @Override
    public Set<CDConfParameter> getConformanceParams() {
      return parentContext.getConformanceParams();
    }

    @Override
    public MatchingStrategy<ASTCDType> getTypeIncStrategy() {
      return parentContext.getTypeIncStrategy();
    }

    @Override
    public MatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes() {
      return parentContext.getTypeIncStrategyMatchingSubTypes();
    }

    @Override
    public MatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
      return parentContext.getAssociationIncStrategy();
    }

    // === TypeCompletionContext specific ===

    @Override
    public ASTCDType getConcreteType() {
      return concreteType;
    }

    @Override
    public ASTCDType getReferenceType() {
      return referenceType;
    }

    @Override
    public CompAttributeChecker getAttributeIncStrategy() {
      return attributeIncStrategy;
    }
  }
}
