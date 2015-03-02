/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package transformation.ast;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;
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
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.cd4analysis._parser.CDAttributeMCParser;
import de.cd4analysis._parser.CDMethodMCParser;
import de.cd4analysis._parser.ReturnTypeMCParser;
import de.cd4analysis._parser.TypeMCParser;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTReturnType;
import de.monticore.types._ast.ASTType;
import de.monticore.types._ast.TypesNodeFactory;
import de.se_rwth.commons.logging.Log;

/**
 * Some help methods for the CD ast transformations
 *
 * @author Galina Volkova
 */
public class ASTCDTransformation {
  
  public static Optional<ASTCDAttribute> addCdAttribute(ASTCDClass astClass, String attrName, String attrType) {
    checkArgument(!Strings.isNullOrEmpty(attrName));
    checkArgument(!Strings.isNullOrEmpty(attrType));
    Optional<ASTType> attributeType = createSimpleRefType(attrType);
    if (!attributeType.isPresent()) {
      Log.error("Attribute can't be added to the CD class " + astClass.getName());
      return Optional.absent();
    }
    ASTCDAttribute attribute = ASTCDAttribute.getBuilder().name(attrName).type(attributeType.get())
        .build();
    addCdAttribute(astClass, attribute);
    return Optional.of(attribute);
  }
  
  public static Optional<ASTCDAttribute> addCdAttributeUsingDefinition(ASTCDClass astClass,
      String attributeDefinition) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(attributeDefinition));
    Optional<ASTCDAttribute> astAttribute = Optional.absent();
    try {
      astAttribute = new CDAttributeMCParser().parse(new StringReader(attributeDefinition));
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
    return astAttribute;
  }
  
  public static void addCdAttribute(ASTCDClass astClass, ASTCDAttribute astAttribute) {
    checkNotNull(astClass);
    checkNotNull(astAttribute);
    astClass.getCDAttributes().add(astAttribute);
  }
  
  public static Optional<ASTCDMethod> addCdMethodUsingDefinition(ASTCDClass astClass,
      String methodDefinition) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(methodDefinition));
    Optional<ASTCDMethod> astMethod = Optional.absent();
    try {
      astMethod = new CDMethodMCParser().parse(new StringReader(methodDefinition));
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
    return astMethod;
  }
  
  public static ASTCDMethod addCdMethod(ASTCDClass astClass, String methodName) {
    return addCdMethod(astClass, methodName, "void", Lists.newArrayList()).get();
  }
  
  public static Optional<ASTCDMethod> addCdMethod(ASTCDClass astClass, String methodName,
      String returnType,
      List<String> paramTypes) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(methodName));
    Optional<ASTCDMethod> astMethod = Optional.absent();
    Optional<ASTReturnType> parsedReturnType = createReturnType(returnType);
    Optional<ASTCDParameterList> cdParameters = createCdMethodParameters(paramTypes);
    if (!parsedReturnType.isPresent() || !cdParameters.isPresent()) {
      Log.error("Method " + methodName + " can't be added to the CD class " + astClass.getName());
    }
    else {
      ASTCDMethod cdMethod = ASTCDMethod.getBuilder()
          .name(methodName)
          .modifier(ASTModifier.getBuilder().r_public(true).build())
          .returnType(parsedReturnType.get())
          .cDParameters(cdParameters.get())
          .build();
      addCdMethod(astClass, cdMethod);
      return Optional.of(cdMethod);
    }
    
    return astMethod;
  }
  
  public static void addCdMethod(ASTCDClass astClass, ASTCDMethod astMethod) {
    checkNotNull(astClass);
    checkNotNull(astMethod);
    astClass.getCDMethods().add(astMethod);
  }
  
  public static Optional<ASTReturnType> createReturnType(String typeName) {
    checkArgument(!Strings.isNullOrEmpty(typeName));
    Optional<ASTReturnType> astType = Optional.absent();
    try {
      astType = new ReturnTypeMCParser().parse(new StringReader(typeName));
      if (!astType.isPresent()) {
        Log.error("Return type " + typeName + " wasn't defined correctly");
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Return type  " + typeName + " wasn't defined correctly: "
          + "\nCatched exception: " + e);
    }
    return astType;
  }
  
  public static Optional<ASTType> createSimpleRefType(String typeName) {
    checkArgument(!Strings.isNullOrEmpty(typeName));
    Optional<ASTType> astType = Optional.absent();
    try {
      astType = new TypeMCParser().parse(new StringReader(typeName));
      if (!astType.isPresent()) {
        Log.error("Parameter type " + typeName + " wasn't defined correctly");
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Parameter type  " + typeName + " wasn't defined correctly: "
          + "\nCatched exception: " + e);
    }
    return astType;
  }
  
  public static Optional<ASTCDParameterList> createCdMethodParameters(List<String> paramTypes) {
    checkNotNull(paramTypes);
    ASTCDParameterList params = CD4AnalysisNodeFactory.createASTCDParameterList();
    List<ASTType> types = Lists.newArrayList();
    for (String paramType : paramTypes) {
      Optional<ASTType> type = createSimpleRefType(paramType);
      if (!type.isPresent()) {
        return Optional.absent();
      }
      types.add(type.get());
    }
    types.forEach(param -> params.add(ASTCDParameter.getBuilder()
        .type(param)
        .name("param" + types.indexOf(param)).build()));
    return Optional.of(params);
  }
  
  public static ASTCDClass addCdClass(ASTCDDefinition astDef, String className) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(className));
    ASTCDClass astClass = ASTCDClass.getBuilder().name(className).build();
    addCdClass(astDef, astClass);
    return astClass;
  }
  
  public static Optional<ASTCDClass> addCdClass(ASTCDDefinition astDef, String className,
      String superClassName,
      List<String> interfaceNames) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(className));
    Optional<ASTType> superClass = createSimpleRefType(superClassName);
    ASTReferenceTypeList interfaces = TypesNodeFactory.createASTReferenceTypeList();
    for (String paramType : interfaceNames) {
      Optional<ASTType> type = createSimpleRefType(paramType);
      if (!type.isPresent() || !(type.get() instanceof ASTReferenceType)) {
        Log.error("Class " + className + " can't be added to the CD drfinition.");
        return Optional.absent();
      }
      interfaces.add((ASTReferenceType)type.get());
    }
    if (!superClass.isPresent()) {
      Log.error("Class " + className + " can't be added to the CD drfinition.");
      return Optional.absent();
    }
    ASTCDClass astClass = ASTCDClass.getBuilder().name(className)
        .superclass((ASTReferenceType)superClass.get()).interfaces(interfaces).build();
    addCdClass(astDef, astClass);
    return Optional.of(astClass);
  }
  
  public static void addCdClass(ASTCDDefinition astDef, ASTCDClass astClass) {
    checkNotNull(astDef);
    checkNotNull(astClass);
    astDef.getCDClasses().add(astClass);
  }
  
  public static ASTCDInterface addCdInterface(ASTCDDefinition astDef, String interfaceName) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(interfaceName));
    ASTCDInterface astInterface = ASTCDInterface.getBuilder().name(interfaceName).build();
    addCdInterface(astDef, astInterface);
    return astInterface;
  }
  
  public static void addCdInterface(ASTCDDefinition astDef, ASTCDInterface astInterface) {
    checkNotNull(astDef);
    checkNotNull(astInterface);
    astDef.getCDInterfaces().add(astInterface);
  }
  
  public static Optional<ASTCDInterface> addCdInterface(ASTCDDefinition astDef, String interfaceName,
      List<String> interfaceNames) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(interfaceName));
    ASTReferenceTypeList interfaces = TypesNodeFactory.createASTReferenceTypeList();
    for (String paramType : interfaceNames) {
      Optional<ASTType> type = createSimpleRefType(paramType);
      if (!type.isPresent() || !(type.get() instanceof ASTReferenceType)) {
        Log.error("Class " + interfaceName + " can't be added to the CD drfinition.");
        return Optional.absent();
      }
      interfaces.add((ASTReferenceType)type.get());
    }
    ASTCDInterface astInterface = ASTCDInterface.getBuilder().name(interfaceName)
        .interfaces(interfaces).build();
    addCdInterface(astDef, astInterface);
    return Optional.of(astInterface);
  }
  
}
