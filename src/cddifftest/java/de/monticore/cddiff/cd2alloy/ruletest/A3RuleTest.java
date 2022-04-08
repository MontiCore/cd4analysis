/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the U1 rule for the generation of common class names
 *
 */
public class A3RuleTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  @Before
  public void prepareASTs(){
    prepareAST(mvAst);
  }

  private void checkA3(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 3);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ A3\\: .*"));

    // Check number of outputs
    // assertTrue((result.length - 1) == expectedResult.size());

    // Preprocess inputs (ignore whitespaces)
    for (String string : expectedResult) {
      string = string.replaceAll("\\p{Space}", "");
    }

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 3; i < result.length; i++) {
      // Ignore whitespaces
      result[i] = result[i].replaceAll("\\p{Space}", "");

      // Check structure
      assertTrue(
          result[i].matches("ObjLU?Attrib\\[\\w*(,\\w*)*\\]"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;

        System.out.println("Error in A3: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testA1_MV() {
    String a = CD2AlloyGenerator.executeRuleA3(mvAst);
    String[] lines = a.split(System.getProperty("line.separator"));

    System.out.println(a);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjLUAttrib[CarSubsCDcd1,drivenBy,DriverSubsCDcd1,1,1]");
    expectedResult.add("ObjLAttrib[CompanySubsCDcd1,emps,EmployeeSubsCDcd1,0]");
    expectedResult.add("ObjLUAttrib[LicenseSubsCDcd1,owner,DriverSubsCDcd1,1,1]");

    checkA3(lines, expectedResult);
  }
}
