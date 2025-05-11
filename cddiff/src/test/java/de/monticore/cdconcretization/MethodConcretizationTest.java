package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class MethodConcretizationTest extends AbstractCDConcretizationTest {

  @ParameterizedTest
  @ValueSource(strings = {"ClassEmptyConc.cd", "ClassMissingConc.cd", "MethodMissingConc.cd",
          "MultipleMethodsMissingConc.cd"})
  void testBasicCompletion(String concrete) {
    testConcretizedEqualsRef(
        "methods/basic/valid/" + concrete, "methods/basic/Reference.cd");
  }

  @Test
  void testMethodNameExistsButWrongSignature() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/basic/valid/WrongSignatureConc.cd",
        "methods/basic/Reference.cd",
        "methods/basic/valid/WrongSignatureOut.cd"
    );
  }

  @Test
  void testMethodExistsInSuperClass() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/basic/valid/MethodInSuperClassConc.cd",
        "methods/basic/Reference.cd",
        "methods/basic/valid/MethodInSuperClassOut.cd");
  }

  // --- Multi Incarnation (without forEach) ---

  @Test
  void testParameterTypeMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/multiIncarnation/ParameterTypeMIConc.cd",
        "methods/multiIncarnation/Reference.cd",
        "methods/multiIncarnation/ParameterTypeMIOut.cd");
  }

  @Test
  void testParameterTypeMIOneExists() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/multiIncarnation/ParameterTypeMIOneExistsConc.cd",
        "methods/multiIncarnation/Reference.cd",
        "methods/multiIncarnation/ParameterTypeMIOneExistsOut.cd");
  }

  @Test
  void testReturnTypeMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/multiIncarnation/ReturnTypeMIConc.cd",
        "methods/multiIncarnation/Reference.cd",
        "methods/multiIncarnation/ReturnTypeMIOut.cd");
  }

  @Test
  void testReturnTypeMIOneExists() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/multiIncarnation/ReturnTypeMIOneExistsConc.cd",
        "methods/multiIncarnation/Reference.cd",
        "methods/multiIncarnation/ReturnTypeMIOneExistsOut.cd");
  }

  @Test
  void testParameterAndReturnTypeMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/multiIncarnation/ParameterAndReturnTypeMIConc.cd",
        "methods/multiIncarnation/Reference.cd",
        "methods/multiIncarnation/ParameterAndReturnTypeMIOut.cd");
  }

  // --- Underspecification ---

  @Test
  void testReturnTypeUnderspecifiedNoIncarnationError() {
    try {
      parseAndConcretize(
              "methods/underspecified/ReturnTypeUnderspecifiedNoIncConc.cd",
              "methods/underspecified/ReturnTypeUnderspecifiedRef.cd");
      fail("Expected CompletionException. But the concretization was successful.");
    } catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
  }

  @Test
  void testParameterTypeUnderspecifiedNoIncarnationError() {
    try {
      parseAndConcretize(
              "methods/underspecified/ParameterTypeUnderspecifiedNoIncConc.cd",
              "methods/underspecified/ParameterTypeUnderspecifiedRef.cd");
      fail("Expected CompletionException. But the concretization was successful.");
    } catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
  }

  @Test
  void testReturnTypeUnderspecifiedWithIncarnation() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/underspecified/ReturnTypeUnderspecifiedIncarnatedConc.cd",
        "methods/underspecified/ReturnTypeUnderspecifiedRef.cd",
        "methods/underspecified/ReturnTypeUnderspecifiedIncarnatedOut.cd");
  }

  @Test
  void testParameterTypeUnderspecifiedWithIncarnation() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/underspecified/ParameterTypeUnderspecifiedIncarnatedConc.cd",
        "methods/underspecified/ParameterTypeUnderspecifiedRef.cd",
        "methods/underspecified/ParameterTypeUnderspecifiedIncarnatedOut.cd");
  }

  // --- ForEach ---

  @Test
  void testMethodForEachAttribute() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/forEach/ForEachAttributeConc.cd",
        "methods/forEach/ForEachAttributeRef.cd",
        "methods/forEach/ForEachAttributeOut.cd");
  }

  @Test
  void testMethodForEachAttributeDifferentReturnType() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/forEach/ForEachAttributeDifferentReturnTypeConc.cd",
        "methods/forEach/ForEachAttributeDifferentReturnTypeRef.cd",
        "methods/forEach/ForEachAttributeDifferentReturnTypeOut.cd");
  }

  /**
   * Multiple parameters. One matches the name of the attribute and is therefore adapted, the
   * other one stays the same for all incarnations.
   */
  @Test
  void testMethodForEachAttributeMultipleParameters() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/forEach/ForEachAttributeMultipleParametersConc.cd",
        "methods/forEach/ForEachAttributeMultipleParametersRef.cd",
        "methods/forEach/ForEachAttributeMultipleParametersOut.cd");
  }

  @Test
  void testMethodForEachAttributeSameReturnType() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/forEach/ForEachAttributeSameReturnTypeConc.cd",
        "methods/forEach/ForEachAttributeSameReturnTypeRef.cd",
        "methods/forEach/ForEachAttributeSameReturnTypeOut.cd");
  }

  @Test
  void testMethodForEachAttributeSameReturnTypeClassMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "methods/forEach/ForEachAttributeSameReturnTypeClassMIConc.cd",
        "methods/forEach/ForEachAttributeSameReturnTypeRef.cd",
        "methods/forEach/ForEachAttributeSameReturnTypeClassMIOut.cd");
  }
}
