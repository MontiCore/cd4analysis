/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/** Unit test for the U1 rule for the generation of common class names */
public class A4RuleTest extends CDDiffTestBasis {

  protected ASTCDCompilationUnit mvAst =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  @Before
  public void prepareASTs() {
    prepareAST(mvAst);
  }

  private void checkA4(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 2);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ A4\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 2));

    // Preprocess inputs (ignore whitespaces)
    for (String string : expectedResult) {
      string = string.replaceAll("\\p{Space}", "");
    }

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 2; i < result.length; i++) {
      // Ignore whitespaces
      result[i] = result[i].replaceAll("\\p{Space}", "");

      // Check structure
      assertTrue(result[i].matches("ObjLU?\\[\\w*(,\\w*)*\\]"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;

        System.out.println("Error in A4: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testA4_MV() {
    String a = CD2AlloyGenerator.getInstance().executeRuleA4(mvAst);
    String[] lines = a.split(System.getProperty("line.separator"));

    //    System.out.println(a);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjL[EmployeeSubsCDcd1,emps,CompanySubsCDcd1,0]");

    checkA4(lines, expectedResult);
  }
}
