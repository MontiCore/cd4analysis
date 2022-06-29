package de.monticore.sydiff2semdiff.cg2od;

import de.monticore.od4data.OD4DataMill;
import de.monticore.od4data.prettyprinter.OD4DataFullPrettyPrinter;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.*;
import static de.monticore.sydiff2semdiff.cg2od.GenerateODHelper.*;

public class CG2ODGenerator {

  /**
   * generate ODs by solving items with syntactic differences in globalClassQueue and globalAssociationQueue
   */
  public List<String> generateObjectDiagrams(DifferentGroup dg, CompareGroup cg) {
    List<String> oDResultList = new ArrayList<>();

    Deque<CompClass> globalClassQueue = cg.getCompClassResultQueueWithDiff();
    Deque<CompAssociation> globalAssociationQueue = cg.getCompAssociationResultQueueWithDiff();

    // solve globalAssociationQueue
    int i = 1;
    while (!globalAssociationQueue.isEmpty()) {
      CompAssociation currentCompAssociation = globalAssociationQueue.pop();
      List<ASTODElement> currentElementList = generateODByAssociation(dg, currentCompAssociation);
      if (currentElementList == null) {
        continue;
      }
      currentElementList = organizeASTODElementList(currentElementList);
      String odTitle = generateODTitle(currentCompAssociation, i);
      oDResultList.add(printOD(currentElementList, odTitle));
      i++;
    }

    // solve globalClassQueue
    i = 1;
    while (!globalClassQueue.isEmpty()) {
      CompClass currentCompClass = globalClassQueue.pop();
      List<ASTODElement> currentElementList = generateODByClass(dg, currentCompClass);
      if (currentElementList == null) {
        continue;
      }
      currentElementList = organizeASTODElementList(currentElementList);
      String odTitle = generateODTitle(currentCompClass, i);
      oDResultList.add(printOD(currentElementList, odTitle));
      i++;
    }

    return oDResultList;
  }

  /**
   * using pretty printer to print OD
   */
  public String printOD(List<ASTODElement> astodElementList, String odTitle) {
    // set ASTObjectDiagram
    ASTObjectDiagram objectDiagram = OD4DataMill.objectDiagramBuilder()
      .setName(odTitle)
      .setODElementsList(astodElementList)
      .build();

    ASTODArtifact astodArtifact = OD4DataMill.oDArtifactBuilder()
      .setObjectDiagram(objectDiagram)
      .build();

    // pretty print the AST
    String ppResult = new OD4DataFullPrettyPrinter(new IndentPrinter()).prettyprint(
      astodArtifact);

    return ppResult;
  }

  /**
   * solve the item in globalClassQueue
   */
  protected List<ASTODElement> generateODByClass(DifferentGroup dg, CompClass compClass) {
    Deque<Map<String, Object>> classStack4TargetClass = new LinkedList<>();
    Deque<Map<String, Object>> classStack4SourceClass = new LinkedList<>();
    Deque<DiffAssociation> associationStack4TargetClass = new LinkedList<>();
    Deque<DiffAssociation> associationStack4SourceClass = new LinkedList<>();
    Map<DiffRefSetAssociation, Integer> refLinkCheckList = convertRefSetAssociationList2CheckList(dg.getRefSetAssociationList());

    // initial for generate ODs
    List<ASTODElement> astodElementList = initGenerateODByClass(dg, compClass, classStack4TargetClass, classStack4SourceClass);

    // using basic process
    astodElementList = generateODBasicProcess(dg, "target", classStack4TargetClass, associationStack4TargetClass, refLinkCheckList, astodElementList);
    if (astodElementList != null) {
      return generateODBasicProcess(dg, "source", classStack4SourceClass, associationStack4SourceClass, refLinkCheckList, astodElementList);
    } else {
      return null;
    }
  }

  /**
   * initial step for item in globalClassQueue
   *
   * create an object that its corresponding class has syntactic differences and
   * put this object into ASTODElement list
   */
  public List<ASTODElement> initGenerateODByClass(DifferentGroup dg, CompClass compClass, Deque<Map<String, Object>> classStack4TargetClass, Deque<Map<String, Object>> classStack4SourceClass) {
    // get the necessary information
    DiffClass diffClass = compClass.getOriginalElement();
    List<ASTODElement> astodElementList = new LinkedList<>();

    // create ASTODElement list
    if (diffClass.getDiffKind() != DifferentGroup.DiffClassKind.DIFF_ENUM) {
      // simple class, abstract class, interface
      astodElementList.addAll(createObjectList(dg, Optional.of(compClass), diffClass, 1, classStack4TargetClass, classStack4SourceClass));
    } else {
      // enum
      DiffClass diffClassUsingEnum = dg.getDiffClassGroup().get(diffClass.getDiffLink4EnumClass().iterator().next());
      astodElementList.addAll(createObjectList(dg, Optional.of(compClass), diffClassUsingEnum, 1, classStack4TargetClass, classStack4SourceClass));
    }
    return astodElementList;
  }

  /**
   * solve the item in globalAssociationQueue
   */
  protected List<ASTODElement> generateODByAssociation(DifferentGroup dg, CompAssociation compAssociation) {

    Deque<Map<String, Object>> classStack4TargetClass = new LinkedList<>();
    Deque<Map<String, Object>> classStack4SourceClass = new LinkedList<>();
    Deque<DiffAssociation> associationStack4TargetClass = new LinkedList<>();
    Deque<DiffAssociation> associationStack4SourceClass = new LinkedList<>();
    Map<DiffRefSetAssociation, Integer> refLinkCheckList = convertRefSetAssociationList2CheckList(dg.getRefSetAssociationList());

    // initial for generate ODs
    List<ASTODElement> astodElementList = initGenerateODByAssociation(dg, compAssociation, classStack4TargetClass, classStack4SourceClass);
    Optional<List<DiffRefSetAssociation>> optInitDiffRefSetAssociation = findRelatedDiffRefSetAssociationByDiffAssociation(compAssociation.getOriginalElement(), refLinkCheckList);
    refLinkCheckList = updateCounterInCheckList(optInitDiffRefSetAssociation, refLinkCheckList);

    // using basic process
    astodElementList = generateODBasicProcess(dg, "target", classStack4TargetClass, associationStack4TargetClass, refLinkCheckList, astodElementList);
    if (astodElementList != null) {
      astodElementList = generateODBasicProcess(dg, "source", classStack4SourceClass, associationStack4SourceClass, refLinkCheckList, astodElementList);
      return astodElementList;
    } else {
      return null;
    }
  }

  /**
   * initial step for item in globalAssociationQueue
   *
   * create objects that its corresponding association has syntactic differences in direction, left cardinality and right cardinalit then
   * create corresponding link among those objects and
   * put those objects into ASTODElement list
   */
  public List<ASTODElement> initGenerateODByAssociation(DifferentGroup dg, CompAssociation compAssociation, Deque<Map<String, Object>> classStack4TargetClass, Deque<Map<String, Object>> classStack4SourceClass) {

    DiffAssociation currentDiffAssoc = intersectDiffAssociationCardinalityByDiffAssociationOnlyWithLeftToRightAndRightToLeft(compAssociation.getOriginalElement(), dg);

    // get the necessary information
    DiffClass leftDiffClass = currentDiffAssoc.getDiffLeftClass();
    DiffClass rightDiffClass = currentDiffAssoc.getDiffRightClass();
    String leftRoleName = currentDiffAssoc.getDiffLeftClassRoleName();
    String rightRoleName = currentDiffAssoc.getDiffRightClassRoleName();
    int directionType = 0;
    int leftCardinalityCount = 0;
    int rightCardinalityCount = 0;

    switch (compAssociation.getCompCategory()) {
      case DELETED:
        directionType = mappingDirection(currentDiffAssoc.getDiffDirection().toString());
        leftCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getDiffLeftClassCardinality().toString());
        rightCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getDiffRightClassCardinality().toString());
        break;
      case DIRECTION_CHANGED:
        directionType = mappingDirection(compAssociation.getCompDirectionResult().get().toString());
        leftCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getDiffLeftClassCardinality().toString());
        rightCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getDiffRightClassCardinality().toString());
        break;
      case CARDINALITY_CHANGED:
        directionType = mappingDirection(currentDiffAssoc.getDiffDirection().toString());
        switch (compAssociation.getWhichPartDiff().get()) {
          case LEFT_CARDINALITY:
            leftCardinalityCount = mappingCardinality(compAssociation.getCompLeftClassCardinalityResult().get().toString());
            rightCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getDiffRightClassCardinality().toString());
            break;
          case RIGHT_CARDINALITY:
            leftCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getDiffLeftClassCardinality().toString());
            rightCardinalityCount = mappingCardinality(compAssociation.getCompRightClassCardinalityResult().get().toString());
            break;
        }
        break;
    }

    // create ASTODElement list
    List<ASTODNamedObject> leftElementList = createObjectList(dg, Optional.empty(), leftDiffClass, leftCardinalityCount, classStack4TargetClass, classStack4SourceClass);
    List<ASTODNamedObject> rightElementList = createObjectList(dg, Optional.empty(), rightDiffClass, rightCardinalityCount, classStack4TargetClass, classStack4SourceClass);
    List<ASTODLink> linkElementList = createLinkList(leftElementList, rightElementList, leftRoleName, rightRoleName, directionType);

    // remove duplicate
    for (int i = 0; i < leftElementList.size(); i++) {
      for (int j = 0; j < rightElementList.size(); j++) {
        if (leftElementList.get(i).deepEquals(rightElementList.get(j))) {
          rightElementList.remove(j);
        }
      }
    }

    List<ASTODElement> astodElementList = new LinkedList<>();
    astodElementList.addAll(leftElementList);
    astodElementList.addAll(rightElementList);
    astodElementList.addAll(linkElementList);

    return astodElementList;
  }

  /**
   * The general step for generating ODs after initial step for class and association
   *
   * First consider the created objects in initial step as target class and find source classes and so on
   * Second consider the created objects in initial step as source class and find target classes and so on
   * All generated ASTODElement will be added into the same ASTODElement list
   *
   * situation 1:
   *    ... [o_i] "->" / "<-"/ "<->" [o_0] "->" / "<-"/ "<->" [o_j] ...
   *    There is no conflict and successfully generating ODs
   *
   * situation 2: (initial object as target class)
   *    [o_j1] -> ... -> [o_i1]
   *                          \
   *                           -> [o_0] -> ?? [o_j] ??
   *                          /
   *    [o_j2] -> ... -> [o_i2]
   *    There is a conflict that [1] o_0 "->" / "<-"/ "<->" o_j [1]
   *    But this conflict is also present in the original CD, so this semantic difference should be removed.
   *    That means current syntactic difference for association can not lead to semantic difference.
   *
   * situation 3: (initial object as source class)
   *           -> [o_i1] -> ... -> [o_j1]
   *         /                           \
   *    [o_0]                             -> ?? [o_0] ??
   *         \                           /
   *           -> [o_i2] -> ... -> [o_j2]
   *    There is a conflict that [1] o_0 "->" / "<-"/ "<->" o_j [1]
   *    But this conflict is also present in the original CD, so this semantic difference should be removed.
   *    That means current syntactic difference for association can not lead to semantic difference.
   */
  protected List<ASTODElement> generateODBasicProcess(DifferentGroup dg, String opt4StartClassKind, Deque<Map<String, Object>> classStack, Deque<DiffAssociation> associationStack, Map<DiffRefSetAssociation, Integer> refLinkCheckList, List<ASTODElement> astodElementList) {
    // start basic process for compClass and compAssociation

    DiffClass currentDiffClass = null;
    while (!classStack.isEmpty()) {
      Map<String, Object> diffClassMap = classStack.pop();
      List<ASTODNamedObject> objectList = (List<ASTODNamedObject>) diffClassMap.get("objectList");
      DiffClass diffClass = (DiffClass) diffClassMap.get("diffClass");
      currentDiffClass = diffClass;
      if (opt4StartClassKind.equals("target")) {
        findAllDiffAssociationByTargetClass(dg, diffClass).forEach(e -> associationStack.push(e));
      } else {
        findAllDiffAssociationBySourceClass(dg, diffClass).forEach(e -> associationStack.push(e));
      }

      while (!associationStack.isEmpty()) {
        DiffAssociation currentDiffAssociation =
          intersectDiffAssociationCardinalityByDiffAssociationOnlyWithLeftToRightAndRightToLeft(associationStack.pop(), dg);
        if (!checkRelatedDiffRefSetAssociationIsUsed(currentDiffAssociation, refLinkCheckList)) {

          // get the information of currentSideClass and otherSideClass

          Map<String, Object> otherSideClassMap = findOtherSideClassAndPositionInDiffAssociation(currentDiffAssociation, currentDiffClass);

          List<ASTODNamedObject> currentSideObjectList = objectList;
          DiffClass otherSideClass = (DiffClass) otherSideClassMap.get("otherSideClass");
          DiffClass currentSideClass = currentDiffClass;
          String otherSideRoleName = null;
          String currentSideRoleName = null;
          int directionType = 0;
          boolean isPresentOtherSideCardinality = false;

          String otherSideClassPosition = (String) otherSideClassMap.get("position");
          if (otherSideClassPosition.equals("left")) {
            otherSideRoleName = currentDiffAssociation.getDiffLeftClassRoleName();
            currentSideRoleName = currentDiffAssociation.getDiffRightClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 1 : 2;
            if (isPresentObjectInASTODElementListByDiffClass(dg, otherSideClass, astodElementList)) {
              isPresentOtherSideCardinality = true;
            } else {
              isPresentOtherSideCardinality = mappingCardinality(currentDiffAssociation.getDiffLeftClassCardinality().toString()) > 0 ? true : false;
            }
          } else {
            otherSideRoleName = currentDiffAssociation.getDiffRightClassRoleName();
            currentSideRoleName = currentDiffAssociation.getDiffLeftClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 2 : 1;
            if (isPresentObjectInASTODElementListByDiffClass(dg, otherSideClass, astodElementList)) {
              isPresentOtherSideCardinality = true;
            } else {
              isPresentOtherSideCardinality = mappingCardinality(currentDiffAssociation.getDiffRightClassCardinality().toString()) > 0 ? true : false;
            }
          }
          if (mappingDirection(currentDiffAssociation.getDiffDirection().toString()) == 3) {
            directionType = 3;
          }

          // determine if a new connection needs to be added by otherSideCardinalityCount
          if (isPresentOtherSideCardinality) {

            // get otherSideObject
            Map<String, Object> otherSideObjectMap = getObjectInASTODElementListByDiffClass(dg, otherSideClass, astodElementList);
            List<ASTODNamedObject> otherSideObjectList = (List<ASTODNamedObject>) otherSideObjectMap.get("objectList");

            // determining illegal situations
            // create an object map according the class of Object
            Map<String, List<ASTODNamedObject>> otherSideObjectGroupByClass = otherSideObjectList
              .stream()
              .collect(Collectors.groupingBy(e -> e.getName().split("_")[0]));
            if (opt4StartClassKind.equals("target")) {
              boolean isTwoObjectsInOneClass = otherSideObjectGroupByClass.values().stream().anyMatch(e -> e.size() >= 2);
              /* if there are two objects in one class and those two objects are not newly created,
               * then the object association graph of this semantic difference can not be created,
               * because this object association graph with current cardinality is not valid in the original CD.
               */
              if (isTwoObjectsInOneClass && (Boolean) otherSideObjectMap.get("isInList") == true) {
                return null;
              }
            } else {
              boolean isTwoObjectsInOneClass4SourceSide = currentSideObjectList.size() == 2 ? true : false;
              boolean isOneObjectsInOneClass4TargetSide = otherSideObjectGroupByClass.values().stream().anyMatch(e -> e.size() == 1);
              if (isTwoObjectsInOneClass4SourceSide && (Boolean) otherSideObjectMap.get("isInList") == true && isOneObjectsInOneClass4TargetSide) {
                return null;
              }
            }

            if (currentSideObjectList.size() == 1) {
              ASTODNamedObject otherSideObject0 = otherSideObjectList.get(0);
              if ((Boolean) otherSideObjectMap.get("isInList") == false) {
                // add new object into ASTODElementList
                astodElementList.add(otherSideObject0);
                // push sourceClass into classStack
                classStack.push(Map.of("objectList", List.of(otherSideObject0), "diffClass", otherSideClass));
              }
              // create new ASTODLinkList and add into ASTODElementList
              List<ASTODLink> linkList0;
              if (otherSideClassPosition.equals("left")) {
                linkList0 = createLinkList(otherSideObject0, currentSideObjectList.get(0), otherSideRoleName, currentSideRoleName, directionType);
              } else {
                linkList0 = createLinkList(currentSideObjectList.get(0), otherSideObject0, currentSideRoleName, otherSideRoleName, directionType);
              }
              astodElementList.addAll(linkList0);
            } else if (currentSideObjectList.size() == 2) {
              ASTODNamedObject otherSideObject0 = otherSideObjectList.get(0);
              ASTODNamedObject otherSideObject1 = otherSideObject0.deepClone();
              String objectName = otherSideObject1.getName().split("_")[0];
              otherSideObject1.setName(objectName + "_1");
              if ((Boolean) otherSideObjectMap.get("isInList") == false) {
                // add new object into ASTODElementList
                astodElementList.add(otherSideObject0);
                astodElementList.add(otherSideObject1);
                // push sourceClass into classStack
                classStack.push(Map.of("objectList", List.of(otherSideObject0, otherSideObject1), "diffClass", otherSideClass));
              }
              // create new ASTODLinkList and add into ASTODElementList
              List<ASTODLink> linkList0;
              List<ASTODLink> linkList1;
              if (otherSideClassPosition.equals("left")) {
                linkList0 = createLinkList(otherSideObject0, currentSideObjectList.get(0), otherSideRoleName, currentSideRoleName, directionType);
                linkList1 = createLinkList(otherSideObject1, currentSideObjectList.get(1), otherSideRoleName, currentSideRoleName, directionType);
              } else {
                linkList0 = createLinkList(currentSideObjectList.get(0), otherSideObject0, currentSideRoleName, otherSideRoleName, directionType);
                linkList1 = createLinkList(currentSideObjectList.get(1), otherSideObject1, currentSideRoleName, otherSideRoleName, directionType);
              }
              astodElementList.addAll(linkList0);
              astodElementList.addAll(linkList1);
            }

            // mark related refLink
            Optional<List<DiffRefSetAssociation>> optCurrentDiffRefSetAssociation = findRelatedDiffRefSetAssociationByDiffAssociation(currentDiffAssociation, refLinkCheckList);
            refLinkCheckList = updateCounterInCheckList(optCurrentDiffRefSetAssociation, refLinkCheckList);
          }
        }
      }
    }

    return astodElementList;
  }
}
