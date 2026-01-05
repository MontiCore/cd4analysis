/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.hookpoints;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.SourceMapAwareTemplateController;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;

public class TemplateHookPointWithInfo extends TemplateHookPoint {
  
  public TemplateHookPointWithInfo(String templateName) {
    super(templateName);
  }
  
  public TemplateHookPointWithInfo(String templateName, Object... templateArguments) {
    super(templateName, templateArguments);
  }
  
  protected String decorator;
  
  public void setDecorator(String decorator) { this.decorator = decorator; }
  
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    if (controller instanceof SourceMapAwareTemplateController) {
      ((SourceMapAwareTemplateController) controller).reportSource(ast);
      ((SourceMapAwareTemplateController) controller).reportDecorator(this.decorator);
      ((SourceMapAwareTemplateController) controller).reportTemplateName(super.getTemplateName());
    }
    return super.processValue(controller, ast);
  }
  
  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
    if (controller instanceof SourceMapAwareTemplateController) {
      ((SourceMapAwareTemplateController) controller).reportSource(node);
      ((SourceMapAwareTemplateController) controller).reportDecorator(this.decorator);
      ((SourceMapAwareTemplateController) controller).reportTemplateName(super.getTemplateName());
    }
    return super.processValue(controller, node, args);
  }
  
  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    if (controller instanceof SourceMapAwareTemplateController) {
      ((SourceMapAwareTemplateController) controller).reportDecorator(this.decorator);
      ((SourceMapAwareTemplateController) controller).reportTemplateName(super.getTemplateName());
    }
    return super.processValue(controller, args);
  }
  
}
