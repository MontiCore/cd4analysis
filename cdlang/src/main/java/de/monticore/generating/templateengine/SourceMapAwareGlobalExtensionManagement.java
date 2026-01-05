/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.hookpoints.TemplateHookPointWithInfo;
import de.monticore.generating.templateengine.reporting.Reporting;

import java.util.Arrays;
import java.util.List;

public class SourceMapAwareGlobalExtensionManagement extends GlobalExtensionManagement {
  
  //
  
  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName, ASTNode ast) {
    
    StringBuffer result = new StringBuffer(controller.getGeneratorSetup().isTracing()
        ? "/* Hookpoint: " + hookName + " */" : "");
    
    // Before replacement
    List<HookPoint> beforeHooks = getBeforeTemplates(hookName, ast);
    for (HookPoint h : beforeHooks) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "beforeHP,"
          + hookName);
      result.append(h.processValue(controller, ast));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    // HookPoint
    HookPoint hp = hookPoints.get(hookName);
    Reporting.reportCallHookPointStart(hookName, hp, ast);
    if (hookPoints.containsKey(hookName)) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "defineHP,"
          + hookName);
      result.append(hp.processValue(controller, ast));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    Reporting.reportCallHookPointEnd(hookName);
    
    // After replacement
    List<HookPoint> afterHooks = getAfterTemplates(hookName, ast);
    for (HookPoint h : afterHooks) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "afterHP,"
          + hookName);
      result.append(h.processValue(controller, ast));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    return result.toString();
  }
  
  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName, ASTNode ast,
      Object... args) {
    
    StringBuffer result = new StringBuffer();
    
    // Before replacement
    List<HookPoint> beforeHooks = getBeforeTemplates(hookName, ast);
    for (HookPoint h : beforeHooks) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "beforeHP,"
          + hookName);
      result.append(h.processValue(controller, ast));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    HookPoint hp = hookPoints.get(hookName);
    Reporting.reportCallHookPointStart(hookName, hp, ast);
    if (hookPoints.containsKey(hookName)) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "defineHP,"
          + hookName);
      result.append(hp.processValue(controller, ast, Arrays.asList(args)));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    Reporting.reportCallHookPointEnd(hookName);
    
    // After replacement
    List<HookPoint> afterHooks = getAfterTemplates(hookName, ast);
    for (HookPoint h : afterHooks) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "afterHP,"
          + hookName);
      result.append(h.processValue(controller, ast));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    return result.toString();
  }
  
  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName, Object... args) {
    
    StringBuffer result = new StringBuffer();
    
    // Before replacement
    List<HookPoint> beforeHooks = getBeforeTemplates(hookName, controller.getAST());
    for (HookPoint h : beforeHooks) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "beforeHP,"
          + hookName);
      result.append(h.processValue(controller, controller.getAST()));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    HookPoint hp = hookPoints.get(hookName);
    Reporting.reportCallHookPointStart(hookName, hp, controller.getAST());
    
    if (hookPoints.containsKey(hookName)) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "defineHP,"
          + hookName);
      result.append(hp.processValue(controller, Arrays.asList(args)));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    Reporting.reportCallHookPointEnd(hookName);
    
    // After replacement
    List<HookPoint> afterHooks = getAfterTemplates(hookName, controller.getAST());
    for (HookPoint h : afterHooks) {
      ((SourceMapAwareTemplateController) controller).enterNewTemplate(result.length(), "afterHP,"
          + hookName);
      result.append(h.processValue(controller, controller.getAST()));
      ((SourceMapAwareTemplateController) controller).popNewTemplate(result.length());
    }
    
    return result.toString();
  }
  
  @Override
  protected List<HookPoint> getTemplateForwardingsX(String templateName, ASTNode ast) {
    List<HookPoint> forwardings = Lists.newArrayList();
    
    if (containsTemplateForwarding(templateName)) {
      if (this.replace.containsKey(templateName)) {
        forwardings.addAll(this.replace.get(templateName));
        Reporting.reportCallReplacementHookPoint(templateName, forwardings, ast);
      }
      else {
        forwardings.addAll(Lists.newArrayList(new TemplateHookPointWithInfo(templateName)));
      }
    }
    else {
      forwardings.add(new TemplateHookPointWithInfo(templateName));
      Reporting.reportExecuteStandardTemplate(templateName, ast);
    }
    return forwardings;
  }
  
}
