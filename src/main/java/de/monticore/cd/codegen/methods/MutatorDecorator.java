/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class MutatorDecorator extends SpecificMethodDecorator {

  public MutatorDecorator(final GlobalExtensionManagement glex) {
    super(
        glex,
        new MandatoryMutatorDecorator(glex),
        new OptionalMutatorDecorator(glex),
        new ListMutatorDecorator(glex));
  }

  public MutatorDecorator(final GlobalExtensionManagement glex,
                          final CDGenService service) {
    super(
      glex,
      new MandatoryMutatorDecorator(glex, service),
      new OptionalMutatorDecorator(glex, service),
      new ListMutatorDecorator(glex, service));
  }

  public MutatorDecorator(
      final GlobalExtensionManagement glex,
      final AbstractMethodDecorator mandatoryMethodDecorator,
      final AbstractMethodDecorator optionalMethodDecorator,
      final AbstractMethodDecorator listMethodDecorator) {
    super(glex, mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
