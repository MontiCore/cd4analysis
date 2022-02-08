/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.types.MCTypeFacade;

public abstract class AbstractDecorator {

  /**
   * Do not use for creation of new Decorators
   * Decide if your new Decorator is a Creator or a Transformer, to overwrite the correct decorate method
   * Only a class to sum up general Decorator functionality
   **/

  protected final GlobalExtensionManagement glex;

  protected boolean templatesEnabled;

  protected final MCTypeFacade mcTypeFacade;

  protected final CDAttributeFacade cdAttributeFacade;

  protected final CDConstructorFacade cdConstructorFacade;

  protected final CDMethodFacade cdMethodFacade;

  protected final CDParameterFacade cdParameterFacade;

  protected final AbstractService service;

  public AbstractDecorator() {
    this(null);
  }

  public AbstractDecorator(final GlobalExtensionManagement glex) {
    this(glex,
        MCTypeFacade.getInstance(),
        CDAttributeFacade.getInstance(),
        CDConstructorFacade.getInstance(),
        CDMethodFacade.getInstance(),
        CDParameterFacade.getInstance(),
        new AbstractService()
    );
  }

  public AbstractDecorator(final GlobalExtensionManagement glex,
                           final MCTypeFacade mcTypeFacade,
                           final CDAttributeFacade cdAttributeFacade,
                           final CDConstructorFacade cdConstructorFacade,
                           final CDMethodFacade cdMethodFacade,
                           final CDParameterFacade cdParameterFacade,
                           final AbstractService service) {
    this.glex = glex;
    this.templatesEnabled = true;
    this.mcTypeFacade = mcTypeFacade;
    this.cdAttributeFacade = cdAttributeFacade;
    this.cdConstructorFacade = cdConstructorFacade;
    this.cdMethodFacade = cdMethodFacade;
    this.cdParameterFacade = cdParameterFacade;
    this.service = service;
  }

  public void enableTemplates() {
    this.templatesEnabled = true;
  }

  public void disableTemplates() {
    this.templatesEnabled = false;
  }

  protected boolean templatesEnabled() {
    return this.templatesEnabled;
  }

  protected void replaceTemplate(String template, ASTNode node, HookPoint hookPoint) {
    if (this.templatesEnabled()) {
      this.glex.replaceTemplate(template, node, hookPoint);
    }
  }

  protected MCTypeFacade getMCTypeFacade() {
    return this.mcTypeFacade;
  }

  protected CDAttributeFacade getCDAttributeFacade() {
    return this.cdAttributeFacade;
  }

  protected CDConstructorFacade getCDConstructorFacade() {
    return this.cdConstructorFacade;
  }

  protected CDMethodFacade getCDMethodFacade() {
    return this.cdMethodFacade;
  }

  protected CDParameterFacade getCDParameterFacade() {
    return this.cdParameterFacade;
  }

}
