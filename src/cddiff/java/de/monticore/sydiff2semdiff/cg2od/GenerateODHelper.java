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
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.*;

public class GenerateODHelper {

  /**
   * change the first character of class name to lower case except the package name
   */
  public static String toLowerCaseFirstOne4ClassName(String s) {
    if (s.contains(".")) {
      int position = s.lastIndexOf('.');
      String packageName = s.substring(0, position);
      String className = s.substring(position + 1, s.length());
      return (new StringBuilder())
        .append(packageName)
        .append(".")
        .append(Character.toLowerCase(className.charAt(0)))
        .append(className.substring(1)).toString();
    } else {
      if (Character.isLowerCase(s.charAt(0)))
        return s;
      else
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

  }

  /**
   * mapping the type of compared direcrtion into integer
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
   * mapping the type of compared cardinality into integer in general step
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
   * mapping the type of compared cardinality into integer in initial step
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
  public static Map<DiffRefSetAssociation, Integer> convertRefSetAssociationList2CheckList(List<DiffRefSetAssociation> refSetAssociationList) {
    Map<DiffRefSetAssociation, Integer> checkList = new HashMap<>();
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
   * according to DiffAssociation to find corresponding DiffRefSetAssociation
   */
  public static Optional<List<DiffRefSetAssociation>> findRelatedDiffRefSetAssociationByDiffAssociation(DiffAssociation originalAssoc, Map<DiffRefSetAssociation, Integer> refLinkCheckList) {
    List<DiffRefSetAssociation> resultList = new ArrayList<>();

    refLinkCheckList.keySet().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getDiffLeftClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getDiffRightClassRoleName()) &&
        item.getDirection().equals(originalAssoc.getDiffDirection()) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffLeftClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffRightClass().getName()))) {
        resultList.add(item);
      } else if (item.getLeftRoleName().equals(originalAssoc.getDiffRightClassRoleName()) &&
        item.getRightRoleName().equals(originalAssoc.getDiffLeftClassRoleName()) &&
        item.getDirection().equals(reverseDirection(originalAssoc.getDiffDirection())) &&
        item.getLeftRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffRightClass().getName())) &&
        item.getRightRefSet().stream().anyMatch(e -> e.getName().equals(originalAssoc.getDiffLeftClass().getName()))) {
        resultList.add(item);
      }
    });

    if (resultList.size() == 0) {
      return Optional.empty();
    } else {
      return Optional.of(resultList);
    }
  }

  public static Map<DiffRefSetAssociation, Integer> updateCounterInCheckList(Optional<List<DiffRefSetAssociation>> optAssociationList, Map<DiffRefSetAssociation, Integer> refLinkCheckList) {
    if (optAssociationList.isPresent()) {
      optAssociationList.get().forEach(e -> {
        refLinkCheckList.put(e, refLinkCheckList.get(e) - 1);
      });
    }
    return refLinkCheckList;
  }

  /**
   * check whether the related DiffRefSetAssociation is used by DiffAssociation
   */
  public static boolean checkRelatedDiffRefSetAssociationIsUsed(DiffAssociation association, Map<DiffRefSetAssociation, Integer> refLinkCheckList) {
    Optional<List<DiffRefSetAssociation>> optRefSetAssociationList = findRelatedDiffRefSetAssociationByDiffAssociation(association, refLinkCheckList);
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
  public static String createValue(DifferentGroup dg, Optional<CompClass> compClass, String type, Boolean isEnumClass) {
    String result = null;
    String diffClassKey = "DiffEnum_" + type;
    if (dg.getDiffClassGroup().containsKey(diffClassKey)) {
      if (isEnumClass) {
        result = compClass.get().getWhichAttributesDiff().get().get(0);
      } else {
        result = ((ASTCDEnum) dg.getDiffClassGroup().get(diffClassKey).getEditedElement()).getCDEnumConstantList().get(0).getName();
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
  public static List<ASTODAttribute> createASTODAttributeList(DifferentGroup dg, Optional<CompClass> compClass, DiffClass diffClass) {
    List<ASTODAttribute> astodAttributeList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : dg.getDiffClassGroup().get(diffClass.getName()).getEditedElement().getCDAttributeList()) {
      // set attribute
      if (Pattern.matches("List<(.*)>", astcdAttribute.printType())) {
        // List<> attribute
        Matcher listMatcher = Pattern.compile("List<(.*)>").matcher(astcdAttribute.printType());
        if (listMatcher.find()) {
          String value = createValue(dg, compClass, listMatcher.group(1), false);
          ASTODList oDvalue = OD4DataMill
            .oDListBuilder()
            .addODValue(OD4DataMill.oDSimpleAttributeValueBuilder().setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build()).build())
            .addODValue(OD4DataMill.oDAbsentBuilder().build())
            .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else if (Pattern.matches("Set<(.*)>", astcdAttribute.printType())) {
        // Set<> attribute
        Matcher setMatcher = Pattern.compile("Set<(.*)>").matcher(astcdAttribute.printType());
        if (setMatcher.find()) {
          String value = createValue(dg, compClass, astcdAttribute.printType(), false);
          ASTODSimpleAttributeValue oDvalue = OD4DataMill
            .oDSimpleAttributeValueBuilder().setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build()).build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else if (Pattern.matches("Optional<(.*)>", astcdAttribute.printType())) {
        // Optional<> attribute
        Matcher optMatcher = Pattern.compile("Optional<(.*)>").matcher(astcdAttribute.printType());
        if (optMatcher.find()) {
          String value = createValue(dg, compClass, optMatcher.group(1), false);
          ASTODSimpleAttributeValue oDvalue = OD4DataMill
            .oDSimpleAttributeValueBuilder().setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build()).build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      } else if (Pattern.matches("Map<(.*),(.*)>", astcdAttribute.printType())) {
        // Map<,> attribute
        Matcher mapMatcher = Pattern.compile("Map<(.*),(.*)>").matcher(astcdAttribute.printType());
        if (mapMatcher.find()) {
          String kValue = createValue(dg, compClass, mapMatcher.group(1), false);
          String vValue = createValue(dg, compClass, mapMatcher.group(2), false);
          ASTODMap oDvalue = OD4DataMill
            .oDMapBuilder()
            .addODMapElement(OD4DataMill.oDMapElementBuilder()
              .setKey(OD4DataMill.oDSimpleAttributeValueBuilder().setExpression(OD4DataMill.nameExpressionBuilder().setName(kValue).build()).build())
              .setVal(OD4DataMill.oDSimpleAttributeValueBuilder().setExpression(OD4DataMill.nameExpressionBuilder().setName(vValue).build()).build())
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
        String value = null;
        if (compClass.isPresent()) {
          if (compClass.get().getCompKind() == CompareGroup.CompClassKind.COMP_ENUM) {
            value = createValue(dg, compClass, astcdAttribute.printType(), true);
          } else {
            value = createValue(dg, compClass, astcdAttribute.printType(), false);
          }
        } else {
          value = createValue(dg, compClass, astcdAttribute.printType(), false);
        }
        ASTODSimpleAttributeValue oDvalue = OD4DataMill
          .oDSimpleAttributeValueBuilder().setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build()).build();
        astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
      }
    }

    return astodAttributeList;
  }

  /**
   * create an object
   */
  public static ASTODNamedObject createObject(DifferentGroup dg, Optional<CompClass> compClass, DiffClass diffClass, int index) {

    // if this DiffClass is interface or abstract class, then find a simple class on inheritancePath
    DiffClass newDiffClass = null;
    if (diffClass.getDiffKind() == DifferentGroup.DiffClassKind.DIFF_INTERFACE ||
      diffClass.getDiffKind() == DifferentGroup.DiffClassKind.DIFF_ABSTRACT_CLASS) {
      newDiffClass = getAllSimpleSubClasses4DiffClass(diffClass, dg.getInheritanceGraph(), dg.getDiffClassGroup()).get(0);
    } else {
      newDiffClass = diffClass;
    }

    // set attributes
    List<ASTODAttribute> astodAttributeList = createASTODAttributeList(dg, compClass, newDiffClass);

    // set objects
    ASTODNamedObject astodNamedObject = OD4DataMill.oDNamedObjectBuilder()
      .setName(toLowerCaseFirstOne4ClassName(newDiffClass.getOriginalClassName()) + "_" + index)
      .setModifier(OD4DataMill.modifierBuilder().build())
      .setMCObjectType(OD4DataMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(newDiffClass.getOriginalClassName()))
        .build())
      .setODAttributesList(astodAttributeList)
      .build();

    return astodNamedObject;
  }

  /**
   * create all objects that should be used in an OD
   */
  public static List<ASTODNamedObject> createObjectList(DifferentGroup dg,
                                                        Optional<CompClass> compClass,
                                                        DiffClass diffClass,
                                                        int cardinalityCount,
                                                        Deque<Map<String, Object>> classStack4TargetClass,
                                                        Deque<Map<String, Object>> classStack4SourceClass) {
    List<ASTODNamedObject> astodNamedObjectList = new ArrayList<>();
    List<ASTODNamedObject> tempList = new ArrayList<>();
    for (int i = 0; i < cardinalityCount; i++) {
      // set objects
      ASTODNamedObject object = createObject(dg, compClass, diffClass, i);
      astodNamedObjectList.add(object);
      tempList.add(object);
    }
    classStack4TargetClass.push(Map.of("objectList", tempList, "diffClass", diffClass));
    classStack4SourceClass.push(Map.of("objectList", tempList, "diffClass", diffClass));
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
    List<ASTODLink> linkElementList = new ArrayList<>();
    switch (directionType) {
      case 1:
        leftElementList.forEach(left -> {
          rightElementList.forEach(right -> {
            // set link
            linkElementList.add(createLink(left, right, leftRoleName, rightRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
          });
        });
        break;
      case 2:
        leftElementList.forEach(left -> {
          rightElementList.forEach(right -> {
            // set link
            linkElementList.add(createLink(right, left, rightRoleName, leftRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
          });
        });
        break;
      case 3:
        leftElementList.forEach(left -> {
          rightElementList.forEach(right -> {
            // set link
            linkElementList.add(createLink(left, right, leftRoleName, rightRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
            linkElementList.add(createLink(right, left, rightRoleName, leftRoleName, ODLinkMill.oDLeftToRightDirBuilder().build()));
          });
        });
        break;
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
  public static List<DiffAssociation> findAllDiffAssociationByTargetClass(DifferentGroup dg, DiffClass diffClass) {
    List<DiffAssociation> result = new ArrayList<>();
    Map<String, DiffAssociation> diffAssociationMap = fuzzySearchDiffAssociationByClassName(dg.getDiffAssociationGroup(), diffClass.getOriginalClassName());
    diffAssociationMap.forEach((name, assoc) -> {
      // diffClass <-
      if (assoc.getDiffLeftClass().getOriginalClassName().equals(diffClass.getOriginalClassName()) &&
        assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT) {
        result.add(assoc);
      }
      // -> diffClass
      if (assoc.getDiffRightClass().getOriginalClassName().equals(diffClass.getOriginalClassName()) &&
        assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT) {
        result.add(assoc);
      }
      // <->  --
      if (assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.BIDIRECTIONAL ||
        assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.UNDEFINED) {
        result.add(assoc);
      }
    });
    return result;
  }

  /**
   * the created object as source class
   * find all related associations of this created object
   */
  public static List<DiffAssociation> findAllDiffAssociationBySourceClass(DifferentGroup dg, DiffClass diffClass) {
    List<DiffAssociation> result = new ArrayList<>();
    Map<String, DiffAssociation> diffAssociationMap = fuzzySearchDiffAssociationByClassName(dg.getDiffAssociationGroup(), diffClass.getOriginalClassName());
    diffAssociationMap.forEach((name, assoc) -> {
      // <- diffClass
      if (assoc.getDiffRightClass().getOriginalClassName().equals(diffClass.getOriginalClassName()) &&
        assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.RIGHT_TO_LEFT) {
        result.add(assoc);
      }
      // diffClass ->
      if (assoc.getDiffLeftClass().getOriginalClassName().equals(diffClass.getOriginalClassName()) &&
        assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT) {
        result.add(assoc);
      }
      // <->  --
      if (assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.BIDIRECTIONAL ||
        assoc.getDiffDirection() == DifferentGroup.DiffAssociationDirection.UNDEFINED) {
        result.add(assoc);
      }
    });
    return result;
  }

  /**
   * check the object of given diffClass whether is in ASTODElementList
   * if it is in ASTODElementList, return the existed ASTODElement
   * if it is not in ASTODElementList, create a new ASTODElement as return element.
   *
   * @return: Map
   * {  "objectList"  : List<ASTODNamedObject>
   *    "isInList"    : boolean                 }
   */
  public static Map<String, Object> getObjectInASTODElementListByDiffClass(DifferentGroup dg,
                                                                           DiffClass diffClass,
                                                                           List<ASTODElement> astodElementList) {
    AtomicBoolean isInList = new AtomicBoolean(false);
    AtomicReference<List<ASTODNamedObject>> resultList = new AtomicReference<>(new ArrayList<>());

    // choose ASTODNamedObjects from ASTODElementList
    List<ASTODNamedObject> objectList = new ArrayList<>();
    astodElementList.forEach(e -> {
      if (e.getClass().equals(ASTODNamedObject.class)) {
        objectList.add((ASTODNamedObject) e);
      }
    });

    // if this DiffClass is interface or abstract class, check the subclass of this DiffClass whether is in objectList.
    if (diffClass.getDiffKind() == DifferentGroup.DiffClassKind.DIFF_INTERFACE ||
      diffClass.getDiffKind() == DifferentGroup.DiffClassKind.DIFF_ABSTRACT_CLASS) {
      List<DiffClass> subClassList = getAllSimpleSubClasses4DiffClass(diffClass, dg.getInheritanceGraph(), dg.getDiffClassGroup());
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
        if (e.getName().split("_")[0].equals(toLowerCaseFirstOne4ClassName(diffClass.getOriginalClassName()))) {
          List<ASTODNamedObject> tempList = resultList.get();
          tempList.add(e);
          resultList.set(tempList);
          isInList.set(true);
        }
      });
    }

    // create a new ASTODNamedObject if the object of given diffClass is not in ASTODElementList
    if (!isInList.get()) {

      // determine the class of new ASTODNamedObject
      DiffClass newDiffClass = null;
      if (diffClass.getDiffKind() == DifferentGroup.DiffClassKind.DIFF_INTERFACE ||
        diffClass.getDiffKind() == DifferentGroup.DiffClassKind.DIFF_ABSTRACT_CLASS) {
        List<DiffClass> diffClassList = getAllSimpleSubClasses4DiffClass(diffClass, dg.getInheritanceGraph(), dg.getDiffClassGroup());
        newDiffClass = diffClassList.get(diffClassList.size() - 1);
      } else {
        newDiffClass = diffClass;
      }
      // put new object into resultList
      List<ASTODNamedObject> tempList = resultList.get();
      tempList.add(createObject(dg, Optional.empty(), newDiffClass, 0));

      resultList.set(tempList);
    }

    return Map.of("objectList", resultList.get(), "isInList", isInList.get());
  }

  /**
   * get the other side class in DiffAssociation
   * if the given DiffAssociation is self-loop, that is no problem.
   * return the found the other side class and it's positon side.
   *
   * @return: Map
   * {  "otherSideClass" : DiffClass
   *    "position"       : ["left", "right"] }
   */
  public static Map<String, Object> findOtherSideClassAndPositionInDiffAssociation(DiffAssociation diffAssociation, DiffClass currentClass) {
    String position;
    DiffClass otherSideClass = null;
    if (diffAssociation.getDiffLeftClass().getOriginalClassName().equals(currentClass.getOriginalClassName())) {
      otherSideClass = diffAssociation.getDiffRightClass();
      position = "right";
    } else {
      otherSideClass = diffAssociation.getDiffLeftClass();
      position = "left";
    }

    return Map.of("otherSideClass", otherSideClass, "position", position);
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
    stringBuilder.append(association.getName());
    stringBuilder.append("]-[");

    // set assoc type
    stringBuilder.append(association.getCompCategory().toString().toLowerCase());
    stringBuilder.append("]");

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
    stringBuilder.append(compClass.getName());
    stringBuilder.append("]-[");

    // set assoc type
    stringBuilder.append(compClass.getCompCategory().toString().toLowerCase());
    stringBuilder.append("]");

    return stringBuilder.toString();
  }

  /**
   * ensure the order of elements in OD
   * first objects, then associations
   */
  public static List<ASTODElement> organizeASTODElementList(List<ASTODElement> originalList) {
    List<ASTODElement> resultList = new ArrayList<>();
    resultList.addAll(originalList.stream().filter(e -> e.getClass().equals(ASTODNamedObject.class)).collect(Collectors.toList()));
    resultList.addAll(originalList.stream().filter(e -> e.getClass().equals(ASTODLink.class)).collect(Collectors.toList()));
    return resultList;
  }


}
