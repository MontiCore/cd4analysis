/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the U1 rule for the generation of common class names
 */
public class A1RuleTest extends CDDiffTestBasis {

  protected ASTCDCompilationUnit mvAst = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  protected ASTCDCompilationUnit m1Ast = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");

  protected ASTCDCompilationUnit m2Ast = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");

  @Before
  public void prepareASTs() {
    prepareAST(mvAst);
    prepareAST(m1Ast);
    prepareAST(m2Ast);
  }

  private void checkA1(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ A1\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(result[i].matches("BidiAssoc\\[\\w*(,\\w*)*\\]"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;

        System.out.println("Error in A1: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testA1_MV() {
    String a = CD2AlloyGenerator.getInstance().executeRuleA1(mvAst);
    String[] lines = a.split(System.getProperty("line.separator"));

    //    System.out.println(a);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("BidiAssoc[DriverSubsCDcd1,drives,CarSubsCDcd1,drivenBy]");

    // TODO: Is this correct
    expectedResult.add("BidiAssoc[DriverSubsCDcd1,license,LicenseSubsCDcd1,owner]");

    checkA1(lines, expectedResult);
  }

  @Test
  public void testF1_Employees1() {
    String a = CD2AlloyGenerator.getInstance().executeRuleA1(m1Ast);
    String[] lines = a.split(System.getProperty("line.separator"));

    //    System.out.println(a);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("BidiAssoc[EmployeeSubsCDEmployees1,task,TaskSubsCDEmployees1,employee]");

    checkA1(lines, expectedResult);
  }

  @Test
  public void testA1_Employees2() {
    String a = CD2AlloyGenerator.getInstance().executeRuleA1(m2Ast);
    String[] lines = a.split(System.getProperty("line.separator"));

    //    System.out.println(a);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("BidiAssoc[EmployeeSubsCDEmployees2,task,TaskSubsCDEmployees2,employee]");

    checkA1(lines, expectedResult);
  }

}
