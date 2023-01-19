/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods.mutator;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class OptionalMutatorDecorator extends AbstractMethodDecorator {

  protected static final String SET = "set%s";

  protected static final String SET_ABSENT = "set%sAbsent";

  protected String naiveAttributeName;

  public OptionalMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public OptionalMutatorDecorator(final GlobalExtensionManagement glex,
                                  final CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    if (ast.getModifier().isDerived() || ast.getModifier().isReadonly()) {
      return Lists.newArrayList();
    }
    List<ASTCDMethod> methodList = new ArrayList<>();
    naiveAttributeName = StringUtils.capitalize(service.getNativeAttributeName(ast.getName()));
    methodList.add(createSetMethod(ast));
    methodList.add(createSetAbsentMethod(ast));
    return methodList;
  }

  protected ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    String name = String.format(SET, naiveAttributeName);
    ASTMCType parameterType = service.getFirstTypeArgument(ast.getMCType()).deepClone();
    ASTCDParameter parameter =
        this.getCDParameterFacade().createParameter(parameterType, ast.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name, parameter);
    this.replaceTemplate(
        EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Set4Opt", ast, naiveAttributeName));
    return method;
  }

  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    String name = String.format(SET_ABSENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.SetAbsent", ast));
    return method;
  }
}
