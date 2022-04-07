package de.monticore.cd.codegen.methods;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.types.MCTypeFacade;

import java.util.List;

abstract public class AbstractMethodDecorator {

  protected final GlobalExtensionManagement glex;

  protected boolean templatesEnabled;

  protected final MCTypeFacade mcTypeFacade;

  protected final CDMethodFacade cdMethodFacade;

  protected final CDParameterFacade cdParameterFacade;

  protected final CDGenService service;

  public AbstractMethodDecorator(final GlobalExtensionManagement glex) {
    this(glex,
      MCTypeFacade.getInstance(),
      CDMethodFacade.getInstance(),
      CDParameterFacade.getInstance(),
      new CDGenService()
    );
  }

  public AbstractMethodDecorator(final GlobalExtensionManagement glex,
                           final MCTypeFacade mcTypeFacade,
                           final CDMethodFacade cdMethodFacade,
                           final CDParameterFacade cdParameterFacade,
                           final CDGenService service) {
    this.glex = glex;
    this.templatesEnabled = true;
    this.mcTypeFacade = mcTypeFacade;
    this.cdMethodFacade = cdMethodFacade;
    this.cdParameterFacade = cdParameterFacade;
    this.service = service;
  }

  public abstract List<ASTCDMethod> decorate(ASTCDAttribute input);

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

  protected CDMethodFacade getCDMethodFacade() {
    return this.cdMethodFacade;
  }

  protected CDParameterFacade getCDParameterFacade() {
    return this.cdParameterFacade;
  }

}
