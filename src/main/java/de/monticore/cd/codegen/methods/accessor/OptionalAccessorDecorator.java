/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods.accessor;

import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class OptionalAccessorDecorator extends AbstractMethodDecorator {

  protected static final String GET = "get%s";

  protected static final String IS_PRESENT = "isPresent%s";

  protected String nativeAttributeName;

  public OptionalAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    nativeAttributeName = getNativeAttributeName(ast);
    ASTCDMethod get = createGetMethod(ast);
    ASTCDMethod isPresent = createIsPresentMethod(ast);
    return new ArrayList<>(Arrays.asList(get, isPresent));
  }

  protected String getNativeAttributeName(ASTCDAttribute astcdAttribute) {
    return StringUtils.capitalize(service.getNativeAttributeName(astcdAttribute.getName()));
  }

  protected ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, nativeAttributeName);
    ASTMCType type = service.getFirstTypeArgument(ast.getMCType()).deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    String generatedErrorCode = service.getGeneratedErrorCode(ast.getName() + ast.printType());
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Get4Opt", ast, nativeAttributeName, generatedErrorCode));
    return method;
  }

  protected ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, nativeAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.IsPresent4Opt", ast));
    return method;
  }
}
