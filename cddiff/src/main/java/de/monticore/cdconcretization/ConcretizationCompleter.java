package de.monticore.cdconcretization;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.association.DefaultAssocDetailsCompleter;
import de.monticore.cdconcretization.association.DefaultAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssociationDetailsCompleter;
import de.monticore.cdconcretization.attribute.AbstractAttributeCompleter;
import de.monticore.cdconcretization.attribute.BaseAttributeCompleter;
import de.monticore.cdconcretization.attribute.IAttributeCompleter;
import de.monticore.cdconcretization.cd.*;
import de.monticore.cdconcretization.cd.MissingAssociationsCDCompleter;
import de.monticore.cdconcretization.type.*;
import de.monticore.cdconcretization.typedetails.*;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cdmatcher.MatchCDTypesToSubTypes;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.Set;

public class ConcretizationCompleter {

  private final String mapping;

  /**
   * If true, the conformance checker is used to check the conformance of the concretization result.
   */
  private boolean checkConformance = true;

  /**
   * If true, redundant attributes are removed from the concretization result, even if they were
   * part of the concrete CD input.
   */
  private boolean removeRedundantAttributes = true;

  /** If true, the elements in the concretization result are reordered for consistent results. */
  private boolean reorderElements = true;

  protected Set<CDConfParameter> conformanceParams;

  public ConcretizationCompleter(String mapping, Set<CDConfParameter> conformanceParams) {
    this.mapping = mapping;
    this.conformanceParams = conformanceParams;
  }

  public void completeCD(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {

    /*
     * Basically we do a couple of dependency initialization here. We create a chain of completers
     * that are responsible for completing the different aspects of the CD. These completers are then used
     * to perform the actual concretization.
     */
    CompletionContext context =
        new DefaultCompletionContext(concreteCD, referenceCD, mapping, conformanceParams);

    ITypeCompleter typeCompleter =
        new ChainBuilder<AbstractTypeCompleter>()
            .add(new BaseTypeCompleter())
            .add(new TypeNameStereotypeCompleter())
            // TODO add forEach support here
            .build();

    IAttributeCompleter attributeCompleter =
        new ChainBuilder<AbstractAttributeCompleter>()
            .add(new BaseAttributeCompleter())
            // TODO add name stereotype support here
            // TODO add forEach stereotype support here
            .build();

    ITypeDetailsCompleter typeDetailsCompleter =
        new ChainBuilder<AbstractTypeDetailsCompleter>()
            .add(new ClassModifierCompleter())
            .add(new TypeAttributesCompleter(attributeCompleter))
            // TODO add method completer here
            .add(new DefaultEnumConstantsCompleter())
            .build();

    IAssocSideCompleter assocSideCompleter = new DefaultAssocSideCompleter();
    IAssociationDetailsCompleter assocDetailsCompleter =
        new DefaultAssocDetailsCompleter(concreteCD, assocSideCompleter);

    ChainBuilder<AbstractCDCompleter> completerChainBuilder =
        new ChainBuilder<AbstractCDCompleter>()
            .add(new ImportsCompleter())
            .add(new MissingTypesCDCompleter(typeCompleter))
            .add(new InheritanceCompleter())
            .add(new TypeDetailsCDCompleter(typeDetailsCompleter))
            .add(new ExistingAssociationsCDCompleter(assocDetailsCompleter))
            .add(new MissingAssociationsCDCompleter(assocDetailsCompleter))
            .add(
                new ConformanceCheckCompletionStep(
                    mapping, "The association completion result is not conform"));

    // add configurable,optional steps
    if (removeRedundantAttributes) {
      completerChainBuilder.add(new RemoveRedundantAttributesStep());
    }
    if (reorderElements) {
      completerChainBuilder.add(new ReorderElementsCompletionStep());
    }
    if (checkConformance) {
      completerChainBuilder.add(
          new ConformanceCheckCompletionStep(mapping, "Final conformance check failed."));
    }

    // perform the actual concretization
    completerChainBuilder.build().complete(concreteCD, referenceCD, context);
  }

  /***
   * Provides default configurations for the matching strategies used in the concretization process.
   */
  static class DefaultCompletionContext implements CompletionContext {
    private final ASTCDCompilationUnit concreteCD;
    private final ASTCDCompilationUnit referenceCD;
    private final String mapping;
    private final Set<CDConfParameter> conformanceParams;
    private final CompTypeIncStrategy typeIncStrategy;
    private final CompTypeIncStrategy typeIncStrategyMatchingSubTypes;
    private final CompAssocIncStrategy assocIncStrategy;

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
  }
}
