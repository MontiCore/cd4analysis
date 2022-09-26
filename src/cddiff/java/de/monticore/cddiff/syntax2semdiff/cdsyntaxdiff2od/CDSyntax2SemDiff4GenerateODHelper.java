package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;
import de.monticore.odbasis._ast.*;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4InheritanceHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4SearchHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4ASTODHelper.createObject;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4ASTODHelper.toLowerCaseFirstOne4ClassName;

public class CDSyntax2SemDiff4GenerateODHelper {

  /**
   * mapping the type of compare direction into integer it's easy to determine which the direction
   * should be used in OD
   */
  public static int mappingDirection(CDAssociationWrapperDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return 1;
      case RIGHT_TO_LEFT:
        return 2;
      case BIDIRECTIONAL:
        return 3;
      case UNDEFINED:
        return 3;
      default:
        return 0;
    }
  }

  public static int mappingDirection(CDAssociationDiffDirection direction) {
    switch (direction) {
      case LEFT_TO_RIGHT:
        return 1;
      case RIGHT_TO_LEFT:
        return 2;
      case BIDIRECTIONAL:
        return 3;
      case LEFT_TO_RIGHT_OR_RIGHT_TO_LEFT:
        return 1;
      default:
        return 0;
    }
  }

  /**
   * mapping the type of compare cardinality into integer in general step it's easy to determine how
   * many objects of current association should be created in OD
   */
  public static int mappingCardinality(CDAssociationWrapperCardinality cardinality) {
    switch (cardinality) {
      case ONE:
        return 1;
      case ZERO_TO_ONE:
        return 0;
      case ONE_TO_MORE:
        return 1;
      case MORE:
        return 0;
      default:
        return 0;
    }
  }

  public static int mappingCardinality(CDAssociationDiffCardinality cardinality) {
    switch (cardinality) {
      case ZERO:
        return 0;
      case TWO_TO_MORE:
        return 2;
      case ZERO_AND_TWO_TO_MORE:
        return 0;
      default:
        return 0;
    }
  }

  /**
   * mapping the type of compare cardinality into integer in initial step it's easy to determine how
   * many objects of current association should be created in OD
   */
  public static int mappingCardinality4Initial(CDAssociationWrapperCardinality cardinality) {
    switch (cardinality) {
      case ONE:
        return 1;
      case ZERO_TO_ONE:
        return 1;
      case ONE_TO_MORE:
        return 1;
      case MORE:
        return 1;
      default:
        return 0;
    }
  }

  public static int mappingCardinality4Initial(CDAssociationDiffCardinality cardinality) {
    switch (cardinality) {
      case ZERO:
        return 0;
      case TWO_TO_MORE:
        return 2;
      case ZERO_AND_TWO_TO_MORE:
        return 2;
      default:
        return 0;
    }
  }

  /**
   * convert refSetAssociationList to checkList if the item in refSetAssociationList is created,
   * then the corresponding counter of this item should be minus one until the counter equals zero
   */
  public static Map<CDRefSetAssociationWrapper, Integer> convertRefSetAssociationList2CheckList(
      List<CDRefSetAssociationWrapper> refSetAssociationList) {
    Map<CDRefSetAssociationWrapper, Integer> checkList = new HashMap<>();
    refSetAssociationList.forEach(item -> checkList.put(item, 1));
    return checkList;
  }

  public static Map<CDRefSetAssociationWrapper, Integer> convertRefSetAssociationList2CheckList(
      List<CDRefSetAssociationWrapper> refSetAssociationList,
      CDAssocWrapperDiff cDAssocWrapperDiff) {

    // special situation for LEFT_SPECIAL_CARDINALITY and RIGHT_SPECIAL_CARDINALITY
    if (cDAssocWrapperDiff.getWhichPartDiff().isPresent()) {
      if (cDAssocWrapperDiff.getCDDiffCategory() ==
          CDAssociationDiffCategory.CARDINALITY_CHANGED &&
          (cDAssocWrapperDiff.getWhichPartDiff().get() ==
              WhichPartDiff.LEFT_SPECIAL_CARDINALITY ||
              cDAssocWrapperDiff.getWhichPartDiff().get() ==
                  WhichPartDiff.RIGHT_SPECIAL_CARDINALITY)) {
        return convertRefSetAssociationList2CheckList(refSetAssociationList);
      }
    }

    Map<CDRefSetAssociationWrapper, Integer> checkList = new HashMap<>();
    refSetAssociationList.forEach(item -> {
      List<CDRefSetAssociationWrapper> temp = new ArrayList<>();
      if (!item.isPresentInCDRefSetAssociationWrapper(cDAssocWrapperDiff.getBaseElement())) {
        temp = refSetAssociationList.stream()
            .filter(e ->
                e.getLeftRefSet().equals(item.getLeftRefSet()) &&
                e.getLeftRoleName().equals(item.getLeftRoleName()) &&
                e.getDirection().equals(reverseDirection(item.getDirection())) &&
                e.getRightRoleName().equals(item.getRightRoleName()) &&
                e.getRightRefSet().equals(item.getRightRefSet())
            )
            .collect(Collectors.toList());
      }
      if (temp.isEmpty()) {
        checkList.put(item, 1);
      } else {
        checkList.put(item, 2);
      }
    });
    return checkList;
  }

  /**
   * decrease the counter of relevant CDRefSetAssociationWrapper in checklist
   */
  public static void decreaseCounterInCheckList(
      List<CDRefSetAssociationWrapper> associationList,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    if (!associationList.isEmpty()) {
      associationList.forEach(e -> refLinkCheckList.put(e, refLinkCheckList.get(e) - 1));
    }
  }

  /**
   * increase the counter of relevant CDRefSetAssociationWrapper in checklist
   */
  public static void increaseCounterInCheckList(
      List<CDRefSetAssociationWrapper> associationList,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    if (!associationList.isEmpty()) {
      associationList.forEach(e -> refLinkCheckList.put(e, refLinkCheckList.get(e) + 1));
    }
  }

  /**
   * check whether the related CDRefSetAssociationWrapper is used by CDAssociationWrapper
   */
  public static boolean checkRelatedCDRefSetAssociationWrapperIsUsed(CDWrapper cdw,
      CDAssociationWrapper association, Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
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
   * if current Assoc = A -> B then check if exist A <- B in CD and if A <- B is created
   * if it is created, this situation is not illegal
   * otherwise this situation is illegal
   *
   * if current Assoc = A -> B then check if exist B -> A in CD and if B -> A is created
   * if it is created, this situation is not illegal
   * otherwise this situation is illegal
   */
  public static boolean checkIllegalSituationOnly4CDAssociationWrapperWithLeftToRightAndRightToLeft(
      CDWrapper cdw,
      CDAssociationWrapper currentAssoc,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {

    if (currentAssoc.getCDAssociationWrapperDirection()
        == CDAssociationWrapperDirection.LEFT_TO_RIGHT
        || currentAssoc.getCDAssociationWrapperDirection()
        == CDAssociationWrapperDirection.RIGHT_TO_LEFT) {

      List<CDAssociationWrapperPack> cDAssociationWrapperPacks =
          fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
          cdw.getCDAssociationWrapperGroup(), currentAssoc);

      return cDAssociationWrapperPacks
          .stream()
          .filter(pack ->
            // A -> B, A <- B
            (!pack.isReverse() &&
                  pack.getCDAssociationWrapper()
                      .getCDAssociationWrapperDirection()
                      .equals(reverseDirection(currentAssoc.getCDAssociationWrapperDirection()))) ||
            // A -> B, B -> A
            (pack.isReverse() &&
                pack.getCDAssociationWrapper()
                    .getCDAssociationWrapperDirection()
                    .equals(currentAssoc.getCDAssociationWrapperDirection())))
          .noneMatch(pack -> {
            // check pack.getCDAssociationWrapper() whether is used ?
            Set<CDRefSetAssociationWrapper> cDRefSetAssociationWrappers =
                findDirectRelatedCDRefSetAssociationWrapperByCDAssociationWrapper(
                    pack.getCDAssociationWrapper(), refLinkCheckList);
            return cDRefSetAssociationWrappers
                .stream()
                .filter(e -> e.getOriginalElement().equals(pack.getCDAssociationWrapper()))
                .allMatch(e -> refLinkCheckList.get(e) == 0);
          });
    }
    return true;
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
    if (cDTypeWrapper.getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_INTERFACE
        || cDTypeWrapper.getCDWrapperKind()
        == CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
      List<CDTypeWrapper> subClassList =
          getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper, cdw.getCDTypeWrapperGroup());
      subClassList.forEach(c -> objectList.forEach(e -> {
        if (e.getName().split("_")[0].equals(
            toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
          isInList.set(true);
        }
      }));
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
   * check the inherited object of given CDTypeWrapper whether is in ASTODElementList
   */
  public static boolean isPresentInheritedObjectInASTODElementListByCDTypeWrapper(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper, ASTODPack astodPack) {
    AtomicBoolean isInList = new AtomicBoolean(false);

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    List<CDTypeWrapper> subClassList =
        getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper, cdw.getCDTypeWrapperGroup());
    subClassList.forEach(c -> objectList.forEach(e -> {
      if (e.getName().split("_")[0].equals(
          toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
        isInList.set(true);
      }
    }));

    return isInList.get();
  }

  /**
   * check the super object of given CDTypeWrapper whether is in ASTODElementList
   */
  public static boolean isPresentSuperObjectInASTODElementListByCDTypeWrapper(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper, ASTODPack astodPack) {
    AtomicBoolean isInList = new AtomicBoolean(false);

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    List<CDTypeWrapper> superClassList =
        getAllSimpleSuperClasses4CDTypeWrapper(cDTypeWrapper, cdw.getCDTypeWrapperGroup());
    superClassList.forEach(c -> objectList.forEach(e -> {
      if (e.getName().split("_")[0].equals(
          toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
        isInList.set(true);
      }
    }));

    return isInList.get();
  }

  /**
   * check the object of given CDTypeWrapper whether is in ASTODElementList
   * if it is in ASTODElementList, return the existed ASTODElement;
   * if it is not in ASTODElementList, create a new ASTODElement as return element.
   *
   * Return: ASTODNamedObjectPack {
   *   "objectList"  : List<ASTODNamedObject>
   *   "isInList"    : boolean                }
   */
  public static ASTODNamedObjectPack getObjectInASTODElementListByCDTypeWrapper(CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper, ASTODPack astodPack, CDSemantics cdSemantics) {

    AtomicBoolean isInList = new AtomicBoolean(false);
    AtomicReference<List<ASTODNamedObject>> resultList = new AtomicReference<>(new ArrayList<>());

    // choose ASTODNamedObjects from ASTODPack
    List<ASTODNamedObject> objectList = astodPack.getNamedObjects();

    // if this CDTypeWrapper is interface or abstract class,
    // check the subclass of this CDTypeWrapper whether is in objectList.
    if (cDTypeWrapper.getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_INTERFACE
        || cDTypeWrapper.getCDWrapperKind()
        == CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
      List<CDTypeWrapper> subClassList =
          getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper, cdw.getCDTypeWrapperGroup());
      subClassList.forEach(c -> objectList.forEach(e -> {
        if (e.getName().split("_")[0].equals(
            toLowerCaseFirstOne4ClassName(c.getOriginalClassName()))) {
          List<ASTODNamedObject> tempList = resultList.get();
          tempList.add(e);
          resultList.set(tempList);
          isInList.set(true);
        }
      }));
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
      if (cDTypeWrapper.getCDWrapperKind() == CDTypeWrapperKind.CDWRAPPER_INTERFACE
          || cDTypeWrapper.getCDWrapperKind()
          == CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS) {
        List<CDTypeWrapper> CDTypeWrapperList =
            getAllSimpleSubClasses4CDTypeWrapperWithStatusOpen(cDTypeWrapper, cdw.getCDTypeWrapperGroup());

        // Guaranteed CDTypeWrapper status is OPEN
        if (CDTypeWrapperList.isEmpty()) {
          return new ASTODNamedObjectPack();
        }

        newCDTypeWrapper = CDTypeWrapperList.get(CDTypeWrapperList.size() - 1);
      }
      else {
        newCDTypeWrapper = cDTypeWrapper;
      }
      // put new object into resultList
      List<ASTODNamedObject> tempList = resultList.get();

      CDWrapperObjectPack cdWrapperObjectPack =
          createObject(cdw, Optional.empty(), newCDTypeWrapper, 0, Optional.empty(), cdSemantics);

      // Guaranteed CDTypeWrapper status is OPEN
      if (cdWrapperObjectPack.isEmpty()) {
        return new ASTODNamedObjectPack();
      }

      tempList.add(cdWrapperObjectPack.getNamedObject());
      resultList.set(tempList);
    }

    return new ASTODNamedObjectPack(resultList.get(), isInList.get());
  }
}
