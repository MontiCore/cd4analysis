package de.monticore.cdconcretization.cd;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.ITypeCompleter;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.Set;

/** Completes the details of types (attributes, methods, etc.) in a CD. */
public class TypeDetailsCDCompleter extends AbstractCDCompleter {

  private final ITypeCompleter typeDetailsCompleter;

  public TypeDetailsCDCompleter(

      ITypeCompleter typeDetailsCompleter) {
    this.typeDetailsCompleter = typeDetailsCompleter;
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD,
      CDCompletionContext context)
      throws CompletionException {MatchingStrategy<ASTCDType> typeIncStrategy = context.getTypeIncStrategy();
    // complete member incarnations
    for (ASTCDClass cClass : concreteCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cClass)) {
        TypeCompletionContext typeCompletionContext =
            new DefaultTypeCompletionContext(context, cClass, rType);
        typeDetailsCompleter.completeType(cClass, rType, typeCompletionContext);
      }
    }
    for (ASTCDInterface cInterface : concreteCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cInterface)) {
        TypeCompletionContext typeCompletionContext =
            new DefaultTypeCompletionContext(context, cInterface, rType);
        typeDetailsCompleter.completeType(cInterface, rType, typeCompletionContext);
      }
    }
    for (ASTCDEnum cEnum : concreteCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDType rType : typeIncStrategy.getMatchedElements(cEnum)) {
        TypeCompletionContext typeCompletionContext =
            new DefaultTypeCompletionContext(context, cEnum, rType);
        typeDetailsCompleter.completeType(cEnum, rType, typeCompletionContext);
      }
    }
    super.complete(concreteCD, referenceCD, context);
  }

  static class DefaultTypeCompletionContext implements TypeCompletionContext {

    private final CDCompletionContext parentContext;

    private final ASTCDType concreteType;

    private final ASTCDType referenceType;

    private final MatchingStrategy<ASTCDAttribute> attributeIncStrategy;

    DefaultTypeCompletionContext(
        CDCompletionContext parentContext, ASTCDType concreteType, ASTCDType referenceType) {
      this.parentContext = parentContext;
      this.concreteType = concreteType;
      this.referenceType = referenceType;

      attributeIncStrategy = parentContext.getAttributeIncStrategy(concreteType, referenceType);
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
    public String getUnderspecifiedPlaceholderTypeName() {
      return parentContext.getUnderspecifiedPlaceholderTypeName();
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
    public MatchingStrategy<ASTCDAttribute> getAttributeIncStrategy() {
      return attributeIncStrategy;
    }

    @Override
    public MatchingStrategy<ASTCDAttribute> getAttributeIncStrategy(
        ASTCDType concreteType, ASTCDType referenceType) {
      return parentContext.getAttributeIncStrategy(concreteType, referenceType);
    }
  }
}
