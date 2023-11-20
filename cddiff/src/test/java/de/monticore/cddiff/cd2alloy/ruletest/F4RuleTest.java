/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/** Unit test for the U1 rule for the generation of common class names */
public class F4RuleTest extends CDDiffTestBasis {

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

  private void checkF4(String f4, Set<String> expectedResult) {
    // Extract function names from result
    String[] funNameStr = f4.split("fun");
    for (int i = 2; i < funNameStr.length; i++) {
      funNameStr[i] = funNameStr[i].substring(0, funNameStr[i].indexOf(":"));
      funNameStr[i] = funNameStr[i].replaceAll("\\p{Space}", "");
    }

    // Get function implementation
    f4 = f4.replaceAll(System.getProperty("line.separator"), "");
    String[] result = f4.split("fun [\\w]+\\: Obj->Obj");

    // Check if the output starts with a comment:
    // 1. It contains two lines
    assertTrue(result.length >= 1);

    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("\\/\\/ F4\\: .*"));

    // Check number of outputs
    // assertTrue((result.length - 1) == expectedResult.size());

    // String correctRegex = "fun [\\w]+\\: set Obj->Obj \\{ ([\\w]+ [\\+ [\\w]+]*) \\}";

    // Preprocess expected result to extract expected names and function
    // implementations
    Map<String, String> expNameImpl = new HashMap<>();
    for (String string : expectedResult) {
      // extract function name
      String funName = string;
      funName = funName.replaceFirst("fun ", "");
      funName = funName.substring(0, funName.indexOf(":"));
      //      System.out.println("ExpName: " + funName);

      // extract function implementation
      String funImpl = string;
      funImpl = funImpl.replaceFirst("fun [\\w]+\\:", "");
      funImpl = funImpl.substring(funImpl.indexOf("{") + 1, funImpl.indexOf("}"));
      funImpl = funImpl.replaceAll("\\p{Space}", "");
      //      System.out.println("ExpImpl: " + funImpl);

      // Save as map
      expNameImpl.put(funName, funImpl);
      //      System.out.println(expNameImpl);
    }

    boolean correct = true;
    for (int i = 1; i < result.length; i++) {
      // remove whitespaces and braces
      result[i] = result[i].replaceAll("\\p{Space}", "");
      result[i] = result[i].replaceAll("(\\{|\\})", "");

      //      System.out.println("Impl: " + result[i]);

      // Check if result is valid
      if (!(expNameImpl.get(funNameStr[i + 1]).equals(result[i]))) {
        correct = false;
        System.out.println(
            "Error in F4: Implementation "
                + result[i]
                + " for function "
                + funNameStr[i]
                + " not in "
                + expNameImpl);
      }
    }
    assertTrue(correct);
  }

  @Test
  public void testF4_MV() {
    String f4 = CD2AlloyGenerator.getInstance().executeRuleF4(mvAst);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("fun InsuranceCompFieldsCDcd1:Obj->Obj {rel[EmployeeSubsCDcd1, ins]}");

    checkF4(f4, expectedResult);
  }

  @Test
  public void testF4_cd2v1() {
    String f4 = CD2AlloyGenerator.getInstance().executeRuleF4(m1Ast);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkF4(f4, expectedResult);
  }

  @Test
  public void testF4_cd2v2() {
    String f4 = CD2AlloyGenerator.getInstance().executeRuleF4(m2Ast);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();

    checkF4(f4, expectedResult);
  }
}
