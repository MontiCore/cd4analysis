// (c) https://github.com/MontiCore/monticore

package de.monticore.cd.methodtemplates;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.Stack;

public class CD4C {

  /**
   * build a stack for the defined methods to match the body to the "current" defined method signature
   */
  protected final Stack<CD4CTemplateMethodHelper> methodQueue = new Stack<>();
  protected final GeneratorSetup config;
  protected String emptyBodyTemplate = "de.monticore.cd.methodtemplates.core.EmptyMethod";

  public CD4C(GeneratorSetup setup) {
    this.config = setup;
    setup.getGlex().defineGlobalVar("cd4c", this);

  }

  public void setEmptyBodyTemplate(String emptyBodyTemplate) {
    this.emptyBodyTemplate = emptyBodyTemplate;
  }

  /***************************************************************************/
  /* Methods                                                                 */
  /***************************************************************************/

  /**
   * create the new method
   *
   * @param clazz
   * @param templateName
   * @param arguments
   * @return the created method
   */
  public Optional<ASTCDMethodSignature> createMethod(ASTCDClass clazz, String templateName, Object... arguments) {
    return createMethodSignatureAndBody(clazz, templateName, arguments).flatMap(CD4CTemplateMethodHelper::getMethod);
  }

  /**
   * add the new method to the provided class
   *
   * @param clazz
   * @param templateName
   * @param arguments
   */
  public void addMethod(ASTCDClass clazz, String templateName, Object... arguments) {
    final Optional<ASTCDMethodSignature> method = createMethod(clazz, templateName, arguments);
    if (!method.isPresent()) {
      Log.error("11000: There was method created in the template '" + templateName + "'");
      return;
    }
    clazz.addCDMember(method.get());
  }

  /**
   * Use this method to describe the signature in templates
   * @param methodSignature
   */
  public void method(String methodSignature) {
    final CD4CTemplateMethodHelper m = new CD4CTemplateMethodHelper();
    m.method(methodSignature);
    methodQueue.add(m);
  }

  /***************************************************************************/
  /* Constructors                                                                */
  /***************************************************************************/

  /**
   * create the new constructor
   *
   * @param clazz
   * @param templateName
   * @param arguments
   * @return the created method
   */
  public Optional<ASTCDMethodSignature> createConstructor(ASTCDClass clazz, String templateName, Object... arguments) {
    return createMethodSignatureAndBody(clazz, templateName, arguments).flatMap(CD4CTemplateMethodHelper::getMethod);
  }

  /**
   * add the new constrcutor to the provided class
   *
   * @param clazz
   * @param templateName
   * @param arguments
   */
  public void addConstructor(ASTCDClass clazz, String templateName, Object... arguments) {
    final Optional<ASTCDMethodSignature> method = createConstructor(clazz, templateName, arguments);
    if (!method.isPresent()) {
      Log.error("11001: There was constructor created in the template '" + templateName + "'");
      return;
    }
    clazz.addCDMember(method.get());
  }

  /**
   * Use this method to describe the signature in templates
   * @param constructorSignature
   */
  public void constructor(String constructorSignature) {
    final CD4CTemplateMethodHelper m = new CD4CTemplateMethodHelper();
    m.constructor(constructorSignature);
    methodQueue.add(m);
  }

  /***************************************************************************/
  /* internal methods                                                                 */
  /***************************************************************************/

  protected Optional<CD4CTemplateMethodHelper> createMethodSignatureAndBody(ASTCDClass clazz, String templateName, Object... arguments) {
    final TemplateHookPoint templateHookPoint = new TemplateHookPoint(
        templateName,
        arguments);

    TemplateController controller = new TemplateController(this.config, templateName);
    return addMethodBody(templateHookPoint.processValue(controller, clazz));
  }

  protected Optional<CD4CTemplateMethodHelper> addMethodBody(String body) {
    if (methodQueue.isEmpty()) {
      throw new RuntimeException("1100A: cannot add method body: no previous method present");
    }

    final CD4CTemplateMethodHelper methodHelper = methodQueue.pop();
    if (!methodHelper.getMethod().isPresent()) {
      // if the method is not set, then add it to the stack again
      methodQueue.add(methodHelper);
      return Optional.empty();
    }

    final ASTCDMethodSignature method = methodHelper.getMethod().get();
    config.getGlex().replaceTemplate(emptyBodyTemplate, method, new StringHookPoint(body));

    return Optional.of(methodHelper);
  }

}
