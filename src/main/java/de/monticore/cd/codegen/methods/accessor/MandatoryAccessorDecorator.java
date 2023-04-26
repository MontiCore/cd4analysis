/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods.accessor;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MandatoryAccessorDecorator extends AbstractMethodDecorator {

  protected static final String GET = "get%s";

  protected static final String IS = "is%s";

  public MandatoryAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public MandatoryAccessorDecorator(
      final GlobalExtensionManagement glex, final CDGenService service) {
    super(glex, service);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createGetter(ast)));
  }

  protected ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String getterPrefix;
    if (getMCTypeFacade().isBooleanType(ast.getMCType())
        || (ast.getMCType() instanceof ASTMCQualifiedType
            && ("Boolean".equals(ast.getMCType().printType())
                || "java.lang.Boolean".equals(ast.getMCType().printType())))) {
      getterPrefix = IS;
    } else {
      getterPrefix = GET;
    }
    String name =
        String.format(
            getterPrefix, StringUtils.capitalize(service.getNativeAttributeName(ast.getName())));
    ASTMCType type = ast.getMCType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    method.getModifier().setAbstract(ast.getModifier().isDerived());
    return method;
  }
}
