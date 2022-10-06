package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import com.google.common.graph.MutableGraph;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4InheritanceHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.CDSyntax2SemDiff4GenerateODHelper.*;

public class CDWrapper4SearchHelper {

  /**
   * Fuzzy search for CDAssociationWrapper without matching direction and cardinality
   *
   * Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutDirectionAndCardinality(
      Map<String, CDAssociationWrapper> map,
      CDAssociationWrapper currentAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    }
    else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getLeftOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getRightOriginalClassName())) {
          result.add(new CDAssociationWrapperPack(existAssoc, false));
        }
        else if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getRightOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getLeftOriginalClassName())) {
          result.add(new CDAssociationWrapperPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /**
   * Fuzzy search for CDAssociationWrapper without checking cardinality in compareCDW
   *
   *Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithoutCardinality(
      Map<String, CDAssociationWrapper> map,
      CDAssociationWrapper currentAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    }
    else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getLeftOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDAssociationWrapperDirection().equals(existAssoc.getCDAssociationWrapperDirection())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getRightOriginalClassName())) {
          result.add(new CDAssociationWrapperPack(existAssoc, false));
        }
        else if (currentAssoc.getLeftOriginalClassName().equals(existAssoc.getRightOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getCDAssociationWrapperDirection().equals(reverseDirection(existAssoc.getCDAssociationWrapperDirection()))
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getLeftOriginalClassName())) {
          result.add(new CDAssociationWrapperPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /**
   * Fuzzy search for CDAssociationWrapper only matching leftRoleName, rightRoleName and direction
   *
   * Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> fuzzySearchCDAssociationWrapperByCDAssociationWrapperWithRoleNameAndDirection(
      Map<String, CDAssociationWrapper> map, CDAssociationWrapper currentAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    }
    else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getCDAssociationWrapperDirection().equals(existAssoc.getCDAssociationWrapperDirection())) {
          result.add(new CDAssociationWrapperPack(existAssoc, false));
        }
        else if (currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDAssociationWrapperDirection().equals(reverseDirection(existAssoc.getCDAssociationWrapperDirection()))) {
          result.add(new CDAssociationWrapperPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /**
   * Fuzzy search for CDAssociationWrapper by ClassName
   */
  public static Map<String, CDAssociationWrapper> fuzzySearchCDAssociationWrapperByClassName(
      Map<String, CDAssociationWrapper> map, String className) {
    Map<String, CDAssociationWrapper> result;
    if (map == null) {
      return null;
    }
    else {
      result = map.values()
          .stream()
          .filter(e -> (e.getLeftOriginalClassName().equals(className)
              || e.getRightOriginalClassName().equals(className)))
          .collect(Collectors.toMap(CDAssociationWrapper::getName, e -> e));
    }
    return result;
  }

  /**
   * Fuzzy search for CDAssociationWrapper for checking conflict
   *
   * Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> fuzzySearchCDAssociationWrapper4CheckingConflict(
      Map<String, CDAssociationWrapper> map,
      MutableGraph<String> inheritanceGraph,
      CDAssociationWrapper baseAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    Set<CDTypeWrapper> targetClassSet4BaseAssoc = getTargetClass(baseAssoc);
    map.values().forEach(currentAssoc -> {

      // calculate intersection set of all superclasses and subclasses
      // for baseAssoc and currentAssoc
      LinkedHashSet<String> originalIntersectionSet = getAllSuperClassAndSubClassSet(inheritanceGraph,
          baseAssoc.getCDWrapperRightClass().getName());
      originalIntersectionSet.retainAll(getAllSuperClassAndSubClassSet(inheritanceGraph,
          currentAssoc.getCDWrapperRightClass().getName()));

      LinkedHashSet<String> reversedIntersectionSet = getAllSuperClassAndSubClassSet(inheritanceGraph,
          baseAssoc.getCDWrapperLeftClass().getName());
      reversedIntersectionSet.retainAll(getAllSuperClassAndSubClassSet(inheritanceGraph,
          currentAssoc.getCDWrapperRightClass().getName()));


      if (currentAssoc.getLeftOriginalClassName().equals(baseAssoc.getLeftOriginalClassName()) &&
          currentAssoc.getCDWrapperLeftClassRoleName().equals(baseAssoc.getCDWrapperLeftClassRoleName()) &&
          currentAssoc.getCDAssociationWrapperDirection() == baseAssoc.getCDAssociationWrapperDirection() &&
          currentAssoc.getCDWrapperRightClassRoleName().equals(baseAssoc.getCDWrapperRightClassRoleName()) &&
          originalIntersectionSet.isEmpty()
      ) {
        // check target class
        Set<CDTypeWrapper> targetClassSet4CurrentAssoc = getTargetClass(currentAssoc);
        if (targetClassSet4BaseAssoc.size() > 1 || !targetClassSet4CurrentAssoc.equals(targetClassSet4BaseAssoc)) {
          result.add(new CDAssociationWrapperPack(currentAssoc, false));
        }
      }
      else if (currentAssoc.getLeftOriginalClassName().equals(baseAssoc.getRightOriginalClassName()) &&
          currentAssoc.getCDWrapperLeftClassRoleName().equals(baseAssoc.getCDWrapperRightClassRoleName()) &&
          currentAssoc.getCDAssociationWrapperDirection() == reverseDirection(baseAssoc.getCDAssociationWrapperDirection()) &&
          currentAssoc.getCDWrapperRightClassRoleName().equals(baseAssoc.getCDWrapperLeftClassRoleName()) &&
          reversedIntersectionSet.isEmpty()) {
        // check target class
        Set<CDTypeWrapper> targetClassSet4CurrentAssoc = getTargetClass(currentAssoc);
        if (targetClassSet4BaseAssoc.size() > 1 || !targetClassSet4CurrentAssoc.equals(targetClassSet4BaseAssoc)) {
          result.add(new CDAssociationWrapperPack(currentAssoc, true));
        }
      }
    });
    if (!result.isEmpty()) {
      result.add(new CDAssociationWrapperPack(baseAssoc, false));
    }
    return result;
  }

  /**
   * find the same CDAssociationWrapper in compareCDW
   *
   * Return: List<CDAssociationWrapperPack>
   *   [{"cDAssociationWrapper" : CDAssociationWrapper
   *     "isReverse"            : boolean             }]
   */
  public static List<CDAssociationWrapperPack> findSameCDAssociationWrapperByCDAssociationWrapper(
      Map<String, CDAssociationWrapper> map,
      CDAssociationWrapper currentAssoc) {
    List<CDAssociationWrapperPack> result = new ArrayList<>();
    if (map == null) {
      return null;
    }
    else {
      map.values().forEach(existAssoc -> {
        if (currentAssoc.getCDWrapperLeftClassCardinality().equals(existAssoc.getCDWrapperLeftClassCardinality())
            && currentAssoc.getLeftOriginalClassName().equals(existAssoc.getLeftOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getCDAssociationWrapperDirection().equals(existAssoc.getCDAssociationWrapperDirection())
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getRightOriginalClassName())
            && currentAssoc.getCDWrapperRightClassCardinality().equals(existAssoc.getCDWrapperRightClassCardinality())) {
          result.add(new CDAssociationWrapperPack(existAssoc, false));
        }
        else if (currentAssoc.getCDWrapperLeftClassCardinality().equals(existAssoc.getCDWrapperRightClassCardinality())
            && currentAssoc.getLeftOriginalClassName().equals(existAssoc.getRightOriginalClassName())
            && currentAssoc.getCDWrapperLeftClassRoleName().equals(existAssoc.getCDWrapperRightClassRoleName())
            && currentAssoc.getCDAssociationWrapperDirection().equals(reverseDirection(existAssoc.getCDAssociationWrapperDirection()))
            && currentAssoc.getCDWrapperRightClassRoleName().equals(existAssoc.getCDWrapperLeftClassRoleName())
            && currentAssoc.getRightOriginalClassName().equals(existAssoc.getLeftOriginalClassName())
            && currentAssoc.getCDWrapperRightClassCardinality().equals(existAssoc.getCDWrapperLeftClassCardinality())) {
          result.add(new CDAssociationWrapperPack(existAssoc, true));
        }
      });
    }
    return result;
  }

  /**
   * according to CDAssociationWrapper to find all corresponding CDRefSetAssociationWrapper
   * including Inheritance CDRefSetAssociationWrapper
   */
  public static List<CDRefSetAssociationWrapper> findAllRelatedCDRefSetAssociationWrapperIncludingInheritanceByCDAssociationWrapper(
      CDWrapper cdw, CDAssociationWrapper originalAssoc,
      Map<CDRefSetAssociationWrapper, Integer> refLinkCheckList) {
    Set<CDRefSetAssociationWrapper> resulSet = new HashSet<>();

    // get all subclasses of leftCDTypeWrapper and rightCDTypeWrapper in originalAssoc
    Set<CDTypeWrapper> CDTypeWrapperSet = new HashSet<>();
    CDTypeWrapperSet.add(originalAssoc.getCDWrapperLeftClass());
    CDTypeWrapperSet.addAll(
        getAllSimpleSubClasses4CDTypeWrapper(originalAssoc.getCDWrapperLeftClass(),
            cdw.getCDTypeWrapperGroup()));
    CDTypeWrapperSet.add(originalAssoc.getCDWrapperRightClass());
    CDTypeWrapperSet.addAll(
        getAllSimpleSubClasses4CDTypeWrapper(originalAssoc.getCDWrapperRightClass(),
            cdw.getCDTypeWrapperGroup()));

    // get all related associations of each class in CDTypeWrapperSet
    Map<String, CDAssociationWrapper> cDAssociationWrapperMap = new HashMap<>();
    CDTypeWrapperSet.forEach(e -> cDAssociationWrapperMap.putAll(
        fuzzySearchCDAssociationWrapperByClassName(cdw.getCDAssociationWrapperGroup(),
            e.getOriginalClassName())));

    // get all related CDRefSetAssociationWrapper of each Assoc in cDAssociationWrapperMap
    cDAssociationWrapperMap.values().forEach(e -> {
      if ((e.getCDWrapperLeftClassRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName())
          && e.getCDWrapperRightClassRoleName()
          .equals(originalAssoc.getCDWrapperRightClassRoleName()) && (
          mappingDirection(originalAssoc.getCDAssociationWrapperDirection()) == 3
              || e.getCDAssociationWrapperDirection()
              .equals(originalAssoc.getCDAssociationWrapperDirection()))) ||
          (e.getCDWrapperLeftClassRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName())
              && e.getCDWrapperRightClassRoleName()
              .equals(originalAssoc.getCDWrapperLeftClassRoleName())) && (
              mappingDirection(originalAssoc.getCDAssociationWrapperDirection()) == 3
                  || e.getCDAssociationWrapperDirection()
                  .equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection())))) {
        resulSet.addAll(
            findDirectRelatedCDRefSetAssociationWrapperByCDAssociationWrapper(e, refLinkCheckList));
      }
    });

    return new ArrayList<>(resulSet);
  }

  /**
   * according to CDTypeWrapper to find all corresponding CDRefSetAssociationWrapper
   * that is except current assoc
   */
  public static List<CDRefSetAssociationWrapper> findAllRelatedCDRefSetAssociationWrapperByCDTypeWrapperWithoutCurrentCDAssociationWrapper(
      CDWrapper cdw,
      CDTypeWrapper originalCDTypeWrapper,
      CDAssociationWrapper currentAssoc) {

    return cdw.getRefSetAssociationList()
        .stream()
        .filter(e ->
            (e.getLeftRefSet()
                .stream()
                .map(CDTypeWrapper::getOriginalClassName)
                .collect(Collectors.toSet())
                .contains(originalCDTypeWrapper.getOriginalClassName())
                && e.getOriginalElement().getCDWrapperLeftClassCardinality() != CDAssociationWrapperCardinality.ZERO_TO_ONE
                && e.getOriginalElement().getCDWrapperLeftClassCardinality() != CDAssociationWrapperCardinality.ONE) ||
                (e.getRightRefSet()
                    .stream()
                    .map(CDTypeWrapper::getOriginalClassName)
                    .collect(Collectors.toSet())
                    .contains(originalCDTypeWrapper.getOriginalClassName())
                    && e.getOriginalElement().getCDWrapperRightClassCardinality() != CDAssociationWrapperCardinality.ZERO_TO_ONE
                    && e.getOriginalElement().getCDWrapperRightClassCardinality() != CDAssociationWrapperCardinality.ONE)
        )
        .filter(e -> !e.isPresentInCDRefSetAssociationWrapper(currentAssoc))
        .collect(Collectors.toList());
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
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperRightClassRoleName()) && (
          mappingDirection(originalAssoc.getCDAssociationWrapperDirection()) == 3
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
          && item.getRightRoleName().equals(originalAssoc.getCDWrapperLeftClassRoleName()) && (
          mappingDirection(originalAssoc.getCDAssociationWrapperDirection()) == 3
              || item.getDirection()
              .equals(reverseDirection(originalAssoc.getCDAssociationWrapperDirection())))
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

  /**
   * the created object as target class find all related associations of this created object
   */
  public static List<CDAssociationWrapper> findAllCDAssociationWrapperByTargetClass(
      CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper,
      boolean checkCardinality) {
    List<CDAssociationWrapper> result = new LinkedList<>();
    Map<String, CDAssociationWrapper> cDAssociationWrapperMap =
        fuzzySearchCDAssociationWrapperByClassName(
            cdw.getCDAssociationWrapperGroup(), cDTypeWrapper.getOriginalClassName());
    cDAssociationWrapperMap.forEach((name, assoc) -> {
      // CDTypeWrapper <-
      if (assoc.getCDWrapperLeftClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
        if (checkCardinality) {
          if (assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE) {
            result.add(assoc);
          }
        } else {
          result.add(assoc);
        }
      }
      // -> CDTypeWrapper
      if (assoc.getCDWrapperRightClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.LEFT_TO_RIGHT) {
        if (checkCardinality) {
          if (assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE) {
            result.add(assoc);
          }
        } else {
          result.add(assoc);
        }
      }
      // <->  --
      if (assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.BIDIRECTIONAL
          || assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.UNDEFINED) {
        if (checkCardinality) {
          if (assoc.getCDWrapperLeftClass().getOriginalClassName().equals(cDTypeWrapper.getOriginalClassName())
              && (assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE)) {
            result.add(assoc);
          }
          if (assoc.getCDWrapperRightClass().getOriginalClassName().equals(cDTypeWrapper.getOriginalClassName())
              && (assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE)) {
            result.add(assoc);
          }
        } else {
          result.add(assoc);
        }
      }
    });

//    Collections.reverse(result);
    return result;
  }

  /**
   * the created object as source class find all related associations of this created object
   */
  public static List<CDAssociationWrapper> findAllCDAssociationWrapperBySourceClass(
      CDWrapper cdw,
      CDTypeWrapper cDTypeWrapper,
      boolean checkCardinality) {
    List<CDAssociationWrapper> result = new ArrayList<>();
    Map<String, CDAssociationWrapper> cDAssociationWrapperMap =
        fuzzySearchCDAssociationWrapperByClassName(
            cdw.getCDAssociationWrapperGroup(), cDTypeWrapper.getOriginalClassName());
    cDAssociationWrapperMap.forEach((name, assoc) -> {
      // <- CDTypeWrapper
      if (assoc.getCDWrapperRightClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
        if (checkCardinality) {
          if (assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE) {
            result.add(assoc);
          }
        } else {
          result.add(assoc);
        }
      }
      // CDTypeWrapper ->
      if (assoc.getCDWrapperLeftClass()
          .getOriginalClassName()
          .equals(cDTypeWrapper.getOriginalClassName()) && assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.LEFT_TO_RIGHT) {
        if (checkCardinality) {
          if (assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE) {
            result.add(assoc);
          }
        } else {
          result.add(assoc);
        }
      }
      // <->  --
      if (assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.BIDIRECTIONAL
          || assoc.getCDAssociationWrapperDirection()
          == CDAssociationWrapperDirection.UNDEFINED) {
        if (checkCardinality) {
          if (assoc.getCDWrapperLeftClass().getOriginalClassName().equals(cDTypeWrapper.getOriginalClassName())
              && (assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperRightClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE)) {
            result.add(assoc);
          }
          if (assoc.getCDWrapperRightClass().getOriginalClassName().equals(cDTypeWrapper.getOriginalClassName())
              && (assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE
              || assoc.getCDWrapperLeftClassCardinality() == CDAssociationWrapperCardinality.ONE_TO_MORE)) {
            result.add(assoc);
          }
        } else {
          result.add(assoc);
        }
      }
    });

    return result;
  }

  /**
   * get the other side class in CDAssociationWrapper
   * if the given CDAssociationWrapper is self-loop, that is no problem.
   * return the found other side class, and it's position side.
   *
   * Return: CDTypeWrapperPack {
   *    "otherSideClass" : CDTypeWrapper
   *    "position"       : ["left", "right"] }
   */
  public static CDTypeWrapperPack findOtherSideClassAndPositionInCDAssociationWrapper(
      CDAssociationWrapper cDAssociationWrapper,
      CDTypeWrapper currentClass,
      String opt4StartClassKind) {
    CDTypeWrapperPack.Position position;
    CDTypeWrapper otherSideClass;
    if (opt4StartClassKind.equals("target")) {
      switch (cDAssociationWrapper.getCDAssociationWrapperDirection()) {
        case LEFT_TO_RIGHT:
          otherSideClass = cDAssociationWrapper.getCDWrapperLeftClass();
          position = CDTypeWrapperPack.Position.LEFT;
          break;
        case RIGHT_TO_LEFT:
          otherSideClass = cDAssociationWrapper.getCDWrapperRightClass();
          position = CDTypeWrapperPack.Position.RIGHT;
          break;
        default:
          if (cDAssociationWrapper.getCDWrapperLeftClass()
              .getOriginalClassName()
              .equals(currentClass.getOriginalClassName())) {
            otherSideClass = cDAssociationWrapper.getCDWrapperRightClass();
            position = CDTypeWrapperPack.Position.RIGHT;
          }
          else {
            otherSideClass = cDAssociationWrapper.getCDWrapperLeftClass();
            position = CDTypeWrapperPack.Position.LEFT;
          }
          break;
      }
    } else {
      switch (cDAssociationWrapper.getCDAssociationWrapperDirection()) {
        case LEFT_TO_RIGHT:
          otherSideClass = cDAssociationWrapper.getCDWrapperRightClass();
          position = CDTypeWrapperPack.Position.RIGHT;
          break;
        case RIGHT_TO_LEFT:
          otherSideClass = cDAssociationWrapper.getCDWrapperLeftClass();
          position = CDTypeWrapperPack.Position.LEFT;
          break;
        default:
          if (cDAssociationWrapper.getCDWrapperLeftClass()
              .getOriginalClassName()
              .equals(currentClass.getOriginalClassName())) {
            otherSideClass = cDAssociationWrapper.getCDWrapperRightClass();
            position = CDTypeWrapperPack.Position.RIGHT;
          }
          else {
            otherSideClass = cDAssociationWrapper.getCDWrapperLeftClass();
            position = CDTypeWrapperPack.Position.LEFT;
          }
          break;
      }
    }
    return new CDTypeWrapperPack(otherSideClass, position);
  }
}
