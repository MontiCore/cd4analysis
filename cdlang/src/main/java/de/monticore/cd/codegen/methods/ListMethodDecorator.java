/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public abstract class ListMethodDecorator extends AbstractMethodDecorator {

  protected String capitalizedAttributeNameWithS;

  protected String capitalizedAttributeNameWithOutS;

  protected String attributeType;

  public ListMethodDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public ListMethodDecorator(final GlobalExtensionManagement glex, final CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    this.capitalizedAttributeNameWithS = getCapitalizedAttributeNameWithS(ast);
    // if the attributeName is set by itself then the s is not removed
    // this means capitalizedAttributeNameWithS == capitalizedAttributeNameWithOutS
    this.capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS;
    // but if the attributeName is derived then the s is removed
    if (capitalizedAttributeNameWithS.endsWith("s") && service.hasDerivedAttributeName(ast)) {
      this.capitalizedAttributeNameWithOutS =
          capitalizedAttributeNameWithS.substring(0, capitalizedAttributeNameWithS.length() - 1);
    }
    this.attributeType = getAttributeType(ast);

    List<ASTCDMethod> methods =
        getMethodSignatures().stream()
            .map(getCDMethodFacade()::createMethodByDefinition)
            .collect(Collectors.toList());

    methods.forEach(m -> this.replaceTemplate(EMPTY_BODY, m, createListImplementation(m)));
    return methods;
  }

  protected abstract List<String> getMethodSignatures();

  protected HookPoint createListImplementation(final ASTCDMethod method) {
    String attributeName = StringUtils.uncapitalize(capitalizedAttributeNameWithOutS);
    int attributeIndex = method.getName().lastIndexOf(capitalizedAttributeNameWithOutS);
    String methodName = method.getName().substring(0, attributeIndex);
    String parameterCall =
        method.getCDParameterList().stream()
            .map(ASTCDParameter::getName)
            .collect(Collectors.joining(", "));
    String returnType =
        (new CD4CodeFullPrettyPrinter(new IndentPrinter())).prettyprint(method.getMCReturnType());

    return new TemplateHookPoint(
        "methods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }

  public String getCapitalizedAttributeNameWithS(ASTCDAttribute attribute) {
    return StringUtils.capitalize(service.getNativeAttributeName(attribute.getName()));
  }

  public String getAttributeType(ASTCDAttribute attribute) {
    ASTMCType type = service.getFirstTypeArgument(attribute.getMCType());
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    return pp.prettyprint(type);
  }
}
