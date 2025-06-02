/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization;

import de.monticore.cdconformance.CDConfParameter;
import org.junit.jupiter.api.Test;

class EvaluationConcretizationTest extends AbstractCDConcretizationTest {
  
  @Test
  void testBuilderAndMillPattern() {
    // TODO Remove once we have explicit support for 'forEach' conformance check
    confParameters.add(CDConfParameter.STRICT_PARAMETER_ORDER);
    testConcretizedConformsToRefAndExpectedOut("evaluation/builder/DataModelConc.cd",
        "evaluation/builder/BuilderAndMillRef.cd", "evaluation/builder/BuilderAndMillOut.cd");
  }
  
  @Test
  void testGetter() {
    testConcretizedConformsToRefAndExpectedOut("evaluation/getter-setter/DataModelConc.cd",
        "evaluation/getter-setter/GetterRef.cd", "evaluation/getter-setter/GetterOut.cd");
  }
  
  @Test
  void testSetter() {
    // TODO Remove once we have explicit support for 'forEach' conformance check
    confParameters.add(CDConfParameter.STRICT_PARAMETER_ORDER);
    testConcretizedConformsToRefAndExpectedOut("evaluation/getter-setter/DataModelConc.cd",
        "evaluation/getter-setter/SetterRef.cd", "evaluation/getter-setter/SetterOut.cd");
  }
  
}
