/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/** Unit test for the U1 rule for the generation of common class names */
public class U2RuleTest extends CDDiffTestBasis {

  protected ASTCDCompilationUnit mvAst =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  protected ASTCDCompilationUnit m1Ast =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");

  protected ASTCDCompilationUnit m2Ast =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");

  @Before
  public void prepareASTs() {
    prepareAST(mvAst);
    prepareAST(m1Ast);
    prepareAST(m2Ast);
  }

  private void checkU2(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ U2\\: .*"));

    // Check number of outputs
    // assertTrue((result.length - 1) == expectedResult.size());

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(result[i].matches("one sig [\\w]+ extends FName \\{\\}"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;
        System.out.println("Error in U2: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testU2_MV() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(mvAst);

    String u2 = CD2AlloyGenerator.getInstance().executeRuleU2(asts);
    String[] lines = u2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("one sig owner extends FName {}");
    expectedResult.add("one sig cars extends FName {}");
    expectedResult.add("one sig license extends FName {}");
    expectedResult.add("one sig licensePlate extends FName {}");
    expectedResult.add("one sig emps extends FName {}");
    expectedResult.add("one sig drives extends FName {}");
    expectedResult.add("one sig kind extends FName {}");
    expectedResult.add("one sig of extends FName {}");
    expectedResult.add("one sig regDate extends FName {}");
    expectedResult.add("one sig exp extends FName {}");
    expectedResult.add("one sig drivenBy extends FName {}");
    expectedResult.add("one sig ins extends FName {}");

    checkU2(lines, expectedResult);
  }

  @Test
  public void testU2_cd2v1_cd2v2() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(m1Ast);
    asts.add(m2Ast);

    String u2 = CD2AlloyGenerator.getInstance().executeRuleU2(asts);
    String[] lines = u2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("one sig task extends FName {}");
    expectedResult.add("one sig managedBy extends FName {}");
    expectedResult.add("one sig kind extends FName {}");
    expectedResult.add("one sig manages extends FName {}");
    expectedResult.add("one sig employee extends FName {}");
    expectedResult.add("one sig startDate extends FName {}");

    checkU2(lines, expectedResult);
  }
}
