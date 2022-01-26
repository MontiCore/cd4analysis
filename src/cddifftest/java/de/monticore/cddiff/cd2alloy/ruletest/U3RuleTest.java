/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cddiff.cd2alloy.AbstractTest;
import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the U1 rule for the generation of common class names
 *
 */
public class U3RuleTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v1.cd");

  ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v2.cd");

  private void checkU3(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ U3\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(result[i].matches("one sig [\\w]+ extends Val \\{\\}"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;
        System.out.println("Error in U3: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testU3_MV() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(mvAst);

    String u3 = CD2AlloyGenerator.executeRuleU3(asts);
    String[] lines = u3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("one sig type_Date extends Val {}");
    expectedResult.add("one sig type_String extends Val {}");

    checkU3(lines, expectedResult);
  }

  @Test
  public void testU3_cd2v1_cd2v2() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(m1Ast);
    asts.add(m2Ast);

    String u3 = CD2AlloyGenerator.executeRuleU3(asts);
    String[] lines = u3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("one sig type_Date extends Val {}");
    checkU3(lines, expectedResult);
  }
}
