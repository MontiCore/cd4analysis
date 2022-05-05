/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the U1 rule for the generation of common class names
 */
public class A2RuleTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  @Before
  public void prepareASTs() {
    prepareAST(mvAst);
  }

  private void checkA2(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ A2\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(result[i].matches("Composition\\[\\w*(,\\w*)*\\]"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;

        System.out.println("Error in A2: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testA2_MV() {
    String a2 = CD2AlloyGenerator.executeRuleA2(mvAst);
    String[] lines = a2.split(System.getProperty("line.separator"));

    //    System.out.println(a2);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("Composition[InsuranceCompFieldsCDcd1,InsuranceSubsCDcd1]");

    checkA2(lines, expectedResult);
  }

}
