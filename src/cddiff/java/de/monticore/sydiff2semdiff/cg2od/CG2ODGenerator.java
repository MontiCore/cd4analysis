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

import static de.monticore.sydiff2semdiff.cg2od.GenerateODHelper.*;

public class CG2ODGenerator {

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

  protected List<ASTODElement> generateODByClass(DifferentGroup dg, CompClass compClass) {
    Deque<DiffClass> classStack = new LinkedList<>();
    Deque<DiffAssociation> associationStack = new LinkedList<>();
    Map<DiffRefSetAssociation, Boolean> refLinkCheckList = convertRefSetAssociationList2CheckList(dg.getRefSetAssociationList());

    // initial for generate ODs
    List<ASTODElement> astodElementList = initGenerateODByClass(dg, compClass);
    classStack.push(compClass.getOriginalElement());

    return generateODBasicProcess(dg, classStack, associationStack, refLinkCheckList, astodElementList);
  }

  public List<ASTODElement> initGenerateODByClass(DifferentGroup dg, CompClass compClass) {
    // get the necessary information
    DiffClass diffClass = compClass.getOriginalElement();
    List<ASTODElement> astodElementList = new LinkedList<>();

    // create ASTODElement list
    if (diffClass.getDiffKind() != DifferentGroup.DiffClassKind.DIFF_ENUM) {
      // simple class, abstract class, interface
      astodElementList.addAll(createObjectList(dg, Optional.of(compClass), diffClass, 1));
    } else {
      // enum
      DiffClass diffClassUsingEnum = dg.getDiffClassGroup().get(diffClass.getDiffLink4EnumClass().iterator().next());
      astodElementList.addAll(createObjectList(dg, Optional.of(compClass), diffClassUsingEnum, 1));
    }
    return astodElementList;
  }

  protected List<ASTODElement> generateODByAssociation(DifferentGroup dg, CompAssociation compAssociation) {

    Deque<DiffClass> classStack = new LinkedList<>();
    Deque<DiffAssociation> associationStack = new LinkedList<>();
    Map<DiffRefSetAssociation, Boolean> refLinkCheckList = convertRefSetAssociationList2CheckList(dg.getRefSetAssociationList());

    // initial for generate ODs
    List<ASTODElement> astodElementList = initGenerateODByAssociation(dg, compAssociation);
    classStack.push(compAssociation.getOriginalElement().getDiffLeftClass());
    classStack.push(compAssociation.getOriginalElement().getDiffRightClass());
    Optional<DiffRefSetAssociation> initDiffRefSetAssociationOpt = findRelatedDiffRefSetAssociationByDiffAssociation(compAssociation.getOriginalElement(), refLinkCheckList);
    if (!initDiffRefSetAssociationOpt.isEmpty()) {
      refLinkCheckList.put(initDiffRefSetAssociationOpt.get(), true);
    }

    // using basic process
    return generateODBasicProcess(dg, classStack, associationStack, refLinkCheckList, astodElementList);
  }

  public List<ASTODElement> initGenerateODByAssociation(DifferentGroup dg, CompAssociation compAssociation) {
    // get the necessary information
    DiffClass leftDiffClass = compAssociation.getOriginalElement().getDiffLeftClass();
    DiffClass rightDiffClass = compAssociation.getOriginalElement().getDiffRightClass();
    String leftRoleName = compAssociation.getOriginalElement().getDiffLeftClassRoleName();
    String rightRoleName = compAssociation.getOriginalElement().getDiffRightClassRoleName();
    int directionType = 0;
    int leftCardinalityCount = 0;
    int rightCardinalityCount = 0;

    switch (compAssociation.getCompCategory()) {
      case DELETED:
        directionType = mappingDirection(compAssociation.getOriginalElement().getDiffDirection().toString());
        leftCardinalityCount = mappingCardinality(compAssociation.getOriginalElement().getDiffLeftClassCardinality().toString());
        rightCardinalityCount = mappingCardinality(compAssociation.getOriginalElement().getDiffRightClassCardinality().toString());
        break;
      case DIRECTION_CHANGED:
        directionType = mappingDirection(compAssociation.getCompDirectionResult().get().toString());
        leftCardinalityCount = mappingCardinality(compAssociation.getOriginalElement().getDiffLeftClassCardinality().toString());
        rightCardinalityCount = mappingCardinality(compAssociation.getOriginalElement().getDiffRightClassCardinality().toString());
        break;
      case CARDINALITY_CHANGED:
        directionType = mappingDirection(compAssociation.getOriginalElement().getDiffDirection().toString());
        switch (compAssociation.getWhichPartDiff().get()) {
          case LEFT_CARDINALITY:
            leftCardinalityCount = mappingCardinality(compAssociation.getCompLeftClassCardinalityResult().get().toString());
            rightCardinalityCount = mappingCardinality(compAssociation.getOriginalElement().getDiffRightClassCardinality().toString());
          case RIGHT_CARDINALITY:
            leftCardinalityCount = mappingCardinality(compAssociation.getOriginalElement().getDiffLeftClassCardinality().toString());
            rightCardinalityCount = mappingCardinality(compAssociation.getCompRightClassCardinalityResult().get().toString());
        }
        break;
    }

    // create ASTODElement list
    List<ASTODNamedObject> leftElementList = createObjectList(dg, Optional.empty(), leftDiffClass, leftCardinalityCount);
    List<ASTODNamedObject> rightElementList = createObjectList(dg, Optional.empty(), rightDiffClass, rightCardinalityCount);
    List<ASTODLink> linkElementList = createLinkList(leftElementList, rightElementList, leftRoleName, rightRoleName, directionType);

    List<ASTODElement> astodElementList = new LinkedList<>();
    astodElementList.addAll(leftElementList);
    astodElementList.addAll(rightElementList);
    astodElementList.addAll(linkElementList);

    return astodElementList;
  }

  protected List<ASTODElement> generateODBasicProcess(DifferentGroup dg, Deque<DiffClass> classStack, Deque<DiffAssociation> associationStack, Map<DiffRefSetAssociation, Boolean> refLinkCheckList, List<ASTODElement> astodElementList) {
    // start basic process for compClass and compAssociation

    DiffClass currentDiffClass = null;
    while (!classStack.isEmpty()) {
      DiffClass diffClass = classStack.pop();
      currentDiffClass = diffClass;
      findAllDiffAssociationByTargetClass(dg, diffClass).forEach(e -> associationStack.push(e));

      while (!associationStack.isEmpty()) {
        DiffAssociation currentDiffAssociation = associationStack.pop();
        if (!checkRelatedDiffRefSetAssociationIsUsed(currentDiffAssociation, refLinkCheckList)) {
          // add this association link into OD

          // get the information of sourceClass and targetClass
          Map<String,Object> sourceClassMap = findSourceClassAndPositionInDiffAssociation(currentDiffAssociation, currentDiffClass);

          DiffClass sourceClass = (DiffClass) sourceClassMap.get("sourceClass");
          DiffClass targetClass = currentDiffClass;
          String sourceRoleName = null;
          String targetRoleName = null;
          int directionType = 0;
          int sourceCardinalityCount = 0;
          int targetCardinalityCount = 0;

          String sourceClassPosition = (String) sourceClassMap.get("position");
          if (sourceClassPosition.equals("left")) {
            sourceRoleName = currentDiffAssociation.getDiffLeftClassRoleName();
            targetRoleName = currentDiffAssociation.getDiffRightClassRoleName();
            directionType = 1;
            sourceCardinalityCount = mappingCardinality(currentDiffAssociation.getDiffLeftClassCardinality().toString());
            targetCardinalityCount = mappingCardinality(currentDiffAssociation.getDiffRightClassCardinality().toString());
          } else {
            sourceRoleName = currentDiffAssociation.getDiffRightClassRoleName();
            targetRoleName = currentDiffAssociation.getDiffLeftClassRoleName();
            directionType = 2;
            sourceCardinalityCount = mappingCardinality(currentDiffAssociation.getDiffRightClassCardinality().toString());
            targetCardinalityCount = mappingCardinality(currentDiffAssociation.getDiffLeftClassCardinality().toString());
          }
          if (mappingDirection(currentDiffAssociation.getDiffDirection().toString()) == 3) {
            directionType = 3;
          }

          if (sourceCardinalityCount != 0) {
            // get sourceObject
            Map<String, Object> sourceObjectMap = getObjectInASTODElementListByDiffClass(dg, sourceClass, astodElementList);
            List<ASTODNamedObject> sourceObjectList = (List<ASTODNamedObject>) sourceObjectMap.get("objectList");
            ASTODNamedObject sourceObject = null;

            // create an object map according the class of Object
            Map<String, List<ASTODNamedObject>> sourceObjectGroupByClass = sourceObjectList
              .stream()
              .collect(Collectors.groupingBy(e -> e.getName().split("_")[0]));

            boolean isTwoObjectsInOneClass = sourceObjectGroupByClass.values().stream().anyMatch(e -> e.size() >= 2);

            /* if there are two objects in one class and those two objects are not newly created,
             * then the object association graph of this semantic difference can not be created,
             * because this object association graph with current cardinality is not valid in the original CD.
             */
            if (isTwoObjectsInOneClass && (Boolean) sourceObjectMap.get("isInList") == true) {
              return null;
            } else {
              sourceObject = sourceObjectList.get(0);
            }

            if ((Boolean) sourceObjectMap.get("isInList") == false) {
              // add new object into ASTODElementList
              astodElementList.add(sourceObject);
              // push sourceClass into classStack
              classStack.push(sourceClass);
            }

            // get targetObject
            Map<String, Object> targetObjectMap = getObjectInASTODElementListByDiffClass(dg, targetClass, astodElementList);
            ASTODNamedObject targetObject = ((List<ASTODNamedObject>) targetObjectMap.get("objectList")).get(0);

            // create new ASTODLinkList and add into ASTODElementList
            List<ASTODLink> linkList = createLinkList(sourceObject, targetObject, sourceRoleName, targetRoleName, directionType);
            astodElementList.addAll(linkList);

            // mark related refLink as true
            Optional<DiffRefSetAssociation> currentDiffRefSetAssociationOpt = findRelatedDiffRefSetAssociationByDiffAssociation(currentDiffAssociation, refLinkCheckList);
            if (!currentDiffRefSetAssociationOpt.isEmpty()) {
              refLinkCheckList.put(currentDiffRefSetAssociationOpt.get(), true);
            }
          }
        }
      }
    }

    return astodElementList;
  }

}
