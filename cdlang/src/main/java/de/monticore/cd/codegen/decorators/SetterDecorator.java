/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.ForwardingTemplateHookPoint;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class SetterDecorator extends AbstractDecorator<SetterDecorator.SetterData> implements
    CDBasisVisitor2 {

  @Override
  public void visit(ASTCDAttribute attribute) {
    if (attribute.getModifier().isDerived() || attribute.getModifier().isReadonly() || attribute
        .getModifier().isFinal())
      return;

    if (decoratorData.shouldDecorate(this.getClass(), attribute)) {
      var originalClazz = decoratorData.getParent(attribute);
      var decClazz = (ASTCDClass) decoratorData.getAsDecorated(originalClazz.get());

      if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
        decorateMandatory(decClazz, attribute);
      }
      else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
        decorateList(decClazz, attribute);
        Log.warn("0xTODO: WIP List Setter", attribute.get_SourcePositionStart());
      }
      else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
        decorateSet(decClazz, attribute);
        Log.warn("0xTODO: WIP Set Setter", attribute.get_SourcePositionStart());
      }
      else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
        decorateOptional(decClazz, attribute);
      }
      else {
        decorateMandatory(decClazz, attribute);
      }
    }
  }

  protected void decorateMandatory(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "set" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = attribute.getMCType().deepClone();
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), name, CDParameterFacade.getInstance().createParameters(attribute));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method,
        new ForwardingTemplateHookPoint("methods.Set", glex, attribute)));

    addToClass(clazz, method);

    updateModifier(attribute);

    // Also track this data
    getData().addMethod(attribute, method);
  }

  protected void decorateOptional(ASTCDClass clazz, ASTCDAttribute attribute) {
    String name = "set" + StringUtils.capitalize(StringTransformations.capitalize(attribute
        .getName()));
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();
    ASTCDMethod method = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), name, CDParameterFacade.getInstance().createParameter(type, attribute
            .getName()));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, method,
        new ForwardingTemplateHookPoint("methods.opt.Set4Opt", glex, attribute, "")));

    addToClass(clazz, method);

    updateModifier(attribute);

    // Also track this data
    getData().addMethod(attribute, method);
  }

  protected void decorateSet(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    String name = "set" + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    ASTCDMethod setListMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), name, CDParameterFacade.getInstance().createParameter(MCTypeFacade
            .getInstance().createSetTypeOf(type), attribute.getName()));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, setListMethod, new TemplateHookPoint(
        "methods.Set", attribute)));
    setListMethod.getModifier().setAbstract(attribute.getModifier().isDerived());
    addToClass(decoratedClazz, setListMethod);

    getData().addMethod(attribute, setListMethod);

    this.updateModifier(attribute);
  }

  protected void decorateList(ASTCDClass decoratedClazz, ASTCDAttribute attribute) {
    String name = "set" + StringTransformations.capitalize(attribute.getName());
    ASTMCType type = getCDGenService().getFirstTypeArgument(attribute.getMCType()).deepClone();

    ASTCDMethod getListMethod = CDMethodFacade.getInstance().createMethod(attribute.getModifier()
        .deepClone(), name, CDParameterFacade.getInstance().createParameter(MCTypeFacade
            .getInstance().createListTypeOf(type), attribute.getName()));
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, getListMethod, new TemplateHookPoint(
        "methods.Set", attribute)));
    getListMethod.getModifier().setAbstract(attribute.getModifier().isDerived());
    addToClass(decoratedClazz, getListMethod);

    getData().addMethod(attribute, getListMethod);

    this.updateModifier(attribute);
  }

  public SetterData getData() {
    return (SetterData) decoratorData.decoratorDataMap.computeIfAbsent(SetterDecorator.class,
        aClass -> new SetterData());
  }

  protected void updateModifier(ASTCDAttribute attribute) {
    var decoratedModifier = decoratorData.getAsDecorated(attribute).getModifier();
    decoratedModifier.setProtected(true);
    decoratedModifier.setPublic(false);
    decoratedModifier.setPrivate(false);
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

  public static class SetterData {

    Map<ASTCDAttribute, List<ASTCDMethod>> methods = new HashMap<>();

    protected void addMethod(ASTCDAttribute attribute, ASTCDMethod method) {
      this.methods.computeIfAbsent(attribute, a -> new ArrayList<>()).add(method);
    }

  }

}
