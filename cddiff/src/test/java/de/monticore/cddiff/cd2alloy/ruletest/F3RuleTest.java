/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import de.se_rwth.commons.logging.Log;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/** Unit test for the U1 rule for the generation of common class names */
public class F3RuleTest extends CDDiffTestBasis {

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

  private void checkF3(String[] result, Set<String> expectedResult) {
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
      if (string.matches("fun [\\w]+\\: set EnumVal \\{ ([\\w]+ [\\+ [\\w]+]*) \\}")) {
        Set<String> objSetExp = new HashSet<>();

        // Remove static part, which is already checked
        String remRes = string.replaceAll("fun [\\w]+\\: set EnumVal \\{", "");

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
      Log.println(result[i]);
      assertTrue(
          result[i].matches("fun [\\w]+\\: set EnumVal \\{ ([\\w]+|([\\w]+ [\\+ [\\w]+]*)) \\}"));

      // Check if result is valid
      if (!(expectedResult.contains(result[i]))) {
        // We have a set and may need further preprocessing?
        if (result[i].matches("fun [\\w]+\\: set EnumVal \\{ ([\\w]+ [\\+ [\\w]+]*) \\}")) {
          Set<String> objSetRes = new HashSet<>();

          // Remove static part, which is already checked
          String remRes = result[i].replaceAll("fun [\\w]+\\: set EnumVal \\{", "");

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
            System.out.println("Error in F3: " + result[i] + " not in " + expectedResult);
          }
        } else {
          correct = false;
          System.out.println("Error in F3: " + result[i] + " not in " + expectedResult);
        }
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testF3_MV() {
    String f3 = CD2AlloyGenerator.getInstance().executeRuleF3(mvAst);
    String[] lines = f3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add(
        "fun DrivingExpEnumCDcd1: set EnumVal { enum_DrivingExp_q_dot_expert + "
            + "enum_DrivingExp_q_dot_beginner"
            + " }");
    expectedResult.add(
        "fun InsuranceKindEnumCDcd1: set EnumVal { enum_InsuranceKind_q_dot_international + "
            + "enum_InsuranceKind_q_dot_transport }");

    checkF3(lines, expectedResult);
  }

  @Test
  public void testF3_cd2v1() {
    String f3 = CD2AlloyGenerator.getInstance().executeRuleF3(m1Ast);
    String[] lines = f3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add(
        "fun PositionKindEnumCDcd2v1: set EnumVal { enum_PositionKind_q_dot_partTime + "
            + "enum_PositionKind_q_dot_fullTime }");

    checkF3(lines, expectedResult);
  }

  @Test
  public void testF3_cd2v2() {
    String f3 = CD2AlloyGenerator.getInstance().executeRuleF3(m2Ast);
    String[] lines = f3.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add(
        "fun PositionKindEnumCDcd2v2: set EnumVal { enum_PositionKind_q_dot_partTime + "
            + "enum_PositionKind_q_dot_external + enum_PositionKind_q_dot_fullTime }");

    checkF3(lines, expectedResult);
  }
}
