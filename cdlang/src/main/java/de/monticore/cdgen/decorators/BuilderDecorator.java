/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdgen.decorators.data.AbstractDecorator;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Applies the Builder-Pattern to the CD
 */
public class BuilderDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  @Override
  public List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Setter for an attribute,
    // thus the Setter decorator has to run before.
    return List.of(SetterDecorator.class);
  }

  Stack<ASTCDClass> decoratedBuilderClasses = new Stack<>();
  Stack<ASTCDMethod> decoratorBuildMethod = new Stack<>();
  Stack<ASTCDMethod> decoratorUnsafeBuildMethod = new Stack<>();
  Stack<ASTCDMethod> decoratorIsValidMethod = new Stack<>();
  Stack<Boolean> enabled = new Stack<>();

  @Override
  public void visit(ASTCDClass node) {
    // Only act if we should decorate the class
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      // Get the parent (package or CDDef)
      var origParent = this.decoratorData.getParent(node).get();
      // and the parent, but now the element of the target CD
      var decParent = this.decoratorData.getAsDecorated(origParent);

      // Create a new class with the "Builder" suffix
      ASTCDClassBuilder builderClassB = CD4CodeMill.cDClassBuilder();
      builderClassB.setName(node.getName() + "Builder");
      builderClassB.setModifier(node.getModifier().deepClone());
      ASTCDClass builderClass = builderClassB.build();
      // Add the builder class to the decorated CD
      addElementToParent(decParent, builderClass);

      // Add the unsafeBuild() method to the builder class
      ASTCDMethod unsafeBuildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "unsafeBuild");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, unsafeBuildMethod, new TemplateHookPoint("methods.builder.build", node.getName())));
      addToClass(builderClass, unsafeBuildMethod);
      decoratorUnsafeBuildMethod.push(unsafeBuildMethod);

      // Add a build() method to the builder class
      ASTCDMethod buildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "build");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("methods.builder.build", node.getName())));
      addToClass(builderClass, buildMethod);
      decoratorBuildMethod.push(buildMethod);

      // Add a isValid() method to the builder class
      ASTCDMethod isValidMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PRIVATE().build(),"isValid",new ArrayList<>());
      addToClass(builderClass,isValidMethod);
      decoratorIsValidMethod.push(isValidMethod);

      // Add Setter methods for all attributes to the builder class
      for(ASTCDAttribute attribute : node.getCDAttributeList()) {
        ASTCDMethod setMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName()+"Builder", "set" + StringTransformations.capitalize(attribute.getName()));
        glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, setMethod, new TemplateHookPoint("methods.builder.set", attribute)));
        addToClass(builderClass, setMethod);
      }

      // Add the builder class & build method to the stack
      decoratedBuilderClasses.add(builderClass);
      enabled.push(true);
    } else
      enabled.push(false);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      decoratedBuilderClasses.pop();
      decoratorBuildMethod.pop();
      decoratorUnsafeBuildMethod.pop();
      decoratorIsValidMethod.pop();
    }
    enabled.pop();
  }

  @Override
  public void visit(ASTCDAttribute attribute) {
    // Only do work if we are in a builder-enabled class
    if (!enabled.peek()) return;

    // We expect that the SetterDecorator has added a Setter for this attribute to the pojo class
    // TODO: In a perfect world, we would extract the name from the symbol or SetterDecorator data
    var methods = decoratorData.getDecoratorData(SetterDecorator.class).methods.get(attribute);
    if (methods == null || methods.isEmpty()) {
      Log.warn("Skipping builder pattern of " + attribute.getName() + " due to missing Setter methods", attribute.get_SourcePositionStart());
      return;
    }

    var decClazz = this.decoratedBuilderClasses.peek();

    // Add an attribute to the builder class
    decClazz.addCDMember(CDAttributeFacade.getInstance().createAttribute(CD4CodeMill.modifierBuilder().PROTECTED().build(), attribute.getMCType(), attribute.getName()));

    // as the unsafeBuild method is added first, it is at index 0

    // Use the template hook-point to add a call to the setter to the build() method
    if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorUnsafeBuildMethod.peek(), new StringHookPoint("v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    } else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorUnsafeBuildMethod.peek(), new StringHookPoint("v.add" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    } else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorUnsafeBuildMethod.peek(), new StringHookPoint("v.add" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    } else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorUnsafeBuildMethod.peek(), new StringHookPoint("if(this." + StringTransformations.capitalize(attribute.getName()) + ".isPresent())v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ".get());\n")));
    } else {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorUnsafeBuildMethod.peek(), new StringHookPoint("v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n")));
    }

    // Use the template hook-point to add a call to the setter to the build() method
    if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorBuildMethod.peek(), new StringHookPoint("if(this."+attribute.getName()+".isPresent()){\n " +
                                                                                                                                                                  "  v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n" +
                                                                                                                                                                  "}else{\n" +
                                                                                                                                                                  "  v.set" + StringTransformations.capitalize(attribute.getName()+"Absent") +"(); \n" +
                                                                                                                                                                  "}\n")));
    } else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorBuildMethod.peek(), new StringHookPoint("if(this."+attribute.getName()+"!=null){\n " +
                                                                                                                                                                  "  v.add" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n" +
                                                                                                                                                                  "}")));

    } else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorBuildMethod.peek(), new StringHookPoint("if(this."+attribute.getName()+"!=null){\n " +
                                                                                                                                                                  "  v.add" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n" +
                                                                                                                                                                  "}")));
    } else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorBuildMethod.peek(), new StringHookPoint("if(this."+attribute.getName()+".isPresent()){\n " +
                                                                                                                                                                  "  if(this." + StringTransformations.capitalize(attribute.getName()) + ".isPresent())v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ".get());\n" +
                                                                                                                                                                  "}else{\n" +
                                                                                                                                                                  "  v.set" + StringTransformations.capitalize(attribute.getName()+"Absent") +"(); \n" +
                                                                                                                                                                  "}\n")));
    } else {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner", decoratorBuildMethod.peek(), new StringHookPoint("if(this."+attribute.getName()+".isPresent()){\n " +
                                                                                                                                                                  "  v.set" + StringTransformations.capitalize(attribute.getName()) + "(this." + attribute.getName() + ");\n" +
                                                                                                                                                                  "}else{\n" +
                                                                                                                                                                  "  v.set" + StringTransformations.capitalize(attribute.getName()+"Absent") +"(); \n" +
                                                                                                                                                                  "}\n")));
    }


    // TODO: Create chainable(?) methods
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
