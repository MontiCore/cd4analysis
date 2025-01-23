/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designPattern.*;
import de.monticore.cdlib.refactorings.*;
import de.monticore.cdlib.utilities.TransformationUtility;

import java.io.IOException;
import java.util.Map;


public enum CDTransformationLibType {
  INTRODUCE_OBJECT_ADAPTER_PATTERN((ast, params) -> {
    if (params.containsKey("method")) {
      return new AdapterPattern()
        .introduceObjectAdapterPattern(params.get("adapteeName").asString(),
          params.get("targetName").asString(), params.get("method").asString(), ast);
    } else {
      return new AdapterPattern()
        .introduceObjectAdapterPattern(params.get("adapteeName").asString(),
          params.get("targetName").asString(), ast);
    }
  }),
  INTRODUCE_CLASS_ADAPTER_PATTERN((ast, params) -> {
    if (params.containsKey("method")) {
      return new AdapterPattern().introduceClassAdapterPattern(params.get("adapteeName").asString(),
          params.get("targetName").asString(), params.get("method").asString(), ast);
    } else {
      return new AdapterPattern().introduceClassAdapterPattern(params.get("adapteeName").asString(),
          params.get("targetName").asString(), ast);
    }
  }),
  INTRODUCE_DECORATOR_PATTERN((ast, params) ->
    new DecoratorPattern().introduceDecoratorPattern(params.get("concreteComponent").asString(),
        params.get("componentName").asString(), params.get("method").asString(), ast)),
  INTRODUCE_FACADE_PATTERN((ast, params) -> {
    if (params.containsKey("facadeClassName")) {
      return new FacadePattern().introduceFacadePattern(params.get("facadeClasses").asList(),
          params.get("facadeClassName").asString(), ast);
    } else {
      return new FacadePattern().introduceFacadePattern(params.get("facadeClasses").asList(), ast);
    }
  }),
  INTRODUCE_FACTORY_PATTERN((ast, params) ->
    new FactoryPattern().introduceFactoryPattern(params.get("subclasses").asList(),
        params.get("className").asString(), ast)),
  INTRODUCE_OBSERVER_PATTERN((ast, params) ->
    new ObserverPattern().introduceObserverPattern(params.get("subjectName").asString(),
        params.get("observerName").asString(), params.get("observableName").asString(), ast)),
  INTRODUCE_PROXY_PATTERN((ast, params) -> {
    if (params.containsKey("methods")) {
      return new ProxyPattern().introduceProxyPattern(params.get("className").asString(),
          params.get("methods").asList(), ast);
    } else {
      return new ProxyPattern().introduceProxyPattern(params.get("className").asString(), ast);
    }
  }),
  INTRODUCE_VISITOR_PATTERN((ast, params) -> {
    if (params.containsKey("visitors")) {
      return new VisitorPattern().introduceVisitorPattern(params.get("className").asString(),
          params.get("replacedMethods").asList(), params.get("visitors").asList(), ast);
    } else {
      return new VisitorPattern().introduceVisitorPattern(params.get("className").asString(),
          params.get("replacedMethods").asList(), ast);
    }
  }),
  COLLAPSE_HIERARCHY((ast, params) ->
    new CollapseHierarchy().collapseHierarchy(params.get("className").asString(), ast)),
  COLLAPSE_HIERARCHY_METHOD((ast, params) ->
    new CollapseHierarchy().collapseHierarchyMethod(params.get("className").asString(), ast)),
  COLLAPSE_HIERARCHY_ATTRIBUTE((ast, params) ->
    new CollapseHierarchy().collapseHierarchyAttribute(params.get("className").asString(), ast)),
  DELETE_SUPERCLASS((ast, params) ->
    new CollapseHierarchy().deleteSuperclass(params.get("className").asString(), ast)),
  DELETE_INHERITANCE((ast, params) ->
    new CollapseHierarchy().deleteInheritance(params.get("className").asString(), ast)),
  ENCAPSULATE_ATTRIBUTES((ast, params) -> {
    if (params.containsKey("attributes")) {
      return new EncapsulateAttributes()
          .encapsulateAttributes(params.get("attributes").asList(), ast);
    } else {
      return new EncapsulateAttributes().encapsulateAttributes(ast);
    }
  }),
  EXTRACT_CLASS((ast, params) ->
    new ExtractClass()
      .extractClass(params.get("oldClass").asString(), params.get("newClass").asString(),
        params.get("attributes").asList(), params.get("methods").asList(), ast)),
  EXTRACT_INTERFACE((ast, params) ->
    new ExtractInterface().extractInterface(params.get("interfaceName").asString(),
        params.get("subclasses").asList(), ast)),
  EXTRACT_ALL_INTERMEDIATE_CLASSES((ast, params) ->
    new ExtractIntermediateClass().extractAllIntermediateClasses(ast)),
  EXTRACT_INTERMEDIATE_CLASS((ast, params) ->
    new ExtractIntermediateClass()
        .extractIntermediateClass(params.get("newSuperclassName").asString(),
        params.get("subclasses").asList(), ast)),
  EXTRACT_ALL_INTERMEDIATE_CLASSES_ATTRIBUTE((ast, params) -> {
    if (params.containsKey("className")) {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesAttribute(ast, params.get("className").asString());
    } else {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesAttribute(ast);
    }
  }),
  EXTRACT_ALL_INTERMEDIATE_CLASSES_METHOD((ast, params) -> {
    if (params.containsKey("className")) {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesMethod(ast, params.get("className").asString());
    } else {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesMethod(ast);
    }
  }),
  EXTRACT_SUPER_CLASS((ast, params) ->
    new ExtractSuperClass().extractSuperClass(ast)),
  EXTRACT_SUPER_CLASS_ATTRIBUTE((ast, params) ->
    new ExtractSuperClass().extractSuperClassAttribute(ast)),
  EXTRACT_SUPER_CLASS_METHOD((ast, params) ->
    new ExtractSuperClass().extractSuperClassMethod(ast)),
  EXTRACT_SUPER_CLASS_WITH_NAME((ast, params) ->
    new ExtractSuperClass()
      .extractSuperClassWithName(ast, params.get("className").asString())),
  EXTRACT_SUPER_CLASS_ATTRIBUTE_WITH_NAME((ast, params) ->
    new ExtractSuperClass()
      .extractSuperClassAttributeWithName(ast, params.get("className").asString())),
  EXTRACT_SUPER_CLASS_METHOD_WITH_NAME((ast, params) ->
    new ExtractSuperClass()
      .extractSuperClassMethodWithName(ast, params.get("className").asString())),
  INLINE_CLASS((ast, params) ->
    new InlineClass()
      .inlineClass(params.get("classToRemove").asString(),
        params.get("newClass").asString(), ast)),
  MOVE_METHODS_AND_ATTRIBUTES((ast, params) ->
    new Move()
      .moveMethodsAndAttributes(params.get("sourceClass").asString(),
        params.get("targetClass").asString(), ast)),
  MOVE_METHODS_AND_ATTRIBUTES_TO_NEIGHBOR_CLASS((ast, params) ->
    new Move()
      .moveMethodsAndAttributesToNeighborClass(params.get("sourceClass").asString(),
        params.get("targetClass").asString(), ast)),
  MOVE_ALL_METHODS((ast, params) ->
    new Move()
      .moveAllMethods(params.get("sourceClass").asString(),
        params.get("targetClass").asString(), ast)),
  MOVE_METHODS((ast, params) ->
    new Move()
      .moveMethods(params.get("sourceClass").asString(), params.get("targetClass").asString(),
        params.get("methodsToMove").asList(), ast)),
  MOVE_METHODS_TO_NEIGHBOR_CLASS((ast, params) ->
    new Move()
      .moveMethodsToNeighborClass(params.get("sourceClass").asString(),
        params.get("targetClass").asString(), params.get("methodsToMove").asList(), ast)),
  MOVE_ALL_ATTRIBUTES((ast, params) ->
    new Move()
      .moveAllAttributes(params.get("sourceClass").asString(),
        params.get("targetClass").asString(), ast)),
  MOVE_ATTRIBUTES((ast, params) ->
    new Move()
      .moveAttributes(params.get("sourceClass").asString(), params.get("targetClass").asString(),
        params.get("attributesToMove").asList(), ast)),
  MOVE_ATTRIBUTES_TO_NEIGHBOR_CLASS((ast, params) ->
    new Move()
      .moveAttributesToNeighborClass(params.get("sourceClass").asString(),
        params.get("targetClass").asString(), params.get("attributesToMove").asList(), ast)),
  PULL_UP((ast, params) ->
    new PullUp().pullUp(ast)),
  PULL_UP_ATTRIBUTES((ast, params) ->
    new PullUp().pullUpAttributes(ast)),
  PULL_UP_METHODS((ast, params) ->
    new PullUp().pullUpMethods(ast)),
  PULL_UP_ASSOCIATIONS((ast, params) ->
    new PullUp().pullUpAssociations(ast)),
  PUSH_DOWN((ast, params) ->
    new PushDown().pushDown(params.get("superClassName").asString(), ast)),
  PUSH_DOWN_ALL_ATTRIBUTES((ast, params) ->
    new PushDown().pushDownAllAttributes(params.get("superClassName").asString(), ast)),
  PUSH_DOWN_ATTRIBUTES((ast, params) -> {
    if (params.containsKey("subClasses")) {
      return new PushDown().pushDownAttributes(params.get("superClassName").asString(),
          params.get("subClasses").asList(), params.get("attributes").asList(), ast);
    } else {
      return new PushDown().pushDownAttributes(params.get("superClassName").asString(),
          params.get("attributes").asList(), ast);
    }
  }),
  PUSH_DOWN_ALL_METHODS((ast, params) ->
    new PushDown().pushDownAllMethods(params.get("superClassName").asString(), ast)),
  PUSH_DOWN_METHODS((ast, params) -> {
    if (params.containsKey("subClasses")) {
      return new PushDown().pushDownMethods(params.get("superClassName").asString(),
          params.get("subClasses").asList(), params.get("methods").asList(), ast);
    } else {
      return new PushDown().pushDownMethods(params.get("superClassName").asString(),
          params.get("methods").asList(), ast);
    }
  }),
  REMOVE_CLASS((ast, params) ->
    new Remove().removeClass(params.get("className").asString(), ast)),
  REMOVE_METHOD((ast, params) ->
    new Remove()
      .removeMethod(params.get("className").asString(), params.get("methodName").asString(), ast)),
  REMOVE_ATTRIBUTE((ast, params) ->
    new Remove()
      .removeAttribute(params.get("className").asString(), params.get("methodName").asString(),
          ast)),
  RENAME_CLASS((ast, params) ->
    new Rename()
      .renameClass(params.get("oldName").asString(), params.get("newName").asString(), ast)),
  RENAME_ATTRIBUTE((ast, params) ->
    new Rename()
      .renameAttribute(params.get("oldName").asString(), params.get("newName").asString(), ast)),
  REPLACE_ASSOCIATION_BY_ATTRIBUTE((ast, params) ->
    new ReplaceDelegationByAttribute()
        .replaceAssociationByAttribute(params.get("className").asString(),
        params.get("classToAttribute").asString(), ast)),
  REPLACE_INHERITANCE_BY_DELEGATION((ast, params) ->
    new SwitchInheritanceDelegation()
        .replaceInheritanceByDelegation(params.get("superClassName").asString(),
        params.get("subclassName").asString(), ast)),
  REPLACE_DELEGATION_BY_INHERITANCE((ast, params) ->
    new SwitchInheritanceDelegation()
        .replaceDelegationByInheritance(params.get("superClassName").asString(),
        params.get("subclassName").asString(), ast)),
  CREATE_RIGHT_ASSOCIATION((ast, params) ->
    new TransformationUtility()
        .createRightDirAssociation(params.get("leftReferenceName").asString(),
        params.get("rightReferenceName").asString(), ast)),
  CREATE_BI_ASSOCIATION((ast, params) ->
    new TransformationUtility().createBiDirAssociations(params.get("leftClass").asString(),
        params.get("rightClasses").asList(), ast)),
  DELETE_ALL_ASSOCIATIONS((ast, params) ->
    new TransformationUtility().deleteAllAssociations(params.get("className").asString(), ast)),
  CREATE_CLASS((ast, params) ->
    new TransformationUtility().createSimpleClass(params.get("className").asString(), ast)),
  CREATE_INTERFACE((ast, params) ->
    new TransformationUtility().createInterface(params.get("interfaceName").asString(), ast)),
  CREATE_INHERITANCE_TO_INTERFACE((ast, params) ->
    new TransformationUtility().createInheritanceToInterface(params.get("subclass").asString(),
        params.get("interfaceName").asString(), ast)),
  ADD_INHERITANCE_TO_INTERFACE((ast, params) ->
    new TransformationUtility().addInheritanceToInterface(params.get("className").asString(),
        params.get("newInterface").asString(), ast)),
  CREATE_INHERITANCE_TO_CLASS((ast, params) ->
    new TransformationUtility().createInheritanceToClass(params.get("subclass").asString(),
        params.get("superclass").asString(), ast)),
  CHANGE_INHERITANCE_CLASS((ast, params) ->
    new TransformationUtility().changeInheritanceClass(params.get("oldSuperclass").asString(),
        params.get("newSuperclass").asString(), ast));

  private final CDTransformationCaller callback;

  CDTransformationLibType(CDTransformationCaller trafoCallback) {
    this.callback = trafoCallback;
  }

  public boolean apply(ASTCDCompilationUnit ast,
      Map<String, CDTransformationParameter> params) throws IOException {
    return this.callback.apply(ast, params);
  }
}
