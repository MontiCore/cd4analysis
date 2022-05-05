/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Tests the P2 rule using the examples from the technical report
 */
public class P2RuleTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  ASTCDCompilationUnit m1Ast = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees1.cd");

  ASTCDCompilationUnit m2Ast = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees2.cd");

  @Before
  public void prepareASTs() {
    prepareAST(mvAst);
    prepareAST(m1Ast);
    prepareAST(m2Ast);
  }

  private void checkP2(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 2);

    // Check structure
    checkAlloyStructs("ObjFNames", result, 2);

    // Process expected results
    Map<String, Set<String>> procExpResults = new HashMap<>();
    for (String res : expectedResult) {
      // Remove white spaces
      res = res.replaceAll("\\p{Space}", "");

      // Extract class name
      String className = res.replaceAll(".*\\[", "");
      className = className.replaceAll(",.*", "");

      // Extract possible values
      String values = res.replaceAll(".*,", "");
      values = values.replaceAll("\\].*", "");

      String[] vals = values.split("[+]");
      Set<String> valSet = new HashSet<>();
      Collections.addAll(valSet, vals);

      // Put result to processed expected results
      procExpResults.put(className, valSet);
    }

    // AS variable to track, if a test failed
    boolean correct = true;
    // Check if the result is expected
    for (int i = 0; i < result.length; i++) {
      if (!result[i].startsWith("//")) {
        // Remove white spaces
        result[i] = result[i].replaceAll("\\p{Space}", "");

        // Extract class name
        String className = result[i].replaceAll(".*\\[", "");
        className = className.replaceAll(",.*", "");

        // Extract possible values
        String values = result[i].replaceAll(".*,", "");
        values = values.replaceAll("\\].*", "");

        String[] vals = values.split("[+]");
        Set<String> valSet = new HashSet<>();
        Collections.addAll(valSet, vals);

        // Extract result and check sets
        Set<String> expValSet = procExpResults.get(className);
        for (String string : valSet) {
          if (!expValSet.contains(string)) {
            System.out.println("Error in " + result[i] + ":");
            System.out.println(string + " not in " + expValSet.toString());
            correct = false;
          }
        }

        // Check sizes
        if (expValSet.size() != valSet.size()) {
          correct = false;
        }
      }
    }
    assertTrue(correct);
  }

  // TODO: None or not none?

  @Test
  public void testP1_MV() {
    String p2 = CD2AlloyGenerator.executeRuleP2(mvAst);
    String[] lines = p2.split(System.getProperty("line.separator"));

    System.out.println(p2);

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjFNames[Vehicle, licensePlate + regDate + none]");
    expectedResult.add("ObjFNames[Company, cars + emps + none]");
    expectedResult.add("ObjFNames[Employee, ins + none]");
    expectedResult.add("ObjFNames[Car, licensePlate + regDate + drivenBy + none]");
    expectedResult.add("ObjFNames[Insurance, kind + none]");
    expectedResult.add("ObjFNames[License, owner + none]");
    expectedResult.add("ObjFNames[Driver, exp + license + drives + ins + none]");
    expectedResult.add("ObjFNames[Truck, licensePlate + regDate + none]");

    checkP2(lines, expectedResult);
  }

  @Test
  public void testP1_cd2v1() {
    String p2 = CD2AlloyGenerator.executeRuleP2(m1Ast);
    String[] lines = p2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjFNames[Task, startDate + employee + none]");
    expectedResult.add("ObjFNames[Employee, kind + task + managedBy + none]");
    expectedResult.add("ObjFNames[Manager, none]");

    checkP2(lines, expectedResult);
  }

  @Test
  public void testP1_cd2v2() {
    String p2 = CD2AlloyGenerator.executeRuleP2(m2Ast);
    String[] lines = p2.split(System.getProperty("line.separator"));

    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjFNames[Task, startDate + employee + none]");
    expectedResult.add("ObjFNames[Employee, kind + task + managedBy + none]");
    expectedResult.add("ObjFNames[Manager, kind + task + managedBy + none]");

    checkP2(lines, expectedResult);
  }

}
