/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTCNode;
import de.monticore.cd._symboltable.CDSymbolTables;
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
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * This decorator generates a visitor interface for each class and interface in the class diagram.
 * The visitor interface contains methods to visit, endVisit, handle, traverse,
 * All classes and interfaces contain an accept method that accepts the visitor interface as a
 * parameter.
 * <p>
 * The visitor interface is used to traverse over classes in the class diagram.
 * When a class inherits from another class or implements an interface, these relations are visited
 * before the
 * class itself is visited. This is done in a deep-first manner.
 * The endVisit method is called after all super classes and interfaces as well as the node have
 * been visited and handled.
 * The endVisit methods are then called in reverse order of the visit methods.
 */
public class InheritanceVisitorDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {
  
  Stack<ASTCDParameter> parameterOfPojo = new Stack<>();
  Stack<ASTCDClass> currentDecoratedClass = new Stack<>();
  Stack<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> currentDecoratedInterface =
      new Stack<>();
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
        .getName() + "InheritanceVisitor");
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
      CD4CodeTraverser t2 = CD4CodeMill.inheritanceTraverser();
      CDTypeCollector cdTypeCollector = new CDTypeCollector();
      t2.add4CDBasis(cdTypeCollector);
      t2.add4CDInterfaceAndEnum(cdTypeCollector);
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
      List<String> upperInterfacesAndSuperClasses = getUpperInterfacesAndSuperClasses(clazz);
      ASTCDMethod handleMethodHeader = CD4CodeMill.cDMethodBuilder().setModifier(CD4CodeMill
          .modifierBuilder().PUBLIC().setAbstract(false).build()).setName("handle").setMCReturnType(
              CD4CodeMill.mCReturnTypeBuilder().setMCVoidType(CD4CodeMill.mCVoidTypeBuilder()
                  .build()).build()).setCDParametersList(List.of(parameterOfPojo.peek())).build();
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, handleMethodHeader,
          new TemplateHookPoint("methods.visitor.inheritanceHandle",
              upperInterfacesAndSuperClasses)));
      visitorInterface.addCDMember(handleMethodHeader);
      
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
  public void visit(de.monticore.cdinterfaceandenum._ast.ASTCDInterface node) {
    if (decoratorData.shouldDecorate(this.getClass(), node)) {
      String packageName = node.getSymbol().getPackageName();
      String visitorInterfaceName = packageName.isEmpty() ? "I" + node.getName() + "Visitor"
          : packageName + ".I" + node.getName() + "Visitor";
      String pojoInterfaceName = packageName.isEmpty() ? node.getName() : packageName + "." + node
          .getName();
      ASTMCQualifiedType visitorInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(visitorInterfaceName);
      ASTMCQualifiedType pojoInterfaceQualifiedType = MCTypeFacade.getInstance()
          .createQualifiedType(pojoInterfaceName);
      ASTCDParameter pojoInterfaceParameter = CD4CodeMill.cDParameterBuilder().setName("node")
          .setMCType(pojoInterfaceQualifiedType).build();
      ASTCDParameter pojoInterfaceClassParameter = CD4CodeMill.cDParameterBuilder().setName("node")
          .setMCType(visitorInterfaceQualifiedType).build();
      de.monticore.cdinterfaceandenum._ast.ASTCDInterface decInterface = decoratorData
          .getAsDecorated(node);
      currentDecoratedInterface.add(decInterface);
      parameterOfPojo.add(pojoInterfaceParameter);
      
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
      List<String> upperInterfacesAndSuperClasses = getUpperInterfacesAndSuperClasses(node);
      ASTCDMethod handleMethodHeader = CD4CodeMill.cDMethodBuilder().setModifier(CD4CodeMill
          .modifierBuilder().PUBLIC().setAbstract(false).build()).setName("handle").setMCReturnType(
              CD4CodeMill.mCReturnTypeBuilder().setMCVoidType(CD4CodeMill.mCVoidTypeBuilder()
                  .build()).build()).setCDParametersList(List.of(parameterOfPojo.peek())).build();
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, handleMethodHeader,
          new TemplateHookPoint("methods.visitor.inheritanceHandle",
              upperInterfacesAndSuperClasses)));
      visitorInterface.addCDMember(handleMethodHeader);
      
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
      decInterface.addCDMember(acceptMethod);
      
      String errorCode = "0x01472";
      glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, acceptMethod,
          new TemplateHookPoint("methods.visitor.accept", node, errorCode)));
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
  public void endVisit(de.monticore.cdinterfaceandenum._ast.ASTCDInterface node) {
    if (decoratorData.shouldDecorate(this.getClass(), node)) {
      parameterOfPojo.pop();
      currentDecoratedInterface.pop();
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
  
  /**
   * This method resolves the super classes and interfaces of a class and returns all in a somewhat
   * expected order
   * <p>
   * The order is as follows:
   * If the class has a single superclass or interface, the order is ascending from the
   * superclass/interface to the class itself.
   * If the class has multiple superclasses or interfaces, the order is unpredictable
   *
   * @param node class that should be inspected for super classes and interfaces
   * @return an ordered list of super classes and interfaces
   */
  private List<String> getUpperInterfacesAndSuperClasses(ASTCNode node) {
    List<String> result = new ArrayList<>();
    List<ASTCNode> allVisited = new ArrayList<>();
    allVisited.add(node);
    List<ASTCNode> lastRoundVisited = new ArrayList<>();
    lastRoundVisited.add(node);
    List<ASTCNode> nextRoundVisited = new ArrayList<>();
    Set<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> visitedInterfaces = new HashSet<>();
    while (!lastRoundVisited.isEmpty()) {
      for (ASTCNode currentNode : lastRoundVisited) {
        if (currentNode instanceof ASTCDClass) {
          //super class
          Optional<ASTCDClass> resultOfTransitiveClass = (CDSymbolTables.getTransitiveSuperClasses(
              (ASTCDClass) currentNode).stream().findFirst());
          if (resultOfTransitiveClass.isPresent()) {
            if (!allVisited.contains(resultOfTransitiveClass.get())) {
              allVisited.add(resultOfTransitiveClass.get());
              result.add(resultOfTransitiveClass.get().getSymbol().getFullName());
              nextRoundVisited.add(resultOfTransitiveClass.get());
            }
          }
          
          //interfaces
          List<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> resultOfTransitiveInterface =
              getASTCDInterfaces((ASTCDClass) currentNode, visitedInterfaces);
          for (de.monticore.cdinterfaceandenum._ast.ASTCDInterface resultOfTransitiveInterfaceElement : resultOfTransitiveInterface) {
            allVisited.add(resultOfTransitiveInterfaceElement);
            result.add(resultOfTransitiveInterfaceElement.getSymbol().getFullName());
            nextRoundVisited.add(resultOfTransitiveInterfaceElement);
          }
        }
        else if (currentNode instanceof ASTCDInterface) {
          //interfaces
          List<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> resultOfTransitiveInterface =
              getASTCDInterfaces((ASTCDInterface) currentNode, visitedInterfaces);
          for (de.monticore.cdinterfaceandenum._ast.ASTCDInterface resultOfTransitiveInterfaceElement : resultOfTransitiveInterface) {
            allVisited.add(resultOfTransitiveInterfaceElement);
            result.add(resultOfTransitiveInterfaceElement.getSymbol().getFullName());
            nextRoundVisited.add(resultOfTransitiveInterfaceElement);
          }
        }
      }
      lastRoundVisited.clear();
      lastRoundVisited.addAll(nextRoundVisited);
      nextRoundVisited.clear();
    }
    return result;
  }
  
  private static List<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> getASTCDInterfaces(
      ASTCDType currentNode,
      Set<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> visitedInterfaces) {
    //direct interfaces
    List<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> resultOfTransitiveInterface =
        (new ArrayList<>(CDSymbolTables.getTransitiveSuperInterfaces(currentNode)));
    //filter out all interfaces that do not match the direct interface list of the class
    List<String> directInterfaces = currentNode.getInterfaceList().stream().map(
        m -> ((ASTMCQualifiedType) m).getMCQualifiedName().getQName()).collect(Collectors.toList());
    List<de.monticore.cdinterfaceandenum._ast.ASTCDInterface> helper = new ArrayList<>();
    for (de.monticore.cdinterfaceandenum._ast.ASTCDInterface directInterface : resultOfTransitiveInterface) {
      for (String directName : directInterfaces) {
        if (directInterface.getSymbol().getFullName().contains(directName)) { //we need to use contains here because the interfaceLists is not resolved
          helper.add(directInterface);
        }
      }
    }
    resultOfTransitiveInterface = helper;
    //filter out all interfaces that have already been visited
    resultOfTransitiveInterface = resultOfTransitiveInterface.stream().filter(
        m -> !visitedInterfaces.contains(m)).collect(Collectors.toList());
    visitedInterfaces.addAll(resultOfTransitiveInterface);
    return resultOfTransitiveInterface;
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
    traverser.add4CDInterfaceAndEnum(this);
  }
  
}
