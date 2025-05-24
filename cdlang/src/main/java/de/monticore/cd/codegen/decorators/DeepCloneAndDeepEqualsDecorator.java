package de.monticore.cd.codegen.decorators;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
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
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;


/**
 * Decorator that adds deepCopy and deepEquals methods to artifacts specified in the class diagram.<br>
 * The deepCopy method is actually two methods: <br>
 * 1. deepClone(): <br>
 *    Creates a new instance of the class and calls the deepClone method with the new instance.<br>
 *    <br>
 * 2. deepClone(result: PojoClass, map: Map‹PojoClass, Object[2]{PojoClass,boolean}›): <br>
 *   This method is used to correctly copy with respect class diagrams which are cyclic or have
 *   data structures containing multiple references to the same object. <br>
 *   To realize this, we need to pass a map of already visited objects to the deepClone method.
 *   If an object is already in the map, we can return the already cloned object instead of cloning it again.
 *   The map is a Map‹PojoClass, Object[2]{PojoClass,boolean}› where the first element is the
 *   original object and the second element is a boolean indicating whether the object has been cloned or not.
 *   If it is set to <code>false</code>' it indicates that the cloning process for this object has started but is not yet complete
 *   (i.e., it is currently being cloned higher up in the call stack).
 *   If the boolean is <code>true</code> indicates that the object has been fully cloned.
 *   This boolean flag is crucial for correctly handling cyclic dependencies, preventing infinite loops
 *   and ensuring that an object instance is created only once, even if referenced multiple times or cyclically.
 *   <br>
 *   <br>
 * The deepEquals method is also three methods: <br>
 * 1. deepEquals(o: Object): <br>
 *    This method calls the deepEquals method with the signature deepEquals(o: Object, <code>forceSameOrder</code>: boolean).<br>
 *    <br>
 * 2. deepEquals(o: Object, <code>forceSameOrder</code>: boolean): <br>
 *    This method calls the deepEquals method with the signature deepEquals(o: Object, <code>forceSameOrder</code>: boolean, visitedObjects: Set<Object>).
 *    With a new set of visited objects to avoid cyclic references.
 *    <br>
 *  3. deepEquals(o: Object, <code>forceSameOrder</code>: boolean, <code>visitedObjects</code>: Set‹Object›): <br>
 *    This method is the actual implementation of the deepEquals method.<br>
 *    It compares the object with the current instance and checks if the attributes are equal.<br>
 *    It begins by adding the <code>currentObject</code> to the set of <code>visitedObjects</code>.<br>
 *    Then it resolves the <code>currentObject</code>.<br>
 *    Because we added the <code>currentObject</code> to the set of <code>visitedObjects</code>,
 *    we can detect cyclic references and avoid them.<br>
 *    Afterward, we remove the <code>currentObject</code> from the set
 *    of <code>visitedObjects</code> to allow further comparisons.<br>
 *    TODO currently the deepEquals method is not symmetric, meaning that if A.equals(B) is true, B.equals(A) is not necessarily true.
 */
public class DeepCloneAndDeepEqualsDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {

  /**
   * a collection of all classes from the class diagram as strings
   */
  List<String> classesFromClassdiagramAsString = new ArrayList<>();
  boolean isInitialized = false;

  @Override
  @SuppressWarnings("rawtypes")
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    return super.getMustRunAfter();
  }

  private void initClassesFromClassDiagramAsString(ASTNode node) {
    if(isInitialized) {
      return;
    }
    //region resolve all types from the class diagram
    decoratorData.getParent(node);
    ASTNode parent = decoratorData.getParent(node).get();
    while(!(parent instanceof ASTCDDefinition)){
      parent = decoratorData.getParent(parent).get();
    }
    ASTCDDefinition def = (ASTCDDefinition)parent;
    ASTCDCompilationUnit compilationUnit = new ASTCDCompilationUnitBuilder()
      .setCDDefinition(def)
      .setMCPackageDeclarationAbsent()
      .build();

    //visitor to get all classes from the class diagram
    CDTypeCollector cdTypeCollector = new CDTypeCollector();
    CD4CodeTraverser t2 = CD4CodeMill.inheritanceTraverser();
    t2.add4CDBasis(cdTypeCollector);
    compilationUnit.accept(t2);

    classesFromClassdiagramAsString.addAll(cdTypeCollector.getClasses().stream().map(e-> e.getSymbol().getFullName()).collect(Collectors.toList()));
    classesFromClassdiagramAsString.addAll(cdTypeCollector.getInterfaces().stream().map(e-> e.getSymbol().getFullName()).collect(Collectors.toList()));
    classesFromClassdiagramAsString.addAll(cdTypeCollector.getEnums().stream().map(e-> e.getSymbol().getFullName()).collect(Collectors.toList()));
    //endregion
    isInitialized = true;
  }

  @Override
  public void visit(ASTCDClass node) {
    initClassesFromClassDiagramAsString(node);

    ASTCDClass decClazz = decoratorData.getAsDecorated(node);

    addDeepCloneMethod(node, decClazz);
    addDeepCloneMethod1(node, decClazz);
    addDeepCloneMethod2(node, decClazz);
    addDeepEquals1Method(node, decClazz);
    addDeepEquals2Method(node, decClazz);
    addDeepEquals3Method(node, decClazz);

    //add a private constructor to the pojo class when no one exists. Needed for deepClone
    if (!decClazz.getCDConstructorList().isEmpty()) {
      boolean hasDefaultConstructor = decClazz.getCDConstructorList().stream().anyMatch(c -> c.getCDParameterList().isEmpty());
      if (!hasDefaultConstructor) {
        ASTCDConstructor constructor1 = CDConstructorFacade.getInstance().createDefaultConstructor(CD4CodeMill.modifierBuilder().PRIVATE().build(), node);
        addToClass(decClazz, constructor1);
      }
    }
  }

  private void addDeepCloneMethod(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty()? originalClass.getName(): packageName +"."+ originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(originalClassFullQualifiedName);
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(originalClassQualifiedType).build();
    ASTCDMethod deepCloneMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), originalClassReturnType,"deepClone",new ArrayList<>());

    decoratedClass.addCDMember(deepCloneMethod);

    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepCloneMethod, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepClone", originalClassQualifiedType.printType())));
  }

  /**
   * Method needed to create the new Result Object, add it to the map and then runs the real DeepClone method
   * Needed to avoid using a Builder while still avoiding public constructors
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  public void addDeepCloneMethod1(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty()? originalClass.getName(): packageName +"."+ originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(originalClassFullQualifiedName);
    ASTMCQualifiedType objectType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCArrayType arrayType = MCTypeFacade.getInstance().createArrayType("Object",1);
    ASTMCMapType visitedObjectsType = MCTypeFacade.getInstance().createMapTypeOf(objectType, arrayType);
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(visitedObjectsType).setName("map").build();
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(originalClassQualifiedType).build();
    ASTCDMethod deepClone2Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), originalClassReturnType,"deepClone",List.of(parameter2));

    decoratedClass.addCDMember(deepClone2Method);

    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepClone2Method, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepClone1",originalClassQualifiedType)));


  }

  /**
   * Adds a deepClone method with the signature deepClone(result: <PojoClass>, map: Map<PojoClass, PojoClass>)
   * We need 2 parameters in the deepClone method to prevent cyclic references causing stack overflow errors and instead copy the cyclic references
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepCloneMethod2(ASTCDClass originalClass, ASTCDClass decoratedClass){
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty()? originalClass.getName(): packageName +"."+ originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(originalClassFullQualifiedName);
    ASTMCQualifiedType objectType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCArrayType arrayType = MCTypeFacade.getInstance().createArrayType("Object",1);

    ASTMCMapType visitedObjectsType = MCTypeFacade.getInstance().createMapTypeOf(objectType, arrayType);
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(originalClassQualifiedType).setName("result").build();
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(visitedObjectsType).setName("map").build();
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(originalClassQualifiedType).build();
    ASTCDMethod deepClone2Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), originalClassReturnType,"deepClone",List.of(parameter1,parameter2));

    decoratedClass.addCDMember(deepClone2Method);

    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepClone2Method, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepClone2",originalClassQualifiedType, originalClass.getCDAttributeList(),classesFromClassdiagramAsString)));
  }


  /**
   * Adds a deepEquals method with the signature deepEquals(o: <Object>)
   * This method calls the deepEquals method with the signature deepEquals(o: <Object>, forceSameOrder: boolean)
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepEquals1Method(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(1).build()).build();
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(originalClassQualifiedType).setName("o").build();
    ASTCDMethod deepEquals1Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), booleanReturnType,"deepEquals",List.of(parameter1));

    decoratedClass.addCDMember(deepEquals1Method);

    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals1Method, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals1")));
  }

  /**
   * Adds a deepEquals method with the signature deepEquals(o: <Object>, forceSameOrder: boolean)
   * to the decorated class.
   * This class calls the deepEquals method with the signature deepEquals(o: <Object>, forceSameOrder: boolean, visitedObjects: Set<Object>)
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepEquals2Method(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(1).build()).build();
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(originalClassQualifiedType).setName("o").build();
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(1).build()).setName("forceSameOrder").build();
    ASTCDMethod deepEquals2Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), booleanReturnType,"deepEquals",List.of(parameter1,parameter2));

    decoratedClass.addCDMember(deepEquals2Method);

    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals2Method, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals2")));
  }

  /**
   * Adds a deepEquals method with the signature deepEquals(o: <Object>, forceSameOrder: boolean, visitedObjects: Set<Object>)
   * We need 3 parameters in the deepEquals method:
   * Because when iterating over lists and sets we need to declare a boolean for every type and check it afterward as return false would not work
   * 1. the object to compare with
   * 2. the forceSameOrder boolean
   * 3. a set of already visited objects as the classdiagram can be cyclic
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepEquals3Method(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty()? originalClass.getName(): packageName +"."+ originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(originalClassFullQualifiedName);
    ASTMCQualifiedType objectType = MCTypeFacade.getInstance().createQualifiedType("Object");
    ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(1).build()).build();
    ASTMCSetType visitedObjectsType = MCTypeFacade.getInstance().createSetTypeOf(objectType);
    ASTCDParameter parameter1 = CD4CodeMill.cDParameterBuilder().setMCType(objectType).setName("o").build();
    ASTCDParameter parameter2 = CD4CodeMill.cDParameterBuilder().setMCType(CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(1).build()).setName("forceSameOrder").build();
    ASTCDParameter parameter3 = CD4CodeMill.cDParameterBuilder().setMCType(visitedObjectsType).setName("visitedObjects").build();
    ASTCDMethod deepEquals3Method = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), booleanReturnType,"deepEquals",List.of(parameter1,parameter2,parameter3));

    decoratedClass.addCDMember(deepEquals3Method);

    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals3Method, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals3", originalClassQualifiedType, originalClass.getCDAttributeList(),classesFromClassdiagramAsString)));
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
