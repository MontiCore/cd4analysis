package de.monticore.cdconcretization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ConcretizationCompleterTest extends AbstractCDConcretizationTest {

  @Test
  void testEvaluation() {
    testConcretizedConformsToRefAndExpectedOut(
        "EvaluationConc.cd", "EvaluationRef.cd", "EvaluationOut.cd");
  }

  /** Test that checks if completeInheritance works correctly (after adding the types) */
  @Test
  void testTypeMissingInheritance() {
    testConcretizedEqualsRef(
        "inheritance/MissingInheritanceConc.cd", "inheritance/MissingInheritanceRef.cd");
  }

  @Test
  void testMultipleIncarnation() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/ClassMIConc.cd",
        "multipleIncarnation/ClassMIRef.cd",
        "multipleIncarnation/ClassMIOut.cd");
  }

  @Test
  void testAssocBothSidesMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/BothAssocSidesMIConc.cd",
        "multipleIncarnation/BothAssocSidesMIRef.cd",
        "multipleIncarnation/BothAssocSidesMIOut.cd");
  }

  @Test
  void testAssocBothSidesMIOneAssocExistsPerTypeIncarnation() {
    /*
     * The tool only adds associations between each pair of type incarnations if the concrete CD
     * does not already contain an association for each SINGLE type incarnation!
     */
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/BothAssocSidesMIOneAssocExistsConc.cd",
        "multipleIncarnation/BothAssocSidesMIRef.cd",
        "multipleIncarnation/BothAssocSidesMIOneAssocExistsOut.cd");
  }

  @Test
  void testMIUnequalCardinalities() {
    try {
      parseAndConcretize(
          "multipleIncarnation/UnequalCardCon.cd", "multipleIncarnation/UnequalCardRef.cd");
      fail("Expected CompletionException");
    } catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
  }

  @Test
  void testInterfaceMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/InterfaceMIConc.cd",
        "multipleIncarnation/InterfaceMIRef.cd",
        "multipleIncarnation/InterfaceMIOut.cd");
  }

  @Test
  void testAttributeTypeMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/AttributeTypeMIConc.cd",
        "multipleIncarnation/AttributeTypeMIRef.cd",
        "multipleIncarnation/AttributeTypeMIOut.cd");
  }

  @Test
  @Disabled
  // todo: this test but later
  void testMultipleMappingIncarnation() {}

  /** Test that checks if attributes are inherited in the correct way with a valid example. */
  @Test
  void testInheritanceValid() {
    testConcretizedConformsToRefAndExpectedOut(
        "inheritance/AttributeInheritanceConc.cd",
        "inheritance/AttributeInheritanceRef.cd",
        "inheritance/AttributeInheritanceOut.cd");
  }

  @Test
  @Disabled
  void testAttributeTypeMismatchWithSuperclass() {
    try {
      parseAndConcretize(
          "inheritance/AttributeTypeMismatchConc.cd", "inheritance/AttributeTypeMismatchRef.cd");
      fail("Expected CompletionException");
    } catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
    // todo: look at cds -> teacher inherites int number and also has attribute double number
  }

  @Test
  void testCDHelperExample() {
    testConcretizedConformsToRefAndExpectedOut(
        "helper/HelperConc.cd", "helper/HelperRef.cd", "helper/HelperOut.cd");
  }

  // ConcretizationHelper tests
  @Test
  void testCDHelperMappings() throws CompletionException {
    String mapping = "ref";
    parseAndConcretize("helper/HelperConc.cd", "helper/HelperRef.cd");

    System.out.println(CD4CodeMill.prettyPrint(conCD, false));

    CompTypeIncStrategy typeIncStrategy = new CompTypeIncStrategy(refCD, mapping);
    typeIncStrategy.addIncStrategy(new STTypeIncStrategy(refCD, mapping));
    typeIncStrategy.addIncStrategy(new EqTypeIncStrategy(refCD, mapping));

    CompAssocIncStrategy assocIncStrategy = new CompAssocIncStrategy(refCD, mapping);
    assocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(refCD, mapping));
    assocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(refCD, mapping));
    assocIncStrategy.addIncStrategy(
        new RolePrefixInNavDirIncStrategy(typeIncStrategy, conCD, refCD));
    assocIncStrategy.addIncStrategy(
        new RolePrefixIfPresentIncStrategy(typeIncStrategy, conCD, refCD));

    ConcretizationHelper helper =
        new ConcretizationHelper(conCD, refCD, typeIncStrategy, assocIncStrategy);

    helper.mapReferenceToConcreteRoles();

    Map<CDTypeSymbol, Set<CDTypeSymbol>> actualMap = helper.typeMapping;

    Map<String, Set<String>> expectedMap = new HashMap<>();
    expectedMap.put("B", new HashSet<>(Arrays.asList("B", "C", "D")));
    expectedMap.put("A", new HashSet<>(Collections.singleton("A")));

    Map<String, Set<String>> actualMapTemp = new HashMap<>();
    for (Map.Entry<CDTypeSymbol, Set<CDTypeSymbol>> entry : actualMap.entrySet()) {
      String keyName = entry.getKey().getName();
      Set<String> valueNames =
          entry.getValue().stream().map(CDTypeSymbol::getName).collect(Collectors.toSet());
      actualMapTemp.put(keyName, valueNames);
    }
    assertEquals(actualMapTemp, expectedMap);

    Map<CDRoleSymbol, Set<CDRoleSymbol>> actualMap2 = helper.roleMapping;

    Map<String, Set<String>> expectedMap2 = new HashMap<>();
    expectedMap2.put(
        "roleNameRight",
        new HashSet<>(Arrays.asList("roleNameRight_C", "roleNameRight_D", "roleNameRight_B")));
    expectedMap2.put(
        "roleNameLeft",
        new HashSet<>(Arrays.asList("roleNameLeft_A", "roleNameLeft_A", "roleNameLeft_A")));

    Map<String, Set<String>> actualMapTemp2 = new HashMap<>();
    for (Map.Entry<CDRoleSymbol, Set<CDRoleSymbol>> entry : actualMap2.entrySet()) {
      String keyName = entry.getKey().getName();
      Set<String> valueNames =
          entry.getValue().stream().map(CDRoleSymbol::getName).collect(Collectors.toSet());
      actualMapTemp2.put(keyName, valueNames);
    }
    assertEquals(actualMapTemp2, expectedMap2);
    // todo: there is a bug somewhere in the mapping of roles to their respective other type
    /*

    Map<CDRoleSymbol, Set<CDTypeSymbol>> actualMap3 = helper.roleToTypeMapping;

    Map<String, Set<String>> expectedMap3 = new HashMap<>();
    expectedMap3.put("roleNameRight_D", new HashSet<>(Collections.singleton("A")));
    expectedMap3.put("roleNameRight_B", new HashSet<>(Collections.singleton("A")));
    expectedMap3.put("roleNameRight_C", new HashSet<>(Collections.singleton("A")));
    expectedMap3.put("roleNameLeft_A", new HashSet<>(Arrays.asList("C", "B", "D")));


    Map<String, Set<String>> actualMapTemp3 = new HashMap<>();
    for (Map.Entry<CDRoleSymbol, Set<CDTypeSymbol>> entry : actualMap3.entrySet()) {
      String keyName = entry.getKey().getName();
      Set<String> valueNames = entry.getValue().stream()
        .map(CDTypeSymbol::getName)
        .collect(Collectors.toSet());
      actualMapTemp3.put(keyName, valueNames);
    }
    assertEquals(actualMapTemp3, expectedMap3);

     */
  }
}
