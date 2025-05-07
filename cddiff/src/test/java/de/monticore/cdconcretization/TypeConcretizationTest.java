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
}
