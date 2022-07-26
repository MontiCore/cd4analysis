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
public class F1RuleTest extends CDDiffTestBasis {

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

  private void checkF1(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ F1\\: .*"));

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
            System.out.println("Error in F1: " + result[i] + " not in " + expectedResult);
          }
        }
        else {
          correct = false;
          System.out.println("Error in F1: " + result[i] + " not in " + expectedResult);
        }
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testF1_MV() {
    String f1 = CD2AlloyGenerator.getInstance().executeRuleF1(mvAst);
    String[] lines = f1.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("fun VehicleSubsCDcd1: set Obj { Vehicle + Car + Truck }");
    expectedResult.add("fun CompanySubsCDcd1: set Obj { Company }");
    expectedResult.add("fun EmployeeSubsCDcd1: set Obj { Employee + Driver }");
    expectedResult.add("fun CarSubsCDcd1: set Obj { Car }");
    expectedResult.add("fun InsuranceSubsCDcd1: set Obj { Insurance }");
    expectedResult.add("fun LicenseSubsCDcd1: set Obj { License }");
    expectedResult.add("fun DriverSubsCDcd1: set Obj { Driver }");
    expectedResult.add("fun TruckSubsCDcd1: set Obj { Truck }");

    checkF1(lines, expectedResult);
  }

  @Test
  public void testF1_Employees1() {
    String f1 = CD2AlloyGenerator.getInstance().executeRuleF1(m1Ast);
    String[] lines = f1.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("fun TaskSubsCDEmployees1: set Obj { Task }");
    expectedResult.add("fun EmployeeSubsCDEmployees1: set Obj { Employee }");
    expectedResult.add("fun ManagerSubsCDEmployees1: set Obj { Manager }");

    checkF1(lines, expectedResult);
  }

  @Test
  public void testF1_Employees2() {
    String f1 = CD2AlloyGenerator.getInstance().executeRuleF1(m2Ast);
    String[] lines = f1.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("fun TaskSubsCDEmployees2: set Obj { Task }");
    expectedResult.add("fun EmployeeSubsCDEmployees2: set Obj { Employee + Manager }");
    expectedResult.add("fun ManagerSubsCDEmployees2: set Obj { Manager }");

    checkF1(lines, expectedResult);
  }

}
