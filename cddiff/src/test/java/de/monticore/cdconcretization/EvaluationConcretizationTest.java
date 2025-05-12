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
}
