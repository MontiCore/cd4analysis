/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators.data;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;

/**
 * A {@link TemplateHookPoint} which respects template forwarding
 * @deprecated Use hook-points instead
 */
@Deprecated
public class ForwardingTemplateHookPoint extends TemplateHookPoint {
  protected final GlobalExtensionManagement glex;

  public ForwardingTemplateHookPoint(String templateName, GlobalExtensionManagement glex, Object... templateArguments) {
    super(templateName, templateArguments);
    this.glex = glex;
  }


  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    StringBuilder ret = new StringBuilder();
    List<HookPoint> templateForwardings = getTemplateForwardings(templateName, ast);
    for (HookPoint tn : templateForwardings) {
      ret.append(tn.processValue(controller, ast, this.templateArguments));
    }
    return ret.toString();
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    StringBuilder ret = new StringBuilder();
    List<HookPoint> templateForwardings = getTemplateForwardings(templateName, null);
    for (HookPoint tn : templateForwardings) {
      ret.append(tn.processValue(controller, joinArgs(args)));
    }
    return ret.toString();
  }


  @Override
  public String processValue(TemplateController controller, ASTNode ast, List<Object> args) {
    StringBuilder ret = new StringBuilder();
    List<HookPoint> templateForwardings = getTemplateForwardings(templateName, ast);
    for (HookPoint tn : templateForwardings) {
      ret.append(tn.processValue(controller, ast, joinArgs(args)));
    }
    return ret.toString();
  }


  protected List<Object> joinArgs(List<Object> args) {
    List<Object> joinedArgs = Lists.newArrayList(args);
    joinedArgs.addAll(this.templateArguments);
    return joinedArgs;
  }

  protected List<HookPoint> getTemplateForwardings(String templateName, ASTNode ast) {
    try {
      var m = glex.getClass().getDeclaredMethod("getTemplateForwardings", String.class, ASTNode.class);
      m.setAccessible(true);
      return (List<HookPoint>) m.invoke(glex, templateName, ast);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
