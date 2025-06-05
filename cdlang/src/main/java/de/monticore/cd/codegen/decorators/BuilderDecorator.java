/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.ast.ASTNode;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis._util.CD4AnalysisTypeDispatcher;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import java.util.Collections;
import java.util.Stack;
import java.util.*;

/**
 * Applies the Builder-Pattern to the CD
 */
public class BuilderDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements
    CDBasisVisitor2 {
  
  CD4AnalysisTypeDispatcher dispatcher = new CD4AnalysisTypeDispatcher();
  
  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    // We check that the SetterDecorator has added a Setter for an attribute,
    // thus the Setter decorator has to run before.
    return Iterables.concat(super.getMustRunAfter(), Collections.singletonList(
        SetterDecorator.class));
  }
  
  Stack<ASTCDClass> builderClasses = new Stack<>();
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
      // Get decorated pojo class
      ASTCDClass decClazz = this.decoratorData.getAsDecorated(node);
      
      // Create a new class with the "Builder" suffix
      ASTCDClassBuilder builderClassB = CD4CodeMill.cDClassBuilder();
      builderClassB.setName(node.getName() + "Builder");
      builderClassB.setModifier(node.getModifier().deepClone());
      ASTCDClass builderClass = builderClassB.build();
      builderClasses.push(builderClass);
      // Add the builder class to the decorated CD
      addElementToParent(decParent, builderClass);
      
      // Add Log import to the builder class
      CD4C.getInstance().addImport(builderClass, "de.se_rwth.commons.logging.Log");
      
      // Add builder attribute for TOP safety
      builderClass.addCDMember(CDAttributeFacade.getInstance().createAttribute(CD4CodeMill
          .modifierBuilder().PROTECTED().build(), builderClass.getName(), "realBuilder"));
      
      // Add a constructor to the builder class
      ASTCDConstructor constructor = CDConstructorFacade.getInstance().createConstructor(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), builderClass.getName());
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(
          "this.realBuilder = (" + builderClass.getName() + ") this;")));
      addToClass(builderClass, constructor);
      
      // Add a isValid() method to the builder class
      String staticErrorCode = "0x16725";
      ASTCDMethod isValidMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PRIVATE().build(), MCTypeFacade.getInstance().createBooleanType(),
          "isValid");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, isValidMethod,
          new TemplateHookPoint("methods.builder.isValid", new ArrayList<>(node
              .getCDAttributeList()), staticErrorCode)));
      addToClass(builderClass, isValidMethod);
      decoratorIsValidMethod.push(isValidMethod);
      
      // Add isAbsent methods for all attributes with cardinality != 1
      for (ASTCDAttribute attribute : node.getCDAttributeList()) {
        if (dispatcher.isMCCollectionTypesASTMCListType(attribute.getMCType()) || dispatcher
            .isMCCollectionTypesASTMCOptionalType(attribute.getMCType()) || dispatcher
                .isMCCollectionTypesASTMCSetType(attribute.getMCType())) {
          ASTCDMethod setAbsentMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
              .modifierBuilder().PUBLIC().build(), builderClass.getName(), "set"
                  + StringTransformations.capitalize(attribute.getName()) + "Absent");
          glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, setAbsentMethod,
              new TemplateHookPoint("methods.builder.setAbsent", attribute)));
          addToClass(builderClass, setAbsentMethod);
        }
      }
      
      // Add a build() method to the builder class
      ASTCDMethod buildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), node.getName(), "build");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint(
          "methods.builder.build", node.getName())));
      addToClass(builderClass, buildMethod);
      decoratorBuildMethod.push(buildMethod);
      
      // Add the unsafeBuild() method to the builder class
      ASTCDMethod unsafeBuildMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), node.getName(), "unsafeBuild");
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, unsafeBuildMethod,
          new TemplateHookPoint("methods.builder.unsafeBuild", node.getName())));
      addToClass(builderClass, unsafeBuildMethod);
      decoratorUnsafeBuildMethod.push(unsafeBuildMethod);
      
      //add a default package private constructor to the pojo class when no one exists. Needed inside the Builder
      if (!decClazz.getCDConstructorList().isEmpty()) {
        boolean hasDefaultConstructor = false;
        for (ASTCDConstructor constructorPojo : decClazz.getCDConstructorList()) {
          if (constructorPojo.getCDParameterList().isEmpty()) {
            if (constructorPojo.getModifier().isPrivate()) {
              //if we have a default constructor which is private, e need to set it to protected at least
              constructorPojo.setModifier(CD4CodeMill.modifierBuilder().PROTECTED().build());
            }
            hasDefaultConstructor = true;
          }
        }
        if (!hasDefaultConstructor) {
          ASTCDConstructor constructor1 = CDConstructorFacade.getInstance()
              .createDefaultConstructor(CD4CodeMill.modifierBuilder().PROTECTED().build(), node);
          addToClass(decClazz, constructor1);
        }
      }
      
      // Add the builder class to the stack c
      decoratedBuilderClasses.add(builderClass);
      enabled.push(true);
    }
    else
      enabled.push(false);
  }
  
  @Override
  public void visit(ASTCDAttribute node) {
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      // Add attributes to the builder class
      builderClasses.peek().addCDMember(CDAttributeFacade.getInstance().createAttribute(CD4CodeMill
          .modifierBuilder().PROTECTED().build(), node.getMCType(), node.getName()));
      
      // Add setter methods to the builder class
      ASTCDParameter param = CD4CodeMill.cDParameterBuilder().setName(node.getName()).setMCType(node
          .getMCType()).build();
      if (dispatcher.isMCCollectionTypesASTMCOptionalType(node.getMCType())) {
        //set of optional with type directly and not with optional<type>
        ASTMCType type = getCDGenService().getFirstTypeArgument(node.getMCType()).deepClone();
        param = CD4CodeMill.cDParameterBuilder().setName(node.getName()).setMCType(type).build();
      }
      ASTCDMethod setMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), builderClasses.peek().getName(), "set"
              + StringTransformations.capitalize(node.getName()), param);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, setMethod, new TemplateHookPoint(
          "methods.builder.set", node)));
      addToClass(builderClasses.peek(), setMethod);
      
      // it is required to check if a setter method exists by checking the methods of the SetterDecorator for
      // an exact match of "set" + attribute.getName()
      // if this method does not exist,
      // we need to reference the attribute directly in the build method
      boolean hasSetterMethod;
      List<ASTCDMethod> methods = decoratorData.getDecoratorData(SetterDecorator.class) != null
          ? decoratorData.getDecoratorData(SetterDecorator.class).methods.get(node) : null;
      if (methods == null || methods.isEmpty() || methods.stream().noneMatch(m -> m.getName()
          .equals("set" + StringTransformations.capitalize(node.getName())))) {
        hasSetterMethod = false;
      }
      else {
        hasSetterMethod = true;
      }
      
      // Add set attributes in the build method
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.build:Inner",
          decoratorBuildMethod.peek(), new TemplateHookPoint("methods.builder.setAttribute", node,
              hasSetterMethod)));
      
      // Add set attributes in the unsafeBuild method
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.builder.unsafeBuild:Inner",
          decoratorUnsafeBuildMethod.peek(), new TemplateHookPoint("methods.builder.setAttribute",
              node, hasSetterMethod)));
    }
  }
  
  @Override
  public void endVisit(ASTCDClass node) {
    if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
      decoratedBuilderClasses.pop();
      decoratorBuildMethod.pop();
      decoratorUnsafeBuildMethod.pop();
      decoratorIsValidMethod.pop();
      builderClasses.pop();
    }
    enabled.pop();
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
