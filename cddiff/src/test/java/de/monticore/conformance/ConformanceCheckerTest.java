package de.monticore.conformance;

import static de.monticore.conformance.ConfParameter.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ConformanceCheckerTest extends ConfAbstractTest {
  @Test
  public void testConformanceCheck() {
    parseModels("Concrete.cd", "Reference.cd");
    checker =
        new ConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }

  @Test
  public void testOptConformanceCheck() {
    parseModels("Concrete.cd", "OptReference.cd");
    checker =
        new ConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }

  @Test
  public void testFalseOptConformanceCheck() {
    parseModels("FalseConcrete.cd", "OptReference.cd");
    checker =
        new ConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }

  @Test
  public void testConformanceCheckAdapter() {
    parseModels("adapter/GraphAdapter.cd", "adapter/Adapter.cd");
    checker =
        new ConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("m1", "m2")));
  }

  @Test
  public void testConformanceCheckAdapterS() {
    parseModels("adapter/GraphAdapterS.cd", "adapter/Adapter.cd");
    checker =
        new ConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("m")));
  }

  @Test
  public void testConformanceCheckAdapterF() {
    parseModels("adapter/GraphAdapterF.cd", "adapter/Adapter.cd");
    checker =
        new ConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("m1", "m2")));
  }

  @Test
  public void testConformanceCheckInvalid() {
    parseModels("Concrete.cd", "Reference.cd");
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING, NO_MULTI_INC));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"EqName.cd", "STName.cd", "composed.cd"})
  public void testAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"DiffName.cd", "DiffType.cd", "NumberAttr.cd"})
  public void testAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AttrInSuperClasses.cd"})
  public void testDeepAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AttrInSuperTypeNoMatch.cd", "AttrInSuperTypeNoMatch.cd"})
  public void testDeepAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AssocInSuperType.cd", "InhrBothSides.cd", "Valid1.cd"})
  public void testDeepAssocConformanceValid(String concrete) {
    parseModels("associations/valid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"FalseDirection.cd", "FalseCard.cd"})
  public void testDeepAssocConformanceInvalid(String concrete) {
    parseModels("associations/invalid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AssocInSuperType.cd", "InhrBothSides.cd", "Valid1.cd", "Valid2.cd"})
  public void testStrictDeepAssocConformanceValid(String concrete) {
    parseModels("associations/valid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"FalseDirection.cd", "FalseCard.cd"})
  public void testStrictDeepAssocConformanceInvalid(String concrete) {
    parseModels("associations/invalid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
}
