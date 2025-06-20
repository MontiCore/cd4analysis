/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.association.DefaultAssocCompleter;
import de.monticore.cdconcretization.association.DefaultAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssociationCompleter;
import de.monticore.cdconcretization.cd.*;
import de.monticore.cdconcretization.cd.type.AbstractTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.BaseTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.ForEachTypeInCDCompleter;
import de.monticore.cdconcretization.cd.type.ITypeInCDCompleter;
import de.monticore.cdconcretization.type.*;
import de.monticore.cdconcretization.type.attribute.AbstractAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.BaseAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.ForEachAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.attribute.IAttributeInTypeCompleter;
import de.monticore.cdconcretization.type.method.AbstractMethodInTypeCompleter;
import de.monticore.cdconcretization.type.method.BaseMethodInTypeCompleter;
import de.monticore.cdconcretization.type.method.ForEachMethodCompleter;
import de.monticore.cdconcretization.type.method.IMethodInTypeCompleter;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.DefaultCDConformanceContext;

import java.util.Set;

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
        underspecifiedPlaceholderTypeName, forEachNameAdaptationEnabled, conformanceParams);
    
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
  static class DefaultCompletionContext extends DefaultCDConformanceContext implements
      CDCompletionContext {
    
    private final boolean forEachNameAdaptationEnabled;
    
    public DefaultCompletionContext(ASTCDCompilationUnit concreteCD,
        ASTCDCompilationUnit referenceCD, String mapping, String underspecifiedPlaceholderTypeName,
        boolean forEachNameAdaptationEnabled, Set<CDConfParameter> conformanceParams) {
      super(concreteCD, referenceCD, mapping, underspecifiedPlaceholderTypeName, conformanceParams);
      this.forEachNameAdaptationEnabled = forEachNameAdaptationEnabled;
    }
    
    @Override
    public boolean isForEachNameAdaptationEnabled() { return forEachNameAdaptationEnabled; }
    
    @Override
    public Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType) {
      return getIncarnationMapping().getIncarnations(referenceType);
    }
    
    @Override
    public Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute) {
      return getIncarnationMapping().getIncarnations(referenceAttribute);
    }
    
    @Override
    public Set<ASTCDMethod> getMethodIncarnations(ASTCDMethod referenceMethod) {
      return getIncarnationMapping().getIncarnations(referenceMethod);
    }
    
  }
  
}
