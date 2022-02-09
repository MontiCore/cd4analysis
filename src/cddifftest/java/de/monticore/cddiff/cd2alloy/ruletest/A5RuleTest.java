/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the U1 rule for the generation of common class names
 *
 */
public class A5RuleTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  private void checkA5(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 3);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ A5\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 3));

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

        System.out.println("Error in A5: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testA5_MV() {
    String a = CD2AlloyGenerator.executeRuleA5(mvAst);
    String[] lines = a.split(System.getProperty("line.separator"));

//    System.out.println(a);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjLAttrib[DriverSubsCDcd1,drives,CarSubsCDcd1,1]");
    expectedResult.add("ObjLAttrib[CompanySubsCDcd1,cars,CarSubsCDcd1,0]");
    expectedResult.add("ObjLUAttrib[EmployeeSubsCDcd1,ins,InsuranceSubsCDcd1,1,1]");
    expectedResult.add("ObjLAttrib[DriverSubsCDcd1,license,LicenseSubsCDcd1,1]");

    checkA5(lines, expectedResult);
  }
}
