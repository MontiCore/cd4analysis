/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the U1 rule for the generation of common class names
 */
public class F2RuleTest extends CDDiffTestBasis {

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

  private void checkF2(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("[// F2\\: ].*"));

    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));

    // Preprocess expected results such that sets can be recognized
    Set<Set<String>> expSets = new HashSet<>();
    for (String string : expectedResult) {
      if (string.matches("fun [\\w]+\\: set Obj \\{ ([\\w]+ [\\+ [\\w]+]+) \\}")) {
        Set<String> objSetExp = new HashSet<>();

        // Remove static part, which is already checked
        String remRes = string.replaceAll("fun [\\w]+\\: set Obj \\{", "");

        // Remove white spaces
        remRes = remRes.replaceAll("\\p{Space}", "");

        // Remove last brace
        remRes = remRes.replaceAll("\\}", "");

        // Split using "+" as separator
        String[] splitRes = remRes.split("\\+");

        // Add to set
        Collections.addAll(objSetExp, splitRes);
        expSets.add(objSetExp);
      }
    }

    boolean correct = true;
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Check structure
      assertTrue(
          result[i].matches("fun [\\w]+\\: set Obj \\{ ([\\w]+|([\\w]+ [\\+ [\\w]+]+)) \\}"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        // We have a set and may need further preprocessing?
        if (result[i].matches("fun [\\w]+\\: set Obj \\{ ([\\w]+ [\\+ [\\w]+]+) \\}")) {
          Set<String> objSetRes = new HashSet<>();

          // Remove static part, which is already checked
          String remRes = result[i].replaceAll("fun [\\w]+\\: set Obj \\{", "");

          // Remove white spaces
          remRes = remRes.replaceAll("\\p{Space}", "");

          // Remove last brace
          remRes = remRes.replaceAll("\\}", "");

          // Split using "+" as separator
          String[] splitRes = remRes.split("\\+");

          // Add to set
          Collections.addAll(objSetRes, splitRes);

          if (!(expSets.contains(objSetRes))) {
            correct = false;
            System.out.println("Error in F2: " + result[i] + " not in " + expectedResult);
          }
        }
        else {
          correct = false;
          System.out.println("Error in F2: " + result[i] + " not in " + expectedResult);
        }
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testF2_MV() {
    String f2 = CD2AlloyGenerator.getInstance().executeRuleF2(mvAst);
    String[] lines = f2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("fun DriveableSubsCDcd1: set Obj { Car + Truck + Vehicle }");

    checkF2(lines, expectedResult);
  }

  @Test
  public void testF2_cd2v1() {
    String f2 = CD2AlloyGenerator.getInstance().executeRuleF2(m1Ast);
    String[] lines = f2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkF2(lines, expectedResult);
  }

  @Test
  public void testF2_cd2v2() {
    String f2 = CD2AlloyGenerator.getInstance().executeRuleF2(m2Ast);
    String[] lines = f2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkF2(lines, expectedResult);
  }

}
