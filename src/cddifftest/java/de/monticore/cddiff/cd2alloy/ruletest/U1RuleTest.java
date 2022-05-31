/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
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
public class U1RuleTest extends CDDiffTestBasis {

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

  private void checkU1(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ U1\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(result[i].matches("sig [\\w]+ extends Obj \\{\\}"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;
        System.out.println("Error in U1: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testU1_MV() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(mvAst);

    String u1 = CD2AlloyGenerator.getInstance().executeRuleU1(asts);
    String[] lines = u1.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("sig Vehicle extends Obj {}");
    expectedResult.add("sig Company extends Obj {}");
    expectedResult.add("sig Employee extends Obj {}");
    expectedResult.add("sig Car extends Obj {}");
    expectedResult.add("sig Insurance extends Obj {}");
    expectedResult.add("sig License extends Obj {}");
    expectedResult.add("sig Driver extends Obj {}");
    expectedResult.add("sig Truck extends Obj {}");
    expectedResult.add("sig Driveable extends Obj {}");

    checkU1(lines, expectedResult);
  }

  @Test
  public void testU1_cd2v1_cd2v2() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(m1Ast);
    asts.add(m2Ast);

    String u1 = CD2AlloyGenerator.getInstance().executeRuleU1(asts);
    String[] lines = u1.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("sig Task extends Obj {}");
    expectedResult.add("sig Employee extends Obj {}");
    expectedResult.add("sig Manager extends Obj {}");

    checkU1(lines, expectedResult);
  }

}
