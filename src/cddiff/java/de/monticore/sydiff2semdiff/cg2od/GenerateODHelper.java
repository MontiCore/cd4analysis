package de.monticore.sydiff2semdiff.cg2od;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.od4data.OD4DataMill;
import de.monticore.odattribute._ast.ASTODList;
import de.monticore.odattribute._ast.ASTODMap;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink.ODLinkMill;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkDirection;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportGroup;
import de.monticore.sydiff2semdiff.cg2od.metamodel.*;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompareGroup;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.*;

public class GenerateODHelper {

  /**
   * change the first character of class name to lower case except the package name
   */
  public static String toLowerCaseFirstOne4ClassName(String s) {
    if (s.contains(".")) {
      int position = s.lastIndexOf('.');
      String packageName = s.substring(0, position);
      String className = s.substring(position + 1);
      return (new StringBuilder())
        .append(packageName)
        .append(".")
        .append(Character.toLowerCase(className.charAt(0)))
        .append(className.substring(1)).toString();
    } else {
      if (Character.isLowerCase(s.charAt(0))) {
        return s;
      } else {
        return (new StringBuilder())
          .append(Character.toLowerCase(s.charAt(0)))
          .append(s.substring(1)).toString();
      }
    }

  }

  /**
   * mapping the type of compare direcrtion into integer
   * it's easy to determine which the direction should be used in OD
   */
  public static int mappingDirection(String str) {
    switch (str) {
      case "LEFT_TO_RIGHT":
        return 1;
      case "RIGHT_TO_LEFT":
        return 2;
      case "BIDIRECTIONAL":
        return 3;
      case "LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT":
        return 1;
      case "UNDEFINED":
        return 3;
      default:
        return 0;
    }
  }

  /**
   * mapping the type of compare cardinality into integer in general step
   * it's easy to determine how many objects of current association should be created in OD
   */
  public static int mappingCardinality(String str) {
    switch (str) {
      case "ONE":
        return 1;
      case "ZORE_TO_ONE":
        return 0;
      case "ONE_TO_MORE":
        return 1;
      case "MORE":
        return 0;
      case "ZERO":
        return 0;
      case "TWO_TO_MORE":
        return 2;
      case "ZERO_AND_TWO_TO_MORE":
        return 0;
      default:
        return 0;
    }
  }

  /**
   * mapping the type of compare cardinality into integer in initial step
   * it's easy to determine how many objects of current association should be created in OD
   */
  public static int mappingCardinality4Initial(String str) {
    switch (str) {
      case "ONE":
        return 1;
      case "ZORE_TO_ONE":
        return 1;
      case "ONE_TO_MORE":
        return 1;
      case "MORE":
        return 1;
      case "ZERO":
        return 0;
      case "TWO_TO_MORE":
        return 2;
      case "ZERO_AND_TWO_TO_MORE":
        return 2;
      default:
        return 0;
    }
  }

  /**
   * convert refSetAssociationList to checkList
   * if the item in refSetAssociationList is created,
   * then the corresponding counter of this item should be minus one until the counter equals zero
   */
  public static Map<SupportRefSetAssociation, Integer> convertRefSetAssociationList2CheckList(List<SupportRefSetAssociation> refSetAssociationList) {
    Map<SupportRefSetAssociation, Integer> checkList = new HashMap<>();
    refSetAssociationList.forEach(item -> {
      // check the overlapped RefSetAssociation count
      int count = refSetAssociationList
        .stream()
        .filter(e ->
          e.getLeftRoleName().equals(item.getLeftRoleName()) &&
            e.getRightRoleName().equals(item.getRightRoleName()) &&
            e.getDirection().equals(item.getDirection()) &&
            item.getLeftRefSet().containsAll(e.getLeftRefSet()) &&
            item.getRightRefSet().containsAll(e.getRightRefSet())
        ).collect(Collectors.toList()).size();

      checkList.put(item, count);
    });
    return checkList;
  }

  /**
   * according to SupportAssociation to find corresponding SupportRefSetAssociation
   */
  public static Optional<List<SupportRefSetAssociation>> findRelatedSupportRefSetAssociationBySupportAssociation(SupportAssociation originalAssoc,
                                                                                                                 Map<SupportRefSetAssociation, Integer> refLinkCheckList) {
    List<SupportRefSetAssociation> resultList = new ArrayList<>();

    refLinkCheckList.keySet().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getSupportLeftClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getSupportRightClassRoleName()) &&
        (mappingDirection(originalAssoc.getSupportDirection().toString()) == 3 ||
          item.getDirection().equals(originalAssoc.getSupportDirection())) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportLeftClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportRightClass().getName()))) {
        resultList.add(item);
      } else if (item.getLeftRoleName().equals(originalAssoc.getSupportRightClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getSupportLeftClassRoleName()) &&
        (mappingDirection(originalAssoc.getSupportDirection().toString()) == 3 ||
          item.getDirection().equals(reverseDirection(originalAssoc.getSupportDirection()))) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportRightClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getSupportLeftClass().getName()))) {
        resultList.add(item);
      }
    });

    if (resultList.size() == 0) {
      return Optional.empty();
    } else {
      return Optional.of(resultList);
    }
  }

  public static Map<SupportRefSetAssociation, Integer> updateCounterInCheckList(Optional<List<SupportRefSetAssociation>> optAssociationList,
                                                                                Map<SupportRefSetAssociation, Integer> refLinkCheckList) {
    optAssociationList.ifPresent(supportRefSetAssociations ->
      supportRefSetAssociations.forEach(e -> refLinkCheckList.put(e, refLinkCheckList.get(e) - 1)));
    return refLinkCheckList;
  }

  /**
   * check whether the related SupportRefSetAssociation is used by SupportAssociation
   */
  public static boolean checkRelatedSupportRefSetAssociationIsUsed(SupportAssociation association,
                                                                   Map<SupportRefSetAssociation, Integer> refLinkCheckList) {
    Optional<List<SupportRefSetAssociation>> optRefSetAssociationList =
      findRelatedSupportRefSetAssociationBySupportAssociation(association, refLinkCheckList);
    AtomicBoolean isUsed = new AtomicBoolean(true);
    optRefSetAssociationList.get().forEach(e -> {
      if (refLinkCheckList.get(e) != 0) {
        isUsed.set(false);
      }
    });
    return isUsed.get();
  }

  /**
   * create an attribute
   */
  public static ASTODAttribute createASTODAttribute(ASTCDAttribute astcdAttribute, ASTODValue oDvalue) {
    return OD4DataMill.oDAttributeBuilder()
      .setName(astcdAttribute.getName())
      .setModifier(OD4DataMill.modifierBuilder().build())
      .setMCType(OD4DataMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(OD4DataMill.mCQualifiedNameBuilder().addParts(astcdAttribute.printType()).build())
        .build())
      .setComplete("=")
      .setODValue(oDvalue)
      .build();
  }

  /**
   * create value for attribute
   */
  public static String createValue(SupportGroup sg, Optional<CompClass> compClass, String type, Boolean isEnumClass) {
    String result;
    String supportClassKey = "SupportEnum_" + type;
    if (sg.getSupportClassGroup().containsKey(supportClassKey)) {
      if (isEnumClass) {
        result = compClass.get().getWhichAttributesDiff().get().get(0);
      } else {
        result = ((ASTCDEnum) sg.getSupportClassGroup().get(supportClassKey).getEditedElement())
          .getCDEnumConstantList().get(0).getName();
      }
    } else {
      result = "some_type_" + type;
    }
    return result;
  }

  /**
   * create all attributes for created object
   * distinguish the type of object, simple class or collection
   */
  public static List<ASTODAttribute> createASTODAttributeList(SupportGroup sg,
                                                              Optional<CompClass> compClass,
                                                              SupportClass supportClass) {
    List<ASTODAttribute> astodAttributeList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute :
      sg.getSupportClassGroup().get(supportClass.getName()).getEditedElement().getCDAttributeList()) {
      // set attribute
      if (Pattern.matches("List<(.*)>", astcdAttribute.printType())) {
        // List<> attribute
        Matcher listMatcher = Pattern.compile("List<(.*)>").matcher(astcdAttribute.printType());
        if (listMatcher.find()) {
          String value = createValue(sg, compClass, listMatcher.group(1), false);
          ASTODList oDvalue = OD4DataMill
            .oDListBuilder()
            .addODValue(OD4DataMill.oDSimpleAttributeValueBuilder()
              .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build()).build())
            .addODValue(OD4DataMill.oDAbsentBuilder().build())
            .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else if (Pattern.matches("Set<(.*)>", astcdAttribute.printType())) {
        // Set<> attribute
        Matcher setMatcher = Pattern.compile("Set<(.*)>").matcher(astcdAttribute.printType());
        if (setMatcher.find()) {
          String value = createValue(sg, compClass, astcdAttribute.printType(), false);
          ASTODSimpleAttributeValue oDvalue = OD4DataMill
            .oDSimpleAttributeValueBuilder()
            .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build())
            .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else if (Pattern.matches("Optional<(.*)>", astcdAttribute.printType())) {
        // Optional<> attribute
        Matcher optMatcher = Pattern.compile("Optional<(.*)>").matcher(astcdAttribute.printType());
        if (optMatcher.find()) {
          String value = createValue(sg, compClass, optMatcher.group(1), false);
          ASTODSimpleAttributeValue oDvalue = OD4DataMill
            .oDSimpleAttributeValueBuilder()
            .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build())
            .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else if (Pattern.matches("Map<(.*),(.*)>", astcdAttribute.printType())) {
        // Map<,> attribute
        Matcher mapMatcher = Pattern.compile("Map<(.*),(.*)>").matcher(astcdAttribute.printType());
        if (mapMatcher.find()) {
          String kValue = createValue(sg, compClass, mapMatcher.group(1), false);
          String vValue = createValue(sg, compClass, mapMatcher.group(2), false);
          ASTODMap oDvalue = OD4DataMill
            .oDMapBuilder()
            .addODMapElement(OD4DataMill.oDMapElementBuilder()
              .setKey(OD4DataMill.oDSimpleAttributeValueBuilder().setExpression(
                OD4DataMill.nameExpressionBuilder().setName(kValue).build()).build())
              .setVal(OD4DataMill.oDSimpleAttributeValueBuilder().setExpression(
                OD4DataMill.nameExpressionBuilder().setName(vValue).build()).build())
              .build())
            .addODMapElement(OD4DataMill.oDMapElementBuilder()
              .setKey(OD4DataMill.oDAbsentBuilder().build())
              .setVal(OD4DataMill.oDAbsentBuilder().build())
              .build())
            .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else {
        // normal attribute
        String value;
        if (compClass.isPresent()) {
          if (compClass.get().getCompKind() == CompareGroup.CompClassKind.COMP_ENUM) {
            value = createValue(sg, compClass, astcdAttribute.printType(), true);
          } else {
            value = createValue(sg, compClass, astcdAttribute.printType(), false);
          }
        } else {
          value = createValue(sg, compClass, astcdAttribute.printType(), false);
        }
        ASTODSimpleAttributeValue oDvalue = OD4DataMill
          .oDSimpleAttributeValueBuilder()
          .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build())
          .build();
        astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
      }
    }

    return astodAttributeList;
  }

  /**
   * create an object
   */
  public static SupportObjectPack createObject(SupportGroup sg,
                                               Optional<CompClass> compClass,
                                               SupportClass supportClass,
                                               int index) {

    // if this SupportClass is interface or abstract class, then find a simple class on inheritancePath
    SupportClass newSupportClass;
    if (supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_INTERFACE ||
      supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_ABSTRACT_CLASS) {
      newSupportClass =
        getAllSimpleSubClasses4SupportClass(supportClass, sg.getInheritanceGraph(), sg.getSupportClassGroup()).get(0);
    } else {
      newSupportClass = supportClass;
    }

    // set attributes
    List<ASTODAttribute> astodAttributeList = createASTODAttributeList(sg, compClass, newSupportClass);

    // set objects
    ASTODNamedObject astodNamedObject = OD4DataMill.oDNamedObjectBuilder()
      .setName(toLowerCaseFirstOne4ClassName(newSupportClass.getOriginalClassName()) + "_" + index)
      .setModifier(OD4DataMill.modifierBuilder().build())
      .setMCObjectType(OD4DataMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(newSupportClass.getOriginalClassName()))
        .build())
      .setODAttributesList(astodAttributeList)
      .build();

    return new SupportObjectPack(astodNamedObject, newSupportClass);
  }

  /**
   * create all objects that should be used in an OD
   */
  public static List<ASTODNamedObject> createObjectList(SupportGroup sg,
                                                        Optional<CompClass> compClass,
                                                        SupportClass offerSupportClass,
                                                        int cardinalityCount,
                                                        Deque<ASTODClassStackPack> classStack4TargetClass,
                                                        Deque<ASTODClassStackPack> classStack4SourceClass) {
    List<ASTODNamedObject> astodNamedObjectList = new LinkedList<>();
    List<ASTODNamedObject> tempList = new LinkedList<>();
    SupportClass actualSupportClass = offerSupportClass;
    for (int i = 0; i < cardinalityCount; i++) {
      // set objects
      SupportObjectPack objectPack = createObject(sg, compClass, offerSupportClass, i);
      actualSupportClass = objectPack.getSupportClass();
      astodNamedObjectList.add(objectPack.getNamedObject());
      tempList.add(objectPack.getNamedObject());
    }
    classStack4TargetClass.push(new ASTODClassStackPack(tempList, actualSupportClass));
    classStack4SourceClass.push(new ASTODClassStackPack(tempList, actualSupportClass));
    return astodNamedObjectList;
  }

  /**
   * create a link between two created objects
   */
  public static ASTODLink createLink(ASTODNamedObject left,
                                     ASTODNamedObject right,
                                     String leftRoleName,
                                     String rightRoleName,
                                     ASTODLinkDirection astodLinkDirection) {
    ASTODLink astodLink = OD4DataMill.oDLinkBuilder()
      .setLink(true)
      .setODLinkLeftSide(OD4DataMill.oDLinkLeftSideBuilder()
        .setModifier(OD4DataMill.modifierBuilder().build())
        .addReferenceNames(0, OD4DataMill.oDNameBuilder().setName(left.getName()).build())
        .setRole(leftRoleName)
        .build())
      .setODLinkDirection(astodLinkDirection)
      .setODLinkRightSide(OD4DataMill.oDLinkRightSideBuilder()
        .setModifier(OD4DataMill.modifierBuilder().build())
        .addReferenceNames(0, OD4DataMill.oDNameBuilder().setName(right.getName()).build())
        .setRole(rightRoleName)
        .build())
      .build();
    return astodLink;
  }

  /**
   * create link for one object to two objects
   */
  public static List<ASTODLink> createLinkList(List<ASTODNamedObject> leftElementList,
                                               List<ASTODNamedObject> rightElementList,
                                               String leftRoleName,
                                               String rightRoleName,
                                               int directionType) {
    List<ASTODLink> linkElementList = new LinkedList<>();
    if (leftElementList.size() != 0 && rightElementList.size() != 0) {
      switch (directionType) {
        case 1:
          leftElementList.forEach(left -> {
            rightElementList.forEach(right -> {
              // set link
              linkElementList.add(
                createLink(left, right, leftRoleName, rightRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
            });
          });
          break;
        case 2:
          leftElementList.forEach(left -> {
            rightElementList.forEach(right -> {
              // set link
              linkElementList.add(
                createLink(right, left, rightRoleName, leftRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
            });
          });
          break;
        case 3:
          leftElementList.forEach(left -> {
            rightElementList.forEach(right -> {
              // set link
              linkElementList.add(
                createLink(left, right, leftRoleName, rightRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
              linkElementList.add(
                createLink(right, left, rightRoleName, leftRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
            });
          });
          break;
      }
    }
    return linkElementList;
  }

  /**
   * create link for one object to one object
   */
  public static List<ASTODLink> createLinkList(ASTODNamedObject left,
                                               ASTODNamedObject right,
                                               String leftRoleName,
                                               String rightRoleName,
                                               int directionType) {
    return createLinkList(List.of(left), List.of(right), leftRoleName, rightRoleName, directionType);
  }

  /**
   * the created object as target class
   * find all related associations of this created object
   */
  public static List<SupportAssociation> findAllSupportAssociationByTargetClass(SupportGroup sg,
                                                                                SupportClass supportClass) {
    List<SupportAssociation> result = new LinkedList<>();
    Map<String, SupportAssociation> supportAssociationMap =
      fuzzySearchSupportAssociationByClassName(sg.getSupportAssociationGroup(), supportClass.getOriginalClassName());
    supportAssociationMap.forEach((name, assoc) -> {
      // supportClass <-
      if (assoc.getSupportLeftClass().getOriginalClassName().equals(supportClass.getOriginalClassName()) &&
        assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.RIGHT_TO_LEFT) {
        result.add(assoc);
      }
      // -> supportClass
      if (assoc.getSupportRightClass().getOriginalClassName().equals(supportClass.getOriginalClassName()) &&
        assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) {
        result.add(assoc);
      }
      // <->  --
      if (assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.BIDIRECTIONAL ||
        assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.UNDEFINED) {
        result.add(assoc);
      }
    });

//    Collections.reverse(result);
    return result;
  }

  /**
   * the created object as source class
   * find all related associations of this created object
   */
  public static List<SupportAssociation> findAllSupportAssociationBySourceClass(SupportGroup sg,
                                                                                SupportClass supportClass) {
    List<SupportAssociation> result = new ArrayList<>();
    Map<String, SupportAssociation> supportAssociationMap =
      fuzzySearchSupportAssociationByClassName(sg.getSupportAssociationGroup(), supportClass.getOriginalClassName());
    supportAssociationMap.forEach((name, assoc) -> {
      // <- supportClass
      if (assoc.getSupportRightClass().getOriginalClassName().equals(supportClass.getOriginalClassName()) &&
        assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.RIGHT_TO_LEFT) {
        result.add(assoc);
      }
      // supportClass ->
      if (assoc.getSupportLeftClass().getOriginalClassName().equals(supportClass.getOriginalClassName()) &&
        assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) {
        result.add(assoc);
      }
      // <->  --
      if (assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.BIDIRECTIONAL ||
        assoc.getSupportDirection() == SupportGroup.SupportAssociationDirection.UNDEFINED) {
        result.add(assoc);
      }
    });

//    Collections.reverse(result);
    return result;
  }

  /**
   * check the object of given supportClass whether is in ASTODElementList
   */
  public static boolean isPresentObjectInASTODElementListBySupportClass(SupportGroup sg,
                                                                        SupportClass supportClass,
                                                                        ASTODPack astodPack) {
    AtomicBoolean isInList = new AtomicBoolean(false);

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    // if this SupportClass is interface or abstract class,
    // check the subclass of this SupportClass whether is in objectList.
    if (supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_INTERFACE ||
      supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_ABSTRACT_CLASS) {
      List<SupportClass> subClassList =
        getAllSimpleSubClasses4SupportClass(supportClass, sg.getInheritanceGraph(), sg.getSupportClassGroup());
      subClassList.forEach(c -> {
        objectList.forEach(e -> {
          if (e.getName().split("_")[0].equals(toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
            isInList.set(true);
          }
        });
      });
    } else {
      objectList.forEach(e -> {
        if (e.getName().split("_")[0].equals(toLowerCaseFirstOne4ClassName(supportClass.getOriginalClassName()))) {
          isInList.set(true);
        }
      });
    }

    return isInList.get();
  }

  /**
   * check the object of given supportClass whether is in ASTODElementList
   * if it is in ASTODElementList, return the existed ASTODElement
   * if it is not in ASTODElementList, create a new ASTODElement as return element.
   *
   * @return: ASTODNamedObjectPack
   * {  "objectList"  : List<ASTODNamedObject>
   *    "isInList"    : boolean                 }
   */
  public static ASTODNamedObjectPack getObjectInASTODElementListBySupportClass(SupportGroup sg,
                                                                               SupportClass supportClass,
                                                                               ASTODPack astodPack) {
    AtomicBoolean isInList = new AtomicBoolean(false);
    AtomicReference<List<ASTODNamedObject>> resultList = new AtomicReference<>(new ArrayList<>());

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    // if this SupportClass is interface or abstract class,
    // check the subclass of this SupportClass whether is in objectList.
    if (supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_INTERFACE ||
      supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_ABSTRACT_CLASS) {
      List<SupportClass> subClassList =
        getAllSimpleSubClasses4SupportClass(supportClass, sg.getInheritanceGraph(), sg.getSupportClassGroup());
      subClassList.forEach(c -> {
        objectList.forEach(e -> {
          if (e.getName().split("_")[0].equals(toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
            List<ASTODNamedObject> tempList = resultList.get();
            tempList.add(e);
            resultList.set(tempList);
            isInList.set(true);
          }
        });
      });
    } else {
      objectList.forEach(e -> {
        if (e.getName().split("_")[0].equals(toLowerCaseFirstOne4ClassName(supportClass.getOriginalClassName()))) {
          List<ASTODNamedObject> tempList = resultList.get();
          tempList.add(e);
          resultList.set(tempList);
          isInList.set(true);
        }
      });
    }

    // create a new ASTODNamedObject if the object of given supportClass is not in ASTODElementList
    if (!isInList.get()) {

      // determine the class of new ASTODNamedObject
      SupportClass newSupportClass;
      if (supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_INTERFACE ||
        supportClass.getSupportKind() == SupportGroup.SupportClassKind.SUPPORT_ABSTRACT_CLASS) {
        List<SupportClass> supportClassList =
          getAllSimpleSubClasses4SupportClass(supportClass, sg.getInheritanceGraph(), sg.getSupportClassGroup());
        newSupportClass = supportClassList.get(supportClassList.size() - 1);
      } else {
        newSupportClass = supportClass;
      }
      // put new object into resultList
      List<ASTODNamedObject> tempList = resultList.get();
      tempList.add(createObject(sg, Optional.empty(), newSupportClass, 0).getNamedObject());

      resultList.set(tempList);
    }

    return new ASTODNamedObjectPack(resultList.get(), isInList.get());
  }

  /**
   * get the other side class in SupportAssociation
   * if the given SupportAssociation is self-loop, that is no problem.
   * return the found the other side class and it's positon side.
   *
   * @return: SupportClassPack
   * {  "otherSideClass" : SupportClass
   *    "position"       : ["left", "right"] }
   */
  public static SupportClassPack findOtherSideClassAndPositionInSupportAssociation(SupportAssociation supportAssociation,
                                                                                   SupportClass currentClass) {
    SupportClassPack.Position position;
    SupportClass otherSideClass;
    if (supportAssociation.getSupportLeftClass().getOriginalClassName().equals(currentClass.getOriginalClassName())) {
      otherSideClass = supportAssociation.getSupportRightClass();
      position = SupportClassPack.Position.RIGHT;
    } else {
      otherSideClass = supportAssociation.getSupportLeftClass();
      position = SupportClassPack.Position.LEFT;
    }
    return new SupportClassPack(otherSideClass, position);
  }

  /**
   * generate OD title for semantic difference with association
   */
  public static String generateODTitle(CompAssociation association, int index) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("OD_");
    stringBuilder.append(index);
    stringBuilder.append("-[");

    // set assoc name
    stringBuilder.append(association.getName(true));
    stringBuilder.append("]-[");

    // set assoc type
    stringBuilder.append(association.getCompCategory().toString().toLowerCase());
    stringBuilder.append("]");

    // set which part
    if (association.getWhichPartDiff().isPresent()) {
      if (association.getWhichPartDiff().get() != CompareGroup.WhichPartDiff.DIRECTION) {
        stringBuilder.append("-[");
        stringBuilder.append(association.getWhichPartDiff().get().toString().toLowerCase());
        stringBuilder.append("]");
      }
    }

    return stringBuilder.toString();
  }

  /**
   * generate OD title for semantic difference with class
   */
  public static String generateODTitle(CompClass compClass, int index) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("OD_");
    stringBuilder.append(index);
    stringBuilder.append("-[");

    // set assoc name
    stringBuilder.append(compClass.getName(true));
    stringBuilder.append("]-[");

    // set assoc type
    stringBuilder.append(compClass.getCompCategory().toString().toLowerCase());
    stringBuilder.append("]");

    // set which part
    if (compClass.getCompCategory() == CompareGroup.CompClassCategory.EDITED) {
      if (compClass.getWhichAttributesDiff().isPresent()) {
        stringBuilder.append("-");
        stringBuilder.append(compClass.getWhichAttributesDiff().get());
      }
    }

    return stringBuilder.toString();
  }

}
