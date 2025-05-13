package de.monticore.cdconcretization;

import org.junit.jupiter.api.Test;

class EvaluationConcretizationTest extends AbstractCDConcretizationTest {

  @Test
  void testBuilderAndMillPattern() {
    ConcretizationCompleter completer =
        new ConcretizationCompleter("ref", DEFAULT_CONFORMANCE_PARAMS);

    testConcretizedEqualsExpectedOut(
        completer,
        "evaluation/builder/DataModelConc.cd",
        "evaluation/builder/BuilderAndMillRef.cd",
        "evaluation/builder/BuilderAndMillOut.cd");
  }

  @Test
  void testGetter() {
    testConcretizedConformsToRefAndExpectedOut(
        "evaluation/getter-setter/DataModelConc.cd",
        "evaluation/getter-setter/GetterRef.cd",
        "evaluation/getter-setter/GetterOut.cd");
  }

  @Test
  void testSetter() {
    testConcretizedConformsToRefAndExpectedOut(
        "evaluation/getter-setter/DataModelConc.cd",
        "evaluation/getter-setter/SetterRef.cd",
        "evaluation/getter-setter/SetterOut.cd");
  }
}
