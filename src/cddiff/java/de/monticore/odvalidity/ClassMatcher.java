/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.odattribute._ast.ASTODList;
import de.monticore.odbasis._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class ClassMatcher {

  // TODO ODMapElement in attributes https://github
  // .com/MontiCore/object-diagram/blob/dev/src/main/grammars/de/monticore/ODAttribute.mc4

  protected ASTODArtifact od;

  protected ASTCDCompilationUnit cd;

  protected ICD4CodeArtifactScope scope;

  protected CDSemantics semantics;

  /**
   * Checks whether all objects are valid in CD
   *
   * @param semantics Open/Closed World
   * @return true, if all objects are valid in CD for the given semantics
   */
  public boolean checkAllObjectsInClassDiagram(
      ASTODArtifact od, ASTCDCompilationUnit cd, CDSemantics semantics) {

    this.od = od;
    this.cd = cd;
    this.scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    this.semantics = semantics;

    // Set all parameters
    List<ASTODObject> odObjects = ODHelper.getAllObjects(od.getObjectDiagram());

    Set<ASTODObject> objectSet = new HashSet<>(odObjects);

    if (cd.getCDDefinition().getCDClassesList().stream()
        .filter(c -> c.getModifier().isPresentStereotype())
        .filter(c -> c.getModifier().getStereotype().contains("singleton"))
        .anyMatch(c -> objectSet.stream().filter(o -> isInstanceOf(o, c)).count() != 1)) {
      return false;
    }

    if (cd.getCDDefinition().getCDInterfacesList().stream()
        .filter(i -> i.getModifier().isPresentStereotype())
        .filter(i -> i.getModifier().getStereotype().contains("singleton"))
        .anyMatch(i -> objectSet.stream().filter(o -> isInstanceOf(o, i)).count() != 1)) {
      return false;
    }

    // Check all objects from OD if they can exist in the CD
    for (ASTODObject obj : odObjects) {

      String objectType = obj.getMCObjectType().printType();
      Optional<ASTCDClass> optClass = getCDClassOfType(objectType);

      if (Semantic.isClosedWorld(semantics) && optClass.isEmpty()) {
        Log.println("[CONFLICT] Could not find class: " + objectType);
        return false;
      } else if (optClass.isPresent()
          && (optClass.get().getModifier().isAbstract()
              || !isObjectValid4Class(obj, optClass.get(), semantics))) {
        Log.println(
            String.format(
                "[CONFLICT] Object %s is not valid for Class %s",
                obj.getName(), optClass.get().getName()));
        return false;
      }

      if (semantics.equals(CDSemantics.MULTI_INSTANCE_OPEN_WORLD)) {
        Optional<Set<String>> optSuper = MultiInstanceMatcher.getSuperSetFromStereotype(obj);
        if (optSuper.isPresent()) {
          for (String type : optSuper.get()) {
            if (!optSuper.get().containsAll(MultiInstanceMatcher.getSuperSet(type, scope))) {
              return false;
            }
            Optional<ASTCDClass> optType = getCDClassOfType(type);
            if (optType.isPresent()) {
              if (optClass.isPresent()
                  && !optType.equals(optClass)
                  && CDInheritanceHelper.isSuperOf(objectType, type, scope)) {
                return false;
              }
              if (!isObjectValid4Class(obj, optType.get(), semantics)) {
                return false;
              }
            }
          }
        }
      }
    }

    return true;
  }

  /** Check if object is instance of type. */
  private boolean isInstanceOf(ASTODObject object, ASTCDType type) {

    // check the intanceof-stereotype iff semantics is multi-instance open-world
    if (Semantic.isMultiInstance(semantics)) {
      Optional<Set<String>> optSuper = MultiInstanceMatcher.getSuperSetFromStereotype(object);
      if (optSuper.isPresent()) {
        return optSuper.get().contains(type.getSymbol().getInternalQualifiedName());
      }
    }
    return CDInheritanceHelper.isSuperOf(
        type.getSymbol().getInternalQualifiedName(), object.getMCObjectType().printType(), scope);
  }

  /**
   * Checks whether an object is valid in a class
   *
   * @param obj Object of OD
   * @param cdClass Class of CD
   * @return true, if object is valid in class
   */
  private boolean isObjectValid4Class(ASTODObject obj, ASTCDClass cdClass, CDSemantics semantics) {

    Set<ASTCDAttribute> superAttributes = buildClassAttributeList(cdClass);
    Log.println("Attributes of " + cdClass.getName() + ": ");
    superAttributes.forEach(attribute -> Log.print(attribute.getName() + " "));
    Log.print(System.lineSeparator());

    for (ASTCDAttribute cdAttribute : superAttributes) {
      boolean attributeMissing = true;
      for (ASTODAttribute odAttribute : obj.getODAttributeList()) {
        // Compare attributes: By name, visibility and type. When no type in OD given then by AST
        // node
        if (cdAttribute.getName().equals(odAttribute.getName())) {
          if (!(isAttributeVisibilityEqual(odAttribute, cdAttribute)
              && areAttributesSemanticallyEqual(odAttribute, cdAttribute))) {
            Log.println(
                odAttribute.getName()
                    + " does not instantiate "
                    + cdAttribute.getName()
                    + " correctly.");
            return false;
          }
          attributeMissing = false;
        }
      }
      if (attributeMissing) {
        Log.println("Could not find: " + cdAttribute.getName());
        // TODO: find in link
        return false;
      }
    }

    if (Semantic.isClosedWorld(semantics)) {
      return areObjectAttributesValidInClass(obj.getODAttributeList(), superAttributes);
    }

    return true;
  }

  /**
   * Builds the attribute list of a CD class also by considering the attribute list of super classes
   *
   * @param type Class of CD
   * @return List of class attributes
   */
  private Set<ASTCDAttribute> buildClassAttributeList(ASTCDType type) {

    Set<ASTCDAttribute> attrSet = new HashSet<>(type.getCDAttributeList());
    for (ASTCDType superType : CDInheritanceHelper.getAllSuper(type, scope)) {
      attrSet.addAll(superType.getCDAttributeList());
    }
    return attrSet;
  }

  /**
   * Checks if all object attributes are valid in class. Check also associations for object
   * attributes which are not defined in class attribute list
   *
   * @param odAttributes Attributes of OD object
   * @param cdAttributes Attributes of CD class
   * @return true, if all object attributes are valid in class
   */
  private boolean areObjectAttributesValidInClass(
      Collection<ASTODAttribute> odAttributes, Collection<ASTCDAttribute> cdAttributes) {
    // Given: All object attributes, all attributes of matching class
    // Check if all object attributes are also attributes in the class
    for (ASTODAttribute odAttr : odAttributes) {
      boolean odAttrFoundInCD = false;
      for (ASTCDAttribute cdAttr : cdAttributes) {
        // Compare attributes: By name, visibility and type. When no type in OD given then by AST
        // node
        if (odAttr.getName().equals(cdAttr.getName())
            && isAttributeVisibilityEqual(odAttr, cdAttr)
            && areAttributesSemanticallyEqual(odAttr, cdAttr)) {
          // We found a matching attribute
          odAttrFoundInCD = true;
          break;
        }
      }
      if (!odAttrFoundInCD) {
        // If we didn't find a matching attribute in class then we check if it exists in the
        // associations
        ASTCDAssociation cdAssociation = getCDAssociationOfODObjectAttribute(odAttr);
        return cdAssociation != null;
      }
    }

    return true;
  }

  /**
   * Checks whether an object attribute and a class attribute are semantically equal by comparing
   * the name, visibility and type. Only for enums the value will be checked
   *
   * @param odAttr Attribute of OD object
   * @param cdAttr Attribute of CD class
   * @return true, if both attributes are equal
   */
  private boolean areAttributesSemanticallyEqual(ASTODAttribute odAttr, ASTCDAttribute cdAttr) {

    // Check names and visibility
    if (!cdAttr.getName().equals(odAttr.getName()) && isAttributeVisibilityEqual(odAttr, cdAttr)) {
      return false;
    }
    // Check if they have the same name and type
    String cdAttrType = cdAttr.getMCType().printType();
    String odAttrType = getObjectAttributeType(odAttr);

    // Handle enums and another objects
    if (isNameEnumInCD(cdAttrType) || isNameClassInCD(cdAttrType)) {
      return validateNameExpression(odAttr.getODValue(), cdAttrType);
    }

    // Handle lists
    if ((odAttr.getODValue() instanceof ASTODList) && cdAttr.getMCType() instanceof ASTMCListType) {
      // Handle special case that list has no elements
      var elementList = ((ASTODList) odAttr.getODValue()).getODValueList();
      if (elementList.size() == 0 && cdAttr.getMCType() instanceof ASTMCListType) {
        // When the list attribute of the object has no elements then we have no chance to
        // determine the list element type
        // Just say it is valid because we can not contradict it
        return true;
      }

      // Check if types are correct
      if (!cdAttrType.equals(odAttrType)) {
        return false;
      }
      // Check if all list elements have the same type
      String listElementType = ((ASTMCListType) cdAttr.getMCType()).getMCTypeArgument().printType();
      for (var element : ((ASTODList) odAttr.getODValue()).getODValueList()) {
        // Compare list element type to with type of all elements in object list attribute
        if (!listElementType.equals(getObjectAttributeTypeByAST(element))) {
          return false;
        }
      }
    }

    // Check types
    return cdAttrType.equals(odAttrType);
  }

  /**
   * Checks if visibility of object and class attribute is equal
   *
   * @param odAttr Attribute of OD object
   * @param cdAttr Attribute of CD class
   * @return true, if visibility is equal
   */
  private boolean isAttributeVisibilityEqual(ASTODAttribute odAttr, ASTCDAttribute cdAttr) {
    return (odAttr.getModifier().isPrivate() == cdAttr.getModifier().isPrivate()
        && odAttr.getModifier().isPublic() == cdAttr.getModifier().isPublic()
        && odAttr.getModifier().isProtected() == cdAttr.getModifier().isProtected()
        && odAttr.getModifier().isStatic() == cdAttr.getModifier().isStatic());
  }

  /**
   * Determines the class association where object attribute is defined
   *
   * @param odAttr Attribute of OD object
   * @return CD association
   */
  private ASTCDAssociation getCDAssociationOfODObjectAttribute(ASTODAttribute odAttr) {
    for (var cdAssociation : cd.getCDDefinition().getCDAssociationsList()) {
      if (cdAssociation.isPresentName()) {
        String associationName = cdAssociation.getName();
        if (associationName.equals(odAttr.getName())) {
          return cdAssociation;
        }
      }
    }
    return null;
  }

  /**
   * Checks if the given name is a defined object in OD
   *
   * @param objectName Object name
   * @return true, if the given name is the name of a defined objects
   */
  private boolean isObjectInList(String objectName) {
    for (var obj : ODHelper.getAllObjects(od.getObjectDiagram())) {
      if (obj instanceof ASTODNamedObject && obj.getName().equals(objectName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether given OD value is of the given type
   *
   * @param odAttrValue Type of OD object attribute
   * @param type object/class type
   * @return true iff odAttrValue is of given type
   */
  private boolean isAttributeValidForType(ASTODValue odAttrValue, String type) {
    return (isODAttributeList(odAttrValue) && listTypeValidation((ASTODList) odAttrValue, type))
        || (isTypePrimitive(type)
            && primitiveTypeValidationByAST((ASTODSimpleAttributeValue) odAttrValue, type))
        || validateNameExpression(odAttrValue, type);
  }

  /**
   * Checks whether OD value is of type ASTODList
   *
   * @param odAttrValue Attribute value of OD object
   * @return iff odAttrValue is of type ASTODList
   */
  private boolean isODAttributeList(ASTODValue odAttrValue) {
    return odAttrValue instanceof ASTODList;
  }

  /**
   * Validates whether all list elements are of the given type
   *
   * @param odAttrList List of OD object values
   * @param type Type
   * @return true iff all elements in odAttrList are of given type
   */
  private boolean listTypeValidation(ASTODList odAttrList, String type) {
    // Check if each element in list has the given type
    for (var odAttrListElement : odAttrList.getODValueList()) {
      if (!isAttributeValidForType(odAttrListElement, type)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if given type is a primitive type. For simplicity, we only consider String, int, double,
   * float and long
   */
  private boolean isTypePrimitive(String typeName) {
    return typeName.equals("String")
        || typeName.equals("int")
        || typeName.equals("float")
        || typeName.equals("double")
        || typeName.equals("long")
        || typeName.equals("boolean");
  }

  /**
   * Validate object attribute value for primitive type by using the type of the AST node
   *
   * @param odAttrValue Attribute value of OD object
   * @param cdAttrType Attribute type of CD class
   * @return true iff ASTNode-Type of odAttrValue fits cdAttrType
   */
  private boolean primitiveTypeValidationByAST(
      ASTODSimpleAttributeValue odAttrValue, String cdAttrType) {
    if (odAttrValue.getExpression() instanceof ASTLiteralExpression) {
      ASTLiteralExpression odAttrValueExpression =
          (ASTLiteralExpression) odAttrValue.getExpression();
      var odAttrValueLiteral = odAttrValueExpression.getLiteral();

      if (odAttrValueLiteral instanceof ASTNatLiteral) {
        // Natural number, integer or long
        return cdAttrType.equals("int") || cdAttrType.equals("long");
      } else if (odAttrValueLiteral instanceof ASTStringLiteral) {
        return cdAttrType.equals("String");
      } else if (odAttrValueLiteral instanceof ASTBasicDoubleLiteral) {
        return cdAttrType.equals("double") || cdAttrType.equals("float");
      } else if (odAttrValueLiteral instanceof ASTBooleanLiteral) {
        return cdAttrType.equals("boolean");
      }
    }
    return false;
  }

  /**
   * Return CD class AST node for a given class name
   *
   * @param name Class name
   */
  private Optional<ASTCDClass> getCDClassOfType(String name) {
    return cd.getCDDefinition().getCDClassesList().stream()
        .filter(cdClass -> cdClass.getSymbol().getInternalQualifiedName().equals(name))
        .findAny();
  }

  /**
   * Checks if the given name is a class in CD
   *
   * @param className Class name
   */
  private boolean isNameClassInCD(String className) {
    for (var cdClass : cd.getCDDefinition().getCDClassesList()) {
      // Check if we can find
      if (cdClass.getSymbol().getInternalQualifiedName().contains(className)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the given name is an enumeration in CD
   *
   * @param name Enum name
   */
  private boolean isNameEnumInCD(String name) {
    for (var cdEnum : cd.getCDDefinition().getCDEnumsList()) {
      // Check if we can find
      if (cdEnum.getSymbol().getInternalQualifiedName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /** Returns AST enum node for given name */
  private Optional<ASTCDEnum> getEnum(String enumName) {
    return cd.getCDDefinition().getCDEnumsList().stream()
        .filter(cdEnum -> cdEnum.getSymbol().getInternalQualifiedName().equals(enumName))
        .findAny();
  }

  /**
   * Checks if the given value is defined in CD enum
   *
   * @param cdEnum CD Enumeration
   * @param value Enumeration value
   */
  private boolean validateEnumValue(ASTCDEnum cdEnum, String value) {
    if (Semantic.isClosedWorld(semantics)) {
      for (var cdEnumMember : cdEnum.getCDEnumConstantList()) {
        if (cdEnumMember.getName().equals(value)) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  /**
   * Validates a name expression value node. Tries validation as enum and object name. If value is
   * not an enum value than it can also be an object name. Check if object name exist in object list
   *
   * @param odAttrValue Attribute value of OD object
   * @param cdAttrType Attribute type of CD class
   * @return true, if value is a valid enum or object name
   */
  private boolean validateNameExpression(ASTODValue odAttrValue, String cdAttrType) {
    if (odAttrValue instanceof ASTODName) {
      // It can be an enum or another object
      var odAttrValueLiteral = ((ASTODName) odAttrValue).getName(); // Enum value but not type name
      Optional<ASTCDEnum> optEnum = getEnum(cdAttrType);
      if (optEnum.isPresent()) {
        return validateEnumValue(optEnum.get(), odAttrValueLiteral);
      } else if (isNameClassInCD(cdAttrType)) {
        // Our literal could be the name of another object, check if it is in the object list
        // We do not need to validate the object here because it will be validated by the algorithm
        // automatically
        return isObjectInList(odAttrValueLiteral);
      }
    }

    // In an older version, the AST parser throws here an ASTODSimpleAttributeValue node but now
    // it is a ASTODName ... let's keep this here, maybe we still need it but probably it is
    // redundant
    if (odAttrValue instanceof ASTODSimpleAttributeValue) {
      // It can be an enum or another object
      var value = (ASTODSimpleAttributeValue) odAttrValue;
      if (value.getExpression() instanceof ASTNameExpression) {
        ASTNameExpression odAttrValueExpression = (ASTNameExpression) value.getExpression();
        var odAttrValueLiteral = odAttrValueExpression.getName(); // Enum value but not type name
        Optional<ASTCDEnum> optEnum = getEnum(cdAttrType);
        if (optEnum.isPresent()) {
          return validateEnumValue(optEnum.get(), odAttrValueLiteral);
        } else if (isNameClassInCD(cdAttrType)) {
          // Our literal could be the name of another object, check if it is in the object list
          // We do not need to validate the object here because it will be validated by the
          // algorithm
          // automatically
          return isObjectInList(odAttrValueLiteral);
        }
      }
    }

    return false;
  }

  /**
   * Determines the type of an object attribute. Returns type when it is given in OD attribute OR
   * determines it by AST node analysis
   *
   * @param odAttr Attribute of OD object
   * @return Type name
   */
  private String getObjectAttributeType(ASTODAttribute odAttr) {
    if (odAttr.isPresentMCType() && odAttr.getMCType() != null) {
      String type = odAttr.getMCType().printType();
      if (type != null && !type.equals("")) {
        return type;
      }
    }
    if (odAttr.getODValue() != null) {
      String type = getObjectAttributeTypeByAST(odAttr.getODValue());
      Log.println("Type of " + odAttr + " was inferred to be: " + type);
      return type;
    }
    return null;
  }

  /** Determines the OD object attribute type by AST node analysis of attribute value */
  private String getObjectAttributeTypeByAST(ASTODValue odAttrValue) {
    if (isODAttributeList(odAttrValue)) {
      var elementList = ((ASTODList) odAttrValue).getODValueList();
      if (elementList.size() == 0) {
        return "";
      }
      String listType = getObjectAttributeTypeByAST(((ASTODList) odAttrValue).getODValue(0));
      if (listType == null) {
        return null;
      }
      return "List<" + listType + ">";
    }
    if (odAttrValue instanceof ASTODSimpleAttributeValue) {
      return getASTObjectSimpleAttributeType((ASTODSimpleAttributeValue) odAttrValue);
    } else if (odAttrValue instanceof ASTODObject) {
      return ((ASTODObject) odAttrValue).getName();
    }
    return null;
  }

  /**
   * Determines OD object attribute type of simple attribute value
   *
   * @param odAttrValue Simple attribute value of OD object attribute
   * @return Attribute type
   */
  private String getASTObjectSimpleAttributeType(ASTODSimpleAttributeValue odAttrValue) {
    if (odAttrValue.getExpression() instanceof ASTLiteralExpression) {
      // Primitive type
      ASTLiteralExpression odAttrValueExpression =
          (ASTLiteralExpression) odAttrValue.getExpression();
      var odAttrValueLiteral = odAttrValueExpression.getLiteral();

      if (odAttrValueLiteral instanceof ASTNatLiteral) {
        // Can also be a long
        // TODO find out which AST is for int/long-type
        return "int";
      } else if (odAttrValueLiteral instanceof ASTStringLiteral) {
        return "String";
      } else if (odAttrValueLiteral instanceof ASTBasicDoubleLiteral) {
        // Can also be a float
        // TODO find out which AST is for float/double-type
        return "double";
      } else if (odAttrValueLiteral instanceof ASTBooleanLiteral) {
        return "boolean";
      }
    }
    return null;
  }
}
