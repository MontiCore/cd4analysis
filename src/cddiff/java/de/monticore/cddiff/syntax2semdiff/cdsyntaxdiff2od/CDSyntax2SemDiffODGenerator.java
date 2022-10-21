package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;
import de.monticore.odbasis._ast.*;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel.ASTODClassStackPack;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel.ASTODNamedObjectPack;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel.ASTODPack;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapperPack;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapperSyntaxDiff4AssocHelper;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDWrapperSyntaxDiff;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDTypeWrapperDiff;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4InheritanceHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4SearchHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4GenerateODHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4ASTODHelper.*;

public class CDSyntax2SemDiffODGenerator {

  /**
   * generate ODs by solving items with syntactic differences in globalClassQueue and
   * globalAssociationQueue
   */
  public List<ASTODArtifact> generateObjectDiagrams(
      CDWrapper cdw,
      CDWrapperSyntaxDiff cg,
      CDSemantics cdSemantics) {

    List<ASTODArtifact> astODArtifacts = new ArrayList<>();

    Deque<CDTypeWrapperDiff> globalClassQueue = cg.getCDTypeDiffResultQueueWithDiff();
    Deque<CDAssocWrapperDiff> globalAssociationQueue = cg.getCDAssociationDiffResultQueueWithDiff();

    // solve globalAssociationQueue
    int i = 1;
    while (!globalAssociationQueue.isEmpty()) {
      CDAssocWrapperDiff currentCDAssociationDiff = globalAssociationQueue.pop();
      if (currentCDAssociationDiff.getCDDiffKind() != CDAssociationDiffKind.CDDIFF_INHERIT_ASC ||
          (currentCDAssociationDiff.getCDDiffKind() == CDAssociationDiffKind.CDDIFF_INHERIT_ASC &&
              currentCDAssociationDiff.getCDDiffCategory() == CDAssociationDiffCategory.CONFLICTING)) {
        Optional<ASTODPack> optionalASTODPack =
            generateODByAssociation(cdw, currentCDAssociationDiff, cdSemantics);
        if (optionalASTODPack.isEmpty()) {
          continue;
        }
        String odTitle = generateODTitle(currentCDAssociationDiff, i);
        String odSourcePosition = generateODSourcePosition(currentCDAssociationDiff);
        astODArtifacts.add(generateASTODArtifact(optionalASTODPack.get().getASTODElementList(),
            odTitle, odSourcePosition));
        i++;
      }
    }

    // solve globalClassQueue
    while (!globalClassQueue.isEmpty()) {
      CDTypeWrapperDiff currentCDTypeDiff = globalClassQueue.pop();
      if (currentCDTypeDiff.getCDDiffKind() == CDTypeDiffKind.CDDIFF_CLASS ||
          currentCDTypeDiff.getCDDiffKind() == CDTypeDiffKind.CDDIFF_ENUM) {
        Optional<ASTODPack> optionalASTODPack =
            generateODByClass(cdw, currentCDTypeDiff, cdSemantics);
        if (optionalASTODPack.isEmpty()) {
          continue;
        }
        String odTitle = generateODTitle(currentCDTypeDiff, i);
        String odSourcePosition = generateODSourcePosition(currentCDTypeDiff);
        astODArtifacts.add(generateASTODArtifact(optionalASTODPack.get().getASTODElementList(),
            odTitle, odSourcePosition));
        i++;
      }
    }

    return astODArtifacts;
  }

  /**
   * solve the item in globalClassQueue
   */
  protected Optional<ASTODPack> generateODByClass(
      CDWrapper cdw,
      CDTypeWrapperDiff cDTypeWrapperDiff,
      CDSemantics cdSemantics) {

    Deque<ASTODClassStackPack> classStack4TargetClass = new LinkedList<>();
    Deque<ASTODClassStackPack> classStack4SourceClass = new LinkedList<>();
    Deque<CDAssociationWrapper> associationStack4TargetClass = new LinkedList<>();
    Deque<CDAssociationWrapper> associationStack4SourceClass = new LinkedList<>();
    Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList =
        convertRefSetAssociationList2CheckList(
        cdw.getRefSetAssociationList());

    // initial for generate ODs
    ASTODPack astodPack = initGenerateODByClass(cdw,
        cDTypeWrapperDiff,
        classStack4TargetClass,
        classStack4SourceClass,
        refLinkCheckList,
        cdSemantics);

    // Guaranteed CDTypeWrapper status is OPEN
    if (astodPack.isEmpty()) {
      return Optional.empty();
    }

    // using basic process
    return usingGenerateODBasicProcess(cdw,
        Optional.of(astodPack),
        classStack4TargetClass,
        classStack4SourceClass,
        associationStack4TargetClass,
        associationStack4SourceClass,
        refLinkCheckList,
        cdSemantics,
        Optional.empty());
  }

  /**
   * initial step for item in globalClassQueue
   *
   * if CDTypeWrapper is in simple class, abstract class, interface, then using
   * initGenerateODByClassHelper()
   * if CDTypeWrapper is enum, get the class that using this enum, then using
   * initGenerateODByClassHelper()
   * if there is no associations about given CDTypeWrapper, then
   * create an object that its corresponding class has syntactic differences and
   * put this object into ASTODPack
   *
   */
  public ASTODPack initGenerateODByClass(
      CDWrapper cdw,
      CDTypeWrapperDiff cDTypeWrapperDiff,
      Deque<ASTODClassStackPack> classStack4TargetClass,
      Deque<ASTODClassStackPack> classStack4SourceClass,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList,
      CDSemantics cdSemantics) {

    // get the necessary information
    CDTypeWrapper cDTypeWrapper = cDTypeWrapperDiff.getBaseElement();
    ASTODPack astodPack = new ASTODPack();

    // create ASTODElement list
    CDTypeWrapper usingCDTypeWrapper;
    if (cDTypeWrapper.getCDWrapperKind() != CDTypeWrapperKind.CDWRAPPER_ENUM) {
      // simple class, abstract class, interface
      usingCDTypeWrapper = cDTypeWrapper;
    }
    else {
      // enum
      usingCDTypeWrapper = cdw.getCDTypeWrapperGroup()
          .get(cDTypeWrapper.getCDWrapperLink4EnumClass().iterator().next());
    }

    Optional<ASTODPack> optionalAstODPack;

    // if CDTypeDiffCategory is FREED, there is only one object instance in OD (no links)
    if (cDTypeWrapperDiff.getCDDiffCategory() == CDTypeDiffCategory.FREED) {
      optionalAstODPack = Optional.empty();
    } else {
      // call initGenerateODByClassHelper()
      optionalAstODPack = initGenerateODByClassHelper(cdw,
          cDTypeWrapperDiff,
          usingCDTypeWrapper,
          classStack4TargetClass,
          classStack4SourceClass,
          refLinkCheckList,
          cdSemantics);
    }

    if (optionalAstODPack.isEmpty()) {
      List<ASTODNamedObject> astodNamedObjectList = createObjectList(cdw,
          Optional.of(cDTypeWrapperDiff),
          usingCDTypeWrapper,
          1,
          classStack4TargetClass,
          classStack4SourceClass,
          Optional.empty(),
          cdSemantics);

      // Guaranteed CDTypeWrapper status is OPEN
      if (astodNamedObjectList.isEmpty()) {
        return new ASTODPack();
      }

      astodPack.extendNamedObjects(astodNamedObjectList);
    }
    else {
      astodPack.extendNamedObjects(optionalAstODPack.get().getNamedObjects());
      astodPack.extendLinks(optionalAstODPack.get().getLinks());
    }

    return astodPack;
  }

  /**
   * if exist associations with current class, then we use initGenerateODByAssociation()
   * if there is no association with current class, then return empty()
   */
  protected Optional<ASTODPack> initGenerateODByClassHelper(
      CDWrapper cdw,
      CDTypeWrapperDiff cDTypeWrapperDiff,
      CDTypeWrapper cDTypeWrapper,
      Deque<ASTODClassStackPack> classStack4TargetClass,
      Deque<ASTODClassStackPack> classStack4SourceClass,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList,
      CDSemantics cdSemantics) {

    // get inheritance path of current CDTypeWrapper
    List<CDTypeWrapper> CDTypeWrapperList =
        getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper, cdw.getCDTypeWrapperGroup());

    // find possible associations about current CDTypeWrapper and its subclasses
    List<CDAssociationWrapper> CDAssociationWrapperList = new LinkedList<>();
    for (CDTypeWrapper currentClass : CDTypeWrapperList) {
      CDAssociationWrapperList.addAll(findAllCDAssociationWrapperByTargetClass(cdw,
          currentClass, true));
      CDAssociationWrapperList.addAll(findAllCDAssociationWrapperBySourceClass(cdw,
          currentClass, true));
      if (!CDAssociationWrapperList.isEmpty()) {
        break;
      }
    }
    // guarantee non-inherited associations as first
    CDAssociationWrapperList.sort((assoc1, assoc2) -> {
      int o1 =
          assoc1.getCDWrapperKind() == CDAssociationWrapperKind.CDWRAPPER_ASC ? 1 : 0;
      int o2 =
          assoc2.getCDWrapperKind() == CDAssociationWrapperKind.CDWRAPPER_ASC ? 1 : 0;
      return o2 - o1;
    });

    // Guaranteed CDAssociationWrapper status is OPEN
    CDAssociationWrapperList = CDAssociationWrapperList
        .stream()
        .filter(CDAssociationWrapper::isOpen)
        .filter(cdAssociationWrapper -> cdAssociationWrapper.getCDWrapperLeftClass().isOpen())
        .filter(cdAssociationWrapper -> cdAssociationWrapper.getCDWrapperRightClass().isOpen())
        .collect(Collectors.toList());

    ASTODPack astodPack = null;
    if (!CDAssociationWrapperList.isEmpty()) {
      // exist associations with current class
      CDAssocWrapperDiff cDAssocWrapperDiff =
          CDWrapperSyntaxDiff4AssocHelper.createCDAssociationDiffHelper(CDAssociationWrapperList.get(0),
              Optional.empty(),
              false,
              true,
              CDAssociationDiffCategory.DELETED);
      astodPack = initGenerateODByAssociation(cdw,
          cDAssocWrapperDiff,
          Optional.of(cDTypeWrapperDiff),
          classStack4TargetClass,
          classStack4SourceClass,
          cdSemantics);

      // Guaranteed CDTypeWrapper status is OPEN
      if (astodPack.isEmpty()) {
        return Optional.empty();
      }

      List<CDRefSetAssociationWrapper> initCDRefSetAssociationWrapper =
          findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(
              cdw, cDAssocWrapperDiff.getBaseElement(), refLinkCheckList);
      decreaseCounterInCheckList(initCDRefSetAssociationWrapper, refLinkCheckList);
    }

    return astodPack == null ? Optional.empty() : Optional.of(astodPack);
  }

  /**
   * solve the item in globalAssociationQueue
   */
  protected Optional<ASTODPack> generateODByAssociation(
      CDWrapper cdw,
      CDAssocWrapperDiff cDAssocWrapperDiff,
      CDSemantics cdSemantics) {

    Deque<ASTODClassStackPack> classStack4TargetClass = new LinkedList<>();
    Deque<ASTODClassStackPack> classStack4SourceClass = new LinkedList<>();
    Deque<CDAssociationWrapper> associationStack4TargetClass = new LinkedList<>();
    Deque<CDAssociationWrapper> associationStack4SourceClass = new LinkedList<>();
    Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList =
        convertRefSetAssociationList2CheckList(cdw.getRefSetAssociationList(), cDAssocWrapperDiff);

    Optional<CDTypeWrapper> specialCDTypeWrapper = Optional.empty();
    if (cDAssocWrapperDiff.getWhichPartDiff().isPresent()) {
      if (cDAssocWrapperDiff.getWhichPartDiff().get() == WhichPartDiff.LEFT_SPECIAL_CARDINALITY) {
        specialCDTypeWrapper =
            Optional.of(cDAssocWrapperDiff.getBaseElement().getCDWrapperRightClass());
      } else if (cDAssocWrapperDiff.getWhichPartDiff().get() == WhichPartDiff.RIGHT_SPECIAL_CARDINALITY) {
        specialCDTypeWrapper =
            Optional.of(cDAssocWrapperDiff.getBaseElement().getCDWrapperLeftClass());
      }
    }

    // initial for generate ODs
    ASTODPack astodPack = initGenerateODByAssociation(cdw,
        cDAssocWrapperDiff,
        Optional.empty(),
        classStack4TargetClass,
        classStack4SourceClass,
        cdSemantics);

    // Guaranteed CDTypeWrapper status is OPEN
    if (astodPack.isEmpty()) {
      return Optional.empty();
    }

    List<CDRefSetAssociationWrapper> initCDRefSetAssociationWrapper;
    if (specialCDTypeWrapper.isPresent()) {

      Optional<CDAssociationWrapperPack> optionalCDAssociationWrapperPack =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
          cdw.getCDAssociationWrapperGroup(), cDAssocWrapperDiff.getBaseElement())
          .stream()
          .filter(e -> !e.getCDAssociationWrapper().getName().equals(cDAssocWrapperDiff.getBaseElement().getName()))
          .filter(cdAssociationWrapperPack -> cdAssociationWrapperPack.getCDAssociationWrapper().isOpen())
          .findFirst();

      // Guaranteed CDAssociationWrapper status is OPEN
      if (optionalCDAssociationWrapperPack.isEmpty()) {
        return Optional.empty();
      }

      CDAssociationWrapper otherCDAssociationWrapper =
          optionalCDAssociationWrapperPack.get().getCDAssociationWrapper();

      initCDRefSetAssociationWrapper =
          findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(cdw,
              otherCDAssociationWrapper, refLinkCheckList);
    } else {
      initCDRefSetAssociationWrapper =
          findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(cdw,
              cDAssocWrapperDiff.getBaseElement(), refLinkCheckList);
    }
    decreaseCounterInCheckList(initCDRefSetAssociationWrapper, refLinkCheckList);

    // using basic process
    return usingGenerateODBasicProcess(cdw,
        Optional.of(astodPack),
        classStack4TargetClass,
        classStack4SourceClass,
        associationStack4TargetClass,
        associationStack4SourceClass,
        refLinkCheckList,
        cdSemantics,
        specialCDTypeWrapper);
  }

  /**
   * initial step for item in globalAssociationQueue
   *
   * create objects that its corresponding association has syntactic differences in direction,
   * left cardinality and right cardinality then
   * create corresponding link among those objects and
   * put those objects into ASTODPack
   */
  public ASTODPack initGenerateODByAssociation(
      CDWrapper cdw,
      CDAssocWrapperDiff cDAssocWrapperDiff,
      Optional<CDTypeWrapperDiff> optionalCDTypeDiff,
      Deque<ASTODClassStackPack> classStack4TargetClass,
      Deque<ASTODClassStackPack> classStack4SourceClass,
      CDSemantics cdSemantics) {

    CDAssociationWrapper currentDiffAssoc = cDAssocWrapperDiff.getBaseElement();

    if (!currentDiffAssoc.isOpen()) {
      return new ASTODPack();
    }

    // get the necessary information
    CDTypeWrapper leftCDTypeWrapper = currentDiffAssoc.getCDWrapperLeftClass();
    CDTypeWrapper rightCDTypeWrapper = currentDiffAssoc.getCDWrapperRightClass();
    String leftRoleName = currentDiffAssoc.getCDWrapperLeftClassRoleName();
    String rightRoleName = currentDiffAssoc.getCDWrapperRightClassRoleName();
    int directionType = 0;
    int leftCardinalityCount = 0;
    int rightCardinalityCount = 0;

    switch (cDAssocWrapperDiff.getCDDiffCategory()) {
      case DELETED:
      case CONFLICTING:
      case SUBCLASS_DIFF:
        directionType = mappingDirection(
            currentDiffAssoc.getCDAssociationWrapperDirection());
        leftCardinalityCount = mappingCardinality4Initial(
            currentDiffAssoc.getCDWrapperLeftClassCardinality());
        rightCardinalityCount = mappingCardinality4Initial(
            currentDiffAssoc.getCDWrapperRightClassCardinality());
        break;
      case DIRECTION_CHANGED:
        directionType = mappingDirection(
            cDAssocWrapperDiff.getCDDiffDirectionResult().get());
        leftCardinalityCount = mappingCardinality4Initial(
            currentDiffAssoc.getCDWrapperLeftClassCardinality());
        rightCardinalityCount = mappingCardinality4Initial(
            currentDiffAssoc.getCDWrapperRightClassCardinality());
        break;
      case CARDINALITY_CHANGED:
        directionType = mappingDirection(
            currentDiffAssoc.getCDAssociationWrapperDirection());
        switch (cDAssocWrapperDiff.getWhichPartDiff().get()) {
          case LEFT_CARDINALITY:
            leftCardinalityCount = mappingCardinality4Initial(
                cDAssocWrapperDiff.getCDDiffLeftClassCardinalityResult().get());
            rightCardinalityCount = mappingCardinality4Initial(
                currentDiffAssoc.getCDWrapperRightClassCardinality());
            break;
          case RIGHT_CARDINALITY:
            leftCardinalityCount = mappingCardinality4Initial(
                currentDiffAssoc.getCDWrapperLeftClassCardinality());
            rightCardinalityCount = mappingCardinality4Initial(
                cDAssocWrapperDiff.getCDDiffRightClassCardinalityResult().get());
            break;
          case LEFT_SPECIAL_CARDINALITY:
            directionType = mappingDirection(
                reverseDirection(currentDiffAssoc.getCDAssociationWrapperDirection()));
            leftCardinalityCount = mappingCardinality4Initial(
                currentDiffAssoc.getCDWrapperLeftClassCardinality());
            rightCardinalityCount = mappingCardinality4Initial(
                cDAssocWrapperDiff.getCDDiffRightClassCardinalityResult().get());
            break;
          case RIGHT_SPECIAL_CARDINALITY:
            directionType = mappingDirection(
                reverseDirection(currentDiffAssoc.getCDAssociationWrapperDirection()));
            leftCardinalityCount = mappingCardinality4Initial(
                cDAssocWrapperDiff.getCDDiffLeftClassCardinalityResult().get());
            rightCardinalityCount = mappingCardinality4Initial(
                currentDiffAssoc.getCDWrapperRightClassCardinality());
            break;
        }
        break;
    }

    // create ASTODElement list
    List<ASTODNamedObject> leftElementList;
    List<ASTODNamedObject> rightElementList;
    if (optionalCDTypeDiff.isPresent()) {
      if (optionalCDTypeDiff.get()
          .getBaseElement()
          .getCDWrapperLink4EnumClass()
          .contains(leftCDTypeWrapper.getName())) {
        leftElementList = createObjectList(cdw,
            optionalCDTypeDiff,
            leftCDTypeWrapper,
            leftCardinalityCount,
            classStack4TargetClass,
            classStack4SourceClass,
            Optional.empty(),
            cdSemantics);
      }
      else {
        leftElementList = createObjectList(cdw,
            Optional.empty(),
            leftCDTypeWrapper,
            leftCardinalityCount,
            classStack4TargetClass,
            classStack4SourceClass,
            Optional.empty(),
            cdSemantics);
      }
      if (optionalCDTypeDiff.get()
          .getBaseElement()
          .getCDWrapperLink4EnumClass()
          .contains(rightCDTypeWrapper.getName())) {
        rightElementList = createObjectList(cdw,
            optionalCDTypeDiff,
            rightCDTypeWrapper,
            rightCardinalityCount,
            classStack4TargetClass,
            classStack4SourceClass,
            Optional.empty(),
            cdSemantics);
      }
      else {
        rightElementList = createObjectList(cdw,
            Optional.empty(),
            rightCDTypeWrapper,
            rightCardinalityCount,
            classStack4TargetClass,
            classStack4SourceClass,
            Optional.empty(),
            cdSemantics);
      }
    }
    else {
      leftElementList = createObjectList(cdw,
          Optional.empty(),
          leftCDTypeWrapper,
          leftCardinalityCount,
          classStack4TargetClass,
          classStack4SourceClass,
          cDAssocWrapperDiff.getCDDiffCategory() == CDAssociationDiffCategory.SUBCLASS_DIFF ?
              cDAssocWrapperDiff.getLeftInstanceClass() : Optional.empty(),
          cdSemantics);
      rightElementList = createObjectList(cdw,
          Optional.empty(),
          rightCDTypeWrapper,
          rightCardinalityCount,
          classStack4TargetClass,
          classStack4SourceClass,
          cDAssocWrapperDiff.getCDDiffCategory() == CDAssociationDiffCategory.SUBCLASS_DIFF ?
              cDAssocWrapperDiff.getRightInstanceClass() : Optional.empty(),
          cdSemantics);
    }

    // Guaranteed CDTypeWrapper status is OPEN
    if (leftElementList.isEmpty() && leftCardinalityCount != 0) {
      return new ASTODPack();
    }
    // Guaranteed CDTypeWrapper status is OPEN
    if (rightElementList.isEmpty() && rightCardinalityCount != 0) {
      return new ASTODPack();
    }

    List<ASTODLink> linkElementList = createLinkList(leftElementList,
        rightElementList,
        leftRoleName,
        rightRoleName,
        directionType);

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
   * First consider the created objects in initial step as target class and find source classes
   * and so on
   * Second consider the created objects in initial step as source class and find target classes
   * and so on
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
   *    But this conflict is also present in the original CD, so this semantic difference should
   *    be removed.
   *    That means current syntactic difference for association can not lead to semantic difference.
   *
   * situation 3: (initial object as source class)
   *           -> [o_i1] -> ... -> [o_j1]
   *         /                           \
   *    [o_0]                             -> ?? [o_0] ??
   *         \                           /
   *           -> [o_i2] -> ... -> [o_j2]
   *    There is a conflict that [1] o_0 "->" / "<-"/ "<->" o_j [1]
   *    But this conflict is also present in the original CD, so this semantic difference should
   *    be removed.
   *    That means current syntactic difference for association can not lead to semantic difference.
   */
  protected Optional<ASTODPack> generateODBasicProcess(
      CDWrapper cdw,
      String opt4StartClassKind,
      Deque<ASTODClassStackPack> classStack,
      Deque<ASTODClassStackPack> otherClassStack,
      Deque<CDAssociationWrapper> associationStack,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList,
      ASTODPack astodPack,
      CDSemantics cdSemantics,
      Optional<CDTypeWrapper> specialCDTypeWrapper) {

    // start basic process for CDTypeWrapperDiff and CDAssocWrapperDiff
    CDTypeWrapper currentCDTypeWrapper;
    while (!classStack.isEmpty()) {
      ASTODClassStackPack cDTypeWrapperPack = classStack.pop();
      List<ASTODNamedObject> objectList = cDTypeWrapperPack.getNamedObjects();
      currentCDTypeWrapper = cDTypeWrapperPack.getCDTypeWrapper();

      // Guaranteed CDTypeWrapper status is OPEN
      if (!currentCDTypeWrapper.isOpen()) {
        return Optional.empty();
      }

      if (opt4StartClassKind.equals("target")) {
        findAllCDAssociationWrapperByTargetClass(cdw, currentCDTypeWrapper, false).forEach(associationStack::push);
      }
      else {
        findAllCDAssociationWrapperBySourceClass(cdw, currentCDTypeWrapper, false).forEach(associationStack::push);
      }

      while (!associationStack.isEmpty()) {
        CDAssociationWrapper currentCDAssociationWrapper = associationStack.pop();

        // Guaranteed CDAssociationWrapper status is OPEN
        if (!currentCDAssociationWrapper.isOpen()) {
          return Optional.empty();
        }

        if (!checkRelatedCDRefSetAssociationWrapperIsUsed(cdw, currentCDAssociationWrapper, refLinkCheckList)) {

          // get the information of currentSideClass and otherSideClass

          CDTypeWrapperPack otherSideClassPack =
              findOtherSideClassAndPositionInCDAssociationWrapper(
              currentCDAssociationWrapper, currentCDTypeWrapper, opt4StartClassKind);

          CDTypeWrapper otherSideClass = otherSideClassPack.getOtherSideClass();
          String otherSideRoleName;
          String currentSideRoleName;
          int directionType;
          boolean isPresentOtherSideCardinality;

          CDTypeWrapperPack.Position otherSideClassPosition = otherSideClassPack.getPosition();
          if (otherSideClassPosition == CDTypeWrapperPack.Position.LEFT) {
            otherSideRoleName = currentCDAssociationWrapper.getCDWrapperLeftClassRoleName();
            currentSideRoleName = currentCDAssociationWrapper.getCDWrapperRightClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 1 : 2;
            if (isPresentObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack)) {
              isPresentOtherSideCardinality = true;
            }
            else {
              if (isPresentInheritedObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack)) {
                continue;
              }
              if (isPresentSuperObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack)) {
                return Optional.empty();
              }
              isPresentOtherSideCardinality = mappingCardinality(
                  currentCDAssociationWrapper.getCDWrapperLeftClassCardinality()) > 0;
            }
          }
          else {
            otherSideRoleName = currentCDAssociationWrapper.getCDWrapperRightClassRoleName();
            currentSideRoleName = currentCDAssociationWrapper.getCDWrapperLeftClassRoleName();
            directionType = opt4StartClassKind.equals("target") ? 2 : 1;
            if (isPresentObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack)) {
              isPresentOtherSideCardinality = true;
            }
            else {
              if (isPresentInheritedObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack)) {
                continue;
              }
              if (isPresentSuperObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack)) {
                return Optional.empty();
              }
              isPresentOtherSideCardinality = mappingCardinality(
                  currentCDAssociationWrapper.getCDWrapperRightClassCardinality()) > 0;
            }
          }
          if (mappingDirection(
              currentCDAssociationWrapper.getCDAssociationWrapperDirection()) == 3) {
            directionType = 3;
          }

          // determine if a new connection needs to be added by otherSideCardinalityCount
          if (isPresentOtherSideCardinality) {

            // get otherSideObject
            ASTODNamedObjectPack astodNamedObjectPack =
                getObjectInASTODElementListByCDTypeWrapper(cdw, otherSideClass, astodPack, cdSemantics);

            // Guaranteed CDTypeWrapper status is OPEN
            if (astodNamedObjectPack.isEmpty()) {
              return Optional.empty();
            }

            List<ASTODNamedObject> otherSideObjectList = astodNamedObjectPack.getNamedObjects();

            // determining illegal situations
            if (checkIllegalSituationOnly4CDAssociationWrapperWithLeftToRightAndRightToLeft(
                cdw, currentCDAssociationWrapper, refLinkCheckList)) {
              // create an object map according the class of Object
              Map<String, List<ASTODNamedObject>> otherSideObjectGroupByClass =
                  otherSideObjectList.stream()
                      .collect(Collectors.groupingBy(e -> e.getName().split("__")[0]));
              if (opt4StartClassKind.equals("target")) {
                boolean isTwoObjectsInOneClass = otherSideObjectGroupByClass.values()
                    .stream()
                    .anyMatch(e -> e.size() >= 2);
                /* if there are two objects in one class and those two objects are not newly created,
                 * then the object association graph of this semantic difference can not be created,
                 * because this object association graph with current cardinality is not valid in
                 * the original CD.
                 */
                if (isTwoObjectsInOneClass && astodNamedObjectPack.isInList()) {
                  return Optional.empty();
                }
              }
              else {
                boolean isTwoObjectsInOneClass4SourceSide = objectList.size() == 2;
                boolean isOneObjectsInOneClass4TargetSide = otherSideObjectGroupByClass.values()
                    .stream()
                    .anyMatch(e -> e.size() == 1);
                if (isTwoObjectsInOneClass4SourceSide && astodNamedObjectPack.isInList()
                    && isOneObjectsInOneClass4TargetSide) {
                  return Optional.empty();
                }
              }
            }

            int objectListSize = objectList.size();
            // special situation
            if (specialCDTypeWrapper.isPresent()) {
              if (currentCDTypeWrapper.equals(specialCDTypeWrapper.get())) {
                objectListSize = 1;
              }
            }

            if (objectListSize == 1) {
              ASTODNamedObject otherSideObject0 = otherSideObjectList.get(0);
              if (!astodNamedObjectPack.isInList()) {
                // add new object into ASTODElementList
                astodPack.addNamedObject(otherSideObject0);
                // push sourceClass into classStack
                classStack.push(new ASTODClassStackPack(List.of(otherSideObject0), otherSideClass));
                otherClassStack.push(
                    new ASTODClassStackPack(List.of(otherSideObject0), otherSideClass));
              }
              // create new ASTODLinkList and add into ASTODElementList
              List<ASTODLink> linkList0;
              if (otherSideClassPosition == CDTypeWrapperPack.Position.LEFT) {
                linkList0 = createLinkList(otherSideObject0, objectList.get(0),
                    otherSideRoleName, currentSideRoleName, directionType);
              }
              else {
                linkList0 = createLinkList(objectList.get(0), otherSideObject0,
                    currentSideRoleName, otherSideRoleName, directionType);
              }
              astodPack.extendLinks(linkList0);
            }
            else if (objectListSize == 2) {
              ASTODNamedObject otherSideObject0 = otherSideObjectList.get(0);
              ASTODNamedObject otherSideObject1 = otherSideObject0.deepClone();

              if (astodNamedObjectPack.getNamedObjects().size() == 1) {
                String objectName = otherSideObject1.getName().split("__")[0];
                otherSideObject1.setName(objectName + "__1");
              }

              if (!astodNamedObjectPack.isInList() || astodNamedObjectPack.getNamedObjects().size() == 1) {
                // add new object into ASTODElementList
                astodPack.addNamedObject(otherSideObject0);
                astodPack.addNamedObject(otherSideObject1);
                // push sourceClass into classStack
                classStack.push(
                    new ASTODClassStackPack(List.of(otherSideObject0, otherSideObject1),
                    otherSideClass));
                otherClassStack.push(
                    new ASTODClassStackPack(List.of(otherSideObject0, otherSideObject1),
                        otherSideClass));

                if (specialCDTypeWrapper.isEmpty()) {
                  // increase related checklist
                  List<CDRefSetAssociationWrapper> increasedCDRefSetAssociationWrapper =
                      findAllRelatedCDRefSetAssociationWrapperByCDTypeWrapperWithoutCurrentCDAssociationWrapper(
                          cdw, otherSideClass, currentCDAssociationWrapper);

                  // illegal situation
                  if (increasedCDRefSetAssociationWrapper.isEmpty()) {
                    return Optional.empty();
                  } else {
                    increaseCounterInCheckList(increasedCDRefSetAssociationWrapper, refLinkCheckList);
                  }
                }

              }
              // create new ASTODLinkList and add into ASTODElementList
              List<ASTODLink> linkList0;
              List<ASTODLink> linkList1;
              if (otherSideClassPosition == CDTypeWrapperPack.Position.LEFT) {
                linkList0 = createLinkList(otherSideObject0, objectList.get(0),
                    otherSideRoleName, currentSideRoleName, directionType);
                linkList1 = createLinkList(otherSideObject1, objectList.get(1),
                    otherSideRoleName, currentSideRoleName, directionType);
              }
              else {
                linkList0 = createLinkList(objectList.get(0), otherSideObject0,
                    currentSideRoleName, otherSideRoleName, directionType);
                linkList1 = createLinkList(objectList.get(1), otherSideObject1,
                    currentSideRoleName, otherSideRoleName, directionType);
              }
              astodPack.extendLinks(linkList0);
              astodPack.extendLinks(linkList1);
            }

            // mark related refLink
            List<CDRefSetAssociationWrapper> currentCDRefSetAssociationWrapper =
                findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(
                    cdw, currentCDAssociationWrapper, refLinkCheckList);
            decreaseCounterInCheckList(currentCDRefSetAssociationWrapper, refLinkCheckList);
          }
        }
      }
    }

    return Optional.of(astodPack);
  }

  protected Optional<ASTODPack> usingGenerateODBasicProcess(
      CDWrapper cdw,
      Optional<ASTODPack> optionalASTODPack,
      Deque<ASTODClassStackPack> classStack4TargetClass,
      Deque<ASTODClassStackPack> classStack4SourceClass,
      Deque<CDAssociationWrapper> associationStack4TargetClass,
      Deque<CDAssociationWrapper> associationStack4SourceClass,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList,
      CDSemantics cdSemantics,
      Optional<CDTypeWrapper> specialCDTypeWrapper) {

    while (!classStack4TargetClass.isEmpty() || !classStack4SourceClass.isEmpty()) {
      if (!classStack4TargetClass.isEmpty()) {
        optionalASTODPack = generateODBasicProcess(cdw,
            "target",
            classStack4TargetClass,
            classStack4SourceClass,
            associationStack4TargetClass,
            refLinkCheckList,
            optionalASTODPack.get(),
            cdSemantics,
            specialCDTypeWrapper);
      }
      else {
        optionalASTODPack = generateODBasicProcess(cdw,
            "source",
            classStack4SourceClass,
            classStack4TargetClass,
            associationStack4SourceClass,
            refLinkCheckList,
            optionalASTODPack.get(),
            cdSemantics,
            specialCDTypeWrapper);
      }
      if (optionalASTODPack.isEmpty()) {
        break;
      }
    }
    return optionalASTODPack;
  }

}