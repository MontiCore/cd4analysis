/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdgen.decorators.data.AbstractDecorator;
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
      ASTNode origParent = this.decoratorData.getParent(node).get();
      // and the parent, but now the element of the target CD
      ASTNode decParent = this.decoratorData.getAsDecorated(origParent);

      // Create a new class with the "Builder" suffix
      ASTCDClassBuilder builderClassB = CD4CodeMill.cDClassBuilder();
      builderClassB.setName(node.getName() + "Builder");
      builderClassB.setModifier(node.getModifier().deepClone());
      ASTCDClass builderClass = builderClassB.build();
      // Add the builder class to the decorated CD
      addElementToParent(decParent, builderClass);

      // Add a build() method to the builder class
      ASTCDMethod buildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "build");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("methods.builder.build.build", node.getName(),true)));
      addToClass(builderClass, buildMethod);
      decoratorBuildMethod.push(buildMethod);

      // Add the unsafeBuild() method to the builder class
      ASTCDMethod unsafeBuildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), node.getName(), "unsafeBuild");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, unsafeBuildMethod, new TemplateHookPoint("methods.builder.build.build", node.getName(),false)));
      addToClass(builderClass, unsafeBuildMethod);
      decoratorUnsafeBuildMethod.push(unsafeBuildMethod);

      // Add a isValid() method to the builder class
      ASTCDMethod isValidMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PRIVATE().build(),MCTypeFacade.getInstance().createBooleanType(), "isValid");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("methods.builder.isValid.isValid")));
      addToClass(builderClass,isValidMethod);
      decoratorIsValidMethod.push(isValidMethod);

      // Add Setter methods for all attributes to the builder class
      for(ASTCDAttribute attribute : node.getCDAttributeList()) {
        ASTCDParameter param = CD4CodeMill.cDParameterBuilder().setName(attribute.getName()).setMCType(attribute.getMCType()).build();
        ASTCDMethod setMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), "set" + StringTransformations.capitalize(attribute.getName()), param);
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

    // Use the template hook-point to add a call to the setter to the build() methods
    String errorMessage = getCDGenService().getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType()) + " " + attribute.getName() + " of type " + attribute.getMCType().printType() + " must not be null";
    if (MCTypeFacade.getInstance().isBooleanType(attribute.getMCType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorBuildMethod.peek(),new TemplateHookPoint("methods.builder.build.buildSetCallBoolean",attribute,true,StringTransformations.capitalize(attribute.getName()))));
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorUnsafeBuildMethod.peek(),new TemplateHookPoint("methods.builder.buildSetCallBoolean",attribute,false,StringTransformations.capitalize(attribute.getName()))));
      //add isValid clause in the build method for attributes with cardinality 1
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:InnerIsValidClause", decoratorBuildMethod.peek(),new TemplateHookPoint("methods.builder.build.buildIsValidClause",attribute,errorMessage)));
      //add attribute clause in the isValid method
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.isValid.isValid:Inner", decoratorIsValidMethod.peek(),new TemplateHookPoint("methods.builder.isValid.isValidAttributeClause",attribute)));
    } else if (MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCallList",attribute,true,StringTransformations.capitalize(attribute.getName()))));
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorUnsafeBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCallList",attribute,false,StringTransformations.capitalize(attribute.getName()))));
      //create Absent method for List
      ASTCDMethod absentMethod = createAbsentMethod(attribute);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, absentMethod, new TemplateHookPoint("methods.builder.isAbsent.isAbsentList",attribute)));
    } else if (MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCallSet",attribute,true,StringTransformations.capitalize(attribute.getName()))));
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorUnsafeBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCallSet",attribute,false,StringTransformations.capitalize(attribute.getName()))));
      //create Absent method for Set
      ASTCDMethod absentMethod = createAbsentMethod(attribute);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, absentMethod, new TemplateHookPoint("methods.builder.isAbsent.isAbsentSet",attribute)));
    } else if (MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())) {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCallOptional",attribute,true,StringTransformations.capitalize(attribute.getName()))));
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorUnsafeBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCallOptional",attribute,false,StringTransformations.capitalize(attribute.getName()))));
      //create Absent method for Optional
      ASTCDMethod absentMethod = createAbsentMethod(attribute);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, absentMethod, new TemplateHookPoint("methods.builder.isAbsent.isAbsentOptional",attribute)));
    } else {
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCall",attribute,true,StringTransformations.capitalize(attribute.getName()))));
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:Inner", decoratorUnsafeBuildMethod.peek(), new TemplateHookPoint("methods.builder.build.buildSetCall",attribute,false,StringTransformations.capitalize(attribute.getName()))));
      //add isValid clause in the build method for attributes with cardinality 1
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build.build:InnerIsValidClause", decoratorBuildMethod.peek(),new TemplateHookPoint("methods.builder.build.buildIsValidClause",attribute,errorMessage)));
      //add attribute clause in the isValid method
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.isValid.isValid:Inner", decoratorIsValidMethod.peek(),new TemplateHookPoint("methods.builder.isValid.isValidAttributeClause",attribute)));
    }

    // TODO: Create chainable(?) methods
  }

  public ASTCDMethod createAbsentMethod(ASTCDAttribute attribute){
    ASTCDMethod setAbsentMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), "set"+StringTransformations.capitalize(attribute.getName())+"Absent");
    decoratedBuilderClasses.peek().addCDMember(setAbsentMethod);
    return setAbsentMethod;
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
