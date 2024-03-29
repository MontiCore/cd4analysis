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

/** Tests the P3 rule using the examples from the technical report */
public class P3RuleTest extends CDDiffTestBasis {

  protected ASTCDCompilationUnit mvAst =
      parseModel("src/test/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  protected ASTCDCompilationUnit m1Ast =
      parseModel("src/test/resources/de/monticore/cddiff/Employees/Employees1.cd");

  protected ASTCDCompilationUnit m2Ast =
      parseModel("src/test/resources/de/monticore/cddiff/Employees/Employees2.cd");

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
    String p3 = CD2AlloyGenerator.getInstance().executeRuleP3(mvAst);
    String[] lines = p3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("no Vehicle");
    expectedResult.add("no Driveable");

    checkP3(lines, expectedResult);
  }

  @Test
  public void testP3_cd2v1() {
    String p3 = CD2AlloyGenerator.getInstance().executeRuleP3(m1Ast);
    String[] lines = p3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkP3(lines, expectedResult);
  }

  @Test
  public void testP3_cd2v2() {
    String p3 = CD2AlloyGenerator.getInstance().executeRuleP3(m2Ast);
    String[] lines = p3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkP3(lines, expectedResult);
  }
}
