package de.monticore.sydiff2semdiff.cg2od;

import de.monticore.od4data.OD4DataMill;
import de.monticore.od4data.prettyprinter.OD4DataFullPrettyPrinter;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportGroup;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompAssociation;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompClass;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompareGroup;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.*;
import static de.monticore.sydiff2semdiff.cg2od.GenerateODHelper.*;

public class CG2ODGenerator {

  /**
   * generate ODs by solving items with syntactic differences in globalClassQueue and globalAssociationQueue
   */
  public List<String> generateObjectDiagrams(SupportGroup sg, CompareGroup cg) {
    List<String> oDResultList = new ArrayList<>();

    Deque<CompClass> globalClassQueue = cg.getCompClassResultQueueWithDiff();
    Deque<CompAssociation> globalAssociationQueue = cg.getCompAssociationResultQueueWithDiff();

    // solve globalAssociationQueue
    int i = 1;
    while (!globalAssociationQueue.isEmpty()) {
      CompAssociation currentCompAssociation = globalAssociationQueue.pop();
      List<ASTODElement> currentElementList = generateODByAssociation(sg, currentCompAssociation);
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
      List<ASTODElement> currentElementList = generateODByClass(sg, currentCompClass);
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
  protected List<ASTODElement> generateODByClass(SupportGroup sg, CompClass compClass) {
    Deque<Map<String, Object>> classStack4TargetClass = new LinkedList<>();
    Deque<Map<String, Object>> classStack4SourceClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4TargetClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4SourceClass = new LinkedList<>();
    Map<SupportRefSetAssociation, Integer> refLinkCheckList = convertRefSetAssociationList2CheckList(sg.getRefSetAssociationList());

    // initial for generate ODs
    List<ASTODElement> astodElementList = initGenerateODByClass(sg, compClass, classStack4TargetClass, classStack4SourceClass);

    // using basic process
    astodElementList = generateODBasicProcess(sg, "target", classStack4TargetClass, associationStack4TargetClass, refLinkCheckList, astodElementList);
    if (astodElementList != null) {
      return generateODBasicProcess(sg, "source", classStack4SourceClass, associationStack4SourceClass, refLinkCheckList, astodElementList);
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
  public List<ASTODElement> initGenerateODByClass(SupportGroup sg, CompClass compClass, Deque<Map<String, Object>> classStack4TargetClass, Deque<Map<String, Object>> classStack4SourceClass) {
    // get the necessary information
    SupportClass supportClass = compClass.getOriginalElement();
    List<ASTODElement> astodElementList = new LinkedList<>();

    // create ASTODElement list
    if (supportClass.getSupportKind() != SupportGroup.SupportClassKind.SUPPORT_ENUM) {
      // simple class, abstract class, interface
      astodElementList.addAll(createObjectList(sg, Optional.of(compClass), supportClass, 1, classStack4TargetClass, classStack4SourceClass));
    } else {
      // enum
      SupportClass supportClassUsingEnum = sg.getSupportClassGroup().get(supportClass.getSupportLink4EnumClass().iterator().next());
      astodElementList.addAll(createObjectList(sg, Optional.of(compClass), supportClassUsingEnum, 1, classStack4TargetClass, classStack4SourceClass));
    }
    return astodElementList;
  }

  /**
   * solve the item in globalAssociationQueue
   */
  protected List<ASTODElement> generateODByAssociation(SupportGroup sg, CompAssociation compAssociation) {

    Deque<Map<String, Object>> classStack4TargetClass = new LinkedList<>();
    Deque<Map<String, Object>> classStack4SourceClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4TargetClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4SourceClass = new LinkedList<>();
    Map<SupportRefSetAssociation, Integer> refLinkCheckList = convertRefSetAssociationList2CheckList(sg.getRefSetAssociationList());

    // initial for generate ODs
    List<ASTODElement> astodElementList = initGenerateODByAssociation(sg, compAssociation, classStack4TargetClass, classStack4SourceClass);
    Optional<List<SupportRefSetAssociation>> optInitSupportRefSetAssociation = findRelatedSupportRefSetAssociationBySupportAssociation(compAssociation.getOriginalElement(), refLinkCheckList);
    refLinkCheckList = updateCounterInCheckList(optInitSupportRefSetAssociation, refLinkCheckList);

    // using basic process
    astodElementList = generateODBasicProcess(sg, "target", classStack4TargetClass, associationStack4TargetClass, refLinkCheckList, astodElementList);
    if (astodElementList != null) {
      astodElementList = generateODBasicProcess(sg, "source", classStack4SourceClass, associationStack4SourceClass, refLinkCheckList, astodElementList);
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
  public List<ASTODElement> initGenerateODByAssociation(SupportGroup sg, CompAssociation compAssociation, Deque<Map<String, Object>> classStack4TargetClass, Deque<Map<String, Object>> classStack4SourceClass) {

    SupportAssociation currentDiffAssoc = intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(compAssociation.getOriginalElement(), sg);

    // get the necessary information
    SupportClass leftSupportClass = currentDiffAssoc.getSupportLeftClass();
    SupportClass rightSupportClass = currentDiffAssoc.getSupportRightClass();
    String leftRoleName = currentDiffAssoc.getSupportLeftClassRoleName();
    String rightRoleName = currentDiffAssoc.getSupportRightClassRoleName();
    int directionType = 0;
    int leftCardinalityCount = 0;
    int rightCardinalityCount = 0;

    switch (compAssociation.getCompCategory()) {
      case DELETED:
        directionType = mappingDirection(currentDiffAssoc.getSupportDirection().toString());
        leftCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getSupportLeftClassCardinality().toString());
        rightCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getSupportRightClassCardinality().toString());
        break;
      case DIRECTION_CHANGED:
        directionType = mappingDirection(compAssociation.getCompDirectionResult().get().toString());
        leftCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getSupportLeftClassCardinality().toString());
        rightCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getSupportRightClassCardinality().toString());
        break;
      case CARDINALITY_CHANGED:
        directionType = mappingDirection(currentDiffAssoc.getSupportDirection().toString());
        switch (compAssociation.getWhichPartDiff().get()) {
          case LEFT_CARDINALITY:
            leftCardinalityCount = mappingCardinality(compAssociation.getCompLeftClassCardinalityResult().get().toString());
            rightCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getSupportRightClassCardinality().toString());
            break;
          case RIGHT_CARDINALITY:
            leftCardinalityCount = mappingCardinality4Initial(currentDiffAssoc.getSupportLeftClassCardinality().toString());
            rightCardinalityCount = mappingCardinality(compAssociation.getCompRightClassCardinalityResult().get().toString());
            break;
        }
        break;
    }

    // create ASTODElement list
    List<ASTODNamedObject> leftElementList = createObjectList(sg, Optional.empty(), leftSupportClass, leftCardinalityCount, classStack4TargetClass, classStack4SourceClass);
    List<ASTODNamedObject> rightElementList = createObjectList(sg, Optional.empty(), rightSupportClass, rightCardinalityCount, classStack4TargetClass, classStack4SourceClass);
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
  protected List<ASTODElement> generateODBasicProcess(SupportGroup sg, String opt4StartClassKind, Deque<Map<String, Object>> classStack, Deque<SupportAssociation> associationStack, Map<SupportRefSetAssociation, Integer> refLinkCheckList, List<ASTODElement> astodElementList) {
    // start basic process for compClass and compAssociation

    SupportClass currentSupportClass = null;
    while (!classStack.isEmpty()) {
      Map<String, Object> supportClassMap = classStack.pop();
      List<ASTODNamedObject> objectList = (List<ASTODNamedObject>) supportClassMap.get("objectList");
      SupportClass supportClass = (SupportClass) supportClassMap.get("supportClass");
      currentSupportClass = supportClass;
      if (opt4StartClassKind.equals("target")) {
        findAllSupportAssociationByTargetClass(sg, supportClass).forEach(e -> associationStack.push(e));
      } else {
        findAllSupportAssociationBySourceClass(sg, supportClass).forEach(e -> associationStack.push(e));
      }

      while (!associationStack.isEmpty()) {
        SupportAssociation currentSupportAssociation =
          intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(associationStack.pop(), sg);
        if (!checkRelatedSupportRefSetAssociationIsUsed(currentSupportAssociation, refLinkCheckList)) {

          // get the information of currentSideClass and otherSideClass

          Map<String, Object> otherSideClassMap = findOtherSideClassAndPositionInSupportAssociation(currentSupportAssociation, currentSupportClass);

          List<ASTODNamedObject> currentSideObjectList = objectList;
          SupportClass otherSideClass = (SupportClass) otherSideClassMap.get("otherSideClass");
          SupportClass currentSideClass = currentSupportClass;
          String otherSideRoleName = null;
          String currentSideRoleName = null;
          int directionType = 0;
          boolean isPresentOtherSideCardinality = false;

          String otherSideClassPosition = (String) otherSideClassMap.get("position");
          if (otherSideClassPosition.equals("left")) {
            otherSideRoleName = currentSupportAssociation.getSupportLeftClassRoleName();
            currentSideRoleName = currentSupportAssociation.getSupportRightClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 1 : 2;
            if (isPresentObjectInASTODElementListBySupportClass(sg, otherSideClass, astodElementList)) {
              isPresentOtherSideCardinality = true;
            } else {
              isPresentOtherSideCardinality = mappingCardinality(currentSupportAssociation.getSupportLeftClassCardinality().toString()) > 0 ? true : false;
            }
          } else {
            otherSideRoleName = currentSupportAssociation.getSupportRightClassRoleName();
            currentSideRoleName = currentSupportAssociation.getSupportLeftClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 2 : 1;
            if (isPresentObjectInASTODElementListBySupportClass(sg, otherSideClass, astodElementList)) {
              isPresentOtherSideCardinality = true;
            } else {
              isPresentOtherSideCardinality = mappingCardinality(currentSupportAssociation.getSupportRightClassCardinality().toString()) > 0 ? true : false;
            }
          }
          if (mappingDirection(currentSupportAssociation.getSupportDirection().toString()) == 3) {
            directionType = 3;
          }

          // determine if a new connection needs to be added by otherSideCardinalityCount
          if (isPresentOtherSideCardinality) {

            // get otherSideObject
            Map<String, Object> otherSideObjectMap = getObjectInASTODElementListBySupportClass(sg, otherSideClass, astodElementList);
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
                classStack.push(Map.of("objectList", List.of(otherSideObject0), "supportClass", otherSideClass));
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
                classStack.push(Map.of("objectList", List.of(otherSideObject0, otherSideObject1), "supportClass", otherSideClass));
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
            Optional<List<SupportRefSetAssociation>> optCurrentSupportRefSetAssociation = findRelatedSupportRefSetAssociationBySupportAssociation(currentSupportAssociation, refLinkCheckList);
            refLinkCheckList = updateCounterInCheckList(optCurrentSupportRefSetAssociation, refLinkCheckList);
          }
        }
      }
    }

    return astodElementList;
  }
}
