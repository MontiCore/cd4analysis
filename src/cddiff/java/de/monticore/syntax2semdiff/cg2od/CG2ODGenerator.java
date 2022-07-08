package de.monticore.syntax2semdiff.cg2od;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.od4data.OD4DataMill;
import de.monticore.od4data.prettyprinter.OD4DataFullPrettyPrinter;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportAssociation;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportRefSetAssociation;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportGroup;
import de.monticore.syntax2semdiff.cg2od.metamodel.ASTODClassStackPack;
import de.monticore.syntax2semdiff.cg2od.metamodel.ASTODNamedObjectPack;
import de.monticore.syntax2semdiff.cg2od.metamodel.ASTODPack;
import de.monticore.syntax2semdiff.cg2od.metamodel.SupportClassPack;
import de.monticore.syntax2semdiff.sg2cg.metamodel.CompAssociation;
import de.monticore.syntax2semdiff.sg2cg.metamodel.CompClass;
import de.monticore.syntax2semdiff.sg2cg.metamodel.CompareGroup;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.syntax2semdiff.cd2sg.SupportHelper.*;
import static de.monticore.syntax2semdiff.cg2od.GenerateODHelper.*;
import static de.monticore.syntax2semdiff.sg2cg.CompareHelper.createCompareAssociationHelper;

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
      if (sg.getType() == CDSemantics.SIMPLE_CLOSED_WORLD) {
        if (currentCompAssociation.getCompKind() == CompareGroup.CompAssociationKind.COMP_ASC) {
          Optional<ASTODPack> optionalASTODPack = generateODByAssociation(sg, currentCompAssociation);
          if (optionalASTODPack.isEmpty()) {
            continue;
          }
          String odTitle = generateODTitle(currentCompAssociation, i);
          oDResultList.add(printOD(optionalASTODPack.get().getASTODElementList(), odTitle));
          i++;
        }
      }
    }

    // solve globalClassQueue
    while (!globalClassQueue.isEmpty()) {
      CompClass currentCompClass = globalClassQueue.pop();
      if (sg.getType() == CDSemantics.SIMPLE_CLOSED_WORLD) {
        Optional<ASTODPack> optionalASTODPack = generateODByClass(sg, currentCompClass);
        if (optionalASTODPack.isEmpty()) {
          continue;
        }
        String odTitle = generateODTitle(currentCompClass, i);
        oDResultList.add(printOD(optionalASTODPack.get().getASTODElementList(), odTitle));
        i++;
      }
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
    return new OD4DataFullPrettyPrinter(new IndentPrinter()).prettyprint(astodArtifact);
  }

  /**
   * solve the item in globalClassQueue
   */
  protected Optional<ASTODPack> generateODByClass(SupportGroup sg, CompClass compClass) {
    Deque<ASTODClassStackPack> classStack4TargetClass = new LinkedList<>();
    Deque<ASTODClassStackPack> classStack4SourceClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4TargetClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4SourceClass = new LinkedList<>();
    Map<SupportRefSetAssociation, Integer> refLinkCheckList =
      convertRefSetAssociationList2CheckList(sg.getRefSetAssociationList());

    // initial for generate ODs
    ASTODPack astodPack = initGenerateODByClass(sg, compClass, classStack4TargetClass, classStack4SourceClass, refLinkCheckList);

    // using basic process
    return usingGenerateODBasicProcess(
      sg,
      Optional.of(astodPack),
      classStack4TargetClass,
      classStack4SourceClass,
      associationStack4TargetClass,
      associationStack4SourceClass,
      refLinkCheckList);
  }

  /**
   * initial step for item in globalClassQueue
   *
   * if supportClass is in simple class, abstract class, interface, then using initGenerateODByClassHelper()
   * if supportClass is enum, get the class that using this enum, then using initGenerateODByClassHelper()
   * if there is no associations about given support class, then
   * create an object that its corresponding class has syntactic differences and
   * put this object into ASTODPack
   *
   */
  public ASTODPack initGenerateODByClass(SupportGroup sg,
                                         CompClass compClass,
                                         Deque<ASTODClassStackPack> classStack4TargetClass,
                                         Deque<ASTODClassStackPack> classStack4SourceClass,
                                         Map<SupportRefSetAssociation, Integer> refLinkCheckList) {
    // get the necessary information
    SupportClass supportClass = compClass.getOriginalElement();
    ASTODPack astodPack = new ASTODPack();

    // create ASTODElement list
    SupportClass usingSupportClass;
    if (supportClass.getSupportKind() != SupportGroup.SupportClassKind.SUPPORT_ENUM) {
      // simple class, abstract class, interface
      usingSupportClass = supportClass;
    } else {
      // enum
      SupportClass supportClassUsingEnum =
        sg.getSupportClassGroup().get(supportClass.getSupportLink4EnumClass().iterator().next());
      usingSupportClass = supportClassUsingEnum;
    }

    // call initGenerateODByClassHelper()
    Optional<ASTODPack> optionalAstodPack =
      initGenerateODByClassHelper(sg, compClass, usingSupportClass, classStack4TargetClass, classStack4SourceClass, refLinkCheckList);
    if (optionalAstodPack.isEmpty()) {
      astodPack.extendNamedObjects(
        createObjectList(sg, Optional.of(compClass), usingSupportClass, 1, classStack4TargetClass, classStack4SourceClass));
    } else {
      astodPack.extendNamedObjects(optionalAstodPack.get().getNamedObjects());
      astodPack.extendLinks(optionalAstodPack.get().getLinks());
    }

    return astodPack;
  }

  /**
   * if exist associations with current class, then we use initGenerateODByAssociation()
   * if there is no associaton with current class, then return empty()
   */
  protected Optional<ASTODPack> initGenerateODByClassHelper(SupportGroup sg,
                                                            CompClass compClass,
                                                            SupportClass supportClass,
                                                            Deque<ASTODClassStackPack> classStack4TargetClass,
                                                            Deque<ASTODClassStackPack> classStack4SourceClass,
                                                            Map<SupportRefSetAssociation, Integer> refLinkCheckList) {

    // get inheritance path of current support class
    List<SupportClass> supportClassList =
      getAllSimpleSubClasses4SupportClass(supportClass, sg.getInheritanceGraph(), sg.getSupportClassGroup());
    supportClassList.add(0, supportClass);

    // find possible associations about current support class and its subclasses
    List<SupportAssociation> supportAssociationList = new LinkedList<>();
    for (SupportClass currentClass : supportClassList) {
      supportAssociationList.addAll(findAllSupportAssociationByTargetClass(sg, currentClass));
      supportAssociationList.addAll(findAllSupportAssociationBySourceClass(sg, currentClass));
      if (!supportAssociationList.isEmpty()) {
        break;
      }
    }
    // guarantee non-inherited associations as first
    Collections.sort(supportAssociationList, (assoc1, assoc2) -> {
      int o1 = assoc1.getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC ? 1 : 0;
      int o2 = assoc2.getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC ? 1 : 0;
      return o2 - o1;
    });
    ASTODPack astodPack = null;
    if (!supportAssociationList.isEmpty()) {
      // exist associations with current class
      CompAssociation compAssociation = createCompareAssociationHelper(
        supportAssociationList.get(0),
        false,
        true,
        CompareGroup.CompAssociationCategory.DELETED);
      astodPack =
        initGenerateODByAssociation(sg, compAssociation, Optional.of(compClass), classStack4TargetClass, classStack4SourceClass);
      Optional<List<SupportRefSetAssociation>> optInitSupportRefSetAssociation =
        findRelatedSupportRefSetAssociationBySupportAssociation(compAssociation.getOriginalElement(), refLinkCheckList);
      updateCounterInCheckList(optInitSupportRefSetAssociation, refLinkCheckList);
    }

    return astodPack == null ? Optional.empty() : Optional.of(astodPack);
  }

  /**
   * solve the item in globalAssociationQueue
   */
  protected Optional<ASTODPack> generateODByAssociation(SupportGroup sg, CompAssociation compAssociation) {

    Deque<ASTODClassStackPack> classStack4TargetClass = new LinkedList<>();
    Deque<ASTODClassStackPack> classStack4SourceClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4TargetClass = new LinkedList<>();
    Deque<SupportAssociation> associationStack4SourceClass = new LinkedList<>();
    Map<SupportRefSetAssociation, Integer> refLinkCheckList =
      convertRefSetAssociationList2CheckList(sg.getRefSetAssociationList());

    // initial for generate ODs
    ASTODPack astodPack =
      initGenerateODByAssociation(sg, compAssociation, Optional.empty(), classStack4TargetClass, classStack4SourceClass);
    Optional<List<SupportRefSetAssociation>> optInitSupportRefSetAssociation =
      findRelatedSupportRefSetAssociationBySupportAssociation(compAssociation.getOriginalElement(), refLinkCheckList);
    updateCounterInCheckList(optInitSupportRefSetAssociation, refLinkCheckList);

    // using basic process
    return usingGenerateODBasicProcess(
      sg,
      Optional.of(astodPack),
      classStack4TargetClass,
      classStack4SourceClass,
      associationStack4TargetClass,
      associationStack4SourceClass,
      refLinkCheckList);
  }

  /**
   * initial step for item in globalAssociationQueue
   *
   * create objects that its corresponding association has syntactic differences in direction, left cardinality and right cardinalit then
   * create corresponding link among those objects and
   * put those objects into ASTODPack
   */
  public ASTODPack initGenerateODByAssociation(SupportGroup sg,
                                               CompAssociation compAssociation,
                                               Optional<CompClass>  optionalCompClass,
                                               Deque<ASTODClassStackPack> classStack4TargetClass,
                                               Deque<ASTODClassStackPack> classStack4SourceClass) {

    SupportAssociation currentDiffAssoc =
      intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(compAssociation.getOriginalElement(), sg);

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
    List<ASTODNamedObject> leftElementList;
    List<ASTODNamedObject> rightElementList;
    if (optionalCompClass.isPresent()) {
      if (optionalCompClass.get().getOriginalElement().getSupportLink4EnumClass().contains(leftSupportClass.getName())) {
        leftElementList =
          createObjectList(sg, optionalCompClass, leftSupportClass, leftCardinalityCount, classStack4TargetClass, classStack4SourceClass);
      } else {
        leftElementList =
          createObjectList(sg, Optional.empty(), leftSupportClass, leftCardinalityCount, classStack4TargetClass, classStack4SourceClass);
      }
      if (optionalCompClass.get().getOriginalElement().getSupportLink4EnumClass().contains(rightSupportClass.getName())) {
        rightElementList =
          createObjectList(sg, optionalCompClass, rightSupportClass, rightCardinalityCount, classStack4TargetClass, classStack4SourceClass);
      } else {
        rightElementList =
          createObjectList(sg, Optional.empty(), rightSupportClass, rightCardinalityCount, classStack4TargetClass, classStack4SourceClass);
      }
    } else {
      leftElementList =
        createObjectList(sg, Optional.empty(), leftSupportClass, leftCardinalityCount, classStack4TargetClass, classStack4SourceClass);
      rightElementList =
        createObjectList(sg, Optional.empty(), rightSupportClass, rightCardinalityCount, classStack4TargetClass, classStack4SourceClass);
    }
    List<ASTODLink> linkElementList =
      createLinkList(leftElementList, rightElementList, leftRoleName, rightRoleName, directionType);

    // remove duplicate
    for (ASTODNamedObject astodNamedObject : leftElementList) {
      for (int i = 0; i < rightElementList.size(); i++) {
        if (astodNamedObject.deepEquals(rightElementList.get(i))) {
          rightElementList.remove(i);
        }
      }
    }

    ASTODPack astodPack = new ASTODPack();
    astodPack.extendNamedObjects(leftElementList);
    astodPack.extendNamedObjects(rightElementList);
    astodPack.extendLinks(linkElementList);

    return astodPack;
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
  protected Optional<ASTODPack> generateODBasicProcess(SupportGroup sg,
                                                       String opt4StartClassKind,
                                                       Deque<ASTODClassStackPack> classStack,
                                                       Deque<ASTODClassStackPack> otherClassStack,
                                                       Deque<SupportAssociation> associationStack,
                                                       Map<SupportRefSetAssociation, Integer> refLinkCheckList,
                                                       ASTODPack astodPack) {

    // start basic process for compClass and compAssociation
    SupportClass currentSupportClass;
    while (!classStack.isEmpty()) {
      ASTODClassStackPack supportClassPack = classStack.pop();
      List<ASTODNamedObject> objectList = supportClassPack.getNamedObjects();
      currentSupportClass = supportClassPack.getSupportClass();
      if (opt4StartClassKind.equals("target")) {
        findAllSupportAssociationByTargetClass(sg, currentSupportClass).forEach(associationStack::push);
      } else {
        findAllSupportAssociationBySourceClass(sg, currentSupportClass).forEach(associationStack::push);
      }

      while (!associationStack.isEmpty()) {
        SupportAssociation currentSupportAssociation =
          intersectSupportAssociationCardinalityBySupportAssociationOnlyWithLeftToRightAndRightToLeft(associationStack.pop(), sg);
        if (!checkRelatedSupportRefSetAssociationIsUsed(currentSupportAssociation, refLinkCheckList)) {

          // get the information of currentSideClass and otherSideClass

          SupportClassPack otherSideClassPack =
            findOtherSideClassAndPositionInSupportAssociation(currentSupportAssociation, currentSupportClass);

          List<ASTODNamedObject> currentSideObjectList = objectList;
          SupportClass otherSideClass = otherSideClassPack.getOtherSideClass();
          String otherSideRoleName;
          String currentSideRoleName;
          int directionType = 0;
          boolean isPresentOtherSideCardinality = false;

          SupportClassPack.Position otherSideClassPosition = otherSideClassPack.getPosition();
          if (otherSideClassPosition == SupportClassPack.Position.LEFT) {
            otherSideRoleName = currentSupportAssociation.getSupportLeftClassRoleName();
            currentSideRoleName = currentSupportAssociation.getSupportRightClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 1 : 2;
            if (isPresentObjectInASTODElementListBySupportClass(sg, otherSideClass, astodPack)) {
              isPresentOtherSideCardinality = true;
            } else {
              isPresentOtherSideCardinality =
                mappingCardinality(currentSupportAssociation.getSupportLeftClassCardinality().toString()) > 0;
            }
          } else {
            otherSideRoleName = currentSupportAssociation.getSupportRightClassRoleName();
            currentSideRoleName = currentSupportAssociation.getSupportLeftClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 2 : 1;
            if (isPresentObjectInASTODElementListBySupportClass(sg, otherSideClass, astodPack)) {
              isPresentOtherSideCardinality = true;
            } else {
              isPresentOtherSideCardinality =
                mappingCardinality(currentSupportAssociation.getSupportRightClassCardinality().toString()) > 0;
            }
          }
          if (mappingDirection(currentSupportAssociation.getSupportDirection().toString()) == 3) {
            directionType = 3;
          }

          // determine if a new connection needs to be added by otherSideCardinalityCount
          if (isPresentOtherSideCardinality) {

            // get otherSideObject
            ASTODNamedObjectPack astodNamedObjectPack =
              getObjectInASTODElementListBySupportClass(sg, otherSideClass, astodPack);
            List<ASTODNamedObject> otherSideObjectList = astodNamedObjectPack.getNamedObjects();

            // determining illegal situations
            // create an object map according the class of Object
            Map<String, List<ASTODNamedObject>> otherSideObjectGroupByClass = otherSideObjectList
              .stream()
              .collect(Collectors.groupingBy(e -> e.getName().split("_")[0]));
            if (opt4StartClassKind.equals("target")) {
              boolean isTwoObjectsInOneClass =
                otherSideObjectGroupByClass.values().stream().anyMatch(e -> e.size() >= 2);
              /* if there are two objects in one class and those two objects are not newly created,
               * then the object association graph of this semantic difference can not be created,
               * because this object association graph with current cardinality is not valid in the original CD.
               */
              if (isTwoObjectsInOneClass && astodNamedObjectPack.isInList()) {
                return Optional.empty();
              }
            } else {
              boolean isTwoObjectsInOneClass4SourceSide = currentSideObjectList.size() == 2;
              boolean isOneObjectsInOneClass4TargetSide =
                otherSideObjectGroupByClass.values().stream().anyMatch(e -> e.size() == 1);
              if (isTwoObjectsInOneClass4SourceSide && astodNamedObjectPack.isInList() && isOneObjectsInOneClass4TargetSide) {
                return Optional.empty();
              }
            }

            if (currentSideObjectList.size() == 1) {
              ASTODNamedObject otherSideObject0 = otherSideObjectList.get(0);
              if (!astodNamedObjectPack.isInList()) {
                // add new object into ASTODElementList
                astodPack.addNamedObject(otherSideObject0);
                // push sourceClass into classStack
                classStack.push(new ASTODClassStackPack(List.of(otherSideObject0), otherSideClass));
                otherClassStack.push(new ASTODClassStackPack(List.of(otherSideObject0), otherSideClass));
              }
              // create new ASTODLinkList and add into ASTODElementList
              List<ASTODLink> linkList0;
              if (otherSideClassPosition == SupportClassPack.Position.LEFT) {
                linkList0 =
                  createLinkList(otherSideObject0, currentSideObjectList.get(0), otherSideRoleName, currentSideRoleName, directionType);
              } else {
                linkList0 =
                  createLinkList(currentSideObjectList.get(0), otherSideObject0, currentSideRoleName, otherSideRoleName, directionType);
              }
              astodPack.extendLinks(linkList0);
            } else if (currentSideObjectList.size() == 2) {
              ASTODNamedObject otherSideObject0 = otherSideObjectList.get(0);
              ASTODNamedObject otherSideObject1 = otherSideObject0.deepClone();
              String objectName = otherSideObject1.getName().split("_")[0];
              otherSideObject1.setName(objectName + "_1");
              if (!astodNamedObjectPack.isInList()) {
                // add new object into ASTODElementList
                astodPack.addNamedObject(otherSideObject0);
                astodPack.addNamedObject(otherSideObject1);
                // push sourceClass into classStack
                classStack.push(new ASTODClassStackPack(List.of(otherSideObject0, otherSideObject1), otherSideClass));
                otherClassStack.push(new ASTODClassStackPack(List.of(otherSideObject0, otherSideObject1), otherSideClass));
              }
              // create new ASTODLinkList and add into ASTODElementList
              List<ASTODLink> linkList0;
              List<ASTODLink> linkList1;
              if (otherSideClassPosition == SupportClassPack.Position.LEFT) {
                linkList0 =
                  createLinkList(otherSideObject0, currentSideObjectList.get(0), otherSideRoleName, currentSideRoleName, directionType);
                linkList1 =
                  createLinkList(otherSideObject1, currentSideObjectList.get(1), otherSideRoleName, currentSideRoleName, directionType);
              } else {
                linkList0 =
                  createLinkList(currentSideObjectList.get(0), otherSideObject0, currentSideRoleName, otherSideRoleName, directionType);
                linkList1 =
                  createLinkList(currentSideObjectList.get(1), otherSideObject1, currentSideRoleName, otherSideRoleName, directionType);
              }
              astodPack.extendLinks(linkList0);
              astodPack.extendLinks(linkList1);
            }

            // mark related refLink
            Optional<List<SupportRefSetAssociation>> optCurrentSupportRefSetAssociation =
              findRelatedSupportRefSetAssociationBySupportAssociation(currentSupportAssociation, refLinkCheckList);
            updateCounterInCheckList(optCurrentSupportRefSetAssociation, refLinkCheckList);
          }
        }
      }
    }

    return Optional.of(astodPack);
  }

  protected Optional<ASTODPack> usingGenerateODBasicProcess(SupportGroup sg,
                                                            Optional<ASTODPack> optionalASTODPack,
                                                            Deque<ASTODClassStackPack> classStack4TargetClass,
                                                            Deque<ASTODClassStackPack> classStack4SourceClass,
                                                            Deque<SupportAssociation> associationStack4TargetClass,
                                                            Deque<SupportAssociation> associationStack4SourceClass,
                                                            Map<SupportRefSetAssociation, Integer> refLinkCheckList) {

    while (!classStack4TargetClass.isEmpty() || !classStack4SourceClass.isEmpty()) {
      if (!classStack4TargetClass.isEmpty()) {
        optionalASTODPack =
          generateODBasicProcess(sg,
            "target",
            classStack4TargetClass,
            classStack4SourceClass,
            associationStack4TargetClass,
            refLinkCheckList,
            optionalASTODPack.get());
        if (optionalASTODPack.isEmpty()) {
          break;
        }
      } else {
        optionalASTODPack =
          generateODBasicProcess(sg,
            "source",
            classStack4SourceClass,
            classStack4TargetClass,
            associationStack4SourceClass,
            refLinkCheckList,
            optionalASTODPack.get());
        if (optionalASTODPack.isEmpty()) {
          break;
        }
      }
    }
    return optionalASTODPack;
  }
}
