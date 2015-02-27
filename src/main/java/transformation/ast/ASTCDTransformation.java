/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package transformation.ast;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.RecognitionException;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDMethod;
import de.cd4analysis._ast.ASTCDParameter;
import de.cd4analysis._ast.ASTCDParameterList;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.cd4analysis._parser.CDAttributeMCParser;
import de.cd4analysis._parser.CDMethodMCParser;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.types._ast.ASTType;
import de.monticore.types._ast.TypesNodeFactory;
import de.se_rwth.commons.logging.Log;

/**
 * Some help methods for the CD ast transformation
 *
 * @author Galina Volkova
 */
public class ASTCDTransformation {
  
  public static void addCdAttribute(ASTCDClass astClass, String attrName, String attrType) {
    checkArgument(!Strings.isNullOrEmpty(attrName));
    checkArgument(!Strings.isNullOrEmpty(attrType));
    ASTType attributeType = ASTSimpleReferenceType.getBuilder()
        .name(Arrays.asList(attrType.split("\\."))).build();
    ASTCDAttribute attribute = ASTCDAttribute.getBuilder().name(attrName).type(attributeType)
        .build();
    addCdAttribute(astClass, attribute);
  }
  
  public static void addCdAttributeUsingDefinition(ASTCDClass astClass, String attributeDefinition) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(attributeDefinition));
    try {
      Optional<ASTCDAttribute> astAttribute = new CDAttributeMCParser().parse(attributeDefinition);
      if (!astAttribute.isPresent()) {
        Log.error("Attribute can't be added to the CD class " + astClass.getName()
            + "\nWrong attribute definition: " + attributeDefinition);
      }
      else {
        addCdAttribute(astClass, astAttribute.get());
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Attribute can't be added to the CD class " + astClass.getName()
          + "\nCatched exception: " + e);
    }
  }
  
  public static void addCdAttribute(ASTCDClass astClass, ASTCDAttribute astAttribute) {
    checkNotNull(astClass);
    checkNotNull(astAttribute);
    astClass.getCDAttributes().add(astAttribute);
  }
  
  public static void addCdMethodUsingDefinition(ASTCDClass astClass, String methodDefinition) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(methodDefinition));
    try {
      Optional<ASTCDMethod> astMethod = new CDMethodMCParser().parse(methodDefinition);
      if (!astMethod.isPresent()) {
        Log.error("Method can't be added to the CD class " + astClass.getName()
            + "\nWrong method definition: " + methodDefinition);
      }
      else {
        addCdMethod(astClass, astMethod.get());
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Method can't be added to the CD class " + astClass.getName()
          + "\nCatched exception: " + e);
    }
  }
  
  public static void addCdMethod(ASTCDClass astClass, String methodName) {
    addCdMethod(astClass, methodName, "void", Lists.newArrayList());
  }
  
  public static void addCdMethod(ASTCDClass astClass, String methodName, String returnType,
      List<String> paramTypes) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(methodName));
    
    ASTCDMethod astMethod = ASTCDMethod.getBuilder()
        .name(methodName)
        .returnType(createSimpleRefType(returnType))
        .cDParameters(createCdMethodParameters(paramTypes))
        .build();
    addCdMethod(astClass, astMethod);
  }
  
  public static void addCdMethod(ASTCDClass astClass, ASTCDMethod astMethod) {
    checkNotNull(astClass);
    checkNotNull(astMethod);
    astClass.getCDMethods().add(astMethod);
  }
  
  public static ASTSimpleReferenceType createSimpleRefType(String typeName) {
    checkArgument(!Strings.isNullOrEmpty(typeName));
    return ASTSimpleReferenceType.getBuilder().name(Lists.newArrayList(typeName)).build();
  }
  
  public static ASTSimpleReferenceType createSimpleRefType(List<String> typeName) {
    checkNotNull(typeName);
    checkArgument(!typeName.isEmpty());
    return ASTSimpleReferenceType.getBuilder().name(typeName).build();
  }
  
  public static ASTCDParameterList createCdMethodParameters(List<String> paramTypes) {
    checkNotNull(paramTypes);
    checkArgument(!paramTypes.isEmpty());
    ASTCDParameterList params = CD4AnalysisNodeFactory.createASTCDParameterList();
    paramTypes.forEach(param -> params.add(ASTCDParameter.getBuilder()
        .type(createSimpleRefType(param))
        .name("param" + paramTypes.indexOf(param)).build()));
    return params;
  }
  
  public static void addCdClass(ASTCDDefinition astDef, String className) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(className));
    addCdClass(astDef, ASTCDClass.getBuilder().name(className).build());
  }
  
  public static void addCdClass(ASTCDDefinition astDef, String className, String superClassName,
      List<String> interfaceNames) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(className));
    ASTReferenceTypeList interfaces = TypesNodeFactory.createASTReferenceTypeList();
    interfaceNames.forEach(i -> interfaces.add(createSimpleRefType(i)));
    ASTCDClass astClass = ASTCDClass.getBuilder().name(className)
        .superclass(createSimpleRefType(superClassName)).interfaces(interfaces).build();
    addCdClass(astDef, astClass);
  }
  
  public static void addCdClass(ASTCDDefinition astDef, ASTCDClass astClass) {
    checkNotNull(astDef);
    checkNotNull(astClass);
    astDef.getCDClasses().add(astClass);
  }
  
  public static void addCdInterface(ASTCDDefinition astDef, String interfaceName) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(interfaceName));
    addCdInterface(astDef, ASTCDInterface.getBuilder().name(interfaceName).build());
  }
  
  public static void addCdInterface(ASTCDDefinition astDef, ASTCDInterface astInterface) {
    checkNotNull(astDef);
    checkNotNull(astInterface);
    astDef.getCDInterfaces().add(astInterface);
  }
  
  public static void addCdInterface(ASTCDDefinition astDef, String interfaceName,
      List<String> interfaceNames) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(interfaceName));
    ASTReferenceTypeList interfaces = TypesNodeFactory.createASTReferenceTypeList();
    interfaceNames.forEach(i -> interfaces.add(createSimpleRefType(i)));
    ASTCDInterface astInterface = ASTCDInterface.getBuilder().name(interfaceName)
        .interfaces(interfaces).build();
    addCdInterface(astDef, astInterface);
  }
  
}
