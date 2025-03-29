package de.monticore.cdconcretization;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.association.DefaultAssocDetailsCompleter;
import de.monticore.cdconcretization.association.DefaultAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssocSideCompleter;
import de.monticore.cdconcretization.association.IAssociationDetailsCompleter;
import de.monticore.cdconcretization.cd.*;
import de.monticore.cdconcretization.cd.MissingAssociationsCDCompleter;
import de.monticore.cdconcretization.type.*;
import de.monticore.cdconcretization.typedetails.*;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cdmatcher.MatchCDTypesToSubTypes;
import de.monticore.cdmatcher.MatchingStrategy;

public class ConcretizationCompleter {

  private final String mapping;

  public ConcretizationCompleter(String mapping) {
    this.mapping = mapping;
  }

  public void completeCD(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {

    /*
     * Basically we do a couple of dependency initialization here. We create a chain of completers
     * that are responsible for completing the different aspects of the CD. These completers are then used
     * to perform the actual concretization.
     */
    CompletionContext context = new DefaultCompletionContext(concreteCD, referenceCD, mapping);

    ITypeCompleter typeCompleter =
        new ChainBuilder<AbstractTypeCompleter>()
            .add(new BaseTypeCompleter())
            .add(new TypeNameStereotypeCompleter())
            // TODO add forEach support here
            .build();

    ITypeDetailsCompleter typeDetailsCompleter =
        new ChainBuilder<AbstractTypeDetailsCompleter>()
            .add(new ClassModifierCompleter())
            .add(new TypeAttributesCompleter(mapping))
            // TODO add method completer here
            .add(new DefaultEnumConstantsCompleter())
            .build();

    IAssocSideCompleter assocSideCompleter = new DefaultAssocSideCompleter();
    IAssociationDetailsCompleter assocDetailsCompleter =
        new DefaultAssocDetailsCompleter(concreteCD, assocSideCompleter);

    // TODO decide for one of the two implementations
    // ALTERNATIVE IMPLEMENTATION

    ICDCompleter completerChain =
        new ChainBuilder<AbstractCDCompleter>()
            .add(new ImportsCompleter())
            .add(new MissingTypesCDCompleter(typeCompleter))
            .add(new InheritanceCompleter())
            .add(new TypeDetailsCDCompleter(typeDetailsCompleter))
            .add(new ExistingAssociationsCDCompleter(assocDetailsCompleter))
            .add(new MissingAssociationsCDCompleter(assocDetailsCompleter))
            .add(
                new ConformanceCheckCompletionStep(
                    mapping, "The association completion result is not conform"))
            .add(new RemoveRedundantAttributesStep())
            .add(new ReorderElementsCompletionStep())
            .add(new ConformanceCheckCompletionStep(mapping, "Final conformance check failed."))
            .build();

    // perform the actual concretization
    completerChain.complete(concreteCD, referenceCD, context);
  }

  /***
   * Provides default configurations for the matching strategies used in the concretization process.
   */
  static class DefaultCompletionContext implements CompletionContext {
    private final ASTCDCompilationUnit concreteCD;
    private final ASTCDCompilationUnit referenceCD;
    private final String mapping;
    private final CompTypeIncStrategy typeIncStrategy;
    private final CompTypeIncStrategy typeIncStrategyMatchingSubTypes;
    private final CompAssocIncStrategy assocIncStrategy;

    public DefaultCompletionContext(
        ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mapping) {
      this.concreteCD = concreteCD;
      this.referenceCD = referenceCD;
      this.mapping = mapping;

      typeIncStrategy = new CompTypeIncStrategy(referenceCD, mapping);
      typeIncStrategy.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
      typeIncStrategy.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));

      // TODO use same config params as for conformance checker, e.g. decide if we want match assocs
      // in super types
      typeIncStrategyMatchingSubTypes = new CompTypeIncStrategy(referenceCD, mapping);
      typeIncStrategyMatchingSubTypes.addIncStrategy(typeIncStrategy);
      typeIncStrategyMatchingSubTypes.addIncStrategy(
          new MatchCDTypesToSubTypes(typeIncStrategy, concreteCD, referenceCD));

      assocIncStrategy = new CompAssocIncStrategy(referenceCD, mapping);
      assocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(referenceCD, mapping));
      assocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(referenceCD, mapping));
      assocIncStrategy.addIncStrategy(
          new RolePrefixInNavDirIncStrategy(
              typeIncStrategyMatchingSubTypes, concreteCD, referenceCD));
      assocIncStrategy.addIncStrategy(
          new RolePrefixIfPresentIncStrategy(
              typeIncStrategyMatchingSubTypes, concreteCD, referenceCD));
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
