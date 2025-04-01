package de.monticore.cdconcretization;

import de.monticore.cdconformance.CDConformanceChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssociationConcretizationTest extends AbstractCDConcretizationTest {

  @Test
  @Disabled("disabled until issue 13 is clarified")
  void testAssocMissingSimple() {
    testConcretizedEqualsRef(
            "associations/AssociationMissingSimpleConc.cd",
            "associations/AssociationMissingSimpleRef.cd");
  }

  @Test
  void testAssocMissingCardinality() {
    testConcretizedEqualsRef(
            "associations/AssociationMissingCardinalityConc.cd",
            "associations/AssociationMissingCardinalityRef.cd");
  }

  @Test
  void testAssocMissingRolename() {
    testConcretizedEqualsRef(
            "associations/AssociationMissingRolenameConc.cd",
            "associations/AssociationMissingRolenameRef.cd");
  }

  @Test
  void testAssocMissingFinal() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/AssociationMissingFinalConc.cd",
            "associations/AssociationMissingFinalRef.cd",
            "associations/AssociationMissingFinalOut.cd");
  }

  @Test
  void testAssocMultipleTypeIncarnation() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/AssociationMultipleTypeIncarnationConc.cd",
            "associations/AssociationMultipleTypeIncarnationRef.cd",
            "associations/AssociationMultipleTypeIncarnationOut.cd");
  }

  @Test
  void testAssocInSuperType() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/AssociationInSuperTypeConc.cd",
            "associations/AssociationInSuperTypeRef.cd",
            "associations/AssociationInSuperTypeOut.cd");
  }

  @Test
  void testAssocSuperMatchingTest() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/AssociationSuperMatchingConc.cd",
            "associations/AssociationSuperMatchingRef.cd",
            "associations/AssociationSuperMatchingOut.cd");
  }

  @Test
  void testAssocSuperMatchingConformanceTest() {
    parseModels(
            "associations/AssociationSuperMatchingOut.cd",
            "associations/AssociationSuperMatchingRef.cd");
    assertTrue(
            new CDConformanceChecker(
                    Set.of(
                            STEREOTYPE_MAPPING,
                            NAME_MAPPING,
                            SRC_TARGET_ASSOC_MAPPING,
                            INHERITANCE,
                            ALLOW_CARD_RESTRICTION))
                    .checkConformance(conCD, refCD, Set.of("ref")));
  }

  @Test
  void testAssociationReverseMatch() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/AssociationReverseMatchConc.cd",
            "associations/AssociationReverseMatchRef.cd",
            "associations/AssociationReverseMatchOut.cd");
  }

  @Test
  void testTypeMIOneAssocExists() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/TypeMIOneAssocExistsConc.cd",
            "associations/TypeMIOneAssocExistsRef.cd",
            "associations/TypeMIOneAssocExistsOut.cd");
  }
}
