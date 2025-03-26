package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.type.*;
import de.monticore.cdconcretization.typedetails.*;
import de.monticore.cdconcretization.util.ChainBuilder;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;

public class ConcretizationCompleter implements ICDCompleter {

  private final String mapping;

  public ConcretizationCompleter(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public void complete(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {

    /*
     * Basically we do a couple of dependency initialization here. We create a chain of completers
     * that are responsible for completing the different aspects of the CD. These completers are then used
     * by the BaseCDCompleter to perform the actual concretization.
     */
    CompTypeIncStrategy typeIncStrategy = new CompTypeIncStrategy(referenceCD, mapping);
    typeIncStrategy.addIncStrategy(new STTypeIncStrategy(referenceCD, mapping));
    typeIncStrategy.addIncStrategy(new EqTypeIncStrategy(referenceCD, mapping));

    ITypeCompleter typeCompleter =
        new ChainBuilder<AbstractTypeCompleter>()
            .add(new BaseTypeCompleter(typeIncStrategy))
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

    DefaultInheritanceCompleter inheritanceCompleter = new DefaultInheritanceCompleter(typeIncStrategy);

    // TODO refactor towards new architecture
    DefaultAssocIncCompleter assocIncCompleter =
        new DefaultAssocIncCompleter(concreteCD, referenceCD, mapping);

    BaseCDCompleter cdCompleter =
        new BaseCDCompleter(
            typeIncStrategy,
            typeCompleter,
            typeDetailsCompleter,
            inheritanceCompleter,
            assocIncCompleter);
    // perform the actual concretization
    cdCompleter.complete(concreteCD, referenceCD);
  }
}
