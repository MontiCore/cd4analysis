/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDConformanceCheckerTest extends ConfAbstractTest {
  
  @Test
  public void testConformanceCheck() {
    parseModels("Concrete.cd", "Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @Test
  public void testOptConformanceCheck() {
    parseModels("Concrete.cd", "OptReference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @Test
  public void testFalseOptConformanceCheck() {
    parseModels("FalseConcrete.cd", "OptReference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "ValidMethods.cd", "FQSTMatchValid.cd" })
  public void testMethodConformanceCheck(String concrete) {
    parseModels("methods/" + concrete, "methods/ReferenceMethods.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("incarnates")));
  }
  
  @Test
  public void testFalseMethodConformanceCheck() {
    parseModels("methods/InvalidMethods.cd", "methods/ReferenceMethods.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("incarnates")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "PrimitiveTypes.cd", "ClassIncarnation.cd" })
  public void testMethodUnderspecifiedValid(String concrete) {
    parseModels("methods/underspecified/valid/" + concrete, "methods/underspecified/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AnyParamInConcrete.cd", "AnyReturnInConcrete.cd" })
  public void testMethodypeUnderspecifiedInvalid(String concrete) {
    parseModels("methods/underspecified/invalid/" + concrete,
        "methods/underspecified/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @Test
  public void testConformanceCheckAdapter() {
    parseModels("adapter/GraphAdapter.cd", "adapter/Adapter.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("m1", "m2")));
  }
  
  @Test
  public void testConformanceCheckAdapterS() {
    parseModels("adapter/GraphAdapterS.cd", "adapter/Adapter.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("m")));
  }
  
  @Test
  public void testConformanceCheckAdapterF() {
    parseModels("adapter/GraphAdapterF.cd", "adapter/Adapter.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, INHERITANCE, ALLOW_CARD_RESTRICTION));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("m1", "m2")));
  }
  
  @Test
  public void testConformanceCheckInvalid() {
    parseModels("Concrete.cd", "Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING, NO_MULTI_INC));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "STMatch.cd", "FQSTMatch.cd" })
  public void testTypeConformanceValid(String concrete) {
    parseModels("types/valid/" + concrete, "types/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "EqName.cd", "STName.cd", "composed.cd", "FQSTName.cd" })
  public void testAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "DiffName.cd", "DiffType.cd", "NumberAttr.cd" })
  public void testAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AttrInSuperClasses.cd" })
  public void testDeepAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AttrInSuperTypeNoMatch.cd", "AttrInSuperTypeNoMatch.cd" })
  public void testDeepAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "Concrete.cd", "Unchanged.cd" })
  public void testAttributeTypeIncarnationValid(String concrete) {
    parseModels("attributes/typeIncarnation/valid/" + concrete,
        "attributes/typeIncarnation/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "NotMarkedAsIncarnation.cd", "TypeParamNoIncarnation.cd" })
  public void testAttributeTypeIncarnationInvalid(String concrete) {
    parseModels("attributes/typeIncarnation/invalid/" + concrete,
        "attributes/typeIncarnation/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "PrimitiveTypes.cd", "ClassIncarnation.cd" })
  public void testAttributeTypeUnderspecifiedValid(String concrete) {
    parseModels("attributes/underspecified/valid/" + concrete,
        "attributes/underspecified/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AnyInConcrete.cd" })
  public void testAttributeTypeUnderspecifiedInvalid(String concrete) {
    parseModels("attributes/underspecified/invalid/" + concrete,
        "attributes/underspecified/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AssocInSuperType.cd", "InhrBothSides.cd", "Valid1.cd", "STMatch.cd",
      "FQSTMatch.cd" })
  public void testDeepAssocConformanceValid(String concrete) {
    parseModels("associations/valid/" + concrete, "associations/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "FalseDirection.cd", "FalseCard.cd", "MissingAssocInc.cd" })
  public void testDeepAssocConformanceInvalid(String concrete) {
    parseModels("associations/invalid/" + concrete, "associations/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AssocInSuperType.cd", "InhrBothSides.cd", "Valid1.cd", "Valid2.cd" })
  public void testStrictDeepAssocConformanceValid(String concrete) {
    parseModels("associations/valid/" + concrete, "associations/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "FalseDirection.cd", "FalseCard.cd" })
  public void testStrictDeepAssocConformanceInvalid(String concrete) {
    parseModels("associations/invalid/" + concrete, "associations/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AssocInSuperType.cd", "FalseDirectionButAbstract.cd" })
  public void testAssocMatchBySourceTypeAndTargetRoleConformanceValid(String concrete) {
    parseModels("associations/match_by_source_type_and_target_role/valid/" + concrete,
        "associations/match_by_source_type_and_target_role/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "FalseDirection.cd" })
  public void testAssocMatchBySourceTypeAndTargetRoleConformanceInvalid(String concrete) {
    parseModels("associations/match_by_source_type_and_target_role/invalid/" + concrete,
        "associations/match_by_source_type_and_target_role/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "AssocInSuperType.cd", "FalseDirectionButAbstract.cd" })
  public void testAssocMatchBySourceAndTargetTypeConformanceValid(String concrete) {
    parseModels("associations/match_by_source_and_target_type/valid/" + concrete,
        "associations/match_by_source_and_target_type/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "FalseDirection.cd" })
  public void testAssocMatchBySourceAndTargetTypeConformanceInvalid(String concrete) {
    parseModels("associations/match_by_source_and_target_type/invalid/" + concrete,
        "associations/match_by_source_and_target_type/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "Valid1.cd" })
  public void testAssocImplicitRoleNameValid(String concrete) {
    parseModels("associations/implicit_role_name/valid/" + concrete,
        "associations/implicit_role_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "Valid1.cd" })
  public void testAssocRoleNameDerivedFromTypeValid(String concrete) {
    parseModels("associations/role_name_derived_from_type/valid/" + concrete,
        "associations/role_name_derived_from_type/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "ExactSame.cd" })
  public void testAssocSameTypeMultipleRolesValid(String concrete) {
    parseModels("associations/sameTypeMultipleRoles/valid/" + concrete,
        "associations/sameTypeMultipleRoles/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "BothMissing.cd", "OneRoleMissing1.cd", "OneRoleMissing2.cd",
      "DifferentRoleNames.cd" })
  public void testAssocSameTypeMultipleRolesInvalid(String concrete) {
    parseModels("associations/sameTypeMultipleRoles/invalid/" + concrete,
        "associations/sameTypeMultipleRoles/Reference.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "DifferentRoleNamesSTMapped.cd" })
  public void testAssocSameTypeMultipleRolesExplicitAssocNamesValid(String concrete) {
    parseModels("associations/sameTypeMultipleRoles/valid/" + concrete,
        "associations/sameTypeMultipleRoles/ReferenceExplicitAssocName.cd");
    checker = new CDConformanceChecker(Set.of(INHERITANCE, NAME_MAPPING, STEREOTYPE_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }
  
  // ---- ADAPTED_NAME_MAPPING: attributes ----

  @ParameterizedTest
  @ValueSource(strings = { "AdaptedName.cd" })
  public void testAttributeAdaptedNameValid(String concrete) {
    parseModels("attributes/adapted_name/valid/" + concrete,
        "attributes/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        ADAPTED_NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "WrongName.cd" })
  public void testAttributeAdaptedNameInvalid(String concrete) {
    parseModels("attributes/adapted_name/invalid/" + concrete,
        "attributes/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        ADAPTED_NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  /** Adapted name is rejected when ADAPTED_NAME_MAPPING is not enabled. */
  @Test
  public void testAttributeAdaptedNameRequiresParam() {
    parseModels("attributes/adapted_name/valid/AdaptedName.cd",
        "attributes/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  // ---- ADAPTED_NAME_MAPPING: methods ----

  @ParameterizedTest
  @ValueSource(strings = { "AdaptedName.cd" })
  public void testMethodAdaptedNameValid(String concrete) {
    parseModels("methods/adapted_name/valid/" + concrete,
        "methods/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        ADAPTED_NAME_MAPPING, STRICT_PARAMETER_ORDER));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "WrongName.cd" })
  public void testMethodAdaptedNameInvalid(String concrete) {
    parseModels("methods/adapted_name/invalid/" + concrete,
        "methods/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        ADAPTED_NAME_MAPPING, STRICT_PARAMETER_ORDER));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  /** Adapted method name is rejected when ADAPTED_NAME_MAPPING is not enabled. */
  @Test
  public void testMethodAdaptedNameRequiresParam() {
    parseModels("methods/adapted_name/valid/AdaptedName.cd",
        "methods/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        STRICT_PARAMETER_ORDER));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  // ---- ADAPTED_NAME_MAPPING: association roles ----
  // Uses custom (non-implicit) role names to specifically exercise AdaptedRoleNameAssocIncStrategy
  // rather than ImplicitRoleNameAssocIncStrategy.

  @ParameterizedTest
  @ValueSource(strings = { "AdaptedRoles.cd" })
  public void testAssocAdaptedRoleNameValid(String concrete) {
    parseModels("associations/adapted_name/valid/" + concrete,
        "associations/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, ADAPTED_NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "WrongRoles.cd" })
  public void testAssocAdaptedRoleNameInvalid(String concrete) {
    parseModels("associations/adapted_name/invalid/" + concrete,
        "associations/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, ADAPTED_NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  /** Custom adapted role names are rejected when ADAPTED_NAME_MAPPING is not enabled. */
  @Test
  public void testAssocAdaptedRoleNameRequiresParam() {
    parseModels("associations/adapted_name/valid/AdaptedRoles.cd",
        "associations/adapted_name/Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  /**
   * Example from KMR24 for multiple mappings.
   */
  @Test
  public void testMutualObserversExample() {
    parseModels("mutualObservers/SimpleMutualObserversConc.cd",
        "mutualObservers/SimpleObserverRef.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        SRC_TARGET_ASSOC_MAPPING, ALLOW_CARD_RESTRICTION));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref1", "ref2")));
  }
  
  /**
   * The conformance relation was defined as reflexive in [KRS+24]. However, we should question
   * this as [KMR24] mentioned it is possible for some language elements to only be allowed in
   * a reference model but not in a concrete model. This would contradict reflexivity, e.g.,
   * how would we handle a {@code forEach} stereotype in a concrete model?
   */
  @Nested
  class ReflexiveConformanceTests {
    
    @Test
    public void visitorConformsToItself() {
      parseModels("visitor/VisitorRef.cd", "visitor/VisitorRef.cd");
      checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
          SRC_TARGET_ASSOC_MAPPING, ALLOW_CARD_RESTRICTION, METHOD_OVERLOADING));
      assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
    }
    
    /**
     * This could work in theory, but currently we have issues when resolving symbols because
     * two symbols with the same name exists (as reference model and concrete model are identical).
     * Also, we currently report an error if the underspecified placeholder type ('any') is
     * used in the concrete model.
     */
    @Disabled
    @Test
    public void builderAndMillConformsToItself() {
      parseModels("builder/BuilderAndMillRef.cd", "builder/BuilderAndMillRef.cd");
      checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
          SRC_TARGET_ASSOC_MAPPING, ALLOW_CARD_RESTRICTION, METHOD_OVERLOADING));
      assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
    }
    
    @Test
    public void assocExampleConformsToItself() {
      parseModels("associations/Reference.cd", "associations/Reference.cd");
      checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
          SRC_TARGET_ASSOC_MAPPING, ALLOW_CARD_RESTRICTION, METHOD_OVERLOADING));
      assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
    }
    
  }
  
  @Nested
  class Evaluation {
    
    @Test
    public void macocoConformsToItself() {
      parseModels("evaluation/MaCoCo.cd", "evaluation/MaCoCo.cd");
      checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
          SRC_TARGET_ASSOC_MAPPING, METHOD_OVERLOADING));
      assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
    }
    
  }
  
}
