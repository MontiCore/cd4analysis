/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
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
 * Tests the P4 rule using the examples from the technical report
 */
public class P4RuleTest extends CDDiffTestBasis {

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

  private void checkP4(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);

    // 2. This line is a comment
    assertTrue(result[0].startsWith("//"));

    // Check length
    assertEquals(expectedResult.size(), (result.length - 1));

    // Extract expected result
    String expPrefix = "";
    Set<String> expClasses = new HashSet<>();
    for (String string : expectedResult) {
      // Replace Whitespaces
      string = string.replaceAll("\\p{Space}", "");

      // Remove braces
      string = string.replaceAll("[(]", "");
      string = string.replaceAll("[)]", "");

      expPrefix = string.split("[=]")[0] + "=";

      String[] classNames = string.split("=")[1].split("[+]");
      Collections.addAll(expClasses, classNames);
    }

    // Check result
    for (int i = 1; i < result.length; i++) {

      // Replace Whitespaces
      result[1] = result[1].replaceAll("\\p{Space}", "");

      // Check brace structure
      assertTrue(result[1].matches(".*[(].*[)].*"));

      // Remove braces
      result[1] = result[1].replaceAll("[(]", "");
      result[1] = result[1].replaceAll("[)]", "");

      // Check prefix
      String prefix = result[1].split("[=]")[0] + "=";
      if (!prefix.equals(expPrefix)) {
        System.out.println("Expected " + expPrefix + " but " + prefix + "found.");
      }
      assertEquals(prefix, expPrefix);

      // Check class combination
      String[] classNames = result[1].split("[=]")[1].split("[+]");
      for (String className : classNames) {
        if (!expClasses.contains(className)) {
          System.out.println(className + " not in " + expClasses);
        }
        assertTrue(expClasses.contains(className));
      }
    }

  }

  @Test
  public void testP4_MV() {
    String p4 = CD2AlloyGenerator.executeRuleP4(mvAst);
    String[] lines = p4.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add(
        "Obj = (Vehicle + Company + Employee + Car + Insurance + License + Driver + Truck)");

    checkP4(lines, expectedResult);
  }

  @Test
  public void testP4_Employees1() {
    String p4 = CD2AlloyGenerator.executeRuleP4(m1Ast);
    String[] lines = p4.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("Obj = (Task + Employee + Manager)");

    checkP4(lines, expectedResult);
  }

  @Test
  public void testP4_Employees2() {
    String p4 = CD2AlloyGenerator.executeRuleP4(m2Ast);
    String[] lines = p4.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("Obj = (Task + Employee + Manager)");

    checkP4(lines, expectedResult);
  }

}
