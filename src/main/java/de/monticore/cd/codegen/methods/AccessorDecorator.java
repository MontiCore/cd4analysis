/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class AccessorDecorator extends SpecificMethodDecorator {

  public AccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex, new MandatoryAccessorDecorator(glex), new OptionalAccessorDecorator(glex), new ListAccessorDecorator(glex));
  }

  public AccessorDecorator(final GlobalExtensionManagement glex,
      final AbstractMethodDecorator mandatoryMethodDecorator,
      final AbstractMethodDecorator optionalMethodDecorator,
      final AbstractMethodDecorator listMethodDecorator) {
    super(glex,mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
