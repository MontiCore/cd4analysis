package de.monticore.cdconcretization;

import org.junit.jupiter.api.Test;

public class TypeConcretizationTest extends AbstractCDConcretizationTest {

  /**
   * Test that checks if all the types in the reference CD that are missing in the concrete CD are
   * added based on predefined CDs.
   */
  @Test
  void testTypeMissing() {
    testConcretizedConformsToRefAndExpectedOut(
        "types/valid/TypeMissingConc.cd",
        "types/valid/TypeMissingRef.cd",
        "types/valid/TypeMissingOut.cd");
  }

  @Test
  void testMissingEnumMember() {
    testConcretizedConformsToRefAndExpectedOut(
        "types/enums/EnumMemberMissingConc.cd",
        "types/enums/EnumMemberMissingRef.cd",
        "types/enums/EnumMemberMissingOut.cd");
  }

  @Test
  void testTypeForEachType() {
    testConcretizedConformsToRefAndExpectedOut(
        "types/forEach/ForEachTypeConc.cd",
        "types/forEach/ForEachTypeRef.cd",
        "types/forEach/ForEachTypeOut.cd");
  }

  /**
   * We have no incarnation of the target type in the concrete CD. However, the default behavior
   * is to add the target type to the concrete CD if there is no incarnation.
   * Thus, the forEach loop has one target to process and adds single 'Builder' to the concrete CD.
   */
  @Test
  void testTypeForEachTypeNoTargetInc() {
    testConcretizedConformsToRefAndExpectedOut(
        "types/forEach/ForEachTypeNoTargetIncConc.cd",
        "types/forEach/ForEachTypeRef.cd",
        "types/forEach/ForEachTypeNoTargetIncOut.cd");
  }
}
