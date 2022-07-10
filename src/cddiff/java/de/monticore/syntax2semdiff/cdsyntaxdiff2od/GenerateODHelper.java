package de.monticore.syntax2semdiff.cdsyntaxdiff2od;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.od4data.OD4DataMill;
import de.monticore.od4data.prettyprinter.OD4DataFullPrettyPrinter;
import de.monticore.odattribute._ast.ASTODList;
import de.monticore.odattribute._ast.ASTODMap;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink.ODLinkMill;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkDirection;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.syntax2semdiff.cdsyntaxdiff2od.metamodel.*;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeDiff;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDAssociationDiff;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.monticore.syntax2semdiff.cd2cdwrapper.CDWrapperHelper.*;

public class GenerateODHelper {

  /**
   * change the first character of class name to lower case except the package name
   */
  public static String toLowerCaseFirstOne4ClassName(String s) {
    if (s.contains(".")) {
      int position = s.lastIndexOf('.');
      String packageName = s.substring(0, position);
      String className = s.substring(position + 1);
      return (new StringBuilder()).append(packageName)
          .append(".")
          .append(Character.toLowerCase(className.charAt(0)))
          .append(className.substring(1))
          .toString();
    }
    else {
      if (Character.isLowerCase(s.charAt(0))) {
        return s;
      }
      else {
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0)))
            .append(s.substring(1))
            .toString();
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
      case "ZERO_TO_ONE":
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
      case "ZERO_TO_ONE":
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
  public static Map<CDRefSetAssociationWrapper, Integer> convertRefSetAssociationList2CheckList(
      List<CDRefSetAssociationWrapper> refSetAssociationList) {
    Map<CDRefSetAssociationWrapper, Integer> checkList = new HashMap<>();
    refSetAssociationList.forEach(item -> {
//      // check the overlapped RefSetAssociation count
//      int count = refSetAssociationList.stream()
//          .filter(e -> e.getLeftRoleName().equals(item.getLeftRoleName()) && e.getRightRoleName()
//              .equals(item.getRightRoleName()) && e.getDirection().equals(item.getDirection())
//              && item.getLeftRefSet().containsAll(e.getLeftRefSet()) && item.getRightRefSet()
//              .containsAll(e.getRightRefSet()))
//          .collect(Collectors.toList())
//          .size();

      checkList.put(item, 1);
    });
    return checkList;
  }

  /**
   * according to CDAssociationWrapper to find all corresponding CDRefSetAssociationWrapper
   * including Inheritance CDRefSetAssociationWrapper
   */
  public static List<CDRefSetAssociationWrapper> findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(
      CDWrapper cdw,
      CDAssociationWrapper originalAssoc,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    Set<CDRefSetAssociationWrapper> resulSet = new HashSet<>();

    // get all subclasses of leftCDTypeWrapper and rightCDTypeWrapper in originalAssoc
    Set<CDTypeWrapper> CDTypeWrapperSet = new HashSet<>();
    CDTypeWrapperSet.add(originalAssoc.getCDWrapperLeftClass());
    CDTypeWrapperSet.addAll(getAllSimpleSubClasses4CDTypeWrapper(originalAssoc.getCDWrapperLeftClass(),
        cdw.getInheritanceGraph(), cdw.getCDTypeWrapperGroup()));
    CDTypeWrapperSet.add(originalAssoc.getCDWrapperRightClass());
    CDTypeWrapperSet.addAll(getAllSimpleSubClasses4CDTypeWrapper(originalAssoc.getCDWrapperRightClass(),
        cdw.getInheritanceGraph(), cdw.getCDTypeWrapperGroup()));

    // get all related Assocs of each class in CDTypeWrapperSet
    Map<String, CDAssociationWrapper> cDAssociationWrapperMap = new HashMap<>();
    CDTypeWrapperSet.forEach(e -> {
      cDAssociationWrapperMap.putAll(
          fuzzySearchCDAssociationWrapperByClassName(
              cdw.getCDAssociationWrapperGroup(),
              e.getOriginalClassName()));
    });

    // get all related CDRefSetAssociationWrapper of each Assoc in cDAssociationWrapperMap
    cDAssociationWrapperMap.values().forEach(e -> {
      if ((e.getCDWrapperLeftClassRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && e.getCDWrapperRightClassRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && (mappingDirection(originalAssoc.getCDAssociationWrapperDirection().toString()) == 3
              || e.getCDAssociationWrapperDirection().equals(originalAssoc.getCDAssociationWrapperDirection())))
          || (e.getCDWrapperLeftClassRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && e.getCDWrapperRightClassRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName()))
          && (mappingDirection(originalAssoc.getCDAssociationWrapperDirection().toString()) == 3
              || e.getCDAssociationWrapperDirection().equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection())))) {
        resulSet.addAll(
            findDirectRelatedCDRefSetAssociationWrapperByCDAssociationWrapper(e, refLinkCheckList));
      }
    });

    return resulSet.stream().collect(Collectors.toList());
  }

  /**
   * according to CDAssociationWrapper to find direct corresponding CDRefSetAssociationWrapper
   */
  public static Set<CDRefSetAssociationWrapper> findDirectRelatedCDRefSetAssociationWrapperByCDAssociationWrapper(
      CDAssociationWrapper originalAssoc,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    Set<CDRefSetAssociationWrapper> resulSet = new HashSet<>();

    refLinkCheckList.keySet().forEach(item -> {
      if (item.getLeftRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && (mappingDirection(originalAssoc.getCDAssociationWrapperDirection().toString()) == 3
              || item.getDirection().equals(originalAssoc.getCDAssociationWrapperDirection()))
          && item.getLeftRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperLeftClass().getName()))
          && item.getRightRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperRightClass().getName()))) {
        resulSet.add(item);
      }
      else if (item.getLeftRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && (mappingDirection(originalAssoc.getCDAssociationWrapperDirection().toString()) == 3
              || item.getDirection().equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection())))
          && item.getLeftRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperRightClass().getName()))
          && item.getRightRefSet()
          .stream()
          .anyMatch(e -> e.getName().equals(originalAssoc.getCDWrapperLeftClass().getName()))) {
        resulSet.add(item);
      }
    });

    return resulSet;
  }


  public static Map<CDRefSetAssociationWrapper, Integer> updateCounterInCheckList(
      List<CDRefSetAssociationWrapper> associationList,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    if (!associationList.isEmpty()) {
      associationList.forEach(e -> refLinkCheckList.put(e, refLinkCheckList.get(e) - 1));
    }
    return refLinkCheckList;

  }

  /**
   * check whether the related CDRefSetAssociationWrapper is used by CDAssociationWrapper
   */
  public static boolean checkRelatedCDRefSetAssociationWrapperIsUsed(CDWrapper cdw,
      CDAssociationWrapper association,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    List<CDRefSetAssociationWrapper> refSetAssociationList =
        findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(
            cdw, association, refLinkCheckList);
    AtomicBoolean isUsed = new AtomicBoolean(true);
    refSetAssociationList.forEach(e -> {
      if (refLinkCheckList.get(e) != 0) {
        isUsed.set(false);
      }
    });
    return isUsed.get();
  }

  /**
   * using in generateOD basic process
   *
   * if current Assoc = A -> B
   * then check if exist A <- B in CD and if A <- B is created
   *    if it is created, this situation is not illegal
   *        otherwise this situation is illegal
   *
   * if current Assoc = A -> B
   * then check if exist B -> A in CD and if B -> A is created
   *    if it is created, this situation is not illegal
   *        otherwise this situation is illegal
   */
  public static boolean checkIllegalSituationOnly4CDAssociationWrapperWithLeftToRightAndRightToLeft(
      CDWrapper cdw,
      CDAssociationWrapper currentAssoc,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {

    if (currentAssoc.getCDAssociationWrapperDirection() == CDWrapper.CDAssociationWrapperDirection.LEFT_TO_RIGHT ||
        currentAssoc.getCDAssociationWrapperDirection() == CDWrapper.CDAssociationWrapperDirection.RIGHT_TO_LEFT) {

      List<CDAssociationWrapperPack> cDAssociationWrapperPacks =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirection(cdw.getCDAssociationWrapperGroup(), currentAssoc);

      boolean isIllegal = cDAssociationWrapperPacks
          .stream()
          .filter(pack ->
              // A -> B, A <- B
              (!pack.isReverse()
                  && pack.getCDAssociationWrapper().getCDAssociationWrapperDirection()
                      .equals(reverseDirection(currentAssoc.getCDAssociationWrapperDirection()))) ||
              // A -> B, B -> A
              (pack.isReverse()
                  && pack.getCDAssociationWrapper().getCDAssociationWrapperDirection()
                  .equals(currentAssoc.getCDAssociationWrapperDirection())))
          .noneMatch(pack -> {
            // check pack.getCDAssociationWrapper() whether is used ?
            Set<CDRefSetAssociationWrapper> cDRefSetAssociationWrappers =
                findDirectRelatedCDRefSetAssociationWrapperByCDAssociationWrapper(pack.getCDAssociationWrapper(), refLinkCheckList);
            return cDRefSetAssociationWrappers
                .stream()
                .filter(e -> e.getOriginalElement().equals(pack.getCDAssociationWrapper()))
                .allMatch(e -> refLinkCheckList.get(e) == 0);
          });
      return isIllegal;
    }
    return true;
  }

  /**
   * create an attribute
   */
  public static ASTODAttribute createASTODAttribute(ASTCDAttribute astcdAttribute,
      ASTODValue oDvalue) {
    return OD4DataMill.oDAttributeBuilder()
        .setName(astcdAttribute.getName())
        .setModifier(OD4DataMill.modifierBuilder().build())
        .setMCType(OD4DataMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(
                OD4DataMill.mCQualifiedNameBuilder().addParts(astcdAttribute.printType()).build())
            .build())
        .setComplete("=")
        .setODValue(oDvalue)
        .build();
  }

  /**
   * create value for attribute
   */
  public static String createValue(CDWrapper cdw, Optional<CDTypeDiff> cDTypeDiff, String type,
      Boolean isEnumClass) {
    String result;
    String cDTypeWrapperKey = "CDWrapperEnum_" + type;
    if (cdw.getCDTypeWrapperGroup().containsKey(cDTypeWrapperKey)) {
      if (isEnumClass) {
        result = cDTypeDiff.get().getWhichAttributesDiff().get().get(0);
      }
      else {
        result = ((ASTCDEnum) cdw.getCDTypeWrapperGroup()
            .get(cDTypeWrapperKey)
            .getEditedElement()).getCDEnumConstantList().get(0).getName();
      }
    }
    else {
      result = "some_type_" + type;
    }
    return result;
  }

  /**
   * create all attributes for created object
   * distinguish the type of object, simple class or collection
   */
  public static List<ASTODAttribute> createASTODAttributeList(CDWrapper cdw,
      Optional<CDTypeDiff> cDTypeDiff, CDTypeWrapper cDTypeWrapper) {
    List<ASTODAttribute> astodAttributeList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : cdw.getCDTypeWrapperGroup()
        .get(cDTypeWrapper.getName())
        .getEditedElement()
        .getCDAttributeList()) {
      // set attribute
      if (Pattern.matches("List<(.*)>", astcdAttribute.printType())) {
        // List<> attribute
        Matcher listMatcher = Pattern.compile("List<(.*)>").matcher(astcdAttribute.printType());
        if (listMatcher.find()) {
          String value = createValue(cdw, cDTypeDiff, listMatcher.group(1), false);
          ASTODList oDvalue = OD4DataMill.oDListBuilder()
              .addODValue(OD4DataMill.oDSimpleAttributeValueBuilder()
                  .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build())
                  .build())
              .addODValue(OD4DataMill.oDAbsentBuilder().build())
              .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      }
      else if (Pattern.matches("Set<(.*)>", astcdAttribute.printType())) {
        // Set<> attribute
        Matcher setMatcher = Pattern.compile("Set<(.*)>").matcher(astcdAttribute.printType());
        if (setMatcher.find()) {
          String value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), false);
          ASTODSimpleAttributeValue oDvalue = OD4DataMill.oDSimpleAttributeValueBuilder()
              .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build())
              .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      }
      else if (Pattern.matches("Optional<(.*)>", astcdAttribute.printType())) {
        // Optional<> attribute
        Matcher optMatcher = Pattern.compile("Optional<(.*)>").matcher(astcdAttribute.printType());
        if (optMatcher.find()) {
          String value = createValue(cdw, cDTypeDiff, optMatcher.group(1), false);
          ASTODSimpleAttributeValue oDvalue = OD4DataMill.oDSimpleAttributeValueBuilder()
              .setExpression(OD4DataMill.nameExpressionBuilder().setName(value).build())
              .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      }
      else if (Pattern.matches("Map<(.*),(.*)>", astcdAttribute.printType())) {
        // Map<,> attribute
        Matcher mapMatcher = Pattern.compile("Map<(.*),(.*)>").matcher(astcdAttribute.printType());
        if (mapMatcher.find()) {
          String kValue = createValue(cdw, cDTypeDiff, mapMatcher.group(1), false);
          String vValue = createValue(cdw, cDTypeDiff, mapMatcher.group(2), false);
          ASTODMap oDvalue = OD4DataMill.oDMapBuilder()
              .addODMapElement(OD4DataMill.oDMapElementBuilder()
                  .setKey(OD4DataMill.oDSimpleAttributeValueBuilder()
                      .setExpression(OD4DataMill.nameExpressionBuilder().setName(kValue).build())
                      .build())
                  .setVal(OD4DataMill.oDSimpleAttributeValueBuilder()
                      .setExpression(OD4DataMill.nameExpressionBuilder().setName(vValue).build())
                      .build())
                  .build())
              .addODMapElement(OD4DataMill.oDMapElementBuilder()
                  .setKey(OD4DataMill.oDAbsentBuilder().build())
                  .setVal(OD4DataMill.oDAbsentBuilder().build())
                  .build())
              .build();
          astodAttributeList.add(createASTODAttribute(astcdAttribute, oDvalue));
        }

      }
      else {
        // normal attribute
        String value;
        if (cDTypeDiff.isPresent()) {
          if (cDTypeDiff.get().getCDDiffKind() == CDSyntaxDiff.CDTypeDiffKind.CDDIFF_ENUM) {
            value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), true);
          }
          else {
            value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), false);
          }
        }
        else {
          value = createValue(cdw, cDTypeDiff, astcdAttribute.printType(), false);
        }
        ASTODSimpleAttributeValue oDvalue = OD4DataMill.oDSimpleAttributeValueBuilder()
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
  public static CDWrapperObjectPack createObject(CDWrapper cdw, Optional<CDTypeDiff> cDTypeDiff,
      CDTypeWrapper cDTypeWrapper, int index) {

    // if this CDTypeWrapper is interface or abstract class, then find a simple class on
    // inheritancePath
    CDTypeWrapper newCDTypeWrapper;
    if (cDTypeWrapper.getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE
        || cDTypeWrapper.getCDWrapperKind()
        == CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
      newCDTypeWrapper = getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper,
          cdw.getInheritanceGraph(), cdw.getCDTypeWrapperGroup()).get(0);
    }
    else {
      newCDTypeWrapper = cDTypeWrapper;
    }

    // set attributes
    List<ASTODAttribute> astodAttributeList = createASTODAttributeList(cdw, cDTypeDiff,
        newCDTypeWrapper);

    // set objects
    ASTODNamedObject astodNamedObject = OD4DataMill.oDNamedObjectBuilder()
        .setName(
            toLowerCaseFirstOne4ClassName(newCDTypeWrapper.getOriginalClassName()) + "_" + index)
        .setModifier(OD4DataMill.modifierBuilder().build())
        .setMCObjectType(OD4DataMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(
                MCQualifiedNameFacade.createQualifiedName(newCDTypeWrapper.getOriginalClassName()))
            .build())
        .setODAttributesList(astodAttributeList)
        .build();

    return new CDWrapperObjectPack(astodNamedObject, newCDTypeWrapper);
  }

  /**
   * create all objects that should be used in an OD
   */
  public static List<ASTODNamedObject> createObjectList(CDWrapper cdw,
      Optional<CDTypeDiff> cDTypeDiff, CDTypeWrapper offerCDTypeWrapper, int cardinalityCount,
      Deque<ASTODClassStackPack> classStack4TargetClass,
      Deque<ASTODClassStackPack> classStack4SourceClass) {
    List<ASTODNamedObject> astodNamedObjectList = new LinkedList<>();
    List<ASTODNamedObject> tempList = new LinkedList<>();
    CDTypeWrapper actualCDTypeWrapper = offerCDTypeWrapper;
    for (int i = 0; i < cardinalityCount; i++) {
      // set objects
      CDWrapperObjectPack objectPack = createObject(cdw, cDTypeDiff, offerCDTypeWrapper, i);
      actualCDTypeWrapper = objectPack.getCDTypeWrapper();
      astodNamedObjectList.add(objectPack.getNamedObject());
      tempList.add(objectPack.getNamedObject());
    }
    classStack4TargetClass.push(new ASTODClassStackPack(tempList, actualCDTypeWrapper));
    classStack4SourceClass.push(new ASTODClassStackPack(tempList, actualCDTypeWrapper));
    return astodNamedObjectList;
  }

  /**
   * create a link between two created objects
   */
  public static ASTODLink createLink(ASTODNamedObject left, ASTODNamedObject right,
      String leftRoleName, String rightRoleName, ASTODLinkDirection astodLinkDirection) {
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
      List<ASTODNamedObject> rightElementList, String leftRoleName, String rightRoleName,
      int directionType) {
    List<ASTODLink> linkElementList = new LinkedList<>();
    if (leftElementList.size() != 0 && rightElementList.size() != 0) {
      switch (directionType) {
        case 1:
          leftElementList.forEach(left -> {
            rightElementList.forEach(right -> {
              // set link
              linkElementList.add(createLink(left, right, leftRoleName, rightRoleName,
                  ODLinkMill.oDLeftToRightDirBuilder().build()));
            });
          });
          break;
        case 2:
          leftElementList.forEach(left -> {
            rightElementList.forEach(right -> {
              // set link
              linkElementList.add(createLink(right, left, rightRoleName, leftRoleName,
                  ODLinkMill.oDLeftToRightDirBuilder().build()));
            });
          });
          break;
        case 3:
          leftElementList.forEach(left -> {
            rightElementList.forEach(right -> {
              // set link
              linkElementList.add(createLink(left, right, leftRoleName, rightRoleName,
                  ODLinkMill.oDLeftToRightDirBuilder().build()));
              linkElementList.add(createLink(right, left, rightRoleName, leftRoleName,
                  ODLinkMill.oDLeftToRightDirBuilder().build()));
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
  public static List<ASTODLink> createLinkList(ASTODNamedObject left, ASTODNamedObject right,
      String leftRoleName, String rightRoleName, int directionType) {
    return createLinkList(List.of(left), List.of(right), leftRoleName, rightRoleName,
        directionType);
  }

  /**
   * the created object as target class
   * find all related associations of this created object
   */
  public static List<CDAssociationWrapper> findAllCDAssociationWrapperByTargetClass(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper) {
    List<CDAssociationWrapper> result = new LinkedList<>();
    Map<String, CDAssociationWrapper> cDAssociationWrapperMap =
        fuzzySearchCDAssociationWrapperByClassName(
        cdw.getCDAssociationWrapperGroup(), cDTypeWrapper.getOriginalClassName());
    cDAssociationWrapperMap.forEach((name, assoc) -> {
      // CDTypeWrapper <-
      if (assoc.getCDWrapperLeftClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
        result.add(assoc);
      }
      // -> CDTypeWrapper
      if (assoc.getCDWrapperRightClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.LEFT_TO_RIGHT) {
        result.add(assoc);
      }
      // <->  --
      if (assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.BIDIRECTIONAL
          || assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.UNDEFINED) {
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
  public static List<CDAssociationWrapper> findAllCDAssociationWrapperBySourceClass(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper) {
    List<CDAssociationWrapper> result = new ArrayList<>();
    Map<String, CDAssociationWrapper> cDAssociationWrapperMap =
        fuzzySearchCDAssociationWrapperByClassName(
        cdw.getCDAssociationWrapperGroup(), cDTypeWrapper.getOriginalClassName());
    cDAssociationWrapperMap.forEach((name, assoc) -> {
      // <- CDTypeWrapper
      if (assoc.getCDWrapperRightClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
        result.add(assoc);
      }
      // CDTypeWrapper ->
      if (assoc.getCDWrapperLeftClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.LEFT_TO_RIGHT) {
        result.add(assoc);
      }
      // <->  --
      if (assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.BIDIRECTIONAL
          || assoc.getCDAssociationWrapperDirection()
          == CDWrapper.CDAssociationWrapperDirection.UNDEFINED) {
        result.add(assoc);
      }
    });

    //    Collections.reverse(result);
    return result;
  }

  /**
   * check the object of given CDTypeWrapper whether is in ASTODElementList
   */
  public static boolean isPresentObjectInASTODElementListByCDTypeWrapper(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper, ASTODPack astodPack) {
    AtomicBoolean isInList = new AtomicBoolean(false);

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    // if this CDTypeWrapper is interface or abstract class,
    // check the subclass of this CDTypeWrapper whether is in objectList.
    if (cDTypeWrapper.getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE
        || cDTypeWrapper.getCDWrapperKind()
        == CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
      List<CDTypeWrapper> subClassList = getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper,
          cdw.getInheritanceGraph(), cdw.getCDTypeWrapperGroup());
      subClassList.forEach(c -> {
        objectList.forEach(e -> {
          if (e.getName().split("_")[0].equals(
              toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
            isInList.set(true);
          }
        });
      });
    }
    else {
      objectList.forEach(e -> {
        if (e.getName().split("_")[0].equals(
            toLowerCaseFirstOne4ClassName(cDTypeWrapper.getOriginalClassName()))) {
          isInList.set(true);
        }
      });
    }

    return isInList.get();
  }

  /**
   * check the object of given CDTypeWrapper whether is in ASTODElementList
   * if it is in ASTODElementList, return the existed ASTODElement
   * if it is not in ASTODElementList, create a new ASTODElement as return element.
   *
   * @return: ASTODNamedObjectPack
   * {  "objectList"  : List<ASTODNamedObject>
   *    "isInList"    : boolean                 }
   */
  public static ASTODNamedObjectPack getObjectInASTODElementListByCDTypeWrapper(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper, ASTODPack astodPack) {
    AtomicBoolean isInList = new AtomicBoolean(false);
    AtomicReference<List<ASTODNamedObject>> resultList = new AtomicReference<>(new ArrayList<>());

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    // if this CDTypeWrapper is interface or abstract class,
    // check the subclass of this CDTypeWrapper whether is in objectList.
    if (cDTypeWrapper.getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE
        || cDTypeWrapper.getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
      List<CDTypeWrapper> subClassList = getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper,
          cdw.getInheritanceGraph(), cdw.getCDTypeWrapperGroup());
      subClassList.forEach(c -> {
        objectList.forEach(e -> {
          if (e.getName().split("_")[0].equals(
              toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
            List<ASTODNamedObject> tempList = resultList.get();
            tempList.add(e);
            resultList.set(tempList);
            isInList.set(true);
          }
        });
      });
    }
    else {
      objectList.forEach(e -> {
        if (e.getName().split("_")[0].equals(
            toLowerCaseFirstOne4ClassName(cDTypeWrapper.getOriginalClassName()))) {
          List<ASTODNamedObject> tempList = resultList.get();
          tempList.add(e);
          resultList.set(tempList);
          isInList.set(true);
        }
      });
    }

    // create a new ASTODNamedObject if the object of given CDTypeWrapper is not in ASTODElementList
    if (!isInList.get()) {

      // determine the class of new ASTODNamedObject
      CDTypeWrapper newCDTypeWrapper;
      if (cDTypeWrapper.getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_INTERFACE
          || cDTypeWrapper.getCDWrapperKind() == CDWrapper.CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
        List<CDTypeWrapper> CDTypeWrapperList = getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper,
            cdw.getInheritanceGraph(), cdw.getCDTypeWrapperGroup());
        newCDTypeWrapper = CDTypeWrapperList.get(CDTypeWrapperList.size() - 1);
      }
      else {
        newCDTypeWrapper = cDTypeWrapper;
      }
      // put new object into resultList
      List<ASTODNamedObject> tempList = resultList.get();
      tempList.add(createObject(cdw, Optional.empty(), newCDTypeWrapper, 0).getNamedObject());

      resultList.set(tempList);
    }

    return new ASTODNamedObjectPack(resultList.get(), isInList.get());
  }

  /**
   * get the other side class in CDAssociationWrapper
   * if the given CDAssociationWrapper is self-loop, that is no problem.
   * return the found the other side class and it's positon side.
   *
   * @return: CDTypeWrapperPack
   * {  "otherSideClass" : CDTypeWrapper
   *    "position"       : ["left", "right"] }
   */
  public static CDTypeWrapperPack findOtherSideClassAndPositionInCDAssociationWrapper(
      CDAssociationWrapper CDAssociationWrapper, CDTypeWrapper currentClass) {
    CDTypeWrapperPack.Position position;
    CDTypeWrapper otherSideClass;
    if (CDAssociationWrapper.getCDWrapperLeftClass()
        .getOriginalClassName()
        .equals(currentClass.getOriginalClassName())) {
      otherSideClass = CDAssociationWrapper.getCDWrapperRightClass();
      position = CDTypeWrapperPack.Position.RIGHT;
    }
    else {
      otherSideClass = CDAssociationWrapper.getCDWrapperLeftClass();
      position = CDTypeWrapperPack.Position.LEFT;
    }
    return new CDTypeWrapperPack(otherSideClass, position);
  }

  /**
   * generate ASTODArtifact
   */
  public static ASTODArtifact generateASTODArtifact(List<ASTODElement> astodElementList, String odTitle) {
    // set ASTObjectDiagram
    ASTObjectDiagram objectDiagram = OD4DataMill.objectDiagramBuilder()
        .setName(odTitle)
        .setODElementsList(astodElementList)
        .build();

    ASTODArtifact astodArtifact = OD4DataMill.oDArtifactBuilder()
        .setObjectDiagram(objectDiagram)
        .build();

    return astodArtifact;
  }

  /**
   * using pretty printer to print OD
   */
  public static String printOD(ASTODArtifact astodArtifact) {
    // pretty print the AST
    return new OD4DataFullPrettyPrinter(new IndentPrinter()).prettyprint(astodArtifact);
  }

  /**
   * using pretty printer to print OD
   */
  public static List<String> printOD(List<ASTODArtifact> astodArtifacts) {
    // pretty print the AST
    List<String> result = new ArrayList<>();
    for (ASTODArtifact od : astodArtifacts) {
      result.add(new OD4DataFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    }
    return result;
  }

  /**
   * generate OD title for semantic difference with association
   */
  public static String generateODTitle(CDAssociationDiff association, int index) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("OD_");
    stringBuilder.append(index);
    stringBuilder.append("-[");

    // set assoc name
    stringBuilder.append(association.getName(true));
    stringBuilder.append("]-[");

    // set assoc type
    stringBuilder.append(association.getCDDiffCategory().toString().toLowerCase());
    stringBuilder.append("]");

    // set which part
    if (association.getWhichPartDiff().isPresent()) {
      if (association.getWhichPartDiff().get() != CDSyntaxDiff.WhichPartDiff.DIRECTION) {
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
  public static String generateODTitle(CDTypeDiff cDTypeDiff, int index) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("OD_");
    stringBuilder.append(index);
    stringBuilder.append("-[");

    // set assoc name
    stringBuilder.append(cDTypeDiff.getName(true));
    stringBuilder.append("]-[");

    // set assoc type
    stringBuilder.append(cDTypeDiff.getCDDiffCategory().toString().toLowerCase());
    stringBuilder.append("]");

    // set which part
    if (cDTypeDiff.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.EDITED) {
      if (cDTypeDiff.getWhichAttributesDiff().isPresent()) {
        stringBuilder.append("-");
        stringBuilder.append(cDTypeDiff.getWhichAttributesDiff().get());
      }
    }

    return stringBuilder.toString();
  }

}
