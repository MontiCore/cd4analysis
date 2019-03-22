/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package transformation.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.types.types._ast.TypesMill;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

/**
 * Some raw (no parameter checks) help methods for the CD ast transformations.
 *
 * @author Galina Volkova
 */
public class ASTCDRawTransformation {
  
  // -------------------- Attributes --------------------
  
  /**
   * Creates an instance of the {@link ASTCDAttribute} with the given name
   * and type and adds it to the given class
   * 
   * @param astClass given class
   * @param attrName given name
   * @param attrType given type
   * @return created {@link ASTCDAttribute} node
   */
  public ASTCDAttribute addCdAttribute(ASTCDClass astClass,
      String attrName,
      String attrType) {
    ASTCDAttribute attribute = CD4AnalysisMill.cDAttributeBuilder().setName(attrName)
        .setType(createType(attrType))
        .build();
    addCdAttribute(astClass, attribute);
    return attribute;
  }
  
  /**
   * Adds the given attribute to the given class
   * 
   * @param astClass given class
   * @param astAttribute given attribute
   */
  public void addCdAttribute(ASTCDClass astClass,
      ASTCDAttribute astAttribute) {
    astClass.getCDAttributeList().add(astAttribute);
  }
  
  // -------------------- Classes --------------------
  
  /**
   * Creates an instance of the {@link ASTCDClass} with the given name and
   * adds it to the given CD definition type definitions
   * 
   * @param astDef given ASTCDDefinition
   * @param className given class name
   * @return created {@link ASTCDClass} node
   */
  public ASTCDClass addCdClass(ASTCDDefinition astDef, String className) {
    ASTCDClass astClass = CD4AnalysisMill.cDClassBuilder().setName(className).build();
    addCdClass(astDef, astClass);
    return astClass;
  }
  
  /**
   * Creates an instance of the {@link ASTCDClass} with the given name,
   * super class and the list of extended interface names and adds it to the
   * given CD definition
   * 
   * @param astDef given ASTCDDefinition
   * @param className given class name
   * @param superClassName super class name
   * @param interfaceNames list of interface names
   * @return created {@link ASTCDClass} node
   */
  public ASTCDClass addCdClass(ASTCDDefinition astDef, String className,
      String superClassName,
      List<String> interfaceNames) {
    
    ASTSimpleReferenceType superClass = createType(superClassName);
    List<ASTReferenceType> interfaces = new ArrayList<>();
    for (String interfName : interfaceNames) {
      ASTSimpleReferenceType interf = createType(interfName);
      interfaces.add(interf);
    }
    ASTCDClass astClass = CD4AnalysisMill.cDClassBuilder().setName(className)
        .setSuperclass(superClass).setInterfaceList(interfaces).build();
    addCdClass(astDef, astClass);
    return astClass;
  }
  
  /**
   * Adds the given class to the given CD definition
   * 
   * @param astDef given ASTCDDefinition
   * @param astClass given class
   */
  public void addCdClass(ASTCDDefinition astDef, ASTCDClass astClass) {
    astDef.getCDClassList().add(astClass);
  }
  
  // -------------------- Interfaces --------------------
  
  /**
   * Creates an instance of the {@link ASTCDInterface} with the given name
   * and adds it to the given CD definition type definitions
   * 
   * @param astDef given ASTCDDefinition
   * @param interfaceName given interface name
   * @return created {@link ASTCDInterface} node
   */
  public ASTCDInterface addCdInterface(ASTCDDefinition astDef,
      String interfaceName) {
    ASTCDInterface astInterface = CD4AnalysisMill.cDInterfaceBuilder()
        .setName(interfaceName).build();
    addCdInterface(astDef, astInterface);
    return astInterface;
  }
  
  /**
   * Adds the given interface to the given CD definition
   * 
   * @param astDef given ASTCDDefinition
   * @param astInterface given ASTCDInterface
   */
  public void addCdInterface(ASTCDDefinition astDef,
      ASTCDInterface astInterface) {
    astDef.getCDInterfaceList().add(astInterface);
  }
  
  /**
   * Creates an instance of the {@link ASTCDInterface} with the given name
   * and the list of extended interface names and adds it to the given CD
   * definition
   * 
   * @param astDef given ASTCDDefinition
   * @param interfaceName given interface name
   * @param interfaceNames list of interface names
   * @return created {@link ASTCDInterface} node
   */
  public ASTCDInterface addCdInterface(ASTCDDefinition astDef,
      String interfaceName,
      List<String> interfaceNames) {
    List<ASTReferenceType> interfaces = new ArrayList<>();
    for (String interfName : interfaceNames) {
      ASTSimpleReferenceType type = createType(interfName);
      interfaces.add(type);
    }
    ASTCDInterface astInterface = CD4AnalysisMill.cDInterfaceBuilder()
        .setName(interfaceName)
        .setInterfaceList(interfaces).build();
    addCdInterface(astDef, astInterface);
    return astInterface;
  }
  
  // -------------------- Methods --------------------
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name,
   * default return type and the empty parameter list and adds it to the
   * given class
   * 
   * @param astType default return type
   * @param methodName method name
   * @return The created {@link ASTCDMethod} node
   */
  public ASTCDMethod addCdMethod(ASTCDType astType, String methodName) {
    return addCdMethod(astType, methodName, Lists.newArrayList());
  }
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name,
   * return type and parameter types and adds it to the given class
   * 
   * @param astType type of method
   * @param methodName name of method
   * @param returnType return type of method
   * @param paramTypes list of parameter types of method
   * @return created {@link ASTCDMethod} node
   */
  public ASTCDMethod addCdMethod(ASTCDType astType, String methodName,
      String returnType, List<String> paramTypes) {
    ASTReturnType astReturnType = createType(returnType);
    List<ASTCDParameter> cdParameters = createCdMethodParameters(paramTypes);
    ASTModifier modifier = CD4AnalysisMill.modifierBuilder().setPublic(true).build();
    ASTCDMethod cdMethod = CD4AnalysisMill.cDMethodBuilder()
        .setName(methodName)
        .setReturnType(astReturnType)
        .setModifier(modifier)
        .setCDParameterList(cdParameters)
        .build();
    addCdMethod(astType, cdMethod);
    return cdMethod;
  }
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name,
   * and parameter types and default return type (void).
   * Created node is added to the given class
   * 
   * @param astType type of method
   * @param methodName name of method
   * @param paramTypes list of parameter types of method
   * @return created {@link ASTCDMethod} node
   */
  public ASTCDMethod addCdMethod(ASTCDType astType, String methodName,
      List<String> paramTypes) {
    ASTVoidType returnType = TypesMill.voidTypeBuilder().build();
    List<ASTCDParameter> cdParameters = createCdMethodParameters(paramTypes);
    ASTModifier modifier = CD4AnalysisMill.modifierBuilder().setPublic(true).build();
    ASTCDMethod cdMethod = CD4AnalysisMill.cDMethodBuilder()
        .setName(methodName)
        .setReturnType(returnType)
        .setModifier(modifier)
        .setCDParameterList(cdParameters)
        .build();
    addCdMethod(astType, cdMethod);
    return cdMethod;
  }
  
  /**
   * Adds the given method to the given class
   * 
   * @param astType given class
   * @param astMethod given method
   */
  public void addCdMethod(ASTCDType astType, ASTCDMethod astMethod) {
    astType.getCDMethodList().add(astMethod);
  }
  
  /**
   * Creates a list of {@link ASTCDParameter} using the list of
   * the type definitions
   * 
   * @param paramTypes list of parameter types
   * @return List of the created {@link ASTCDParameter} objects
   */
  public List<ASTCDParameter> createCdMethodParameters(List<String> paramTypes) {
    List<ASTCDParameter> params = Lists.newArrayList();
    List<ASTSimpleReferenceType> types = Lists.newArrayList();
    for (String paramType : paramTypes) {
      types.add(createType(paramType));
    }
    types.forEach(param -> params.add(CD4AnalysisMill.cDParameterBuilder()
        .setType(param)
        .setName(ASTCDTransformation.PARAM_NAME_PREFIX + types.indexOf(param))
        .build()));
    return params;
  }
  
  /**
   * Creates an instance of the {@link ASTSimpleReferenceType} for the given
   * type name
   * 
   * @param typeName name of the SimpleReferenceType
   * @return created {@link ASTSimpleReferenceType} node
   */
  public ASTSimpleReferenceType createType(String typeName) {
    return TypesMill.simpleReferenceTypeBuilder()
        .setNameList(Arrays.asList(typeName.split("\\."))).build();
  }
  
}
