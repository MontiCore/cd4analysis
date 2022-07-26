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
public class U4RuleTest extends CDDiffTestBasis {

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

  private void checkU4(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ U4\\: .*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(result[i].matches("one sig [\\w]+ extends EnumVal \\{\\}"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        correct = false;
        System.out.println("Error in U4: " + result[i] + " not in " + expectedResult);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testU4_MV() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(mvAst);

    String u4 = CD2AlloyGenerator.getInstance().executeRuleU4(asts);
    String[] lines = u4.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("one sig enum_InsuranceKind_international extends EnumVal {}");
    expectedResult.add("one sig enum_DrivingExp_expert extends EnumVal {}");
    // TODO Does not exist in example
    //expectedResult.add("one sig enum_InsuranceKind_workAcc extends EnumVal {}");
    expectedResult.add("one sig enum_InsuranceKind_transport extends EnumVal {}");
    expectedResult.add("one sig enum_DrivingExp_beginner extends EnumVal {}");

    checkU4(lines, expectedResult);
  }

  @Test
  public void testU4_cd2v1_cd2v2() {
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(m1Ast);
    asts.add(m2Ast);

    String u4 = CD2AlloyGenerator.getInstance().executeRuleU4(asts);
    String[] lines = u4.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("one sig enum_PositionKind_partTime extends EnumVal {}");
    expectedResult.add("one sig enum_PositionKind_external extends EnumVal {}");
    expectedResult.add("one sig enum_PositionKind_fullTime extends EnumVal {}");

    checkU4(lines, expectedResult);
  }

}
