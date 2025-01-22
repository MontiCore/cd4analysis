/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods.accessor;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class OptionalAccessorDecorator extends AbstractMethodDecorator {

  protected static final String GET = "get%s";

  protected static final String IS_PRESENT = "isPresent%s";

  protected String nativeAttributeName;

  public OptionalAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public OptionalAccessorDecorator(
      final GlobalExtensionManagement glex, final CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    nativeAttributeName = getNativeAttributeName(ast);
    List<ASTCDMethod> returnList = Lists.newArrayList();
    returnList.add(createGetMethod(ast));
    if (!ast.getModifier().isDerived()) {
      returnList.add(createIsPresentMethod(ast));
    }
    return returnList;
  }

  protected String getNativeAttributeName(ASTCDAttribute astcdAttribute) {
    return StringUtils.capitalize(service.getNativeAttributeName(astcdAttribute.getName()));
  }

  protected ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, nativeAttributeName);
    ASTMCType type = service.getFirstTypeArgument(ast.getMCType()).deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    String generatedErrorCode =
        service.getGeneratedErrorCode(ast.getName() + ast.getMCType().printType());
    this.replaceTemplate(
        EMPTY_BODY,
        method,
        new TemplateHookPoint("methods.opt.Get4Opt", ast, nativeAttributeName, generatedErrorCode));
    method.getModifier().setAbstract(ast.getModifier().isDerived());
    return method;
  }

  protected ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, nativeAttributeName);
    ASTCDMethod method =
        this.getCDMethodFacade()
            .createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), name);
    this.replaceTemplate(
        EMPTY_BODY, method, new TemplateHookPoint("methods.opt.IsPresent4Opt", ast));
    return method;
  }
}
