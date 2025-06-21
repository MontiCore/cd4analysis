/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.CDTypeCollector;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import java.util.*;
import java.util.stream.Collectors;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * Decorator that adds deepClone and deepEquals methods to artifacts specified in the class
 * diagram.<br>
 * The deepClone method is actually two methods: <br>
 * 1. deepClone(): <br>
 * Creates a new instance of the class and calls the deepClone method with the new instance.<br>
 * returns a cloned instance by value.<br>
 * <br>
 * 2. deepClone(map: Map‹Object, Object›): <br>
 * This method is used to correctly clone with respect class diagrams which are cyclic or have
 * data structures containing multiple references to the same object. <br>
 * To realize this, we need to pass a map of already visited objects to the deepClone method.
 * When cloning an object, we first check if the object is already in the map.<br>
 * If we encounter an object we have not seen yet, we create a new one and add it to our map.
 * This is crucial because if that object later contains a reference to itself (either directly or
 * indirectly),
 * we will recognize it from our map.
 * This prevents us from getting stuck in an endless loop trying to create the same object over and
 * over;
 * instead, we just copy the existing reference found in the map<br>
 * The map is a Map‹Object, Object› where the key is the original object and the value is the copied
 * object of the key.
 * If we do not pass the map, we would end up with a stack overflow error when trying to clone
 * cyclic references.<br>
 * <br>
 * <br>
 * The deepEquals method is also three methods: <br>
 * 1. deepEquals(o: Object): <br>
 * This method calls the deepEquals method with the signature deepEquals(o: Object,
 * <code>forceSameOrder</code>: boolean).<br>
 * <br>
 * 2. deepEquals(o: Object, <code>forceSameOrder</code>: boolean): <br>
 * This method calls the deepEquals method with the signature deepEquals(o: Object,
 * <code>forceSameOrder</code>: boolean, visitedObjects: Set<Object>).
 * With a new Map‹Object, Set‹Object›› of visited objects to avoid cyclic references. <br>
 * <br>
 * 3. deepEquals(o: Object, <code>forceSameOrder</code>: boolean, <code>visitedObjects</code>:
 * Set‹Object›): <br>
 * This method is the actual implementation of the deepEquals method.<br>
 * It compares the object with the current instance and checks if the attributes are equal.<br>
 * It begins by adding the <code>currentObject</code> to the map of <code>visitedObjects</code>.<br>
 * The map maps objects found in the first object onto objects found for that specific object in the
 * second object.<br>
 * Then it resolves the <code>currentObject</code>.<br>
 * Because we added the <code>currentObject</code> to the set of <code>visitedObjects</code> of the
 * specific first object,
 * we can detect cyclic references and avoid them.<br>
 * Afterward, we remove the <code>currentObject</code> from the map of <code>visitedObjects</code>
 * to allow for further comparisons.<br>
 * TODO currently the deepEquals method is not symmetric, meaning that if A.equals(B) is true,
 * B.equals(A) is not necessarily true.
 */
public class DeepCloneAndDeepEqualsDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2 {
  
  /**
   * a collection of all classes from the class diagram as strings
   */
  List<String> classesFromClassdiagramAsString = new ArrayList<>();
  boolean isInitialized = false;
  
  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() { return super.getMustRunAfter(); }
  
  protected void initClassesFromClassDiagramAsString(ASTCDCompilationUnit compilationUnit) {
    if (isInitialized) {
      return;
    }
    //visitor to get all classes from the class diagram
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
    isInitialized = true;
  }
  
  /**
   * Used to init the list of all artifacts defined in the cd
   *
   * @param compilationUnit the compilationUnit containing all artifacts
   */
  public void visit(ASTCDCompilationUnit compilationUnit) {
    initClassesFromClassDiagramAsString(compilationUnit);
  }
  
  /**
   * Only when visiting a class node, we add the deepClone and deepEquals methods to the decorated
   * class.
   *
   * @param node the ASTCDClass node
   */
  @Override
  public void visit(ASTCDClass node) {
    ASTCDClass decClazz = decoratorData.getAsDecorated(node);
    
    //the numbers correspond to arguments of the deepClone and deepEquals methods
    addDeepCloneMethod(node, decClazz);
    addDeepCloneMethod1(node, decClazz);
    addDeepCloneMethod2(node, decClazz);
    addDeepEquals1Method(node, decClazz);
    addDeepEquals2Method(node, decClazz);
    addDeepEquals3Method(node, decClazz);
    
    //add a private constructor to the pojo class when no one exists. Needed for deepClone
    if (!decClazz.getCDConstructorList().isEmpty()) {
      boolean hasDefaultConstructor = decClazz.getCDConstructorList().stream().anyMatch(c -> c
          .getCDParameterList().isEmpty());
      if (!hasDefaultConstructor) {
        ASTCDConstructor constructor1 = CDConstructorFacade.getInstance().createDefaultConstructor(
            CD4CodeMill.modifierBuilder().PRIVATE().build(), node);
        addToClass(decClazz, constructor1);
      }
    }
  }
  
  /**
   * Adds a deepClone method with the signature deepClone()
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepCloneMethod(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty() ? originalClass.getName()
        : packageName + "." + originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
        originalClassFullQualifiedName);
    
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(
        originalClassQualifiedType).build();
    ASTCDMethod deepCloneMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill
        .modifierBuilder().PUBLIC().build(), originalClassReturnType, "deepClone",
        new ArrayList<>());
    
    decoratedClass.addCDMember(deepCloneMethod);
    
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepCloneMethod,
        new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepClone", originalClassQualifiedType
            .printType())));
  }
  
  /**
   * Method needed to create the new Result Object, add it to the map and then runs the real
   * DeepClone method
   * Needed to avoid using a Builder while still avoiding public constructors
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  public void addDeepCloneMethod1(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty() ? originalClass.getName()
        : packageName + "." + originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
        originalClassFullQualifiedName);
    ASTMCQualifiedType objectType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCMapType visitedObjectsType = MCTypeFacade.getInstance().createMapTypeOf(objectType,
        objectType);
    
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(visitedObjectsType)
        .setName("map").build();
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(
        originalClassQualifiedType).build();
    
    ASTCDMethod deepClone2Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill
        .modifierBuilder().PUBLIC().build(), originalClassReturnType, "deepClone", List.of(
            parameter1));
    
    decoratedClass.addCDMember(deepClone2Method);
    
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepClone2Method,
        new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepClone1",
            originalClassQualifiedType)));
  }
  
  /**
   * Adds a deepClone method with the signature deepClone(result: ‹PojoClass›, map: Map‹PojoClass,
   * PojoClass›)
   * We need 2 parameters in the deepClone method to prevent cyclic references causing stack
   * overflow errors and instead copy the cyclic references
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepCloneMethod2(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty() ? originalClass.getName()
        : packageName + "." + originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
        originalClassFullQualifiedName);
    ASTMCQualifiedType objectType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCMapType visitedObjectsType = MCTypeFacade.getInstance().createMapTypeOf(objectType,
        objectType);
    
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(
        originalClassQualifiedType).setName("result").build();
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(visitedObjectsType)
        .setName("map").build();
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(
        originalClassQualifiedType).build();
    
    ASTCDMethod deepClone2Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill
        .modifierBuilder().PUBLIC().build(), originalClassReturnType, "deepClone", List.of(
            parameter1, parameter2));
    
    decoratedClass.addCDMember(deepClone2Method);
    
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepClone2Method,
        new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepClone2",
            originalClassQualifiedType, originalClass.getCDAttributeList(),
            classesFromClassdiagramAsString)));
  }
  
  /**
   * Adds a deepEquals method with the signature deepEquals(o: ‹Object›)
   * This method calls the deepEquals method with the signature deepEquals(o: ‹Object›,
   * forceSameOrder: boolean)
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepEquals1Method(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
        "Object");
    ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(CD4CodeMill
        .mCPrimitiveTypeBuilder().setPrimitive(1).build()).build();
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(
        originalClassQualifiedType).setName("o").build();
    ASTCDMethod deepEquals1Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill
        .modifierBuilder().PUBLIC().build(), booleanReturnType, "deepEquals", List.of(parameter1));
    
    decoratedClass.addCDMember(deepEquals1Method);
    
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals1Method,
        new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals1")));
  }
  
  /**
   * Adds a deepEquals method with the signature deepEquals(o: ‹Object›, forceSameOrder: boolean)
   * to the decorated class.
   * This class calls the deepEquals method with the signature deepEquals(o: ‹Object›,
   * forceSameOrder: boolean, visitedObjects: Map‹Object›,Set‹Object››)
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepEquals2Method(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
        "Object");
    ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(CD4CodeMill
        .mCPrimitiveTypeBuilder().setPrimitive(1).build()).build();
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(
        originalClassQualifiedType).setName("o").build();
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(CD4CodeMill
        .mCPrimitiveTypeBuilder().setPrimitive(1).build()).setName("forceSameOrder").build();
    ASTCDMethod deepEquals2Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill
        .modifierBuilder().PUBLIC().build(), booleanReturnType, "deepEquals", List.of(parameter1,
            parameter2));
    
    decoratedClass.addCDMember(deepEquals2Method);
    
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals2Method,
        new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals2")));
  }
  
  /**
   * Adds a deepEquals method with the signature deepEquals(o: ‹Object›, forceSameOrder: boolean,
   * visitedObjects: Map‹Object,Set‹Object››)
   * We need 3 parameters in the deepEquals method:
   * Because when iterating over lists and sets we need to declare a boolean for every type and
   * check it afterward as return false would not work
   * 1. the object to compare with
   * 2. the forceSameOrder boolean
   * 3. a map which maps objects found in the first object onto a set of objects found for that
   * specific object in the second object
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  //TODO equals is not symmetric, meaning that if A.equals(B) is true, B.equals(A) is not necessarily true.
  // this is only because of the parameter forceSameOrder,
  // which when set to false results in not detecting differt objects in b
  // example: a list with ob1, obj1, obj2 and b with obj1, obj2, ob3 will result in true when forceSameOrder is false
  // as the second list contains all objects from the first list
  // my solution: internally call a.deepEquals(b) and b.deepEquals(a) and return true if both are true
  // remark: in MontiCore deepEquals is also not symmetric
  private void addDeepEquals3Method(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty() ? originalClass.getName()
        : packageName + "." + originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(
        originalClassFullQualifiedName);
    ASTMCQualifiedType objectType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(CD4CodeMill
        .mCPrimitiveTypeBuilder().setPrimitive(1).build()).build();
    ASTMCSetType visitedObjectsSet = MCTypeFacade.getInstance().createSetTypeOf(objectType);
    ASTMCMapType visitedObjectsMapOfSet = MCTypeFacade.getInstance().createMapTypeOf(objectType,
        visitedObjectsSet);
    
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(objectType).setName("o")
        .build();
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(CD4CodeMill
        .mCPrimitiveTypeBuilder().setPrimitive(1).build()).setName("forceSameOrder").build();
    ASTCDParameter parameter3 = CD4CodeMill.cDParameterBuilder().setMCType(visitedObjectsMapOfSet)
        .setName("visitedObjects").build();
    
    ASTCDMethod deepEquals3Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill
        .modifierBuilder().PUBLIC().build(), booleanReturnType, "deepEquals", List.of(parameter1,
            parameter2, parameter3));
    
    decoratedClass.addCDMember(deepEquals3Method);
    
    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals3Method,
        new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals3",
            originalClassQualifiedType, originalClass.getCDAttributeList(),
            classesFromClassdiagramAsString)));
  }
  
  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
  
}
