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
import de.cd4analysis._parser.ModifierMCParser;
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
  
  public static final String PARAM_NAME_PREFIX = "param";
  
  public static final String DEFAULT_RETURN_TYPE = "void";
  
  public static final String DEFAULT_METHOD_MODIFIER = "public";
  
  // -------------------- Attributes --------------------
  
  /**
   * Creates an instance of the {@link ASTCDAttribute} with the given name and
   * type and adds it to the given class
   * 
   * @param astClass
   * @param attrName
   * @param attrType
   * @return Optional of the created ast node or Optional.absent() if the
   * attribute type couldn't be parsed
   */
  public static Optional<ASTCDAttribute> addCdAttribute(ASTCDClass astClass, String attrName,
      String attrType) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(attrName));
    checkArgument(!Strings.isNullOrEmpty(attrType));
    Optional<ASTType> parsedType = createType(attrType);
    if (!parsedType.isPresent()) {
      Log.error("Attribute " + attrName + " can't be added to the CD class " + astClass.getName());
      return Optional.absent();
    }
    ASTCDAttribute attribute = ASTCDAttribute.getBuilder().name(attrName).type(parsedType.get())
        .build();
    addCdAttribute(astClass, attribute);
    return Optional.of(attribute);
  }
  
  /**
   * Creates an instance of the {@link ASTCDAttribute} with the given name and
   * type and adds it to the given class
   * 
   * @param astClass
   * @param attrName
   * @param attrType
   * @param modifier
   * @return Optional of the created ast node or Optional.absent() if the
   * attribute type couldn't be parsed
   */
  public static Optional<ASTCDAttribute> addCdAttribute(ASTCDClass astClass, String attrName,
      String attrType, String modifier) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(attrName));
    checkArgument(!Strings.isNullOrEmpty(attrType));
    Optional<ASTType> parsedType = createType(attrType);
    Optional<ASTModifier> parsedModifier = createModifier(modifier);
    if (!parsedType.isPresent() || !parsedModifier.isPresent()) {
      Log.error("Attribute " + attrName + " can't be added to the CD class " + astClass.getName());
      return Optional.absent();
    }
    ASTCDAttribute attribute = ASTCDAttribute.getBuilder().name(attrName).type(parsedType.get())
        .modifier(parsedModifier.get())
        .build();
    addCdAttribute(astClass, attribute);
    return Optional.of(attribute);
  }
  
  /**
   * Creates an instance of the {@link ASTCDAttribute} using given attribute
   * definition ( e.g. private List<A> a; ) and adds it to the given class
   * 
   * @param astClass
   * @param attributeDefinition attribute definition to parse
   * @return Optional of the created ast node or Optional.absent() if the
   * attribute definition couldn't be parsed
   */
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
  
  /**
   * Adds the given attribute to the given class
   * 
   * @param astClass
   * @param astAttribute
   */
  public static void addCdAttribute(ASTCDClass astClass, ASTCDAttribute astAttribute) {
    checkNotNull(astClass);
    checkNotNull(astAttribute);
    astClass.getCDAttributes().add(astAttribute);
  }
  
  // -------------------- Classes --------------------
  
  /**
   * Creates an instance of the {@link ASTCDClass} with the given name and adds
   * it to the given CD definition type definitions
   * 
   * @param astDef
   * @param className
   * @return created {@link ASTCDClass} node
   */
  public static ASTCDClass addCdClass(ASTCDDefinition astDef, String className) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(className));
    ASTCDClass astClass = ASTCDClass.getBuilder().name(className).build();
    addCdClass(astDef, astClass);
    return astClass;
  }
  
  /**
   * Creates an instance of the {@link ASTCDClass} with the given name, super
   * class and the list of extended interface names and adds it to the given CD
   * definition
   * 
   * @param astDef
   * @param className
   * @param superClassName
   * @param interfaceNames
   * @return Optional of the created {@link ASTCDClass} node or
   * Optional.absent() if the super class- or one of the interface-definitions
   * couldn't be parsed
   */
  public static Optional<ASTCDClass> addCdClass(ASTCDDefinition astDef, String className,
      String superClassName,
      List<String> interfaceNames) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(className));
    Optional<ASTType> superClass = createType(superClassName);
    if (!superClass.isPresent()) {
      Log.error("Class " + className + " can't be added to the CD definition.");
      return Optional.absent();
    }
    ASTReferenceTypeList interfaces = TypesNodeFactory.createASTReferenceTypeList();
    for (String paramType : interfaceNames) {
      Optional<ASTType> type = createType(paramType);
      if (!type.isPresent() || !(type.get() instanceof ASTReferenceType)) {
        Log.error("Class " + className + " can't be added to the CD definition.");
        return Optional.absent();
      }
      interfaces.add((ASTReferenceType) type.get());
    }
    ASTCDClass astClass = ASTCDClass.getBuilder().name(className)
        .superclass((ASTReferenceType) superClass.get()).interfaces(interfaces).build();
    addCdClass(astDef, astClass);
    return Optional.of(astClass);
  }
  
  /**
   * Adds the given class to the given CD definition
   * 
   * @param astDef
   * @param astClass
   */
  public static void addCdClass(ASTCDDefinition astDef, ASTCDClass astClass) {
    checkNotNull(astDef);
    checkNotNull(astClass);
    astDef.getCDClasses().add(astClass);
  }
  
  // -------------------- Interfaces --------------------
  
  /**
   * Creates an instance of the {@link ASTCDInterface} with the given name and
   * adds it to the given CD definition type definitions
   * 
   * @param astDef
   * @param interfaceName
   * @return created {@link ASTCDInterface} node
   */
  public static ASTCDInterface addCdInterface(ASTCDDefinition astDef, String interfaceName) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(interfaceName));
    ASTCDInterface astInterface = ASTCDInterface.getBuilder().name(interfaceName).build();
    addCdInterface(astDef, astInterface);
    return astInterface;
  }
  
  /**
   * Adds the given interface to the given CD definition
   * 
   * @param astDef
   * @param astInterface
   */
  public static void addCdInterface(ASTCDDefinition astDef, ASTCDInterface astInterface) {
    checkNotNull(astDef);
    checkNotNull(astInterface);
    astDef.getCDInterfaces().add(astInterface);
  }
  
  /**
   * Creates an instance of the {@link ASTCDInterface} with the given name and
   * the list of extended interface names and adds it to the given CD definition
   * 
   * @param astDef
   * @param interfaceName
   * @param interfaceNames
   * @return Optional of the created {@link ASTCDInterface} node or
   * Optional.absent() if one of the interface-definitions couldn't be parsed
   */
  public static Optional<ASTCDInterface> addCdInterface(ASTCDDefinition astDef,
      String interfaceName,
      List<String> interfaceNames) {
    checkNotNull(astDef);
    checkArgument(!Strings.isNullOrEmpty(interfaceName));
    ASTReferenceTypeList interfaces = TypesNodeFactory.createASTReferenceTypeList();
    for (String paramType : interfaceNames) {
      Optional<ASTType> type = createType(paramType);
      if (!type.isPresent() || !(type.get() instanceof ASTReferenceType)) {
        Log.error("Interface " + interfaceName + " can't be added to the CD definition.");
        return Optional.absent();
      }
      interfaces.add((ASTReferenceType) type.get());
    }
    ASTCDInterface astInterface = ASTCDInterface.getBuilder().name(interfaceName)
        .interfaces(interfaces).build();
    addCdInterface(astDef, astInterface);
    return Optional.of(astInterface);
  }
  
  // -------------------- Methods --------------------
  
  /**
   * Creates an instance of the {@link ASTCDMethod} using the given method
   * definition ( e.g. protected String getValue(Date d); ) and adds it to the
   * given class
   * 
   * @param astClass
   * @param methodDefinition method definition to parse
   * @return Optional of the created {@link ASTCDMethod} node or
   * Optional.absent() if the method definition couldn't be parsed
   */
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
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name, default
   * return type and the empty parameter list and adds it to the given class
   * 
   * @param astClass
   * @param methodName
   * @return The created {@link ASTCDMethod} node
   */
  public static ASTCDMethod addCdMethod(ASTCDClass astClass, String methodName) {
    return addCdMethod(astClass, methodName, DEFAULT_RETURN_TYPE, DEFAULT_METHOD_MODIFIER,
        Lists.newArrayList()).get();
  }
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name, return
   * type and parameter types and adds it to the given class
   * 
   * @param astClass
   * @param methodName
   * @param returnType
   * @param paramTypes
   * @return Optional of the created {@link ASTCDMethod} node or
   * Optional.absent() if the method definition couldn't be parsed
   */
  public static Optional<ASTCDMethod> addCdMethod(ASTCDClass astClass, String methodName,
      String returnType, String modifier,
      List<String> paramTypes) {
    checkNotNull(astClass);
    checkArgument(!Strings.isNullOrEmpty(methodName));
    Optional<ASTReturnType> parsedReturnType = createReturnType(returnType);
    Optional<ASTCDParameterList> cdParameters = createCdMethodParameters(paramTypes);
    Optional<ASTModifier> parsedModifier = createModifier(modifier);
    if (!parsedReturnType.isPresent() || !cdParameters.isPresent() || !parsedModifier.isPresent()) {
      Log.error("Method " + methodName + " can't be added to the CD class " + astClass.getName());
      return Optional.absent();
    }
    ASTCDMethod cdMethod = ASTCDMethod.getBuilder()
        .name(methodName)
        .returnType(parsedReturnType.get())
        .modifier(parsedModifier.get())
        .cDParameters(cdParameters.get())
        .build();
    addCdMethod(astClass, cdMethod);
    return Optional.of(cdMethod);
  }
  
  /**
   * Adds the given method to the given class
   * 
   * @param astClass
   * @param astMethod
   */
  public static void addCdMethod(ASTCDClass astClass, ASTCDMethod astMethod) {
    checkNotNull(astClass);
    checkNotNull(astMethod);
    astClass.getCDMethods().add(astMethod);
  }
  
  /**
   * Creates an instance of the {@link ASTCDParameterList} using the list of the
   * type definitions
   * 
   * @param paramTypes
   * @return Optional of the created {@link ASTCDParameterList} node or
   * Optional.absent() if one of the type definition couldn't be parsed
   */
  public static Optional<ASTCDParameterList> createCdMethodParameters(List<String> paramTypes) {
    checkNotNull(paramTypes);
    ASTCDParameterList params = CD4AnalysisNodeFactory.createASTCDParameterList();
    List<ASTType> types = Lists.newArrayList();
    for (String paramType : paramTypes) {
      Optional<ASTType> type = createType(paramType);
      if (!type.isPresent()) {
        return Optional.absent();
      }
      types.add(type.get());
    }
    types.forEach(param -> params.add(ASTCDParameter.getBuilder()
        .type(param)
        .name(PARAM_NAME_PREFIX + types.indexOf(param)).build()));
    return Optional.of(params);
  }
  
  // -------------------- Types --------------------
  
  /**
   * Creates an instance of the {@link ASTReturnType} using the type definition
   * 
   * @param typeName
   * @return Optional of the created {@link ASTReturnType} node or
   * Optional.absent() if the type definition couldn't be parsed
   */
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
  
  /**
   * Creates an instance of the {@link ASTType} using the type definition
   * 
   * @param typeName
   * @return Optional of the created {@link ASTType} node or Optional.absent()
   * if the type definition couldn't be parsed
   */
  public static Optional<ASTType> createType(String typeName) {
    checkArgument(!Strings.isNullOrEmpty(typeName));
    Optional<ASTType> astType = Optional.absent();
    try {
      astType = new TypeMCParser().parse(new StringReader(typeName));
      if (!astType.isPresent()) {
        Log.error("The type " + typeName + " wasn't defined correctly");
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("The type  " + typeName + " wasn't defined correctly: "
          + "\nCatched exception: " + e);
    }
    return astType;
  }
  
  /**
   * Creates an instance of the {@link ASTModifier} using the modifier
   * definition
   * 
   * @param modifier
   * @return Optional of the created {@link ASTModifier} node or
   * Optional.absent() if the type definition couldn't be parsed
   */
  public static Optional<ASTModifier> createModifier(String modifier) {
    checkArgument(!Strings.isNullOrEmpty(modifier));
    Optional<ASTModifier> astModifier = Optional.absent();
    try {
      astModifier = new ModifierMCParser().parse(new StringReader(modifier));
      if (!astModifier.isPresent()) {
        Log.error("The modifier " + modifier + " wasn't defined correctly");
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("The modifier  " + modifier + " wasn't defined correctly: "
          + "\nCatched exception: " + e);
    }
    return astModifier;
  }
  
}
