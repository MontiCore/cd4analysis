/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package transformation.ast;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
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
   * @return Optional of the created ast node or Optional.empty() if the
   * attribute type couldn't be parsed
   */
  public Optional<ASTCDAttribute> addCdAttribute(ASTCDClass astClass, String attrName,
      String attrType) {
    checkArgument(!Strings.isNullOrEmpty(attrName),
        "Attribute can't be added to the CD class because of null or empty attribute name");
    checkNotNull(astClass, "Attribute '" + attrName
        + "' can't be added to the CD class because of null reference to the class");
    checkArgument(!Strings.isNullOrEmpty(attrType), "Attribute '" + attrName
        + "' can't be added to the CD class because of null or empty attribute type");
    Optional<ASTType> parsedType = createType(attrType);
    if (!parsedType.isPresent()) {
      Log.error("Attribute '" + attrName + "' can't be added to the CD class " + astClass.getName());
      return Optional.empty();
    }
    ASTCDAttribute attribute = CD4AnalysisMill.cDAttributeBuilder().name(attrName).type(parsedType.get())
        .build();
    addCdAttribute(astClass, attribute);
    return Optional.of(attribute);
  }
  
  /**
   * Creates an instance of the {@link ASTCDAttribute} with the given name, type
   * and modifier and adds it to the given class
   * 
   * @param astClass
   * @param attrName
   * @param attrType
   * @param modifier
   * @return Optional of the created ast node or Optional.empty() if the
   * attribute type couldn't be parsed
   */
  public Optional<ASTCDAttribute> addCdAttribute(ASTCDClass astClass, String attrName,
      String attrType, String modifier) {
    checkArgument(!Strings.isNullOrEmpty(attrName),
        "Attribute can't be added to the CD class because of null or empty attribute name");
    checkNotNull(astClass, "Attribute '" + attrName
        + "' can't be added to the CD class because of null reference to the class");
    checkArgument(!Strings.isNullOrEmpty(attrType), "Attribute '" + attrName
        + "' can't be added to the CD class because of null or empty attribute type");
    checkArgument(!Strings.isNullOrEmpty(modifier), "Attribute '" + attrName
        + "' can't be added to the CD class because of null or empty modifier");
    Optional<ASTType> parsedType = createType(attrType);
    Optional<ASTModifier> parsedModifier = createModifier(modifier);
    if (!parsedType.isPresent() || !parsedModifier.isPresent()) {
      Log.error("Attribute " + attrName + " can't be added to the CD class " + astClass.getName());
      return Optional.empty();
    }
    ASTCDAttribute attribute = CD4AnalysisMill.cDAttributeBuilder().name(attrName).type(parsedType.get())
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
   * @return Optional of the created ast node or Optional.empty() if the
   * attribute definition couldn't be parsed
   */
  public Optional<ASTCDAttribute> addCdAttributeUsingDefinition(ASTCDClass astClass,
      String attributeDefinition) {
    checkArgument(!Strings.isNullOrEmpty(attributeDefinition),
        "Attribute can't be added to the CD class because of null or empty attribute definition");
    checkNotNull(astClass, "Attribute '" + attributeDefinition
        + "' can't be added to the CD class because of null reference to the class");
    Optional<ASTCDAttribute> astAttribute = Optional.empty();
    try {
      astAttribute = (new CD4AnalysisParser()).parseCDAttribute(new StringReader(attributeDefinition));
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
   * Creates an instance of the {@link ASTCDAttribute} using given attribute
   * definition ( e.g. private List<A> a; ) and adds it to the given interface
   * 
   * @param astInterface
   * @param attributeDefinition attribute definition to parse
   * @return Optional of the created ast node or Optional.empty() if the
   * attribute definition couldn't be parsed
   */
  public Optional<ASTCDAttribute> addCdAttributeUsingDefinition(ASTCDInterface astInterface,
      String attributeDefinition) {
    checkArgument(!Strings.isNullOrEmpty(attributeDefinition),
        "Attribute can't be added to the CD interface because of null or empty attribute definition");
    checkNotNull(astInterface, "Attribute '" + attributeDefinition
        + "' can't be added to the CD interface because of null reference to the interface");
    Optional<ASTCDAttribute> astAttribute = Optional.empty();
    try {
      astAttribute = (new CD4AnalysisParser()).parseCDAttribute(new StringReader(attributeDefinition));
      if (!astAttribute.isPresent()) {
        Log.error("Attribute can't be added to the CD interface " + astInterface.getName()
            + "\nWrong attribute definition: " + attributeDefinition);
      }
      else {
        addCdAttribute(astInterface, astAttribute.get());
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Attribute can't be added to the CD interface " + astInterface.getName()
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
  public void addCdAttribute(ASTCDClass astClass, ASTCDAttribute astAttribute) {
    checkNotNull(
        astAttribute,
        "ASTCDAttribute attribute node can't be added to the CD class because of null reference to the added node");
    checkNotNull(astClass, "Attribute '" + astAttribute.getName()
        + "' can't be added to the CD class because of null reference to the class");
    checkNotNull(astAttribute);
    astClass.getCDAttributeList().add(astAttribute);
  }
  
  /**
   * Adds the given attribute to the given interface
   * 
   * @param astInterface
   * @param astAttribute
   */
  public void addCdAttribute(ASTCDInterface astInterface, ASTCDAttribute astAttribute) {
    checkNotNull(
        astAttribute,
        "ASTCDAttribute attribute node can't be added to the CD interface because of null reference to the added node");
    checkNotNull(astInterface, "Attribute '" + astAttribute.getName()
        + "' can't be added to the CD interface because of null reference to the class");
    checkNotNull(astAttribute);
    astInterface.getCDAttributeList().add(astAttribute);
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
  public ASTCDClass addCdClass(ASTCDDefinition astDef, String className) {
    checkArgument(!Strings.isNullOrEmpty(className),
        "Class can't be added to the CD definition because of null or empty class name");
    checkNotNull(astDef, "Class " + className
        + " can't be added to the CD definition because of the null reference to the CD definition");
    ASTCDClass astClass = CD4AnalysisMill.cDClassBuilder().name(className).build();
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
   * Optional.empty() if the super class- or one of the interface-definitions
   * couldn't be parsed
   */
  public Optional<ASTCDClass> addCdClass(ASTCDDefinition astDef, String className,
      String superClassName,
      List<String> interfaceNames) {
    checkArgument(!Strings.isNullOrEmpty(className),
        "Class can't be added to the CD definition because of null or empty class name");
    checkNotNull(astDef, "Class " + className
        + " can't be added to the CD definition because of the null reference to the CD definition");
    
    Optional<ASTType> superClass = createType(superClassName);
    if (!superClass.isPresent()) {
      Log.error("Class " + className + " can't be added to the CD definition.");
      return Optional.empty();
    }
    List<ASTReferenceType> interfaces = new ArrayList<>();
    for (String paramType : interfaceNames) {
      Optional<ASTType> type = createType(paramType);
      if (!type.isPresent() || !(type.get() instanceof ASTReferenceType)) {
        Log.error("Class " + className + " can't be added to the CD definition.");
        return Optional.empty();
      }
      interfaces.add((ASTReferenceType) type.get());
    }
    ASTCDClass astClass = CD4AnalysisMill.cDClassBuilder().name(className)
        .superclass((ASTReferenceType) superClass.get()).interfaces(interfaces).build();
    addCdClass(astDef, astClass);
    return Optional.of(astClass);
  }
  
  /**
   * Creates an instance of the {@link ASTCDMethod} using the given method
   * definition ( e.g. protected String getValue(Date d); ) and adds it to the
   * given class
   * 
   * @param astCd
   * @param classDefinition method definition to parse
   * @return Optional of the created {@link ASTCDMethod} node or
   * Optional.empty() if the method definition couldn't be parsed
   */
  public Optional<ASTCDClass> addCdClassUsingDefinition(ASTCDDefinition astCd,
      String classDefinition) {
    checkArgument(!Strings.isNullOrEmpty(classDefinition),
        "Class can't be added to the classdiagram because of null or empty class definition");
    checkNotNull(astCd, "Class '" + classDefinition
        + "' can't be added to the classdiagram because of null reference to the classdiagram");
    Optional<ASTCDClass> astClass = Optional.empty();
    try {
      astClass = (new CD4AnalysisParser()).parseCDClass(new StringReader(classDefinition));
      if (!astClass.isPresent()) {
        Log.error("Method can't be added to the CD class " + astCd.getName()
            + "\nWrong method definition: " + classDefinition);
      }
      else {
        addCdClass(astCd, astClass.get());
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Method can't be added to the CD class " + astCd.getName()
          + "\nCatched exception: " + e);
    }
    return astClass;
  }
  
  /**
   * Adds the given class to the given CD definition
   * 
   * @param astDef
   * @param astClass
   */
  public void addCdClass(ASTCDDefinition astDef, ASTCDClass astClass) {
    checkNotNull(
        astClass,
        "ASTCDClass node can't be added to the CD class because of null reference to the added node");
    checkNotNull(astDef, "Class " + astClass.getName()
        + " can't be added to the CD definition because of the null reference to the CD definition");
    checkNotNull(astDef);
    checkNotNull(astClass);
    astDef.getCDClassList().add(astClass);
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
  public ASTCDInterface addCdInterface(ASTCDDefinition astDef, String interfaceName) {
    checkArgument(!Strings.isNullOrEmpty(interfaceName),
        "Interface can't be added to the CD definition because of null or empty interface name");
    checkNotNull(astDef, "Interface " + interfaceName
        + " can't be added to the CD definition because of the null reference to the CD definition");
    ASTCDInterface astInterface = CD4AnalysisMill.cDInterfaceBuilder().name(interfaceName).build();
    addCdInterface(astDef, astInterface);
    return astInterface;
  }
  
  /**
   * Adds the given interface to the given CD definition
   * 
   * @param astDef
   * @param astInterface
   */
  public void addCdInterface(ASTCDDefinition astDef, ASTCDInterface astInterface) {
    checkNotNull(
        astInterface,
        "ASTCDInterface node can't be added to the CD class because of null reference to the added node");
    checkNotNull(astDef, "Interface " + astInterface.getName()
        + " can't be added to the CD definition because of the null reference to the CD definition");
    astDef.getCDInterfaceList().add(astInterface);
  }
  
  /**
   * Creates an instance of the {@link ASTCDInterface} with the given name and
   * the list of extended interface names and adds it to the given CD definition
   * 
   * @param astDef
   * @param interfaceName
   * @param interfaceNames
   * @return Optional of the created {@link ASTCDInterface} node or
   * Optional.empty() if one of the interface-definitions couldn't be parsed
   */
  public Optional<ASTCDInterface> addCdInterface(ASTCDDefinition astDef,
      String interfaceName,
      List<String> interfaceNames) {
    checkArgument(!Strings.isNullOrEmpty(interfaceName),
        "Interface can't be added to the CD definition because of null or empty interface name");
    checkNotNull(astDef, "Interface " + interfaceName
        + " can't be added to the CD definition because of the null reference to the CD definition");
    List<ASTReferenceType> interfaces = new ArrayList<>();
    for (String paramType : interfaceNames) {
      Optional<ASTType> type = createType(paramType);
      if (!type.isPresent() || !(type.get() instanceof ASTReferenceType)) {
        Log.error("Interface " + interfaceName + " can't be added to the CD definition.");
        return Optional.empty();
      }
      interfaces.add((ASTReferenceType) type.get());
    }
    ASTCDInterface astInterface = CD4AnalysisMill.cDInterfaceBuilder().name(interfaceName)
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
   * @param astType
   * @param methodDefinition method definition to parse
   * @return Optional of the created {@link ASTCDMethod} node or
   * Optional.empty() if the method definition couldn't be parsed
   */
  public Optional<ASTCDMethod> addCdMethodUsingDefinition(ASTCDType astType,
      String methodDefinition) {
    checkArgument(!Strings.isNullOrEmpty(methodDefinition),
        "Method can't be added to the CD class because of null or empty method definition");
    checkNotNull(astType, "Method '" + methodDefinition
        + "' can't be added to the CD class because of null reference to the class");
    Optional<ASTCDMethod> astMethod = Optional.empty();
    try {
      astMethod = (new CD4AnalysisParser()).parseCDMethod(new StringReader(methodDefinition));
      if (!astMethod.isPresent()) {
        Log.error("Method can't be added to the CD class " + astType.getName()
            + "\nWrong method definition: " + methodDefinition);
      }
      else {
        addCdMethod(astType, astMethod.get());
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("Method can't be added to the CD class " + astType.getName()
          + "\nCatched exception: " + e);
    }
    return astMethod;
  }
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name, default
   * return type and the empty parameter list and adds it to the given type
   * 
   * @param astClass
   * @param methodName
   * @return The created {@link ASTCDMethod} node
   */
  public ASTCDMethod addCdMethod(ASTCDType astType, String methodName) {
    return addCdMethod(astType, methodName, DEFAULT_RETURN_TYPE, DEFAULT_METHOD_MODIFIER,
        Lists.newArrayList()).get();
  }
  
  /**
   * Creates an instance of the {@link ASTCDMethod} with the given name, return
   * type and parameter types and adds it to the given class
   * 
   * @param astType
   * @param methodName
   * @param returnType
   * @param paramTypes
   * @return Optional of the created {@link ASTCDMethod} node or
   * Optional.empty() if the method definition couldn't be parsed
   */
  public Optional<ASTCDMethod> addCdMethod(ASTCDType astType, String methodName,
      String returnType, String modifier,
      List<String> paramTypes) {
    checkNotNull(astType);
    checkArgument(!Strings.isNullOrEmpty(methodName));
    Optional<ASTReturnType> parsedReturnType = createReturnType(returnType);
    Optional<List<ASTCDParameter>> cdParameters = createCdMethodParameters(paramTypes);
    Optional<ASTModifier> parsedModifier = createModifier(modifier);
    if (!parsedReturnType.isPresent() || !cdParameters.isPresent() || !parsedModifier.isPresent()) {
      Log.error("Method " + methodName + " can't be added to the CD class " + astType.getName());
      return Optional.empty();
    }
    ASTCDMethod cdMethod = CD4AnalysisMill.cDMethodBuilder()
        .name(methodName)
        .returnType(parsedReturnType.get())
        .modifier(parsedModifier.get())
        .cDParameters(cdParameters.get())
        .build();
    addCdMethod(astType, cdMethod);
    return Optional.of(cdMethod);
  }
  
  /**
   * Adds the given method to the given type
   * 
   * @param astType
   * @param astMethod
   */
  public void addCdMethod(ASTCDType astType, ASTCDMethod astMethod) {
    checkNotNull(
        astMethod,
        "ASTCDMethod method node can't be added to the CD class because of null reference to the added node");
    checkNotNull(astType, "Method '" + astMethod.getName()
        + "' can't be added to the CD class because of null reference to the class");
    astType.getCDMethodList().add(astMethod);
  }
  
  /**
   * Creates an instance of the {@link ASTCDParameterList} using the list of the
   * type definitions
   * 
   * @param paramTypes
   * @return Optional of the created {@link ASTCDParameterList} node or
   * Optional.empty() if one of the type definition couldn't be parsed
   */
  public Optional<List<ASTCDParameter>> createCdMethodParameters(List<String> paramTypes) {
    checkNotNull(paramTypes,
        "AST parameters node can't be created: the list of the given type names is null");
    List<ASTCDParameter> params = Lists.newArrayList();
    List<ASTType> types = Lists.newArrayList();
    for (String paramType : paramTypes) {
      Optional<ASTType> type = createType(paramType);
      if (!type.isPresent()) {
        return Optional.empty();
      }
      types.add(type.get());
    }
    types.forEach(param -> params.add(CD4AnalysisMill.cDParameterBuilder()
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
   * Optional.empty() if the type definition couldn't be parsed
   */
  public Optional<ASTReturnType> createReturnType(String typeName) {
    checkArgument(!Strings.isNullOrEmpty(typeName),
        "AST return type node can't be created because of null or empty return type definition");
    Optional<ASTReturnType> astType = Optional.empty();
    try {
      astType = (new CD4AnalysisParser()).parseReturnType(new StringReader(typeName));
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
   * @return Optional of the created {@link ASTType} node or Optional.empty()
   * if the type definition couldn't be parsed
   */
  public Optional<ASTType> createType(String typeName) {
    checkArgument(!Strings.isNullOrEmpty(typeName),
        "AST type node can't be created because of null or empty type definition");
    Optional<ASTType> astType = Optional.empty();
    try {
      astType = (new CD4AnalysisParser()).parseType(new StringReader(typeName));
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
   * Optional.empty() if the type definition couldn't be parsed
   */
  public Optional<ASTModifier> createModifier(String modifier) {
    checkArgument(!Strings.isNullOrEmpty(modifier),
        "AST node for the modfier definition can't be created because of null or empty modifier definition");
    Optional<ASTModifier> astModifier = Optional.empty();
    try {
      astModifier = (new CD4AnalysisParser()).parseModifier(new StringReader(modifier));
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
