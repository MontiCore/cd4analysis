/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4SearchHelper.*;
import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4TypeHelper.*;

import com.google.common.graph.MutableGraph;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperCardinality;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperDirection;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperPack;
import java.util.List;
import java.util.Map;

public class CDWrapper4ConflictHelper {

  public static boolean checkConflictLeftCardinalityHelper(
      List<CDAssociationWrapperPack> cdAssociationWrapperPacks,
      boolean isReverse,
      CDAssociationWrapperCardinality cardinality1,
      CDAssociationWrapperCardinality cardinality2) {
    if (!isReverse) {
      return cdAssociationWrapperPacks.stream()
          .filter(e -> !e.isReverse())
          .anyMatch(
              e ->
                  e.getCDAssociationWrapper().getCDWrapperLeftClassCardinality() == cardinality1
                      || e.getCDAssociationWrapper().getCDWrapperLeftClassCardinality()
                          == cardinality2);
    } else {
      return cdAssociationWrapperPacks.stream()
          .filter(CDAssociationWrapperPack::isReverse)
          .anyMatch(
              e ->
                  e.getCDAssociationWrapper().getCDWrapperLeftClassCardinality() == cardinality1
                      || e.getCDAssociationWrapper().getCDWrapperLeftClassCardinality()
                          == cardinality2);
    }
  }

  public static boolean checkConflictRightCardinalityHelper(
      List<CDAssociationWrapperPack> cdAssociationWrapperPacks,
      boolean isReverse,
      CDAssociationWrapperCardinality cardinality1,
      CDAssociationWrapperCardinality cardinality2) {
    if (!isReverse) {
      return cdAssociationWrapperPacks.stream()
          .filter(e -> !e.isReverse())
          .anyMatch(
              e ->
                  e.getCDAssociationWrapper().getCDWrapperRightClassCardinality() == cardinality1
                      || e.getCDAssociationWrapper().getCDWrapperRightClassCardinality()
                          == cardinality2);
    } else {
      return cdAssociationWrapperPacks.stream()
          .filter(CDAssociationWrapperPack::isReverse)
          .anyMatch(
              e ->
                  e.getCDAssociationWrapper().getCDWrapperRightClassCardinality() == cardinality1
                      || e.getCDAssociationWrapper().getCDWrapperRightClassCardinality()
                          == cardinality2);
    }
  }

  /**
   * Helper for original and reversed directions that are "->" / "<->" / "--"
   *
   * <p>CategoryOne: direction in "->" / "<->" / "--" option == true: the left cardinality of
   * baseAssoc in [*, 0..1] option == false: the left cardinality of baseAssoc in [1, 1..*]
   */
  public static void checkConflictOriginalReversedDirectionHelper4CategoryOne(
      List<CDAssociationWrapperPack> cdAssociationWrapperPacks,
      CDAssociationWrapper baseAssoc,
      boolean option) {
    // original
    if (checkConflictRightCardinalityHelper(
        cdAssociationWrapperPacks,
        false,
        CDAssociationWrapperCardinality.MULTIPLE,
        CDAssociationWrapperCardinality.OPTIONAL)) {
      updateCDStatus4CDAssociationWrapper(cdAssociationWrapperPacks);
    }
    if (checkConflictRightCardinalityHelper(
        cdAssociationWrapperPacks,
        false,
        CDAssociationWrapperCardinality.ONE,
        CDAssociationWrapperCardinality.AT_LEAST_ONE)) {
      if (option) {
        updateCDStatus4CDTypeWrapper(baseAssoc.getCDWrapperLeftClass());
      } else {
        updateCDStatus4CDTypeWrapper(cdAssociationWrapperPacks);
      }
    }

    // reversed
    if (checkConflictLeftCardinalityHelper(
        cdAssociationWrapperPacks,
        true,
        CDAssociationWrapperCardinality.MULTIPLE,
        CDAssociationWrapperCardinality.OPTIONAL)) {
      updateCDStatus4CDAssociationWrapper(cdAssociationWrapperPacks);
    }
    if (checkConflictLeftCardinalityHelper(
        cdAssociationWrapperPacks,
        true,
        CDAssociationWrapperCardinality.ONE,
        CDAssociationWrapperCardinality.AT_LEAST_ONE)) {
      if (option) {
        updateCDStatus4CDTypeWrapper(baseAssoc.getCDWrapperLeftClass());
      } else {
        updateCDStatus4CDTypeWrapper(cdAssociationWrapperPacks);
      }
    }
  }

  /**
   * Helper for original and reversed directions that are "<-"
   *
   * <p>CategoryTwo: direction in "<-" option == true: the left cardinality of baseAssoc in [*,
   * 0..1] option == false: the left cardinality of baseAssoc in [1, 1..*]
   */
  public static void checkConflictOriginalReversedDirectionHelper4CategoryTwo(
      List<CDAssociationWrapperPack> cdAssociationWrapperPacks,
      CDAssociationWrapper baseAssoc,
      boolean option) {
    // original
    if (checkConflictLeftCardinalityHelper(
        cdAssociationWrapperPacks,
        false,
        CDAssociationWrapperCardinality.MULTIPLE,
        CDAssociationWrapperCardinality.OPTIONAL)) {
      updateCDStatus4CDAssociationWrapper(cdAssociationWrapperPacks);
    }
    if (checkConflictLeftCardinalityHelper(
        cdAssociationWrapperPacks,
        false,
        CDAssociationWrapperCardinality.ONE,
        CDAssociationWrapperCardinality.AT_LEAST_ONE)) {
      if (option) {
        updateCDStatus4CDTypeWrapper(baseAssoc.getCDWrapperRightClass());
      } else {
        updateCDStatus4CDTypeWrapper(cdAssociationWrapperPacks);
      }
    }

    // reversed
    if (checkConflictRightCardinalityHelper(
        cdAssociationWrapperPacks,
        true,
        CDAssociationWrapperCardinality.MULTIPLE,
        CDAssociationWrapperCardinality.OPTIONAL)) {
      updateCDStatus4CDAssociationWrapper(cdAssociationWrapperPacks);
    }
    if (checkConflictRightCardinalityHelper(
        cdAssociationWrapperPacks,
        true,
        CDAssociationWrapperCardinality.ONE,
        CDAssociationWrapperCardinality.AT_LEAST_ONE)) {
      if (option) {
        updateCDStatus4CDTypeWrapper(baseAssoc.getCDWrapperRightClass());
      } else {
        updateCDStatus4CDTypeWrapper(cdAssociationWrapperPacks);
      }
    }
  }

  public static void checkConflict4CDAssociationWrapper(
      Map<String, CDAssociationWrapper> cDAssociationWrapperGroup,
      MutableGraph<String> inheritanceGraph) {
    cDAssociationWrapperGroup.forEach(
        (name, baseAssoc) -> {
          List<CDAssociationWrapperPack> cdAssociationWrapperPacks =
              fuzzySearchCDAssociationWrapper4CheckingConflict(
                  cDAssociationWrapperGroup, inheritanceGraph, baseAssoc);

          if (!cdAssociationWrapperPacks.isEmpty()) {

            // Cardinality in [*, 0..1]
            if (baseAssoc.getCDWrapperLeftClassCardinality()
                    == CDAssociationWrapperCardinality.MULTIPLE
                || baseAssoc.getCDWrapperLeftClassCardinality()
                    == CDAssociationWrapperCardinality.OPTIONAL) {

              // -> / <-> / --
              if (baseAssoc.getCDAssociationWrapperDirection()
                      == CDAssociationWrapperDirection.LEFT_TO_RIGHT
                  || baseAssoc.getCDAssociationWrapperDirection()
                      == CDAssociationWrapperDirection.BIDIRECTIONAL
                  || baseAssoc.getCDAssociationWrapperDirection()
                      == CDAssociationWrapperDirection.UNDEFINED) {
                checkConflictOriginalReversedDirectionHelper4CategoryOne(
                    cdAssociationWrapperPacks, baseAssoc, true);
              }

              // <-
              if (baseAssoc.getCDAssociationWrapperDirection()
                  == CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
                checkConflictOriginalReversedDirectionHelper4CategoryTwo(
                    cdAssociationWrapperPacks, baseAssoc, true);
              }
            }
            // Cardinality in [1, 1..*]
            else {
              // -> / <-> / --
              if (baseAssoc.getCDAssociationWrapperDirection()
                      == CDAssociationWrapperDirection.LEFT_TO_RIGHT
                  || baseAssoc.getCDAssociationWrapperDirection()
                      == CDAssociationWrapperDirection.BIDIRECTIONAL
                  || baseAssoc.getCDAssociationWrapperDirection()
                      == CDAssociationWrapperDirection.UNDEFINED) {
                checkConflictOriginalReversedDirectionHelper4CategoryOne(
                    cdAssociationWrapperPacks, baseAssoc, false);
              }

              // <-
              if (baseAssoc.getCDAssociationWrapperDirection()
                  == CDAssociationWrapperDirection.RIGHT_TO_LEFT) {
                checkConflictOriginalReversedDirectionHelper4CategoryTwo(
                    cdAssociationWrapperPacks, baseAssoc, false);
              }
            }
          }
        });
  }
}
