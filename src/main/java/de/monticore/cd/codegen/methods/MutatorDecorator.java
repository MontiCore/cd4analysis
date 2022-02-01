/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd.codegen.AbstractCreator;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.List;

public class MutatorDecorator extends SpecificMethodDecorator {

  public MutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex, new MandatoryMutatorDecorator(glex), new OptionalMutatorDecorator(glex), new ListMutatorDecorator(glex));
  }

  public MutatorDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
    super(glex,mandatoryMethodDecorator, optionalMethodDecorator, listMethodDecorator);
  }
}
