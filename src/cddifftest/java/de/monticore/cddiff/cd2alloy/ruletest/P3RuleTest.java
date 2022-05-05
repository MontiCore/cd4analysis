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
 * Tests the P3 rule using the examples from the technical report
 */
public class P3RuleTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  ASTCDCompilationUnit m1Ast = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");

  ASTCDCompilationUnit m2Ast = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");

  @Before
  public void prepareASTs() {
    prepareAST(mvAst);
    prepareAST(m1Ast);
    prepareAST(m2Ast);
  }

  private void checkP3(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. This line is a comment
    assertTrue(result[0].startsWith("//"));

    // Check length
    assertEquals(expectedResult.size(), (result.length - 1));

    for (int i = 1; i < result.length; i++) {
      assertTrue(expectedResult.contains(result[i]));
    }

  }

  @Test
  public void testP3_MV() {
    String p3 = CD2AlloyGenerator.executeRuleP3(mvAst);
    String[] lines = p3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("no Vehicle");

    checkP3(lines, expectedResult);
  }

  @Test
  public void testP3_cd2v1() {
    String p3 = CD2AlloyGenerator.executeRuleP3(m1Ast);
    String[] lines = p3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkP3(lines, expectedResult);
  }

  @Test
  public void testP3_cd2v2() {
    String p3 = CD2AlloyGenerator.executeRuleP3(m2Ast);
    String[] lines = p3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkP3(lines, expectedResult);
  }

}
