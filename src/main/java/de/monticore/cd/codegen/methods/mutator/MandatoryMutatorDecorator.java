/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.methods.mutator;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.AbstractMethodDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MandatoryMutatorDecorator extends AbstractMethodDecorator {

  protected static final String SET = "set%s";

  public MandatoryMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    if (ast.getModifier().isDerived() || ast.getModifier().isReadonly()) {
      return Lists.newArrayList();
    }
    return new ArrayList<>(Arrays.asList(createSetter(ast)));
  }

  protected ASTCDMethod createSetter(final ASTCDAttribute ast) {
    String name =
        String.format(SET, StringUtils.capitalize(service.getNativeAttributeName(ast.getName())));
    ASTCDMethod method =
        this.getCDMethodFacade()
            .createMethod(PUBLIC.build(), name, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Set", ast));
    return method;
  }
}
