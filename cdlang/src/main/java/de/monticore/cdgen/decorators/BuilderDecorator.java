/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Stack;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Applies the Builder-Pattern to the CD
 */
public class BuilderDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  @Override
  public List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    return List.of(SetterDecorator.class);
  }

  Stack<ASTCDClass> decoratedBuilderClasses = new Stack<>();
  Stack<ASTCDMethod> decoratedBuildMethods = new Stack<>();
  Stack<Boolean> enabled = new Stack<>();

  @Override
  public void visit(ASTCDClass node) {
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      var origParent = this.decoratorData.getParent(node).get();
      var decParent = this.decoratorData.getAsDecorated(origParent);

      var builderClassB = CD4CodeMill.cDClassBuilder();
      builderClassB.setName(node.getName() + "Builder");
      builderClassB.setModifier(node.getModifier().deepClone());
      var builderClass = builderClassB.build();

      ASTCDMethod buildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "build");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("methods.builder.build", node.getName())));

      if (decParent instanceof ASTCDDefinition)
        ((ASTCDDefinition) decParent).addCDElement(builderClass);
      else if (decParent instanceof ASTCDPackage)
        ((ASTCDPackage) decParent).addCDElement(builderClass);
      else
        throw new IllegalStateException("Unhandled parent " + decParent.getClass().getName());

      addToClass(builderClass, buildMethod);

      decoratedBuilderClasses.add(builderClass);
      decoratedBuildMethods.add(buildMethod);
      enabled.push(true);
    } else
      enabled.push(false);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      decoratedBuilderClasses.pop();
      decoratedBuildMethods.pop();
    }
    enabled.pop();
  }

  @Override
  public void visit(ASTCDAttribute attribute) {
    if (!enabled.peek()) return;

    var methods = decoratorData.getDecoratorData(SetterDecorator.class).methods.get(attribute);
    if (methods == null || methods.isEmpty()) {
      Log.warn("Skipping builder pattern of " + attribute.getName(), attribute.get_SourcePositionStart());
      return;
    }

    var decClazz = this.decoratedBuilderClasses.peek();
    var decMethod = this.decoratedBuildMethods.peek();
    decClazz.addCDMember(CDAttributeFacade.getInstance().createAttribute(CD4CodeMill.modifierBuilder().PROTECTED().build(), attribute.getMCType(), attribute.getName()));
    if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decMethod, new StringHookPoint("v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    } else if (attribute.getMCType() instanceof ASTMCListType) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decMethod, new StringHookPoint("v.add" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    } else if (attribute.getMCType() instanceof ASTMCSetType) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decMethod, new StringHookPoint("v.add" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    } else if (attribute.getMCType() instanceof ASTMCOptionalType) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decMethod, new StringHookPoint("v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ".get());\n")));
    } else {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decMethod, new StringHookPoint("v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    }
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
