/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd.codegen.AbstractCreator;
import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

public class AccessorDecorator extends SpecificMethodDecorator {

  public AccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex, new MandatoryAccessorDecorator(glex), new OptionalAccessorDecorator(glex), new ListAccessorDecorator(glex));
  }

  public AccessorDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
    super(glex,mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
