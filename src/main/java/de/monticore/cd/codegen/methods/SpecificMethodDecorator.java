/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.List;

abstract class SpecificMethodDecorator extends AbstractMethodDecorator {

  protected final AbstractMethodDecorator mandatoryMethodDecorator;

  protected final AbstractMethodDecorator optionalMethodDecorator;

  protected final AbstractMethodDecorator listMethodDecorator;

  SpecificMethodDecorator(final GlobalExtensionManagement glex,
      final AbstractMethodDecorator mandatoryMethodDecorator,
      final AbstractMethodDecorator optionalMethodDecorator,
      final AbstractMethodDecorator listMethodDecorator) {
    super(glex);
    this.mandatoryMethodDecorator = mandatoryMethodDecorator;
    this.optionalMethodDecorator = optionalMethodDecorator;
    this.listMethodDecorator = listMethodDecorator;
  }

  @Override
  public void enableTemplates() {
    mandatoryMethodDecorator.enableTemplates();
    optionalMethodDecorator.enableTemplates();
    listMethodDecorator.enableTemplates();
  }

  @Override
  public void disableTemplates() {
    mandatoryMethodDecorator.disableTemplates();
    optionalMethodDecorator.disableTemplates();
    listMethodDecorator.disableTemplates();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    AbstractMethodDecorator specificMethodDecorator = determineMethodDecoratorStrategy(ast);
    return specificMethodDecorator.decorate(ast);
  }

  protected AbstractMethodDecorator determineMethodDecoratorStrategy(final ASTCDAttribute ast) {
    if (MCTypeFacade.getInstance().isBooleanType(ast.getMCType())) {
      return mandatoryMethodDecorator;
    } else if (ast.getMCType() instanceof ASTMCListType) {
      return listMethodDecorator;
    } else if (ast.getMCType() instanceof ASTMCOptionalType) {
      return optionalMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
