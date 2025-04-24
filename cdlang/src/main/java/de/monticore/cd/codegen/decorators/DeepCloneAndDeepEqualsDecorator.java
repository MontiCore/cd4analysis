package de.monticore.cd.codegen.decorators;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.DataContainer;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4analysis._util.CD4AnalysisTypeDispatcher;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;


/**
 * Decorator that adds deepCopy and deepEquals methods to artifacts specified in the class diagram.
 */
public class DeepCloneAndDeepEqualsDecorator extends AbstractDecorator<AbstractDecorator.NoData> implements CDBasisVisitor2 {
  boolean hasInitialized = false;
  List<Class<? extends IDecorator<?>>> classesFromClassdiagram = new ArrayList<>();
  List<String> classesFromClassdiagramAsString = new ArrayList<>();

  @Override
  public void visit(ASTCDClass node) {
    if (!hasInitialized) {
      init();
    }
    // As all classes need to for the recursive algorithm to functions
    //if (this.decoratorData.shouldDecorate(this.getClass(), node)) {
    ASTCDClass decClazz = decoratorData.getAsDecorated(node);
    addDeepCloneMethod(node, decClazz);
    addDeepEquals1Method(node, decClazz);
    addDeepEquals2Method(node, decClazz);
    addDeepEqualsMethod3(node, decClazz);

  }

  //TODO: remove this method
  //this should probably be done in a previous step
  private void init(){
    hasInitialized = true;


  }

  private void addDeepCloneMethod(ASTCDClass originalClass, ASTCDClass decoratedClass) {
    String packageName = originalClass.getSymbol().getPackageName();
    String originalClassFullQualifiedName = packageName.isEmpty()? originalClass.getName(): packageName +"."+ originalClass.getName();
    ASTMCQualifiedType originalClassQualifiedType = MCTypeFacade.getInstance().createQualifiedType(originalClassFullQualifiedName);
    ASTMCReturnType originalClassReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(originalClassQualifiedType).build();
    ASTCDMethod deepCloneMethod = CDMethodFacade.getInstance().createMethod(CD4CodeMill.modifierBuilder().PUBLIC().build(), originalClassReturnType,"deepClone",new ArrayList<>());

    decoratedClass.addCDMember(deepCloneMethod);

    //TODO implement deep clone method
  }

  /**
   * Adds a deepEquals method with the signature deepEquals(o: <Object>)
   * This method calls the deepEquals method with the signature deepEquals(o: <Object>, forceSameOrder: boolean)
   *
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
   * 1. the object to compare with
   * 2. the forceSameOrder boolean
   * 3. a set of already visited objects as the classdiagram can be cyclic
   *
   * @param originalClass the original class
   * @param decoratedClass the decorated class where the method is added
   */
  private void addDeepEqualsMethod3(ASTCDClass originalClass, ASTCDClass decoratedClass) {
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

    //TODO fix find all classes before
    List<String> classesFromClassdiagramAsString = new ArrayList<>();
    classesFromClassdiagramAsString.addAll(DataContainer.getInstance().getClasses().stream().map(e-> e.getSymbol().getFullName()).collect(Collectors.toList()));
    classesFromClassdiagramAsString.addAll(DataContainer.getInstance().getInterfaces().stream().map(e-> e.getSymbol().getFullName()).collect(Collectors.toList()));
    classesFromClassdiagramAsString.addAll(DataContainer.getInstance().getEnums().stream().map(e-> e.getSymbol().getFullName()).collect(Collectors.toList()));
    classesFromClassdiagramAsString.add("TestDeepCloneAndDeepEquals.OtherC");
    classesFromClassdiagramAsString.add("TestDeepCloneAndDeepEquals.B");
    //TODO until here

    for(ASTCDAttribute attribute: originalClass.getCDAttributeList()){
      if(attribute.getMCType() instanceof ASTMCSetType){
          ASTMCSetType setType = (ASTMCSetType) attribute.getMCType();
          setType.getMCTypeArgument().printType();
        ((ASTMCSetType) attribute.getMCType()).getMCTypeArgument();



        CD4AnalysisTypeDispatcher CD4AnalysisTypeDispatcher = new CD4AnalysisTypeDispatcher();

        CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(attribute.getMCType());
        CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(attribute.getMCType());
        CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(attribute.getMCType());
        String s = setType.printType();



      }
    }


    glexOpt.ifPresent(glex -> glex.replaceTemplate(EMPTY_BODY, deepEquals3Method, new TemplateHookPoint("methods.deepCloneAndDeepEquals.deepEquals3", originalClassQualifiedType, originalClass.getCDAttributeList(),classesFromClassdiagramAsString)));

  }


  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

  @Override
  public List<Class<? extends IDecorator<?>>> getMustRunAfter() {
    //TODO this decorator should run as the very first as it depends on no changed data
    return super.getMustRunAfter();
  }


}
