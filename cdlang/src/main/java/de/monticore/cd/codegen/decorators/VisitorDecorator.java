/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.CDTypeCollector;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * When visit(node) we add the visitedElements into a set and remove them after the endVisit again
 * to account
 * for circular relations which would otherwise not terminate.
 */
public class VisitorDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements
    CDBasisVisitor2 {
  
  Stack<ASTCDParameter> parameterOfPojo = new Stack<>();
  Stack<ASTCDClass> currentDecoratedClass = new Stack<>();
  ASTCDInterface visitorInterface;
  ASTCDParameter visitorInterfaceParameter;
  Stack<ASTCDMethod> currentTraverseMethod = new Stack<>();
  
  /**
   * a collection of all classes from the class diagram as strings
   */
  List<String> classesFromClassdiagramAsString = new ArrayList<>();
  boolean isInitialized = false;
  
  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    //We check that the SetterDecorator has added a Setter for an attribute,
    // thus the Setter decorator has to run before.
    return Iterables.concat(super.getMustRunAfter(), Collections.singletonList(
        SetterDecorator.class));
  }
  
  @Override
  public void visit(ASTCDCompilationUnit compilationUnit) {
    init(compilationUnit, compilationUnit.getCDDefinition(), "I" + compilationUnit.getCDDefinition()
        .getName() + "Visitor");
  }
  
  public void init(ASTCDCompilationUnit compilationUnit, ASTCDDefinition definition,
      String visitorInterfaceName) {
    if (!isInitialized) {
      isInitialized = true;
      //create the visitor interface
      visitorInterface = CD4CodeMill.cDInterfaceBuilder().setName(visitorInterfaceName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
      
      // add the visitor interface to the definition
      ASTCDDefinition decoratedDefinition = this.decoratorData.getAsDecorated(definition);
      decoratedDefinition.addCDElement(visitorInterface);
      
      // create the visitor interface parameter
      String packageName = definition.getSymbol().getPackageName();
      String visitorInterfaceQualifiedName = packageName.isEmpty() ? visitorInterfaceName
          : packageName + "." + visitorInterfaceName;
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceQualifiedName);
      visitorInterfaceParameter = CD4CodeMill.cDParameterBuilder().setName("visitor").setMCType(
          visitorInterfaceQualifiedType).build();
      
      // add getTraversedElements Set<Object> method to the visitor interface
      ASTMCSetType setType = MCTypeFacade.getInstance().createSetTypeOf("Object");
      ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(setType).build();
      ASTCDMethod getTraversedElementsMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().setAbstract(true).build(), returnType, "getTraversedElements");
      visitorInterface.addCDMember(getTraversedElementsMethod);
      
      // add addTraversedElement method to the visitor interface
      ASTMCReturnType returnTypeAddTraversedElement = CD4CodeMill.mCReturnTypeBuilder()
          .setMCVoidType(CD4CodeMill.mCVoidTypeBuilder().build()).build();
      ASTCDParameter addTraversedElementParameter = CD4CodeMill.cDParameterBuilder().setName(
          "element").setMCType(MCTypeFacade.getInstance().createQualifiedType("Object")).build();
      ASTCDMethod addTraversedElementMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().build(), returnTypeAddTraversedElement, "addTraversedElement",
          addTraversedElementParameter);
      visitorInterface.addCDMember(addTraversedElementMethod);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, addTraversedElementMethod,
          new TemplateHookPoint("methods.visitor.addTraversedElement")));
      
      // add removeTraversedElement method to the visitor interface
      ASTMCReturnType returnTypeRemoveTraversedElement = CD4CodeMill.mCReturnTypeBuilder()
          .setMCVoidType(CD4CodeMill.mCVoidTypeBuilder().build()).build();
      ASTCDParameter removeTraversedElementParameter = CD4CodeMill.cDParameterBuilder().setName(
          "element").setMCType(MCTypeFacade.getInstance().createQualifiedType("Object")).build();
      ASTCDMethod removeTraversedElement = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().build(), returnTypeAddTraversedElement, "removeTraversedElement",
          addTraversedElementParameter);
      visitorInterface.addCDMember(removeTraversedElement);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, removeTraversedElement,
          new TemplateHookPoint("methods.visitor.removeTraversedElement")));
      
      //visitor to get all classes from the original class diagram classes
      CDTypeCollector cdTypeCollector = new CDTypeCollector();
      CD4CodeTraverser t2 = CD4CodeMill.inheritanceTraverser();
      t2.add4CDBasis(cdTypeCollector);
      compilationUnit.accept(t2);
      
      classesFromClassdiagramAsString.addAll(cdTypeCollector.getClasses().stream().map(e -> e
          .getSymbol().getFullName()).collect(Collectors.toList()));
      classesFromClassdiagramAsString.addAll(cdTypeCollector.getInterfaces().stream().map(e -> e
          .getSymbol().getFullName()).collect(Collectors.toList()));
      classesFromClassdiagramAsString.addAll(cdTypeCollector.getEnums().stream().map(e -> e
          .getSymbol().getFullName()).collect(Collectors.toList()));
    }
  }
  
  @Override
  public void visit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      ASTCDClass decClazz = decoratorData.getAsDecorated(clazz);
      currentDecoratedClass.add(decClazz);
      
      String packageName = clazz.getSymbol().getPackageName();
      
      String visitorInterfaceName = packageName.isEmpty() ? "I" + clazz.getName() + "Visitor"
          : packageName + ".I" + clazz.getName() + "Visitor";
      String pojoClassName = packageName.isEmpty() ? clazz.getName() : packageName + "." + clazz
          .getName();
      ASTMCQualifiedType pojoClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
          pojoClassName);
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceName);
      ASTCDParameter pojoClassParameter = CD4CodeMill.cDParameterBuilder().setName("node")
          .setMCType(pojoClassQualifiedType).build();
      ASTCDParameter pojoInterfaceClassParameter = CD4CodeMill.cDParameterBuilder().setName("node")
          .setMCType(visitorInterfaceQualifiedType).build();
      parameterOfPojo.add(pojoClassParameter);
      
      //create the methods for the visitor interface
      //visit:
      ASTCDMethod visitMethodHeader = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "visit", parameterOfPojo.peek());
      visitorInterface.addCDMember(visitMethodHeader);
      // endVisit:
      ASTCDMethod endVisitMethodHeader = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "endVisit", parameterOfPojo.peek());
      visitorInterface.addCDMember(endVisitMethodHeader);
      // handle:
      ASTCDMethod handleMethodHeader = CD4CodeMill.cDMethodBuilder().setModifier(CD4CodeMill
          .modifierBuilder().PUBLIC().setAbstract(false).build()).setName("handle").setMCReturnType(
              CD4CodeMill.mCReturnTypeBuilder().setMCVoidType(CD4CodeMill.mCVoidTypeBuilder()
                  .build()).build()).setCDParametersList(List.of(parameterOfPojo.peek())).build();
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, handleMethodHeader,
          new TemplateHookPoint("methods.visitor.handle", pojoClassParameter,
              pojoInterfaceClassParameter)));
      
      visitorInterface.addCDMember(handleMethodHeader);
      
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, handleMethodHeader,
          new TemplateHookPoint("methods.visitor.handle")));
      // traverse:
      ASTCDMethod traverseMethodHeader = CDMethodFacade.getInstance().createMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "traverse", parameterOfPojo.peek());
      visitorInterface.addCDMember(traverseMethodHeader);
      currentTraverseMethod.add(traverseMethodHeader);
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, traverseMethodHeader,
          new TemplateHookPoint("methods.visitor.traverse", classesFromClassdiagramAsString)));
      
      // add accept method to pojo class
      ASTCDMethod acceptMethod = CDMethodFacade.getInstance().createDefaultMethod(CD4CodeMill
          .modifierBuilder().PUBLIC().build(), "accept", visitorInterfaceParameter);
      decClazz.addCDMember(acceptMethod);
      
      String errorCode = "0x01472";
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, acceptMethod,
          new TemplateHookPoint("methods.visitor.accept", clazz, errorCode)));
    }
  }
  
  @Override
  public void endVisit(ASTCDClass clazz) {
    if (decoratorData.shouldDecorate(this.getClass(), clazz)) {
      parameterOfPojo.pop();
      currentDecoratedClass.pop();
      currentTraverseMethod.pop();
    }
  }
  
  @Override
  public void visit(ASTCDAttribute attribute) {
    if (!decoratorData.shouldDecorate(this.getClass(), attribute)) {
      return;
    }
    // it is required to check if a setter method exists by checking the methods of the SetterDecorator for
    // an exact match of "set" + attribute.getName()
    // if this method does not exist,
    // we need to reference the attribute directly in the build method
    String attributeName;
    List<ASTCDMethod> methods = decoratorData.getDecoratorData(SetterDecorator.class) != null
        ? decoratorData.getDecoratorData(SetterDecorator.class).methods.get(attribute) : null;
    if (methods == null || methods.isEmpty() || methods.stream().noneMatch(m -> m.getName().equals(
        "set" + StringTransformations.capitalize(attribute.getName())))) {
      attributeName = "node." + attribute.getName();
    }
    else {
      attributeName = "node.get" + attribute.getName().substring(0, 1).toUpperCase() + attribute
          .getName().substring(1) + "()";
    }
    
    glexOpt.ifPresent(glex -> glex.addAfterTemplate("methods.visitor.traverse:Inner",
        currentTraverseMethod.peek(), new TemplateHookPoint("methods.visitor.traverseInner",
            classesFromClassdiagramAsString, attribute.getMCType(), attributeName)));
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
