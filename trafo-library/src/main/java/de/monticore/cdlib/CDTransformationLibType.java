/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designPattern.*;
import de.monticore.cdlib.refactorings.*;
import de.monticore.cdlib.utilities.TransformationUtility;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public enum CDTransformationLibType {
  INTRODUCE_OBJECT_ADAPTER_PATTERN((ast, params) -> {
    if (params.containsKey("method")) {
      return new AdapterPattern()
        .introduceObjectAdapterPattern(getStringParam(params, "adapteeName"),
          getStringParam(params, "targetName"), getStringParam(params, "method"), ast);
    } else {
      return new AdapterPattern()
        .introduceObjectAdapterPattern(getStringParam(params, "adapteeName"),
            getStringParam(params, "targetName"), ast);
    }
  }),
  INTRODUCE_CLASS_ADAPTER_PATTERN((ast, params) -> {
    if (params.containsKey("method")) {
      return new AdapterPattern()
          .introduceClassAdapterPattern(getStringParam(params, "adapteeName"),
          getStringParam(params, "targetName"), getStringParam(params, "method"), ast);
    } else {
      return new AdapterPattern()
          .introduceClassAdapterPattern(getStringParam(params, "adapteeName"),
          getStringParam(params, "targetName"), ast);
    }
  }),
  INTRODUCE_DECORATOR_PATTERN((ast, params) ->
    new DecoratorPattern().introduceDecoratorPattern(getStringParam(params, "concreteComponent"),
        getStringParam(params, "componentName"), getStringParam(params, "method"), ast)),
  INTRODUCE_FACADE_PATTERN((ast, params) -> {
    if (params.containsKey("facadeClassName")) {
      return new FacadePattern().introduceFacadePattern(getListParam(params, "facadeClasses"),
          getStringParam(params, "facadeClassName"), ast);
    } else {
      return new FacadePattern()
          .introduceFacadePattern(getListParam(params, "facadeClasses"), ast);
    }
  }),
  INTRODUCE_FACTORY_PATTERN((ast, params) ->
    new FactoryPattern().introduceFactoryPattern(getListParam(params, "subclasses"),
        getStringParam(params, "className"), ast)),
  INTRODUCE_OBSERVER_PATTERN((ast, params) ->
    new ObserverPattern().introduceObserverPattern(getStringParam(params, "subjectName"),
        getStringParam(params, "observerName"), getStringParam(params, "observableName"), ast)),
  INTRODUCE_PROXY_PATTERN((ast, params) -> {
    if (params.containsKey("methods")) {
      return new ProxyPattern().introduceProxyPattern(getStringParam(params, "className"),
          getListParam(params, "methods"), ast);
    } else {
      return new ProxyPattern().introduceProxyPattern(getStringParam(params, "className"), ast);
    }
  }),
  INTRODUCE_VISITOR_PATTERN((ast, params) -> {
    if (params.containsKey("visitors")) {
      return new VisitorPattern().introduceVisitorPattern(getStringParam(params, "className"),
          getListParam(params, "replacedMethods"), getListParam(params, "visitors"), ast);
    } else {
      return new VisitorPattern().introduceVisitorPattern(getStringParam(params, "className"),
          getListParam(params, "replacedMethods"), ast);
    }
  }),
  COLLAPSE_HIERARCHY((ast, params) ->
    new CollapseHierarchy().collapseHierarchy(getStringParam(params, "className"), ast)),
  COLLAPSE_HIERARCHY_METHOD((ast, params) ->
    new CollapseHierarchy().collapseHierarchyMethod(getStringParam(params, "className"), ast)),
  COLLAPSE_HIERARCHY_ATTRIBUTE((ast, params) ->
    new CollapseHierarchy().collapseHierarchyAttribute(getStringParam(params, "className"), ast)),
  DELETE_SUPERCLASS((ast, params) ->
    new CollapseHierarchy().deleteSuperclass(getStringParam(params, "className"), ast)),
  DELETE_INHERITANCE((ast, params) ->
    new CollapseHierarchy().deleteInheritance(getStringParam(params, "className"), ast)),
  ENCAPSULATE_ATTRIBUTES((ast, params) -> {
    if (params.containsKey("attributes")) {
      return new EncapsulateAttributes()
          .encapsulateAttributes(getListParam(params, "attributes"), ast);
    } else {
      return new EncapsulateAttributes().encapsulateAttributes(ast);
    }
  }),
  EXTRACT_CLASS((ast, params) ->
    new ExtractClass()
      .extractClass(getStringParam(params, "oldClass"), getStringParam(params, "newClass"),
          getListParam(params, "attributes"), getListParam(params, "methods"), ast)),
  EXTRACT_INTERFACE((ast, params) ->
    new ExtractInterface().extractInterface(getStringParam(params, "interfaceName"),
        getListParam(params, "subclasses"), ast)),
  EXTRACT_ALL_INTERMEDIATE_CLASSES((ast, params) ->
    new ExtractIntermediateClass().extractAllIntermediateClasses(ast)),
  EXTRACT_INTERMEDIATE_CLASS((ast, params) ->
    new ExtractIntermediateClass()
        .extractIntermediateClass(getStringParam(params, "newSuperclassName"),
            getListParam(params, "subclasses"), ast)),
  EXTRACT_ALL_INTERMEDIATE_CLASSES_ATTRIBUTE((ast, params) -> {
    if (params.containsKey("className")) {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesAttribute(ast, getStringParam(params, "className"));
    } else {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesAttribute(ast);
    }
  }),
  EXTRACT_ALL_INTERMEDIATE_CLASSES_METHOD((ast, params) -> {
    if (params.containsKey("className")) {
      return new ExtractIntermediateClassArbitraryNumber()
        .extractAllIntermediateClassesMethod(ast, getStringParam(params, "className"));
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
      .extractSuperClassWithName(ast, getStringParam(params, "className"))),
  EXTRACT_SUPER_CLASS_ATTRIBUTE_WITH_NAME((ast, params) ->
    new ExtractSuperClass()
      .extractSuperClassAttributeWithName(ast, getStringParam(params, "className"))),
  EXTRACT_SUPER_CLASS_METHOD_WITH_NAME((ast, params) ->
    new ExtractSuperClass()
      .extractSuperClassMethodWithName(ast, getStringParam(params, "className"))),
  INLINE_CLASS((ast, params) ->
    new InlineClass()
      .inlineClass(getStringParam(params, "classToRemove"),
          getStringParam(params, "newClass"), ast)),
  MOVE_METHODS_AND_ATTRIBUTES((ast, params) ->
    new Move()
      .moveMethodsAndAttributes(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), ast)),
  MOVE_METHODS_AND_ATTRIBUTES_TO_NEIGHBOR_CLASS((ast, params) ->
    new Move()
      .moveMethodsAndAttributesToNeighborClass(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), ast)),
  MOVE_ALL_METHODS((ast, params) ->
    new Move()
      .moveAllMethods(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), ast)),
  MOVE_METHODS((ast, params) ->
    new Move()
      .moveMethods(getStringParam(params, "sourceClass"), getStringParam(params, "targetClass"),
          getListParam(params, "methodsToMove"), ast)),
  MOVE_METHODS_TO_NEIGHBOR_CLASS((ast, params) ->
    new Move()
      .moveMethodsToNeighborClass(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), getListParam(params, "methodsToMove"), ast)),
  MOVE_ALL_ATTRIBUTES((ast, params) ->
    new Move()
      .moveAllAttributes(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), ast)),
  MOVE_ATTRIBUTES((ast, params) ->
    new Move()
      .moveAttributes(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), getListParam(params, "attributesToMove"), ast)),
  MOVE_ATTRIBUTES_TO_NEIGHBOR_CLASS((ast, params) ->
    new Move()
      .moveAttributesToNeighborClass(getStringParam(params, "sourceClass"),
          getStringParam(params, "targetClass"), getListParam(params, "attributesToMove"), ast)),
  PULL_UP((ast, params) ->
    new PullUp().pullUp(ast)),
  PULL_UP_ATTRIBUTES((ast, params) ->
    new PullUp().pullUpAttributes(ast)),
  PULL_UP_METHODS((ast, params) ->
    new PullUp().pullUpMethods(ast)),
  PULL_UP_ASSOCIATIONS((ast, params) ->
    new PullUp().pullUpAssociations(ast)),
  PUSH_DOWN((ast, params) ->
    new PushDown().pushDown(getStringParam(params, "superClassName"), ast)),
  PUSH_DOWN_ALL_ATTRIBUTES((ast, params) ->
    new PushDown().pushDownAllAttributes(getStringParam(params, "superClassName"), ast)),
  PUSH_DOWN_ATTRIBUTES((ast, params) -> {
    if (params.containsKey("subClasses")) {
      return new PushDown().pushDownAttributes(getStringParam(params, "superClassName"),
          getListParam(params, "subClasses"), getListParam(params, "attributes"), ast);
    } else {
      return new PushDown().pushDownAttributes(getStringParam(params, "superClassName"),
          getListParam(params, "attributes"), ast);
    }
  }),
  PUSH_DOWN_ALL_METHODS((ast, params) ->
    new PushDown().pushDownAllMethods(getStringParam(params, "superClassName"), ast)),
  PUSH_DOWN_METHODS((ast, params) -> {
    if (params.containsKey("subClasses")) {
      return new PushDown().pushDownMethods(getStringParam(params, "superClassName"),
          getListParam(params, "subClasses"), getListParam(params, "methods"), ast);
    } else {
      return new PushDown().pushDownMethods(getStringParam(params, "superClassName"),
          getListParam(params, "methods"), ast);
    }
  }),
  REMOVE_CLASS((ast, params) ->
    new Remove().removeClass(getStringParam(params, "className"), ast)),
  REMOVE_METHOD((ast, params) ->
    new Remove()
      .removeMethod(getStringParam(params, "className"),
          getStringParam(params, "methodName"), ast)),
  REMOVE_ATTRIBUTE((ast, params) ->
    new Remove()
      .removeAttribute(getStringParam(params, "className"), getStringParam(params, "methodName"),
          ast)),
  RENAME_CLASS((ast, params) ->
    new Rename()
      .renameClass(getStringParam(params, "oldName"), getStringParam(params, "newName"), ast)),
  RENAME_ATTRIBUTE((ast, params) ->
    new Rename()
      .renameAttribute(getStringParam(params, "oldName"), getStringParam(params, "newName"), ast)),
  REPLACE_ASSOCIATION_BY_ATTRIBUTE((ast, params) ->
    new ReplaceDelegationByAttribute()
        .replaceAssociationByAttribute(getStringParam(params, "className"),
            getStringParam(params, "classToAttribute"), ast)),
  REPLACE_INHERITANCE_BY_DELEGATION((ast, params) ->
    new SwitchInheritanceDelegation()
        .replaceInheritanceByDelegation(getStringParam(params, "superClassName"),
            getStringParam(params, "subclassName"), ast)),
  REPLACE_DELEGATION_BY_INHERITANCE((ast, params) ->
    new SwitchInheritanceDelegation()
        .replaceDelegationByInheritance(getStringParam(params, "superClassName"),
            getStringParam(params, "subclassName"), ast)),
  CREATE_RIGHT_ASSOCIATION((ast, params) ->
    new TransformationUtility()
        .createRightDirAssociation(getStringParam(params, "leftReferenceName"),
            getStringParam(params, "rightReferenceName"), ast)),
  CREATE_BI_ASSOCIATION((ast, params) ->
    new TransformationUtility().createBiDirAssociations(getStringParam(params, "leftClass"),
        getListParam(params, "rightClasses"), ast)),
  DELETE_ALL_ASSOCIATIONS((ast, params) ->
    new TransformationUtility().deleteAllAssociations(getStringParam(params, "className"), ast)),
  CREATE_CLASS((ast, params) ->
    new TransformationUtility().createSimpleClass(getStringParam(params, "className"), ast)),
  CREATE_INTERFACE((ast, params) ->
    new TransformationUtility().createInterface(getStringParam(params, "interfaceName"), ast)),
  CREATE_INHERITANCE_TO_INTERFACE((ast, params) ->
    new TransformationUtility().createInheritanceToInterface(getStringParam(params, "subclass"),
        getStringParam(params, "interfaceName"), ast)),
  ADD_INHERITANCE_TO_INTERFACE((ast, params) ->
    new TransformationUtility().addInheritanceToInterface(getStringParam(params, "className"),
        getStringParam(params, "newInterface"), ast)),
  CREATE_INHERITANCE_TO_CLASS((ast, params) ->
    new TransformationUtility().createInheritanceToClass(getStringParam(params, "subclass"),
        getStringParam(params, "superclass"), ast)),
  CHANGE_INHERITANCE_CLASS((ast, params) ->
    new TransformationUtility().changeInheritanceClass(getStringParam(params, "oldSuperclass"),
        getStringParam(params, "newSuperclass"), ast));

  private final CDTransformationCaller callback;

  CDTransformationLibType(CDTransformationCaller trafoCallback) {
    this.callback = trafoCallback;
  }

  public boolean apply(ASTCDCompilationUnit ast,
      Map<String, CDTransformationParameter> params) throws IOException {
    return this.callback.apply(ast, params);
  }
  
  public static String getStringParam(Map<String,CDTransformationParameter> params, String name) {
    CDTransformationParameter param = params.get(name);
    if (param == null) {
      Log.error("0x4A524: Couldn't find required transformation parameter: " + name);
      return "";
    }
    return param.asString();
  }
  
  public static List<String> getListParam(Map<String,CDTransformationParameter> params, String name) {
    CDTransformationParameter param = params.get(name);
    if (param == null) {
      Log.error("0x4A524: Couldn't find required transformation parameter: " + name);
      return Collections.emptyList();
    }
    return param.asList();
  }
}
