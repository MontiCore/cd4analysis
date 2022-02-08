/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import de.monticore.cd.codegen.AbstractCreator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.List;

abstract class SpecificMethodDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator;

  SpecificMethodDecorator(final GlobalExtensionManagement glex,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mandatoryMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> optionalMethodDecorator,
      final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> listMethodDecorator) {
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
    AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> specificMethodDecorator = determineMethodDecoratorStrategy(ast);
    return specificMethodDecorator.decorate(ast);
  }

  protected AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> determineMethodDecoratorStrategy(final ASTCDAttribute ast) {
    if (getMCTypeFacade().isBooleanType(ast.getMCType())) {
      return mandatoryMethodDecorator;
    } else if (ast.getMCType() instanceof ASTMCListType) {
      return listMethodDecorator;
    } else if (ast.getMCType() instanceof ASTMCOptionalType) {
      return optionalMethodDecorator;
    }
    return mandatoryMethodDecorator;
  }
}
