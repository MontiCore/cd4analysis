/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cddiff.cd2alloy.AbstractTest;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the P1 rule using the examples from the technical report
 * 
 */
public class P1RuleTest extends AbstractTest {
  
  ASTCDCompilationUnit mvAst = parseModel("src/test/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
  
  ASTCDCompilationUnit m1Ast = parseModel("src/test/resources/de/monticore/cddiff/Manager/cd2v1.cd");
  
  ASTCDCompilationUnit m2Ast = parseModel("src/test/resources/de/monticore/cddiff/Manager/cd2v2.cd");
  
  private void checkP1(String[] result, Set<String> expectedResult) {
    // Check if the output starts with a comment:
    // 1. It contains a line
    assertTrue(result.length >= 1);
    
    // 2. It is the correct comment prefix
    assertTrue(result[0].matches("[// P1: ].*"));
    
    // Check structure
    checkAlloyStructs("ObjAttrib", result, 1);
    
    // Check number of outputs
    assertEquals(expectedResult.size(), (result.length - 1));
    
    // Check correctnesslines[i]
    for (int i = 1; i < result.length; i++) {
      // Remove all white spaces
      String currentLine = result[i].replaceAll("\\p{Space}", "");
      
      // Check if result is valid
      assertTrue(expectedResult.contains(currentLine));
    }
  }
  
  @Test
  public void testP1_MV() {
    String p1 = CD2AlloyGenerator.executeRuleP1(mvAst);
    String[] lines = p1.split(System.getProperty("line.separator"));
    
    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjAttrib[Vehicle,licensePlate,type_String]");
    expectedResult.add("ObjAttrib[Vehicle,regDate,type_Date]");
    expectedResult.add("ObjAttrib[Car,licensePlate,type_String]");
    expectedResult.add("ObjAttrib[Car,regDate,type_Date]");
    expectedResult.add("ObjAttrib[Insurance,kind,InsuranceKindEnumCDcd1]");
    expectedResult.add("ObjAttrib[Driver,exp,DrivingExpEnumCDcd1]");
    expectedResult.add("ObjAttrib[Truck,licensePlate,type_String]");
    expectedResult.add("ObjAttrib[Truck,regDate,type_Date]");
    
    checkP1(lines, expectedResult);
  }
  
  @Test
  public void testP1_cd2v1() {
    String p1 = CD2AlloyGenerator.executeRuleP1(m1Ast);
    String[] lines = p1.split(System.getProperty("line.separator"));
    
    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjAttrib[Task,startDate,type_Date]");
    expectedResult.add("ObjAttrib[Employee,kind,PositionKindEnumCDcd2v1]");
    
    checkP1(lines, expectedResult);
  }
  
  @Test
  public void testP1_cd2v2() {
    String p1 = CD2AlloyGenerator.executeRuleP1(m2Ast);
    String[] lines = p1.split(System.getProperty("line.separator"));
    
    // Definition of expected result
    Set<String> expectedResult = new HashSet<>();
    expectedResult.add("ObjAttrib[Task,startDate,type_Date]");
    expectedResult.add("ObjAttrib[Employee,kind,PositionKindEnumCDcd2v2]");
    expectedResult.add("ObjAttrib[Manager,kind,PositionKindEnumCDcd2v2]");
    
    checkP1(lines, expectedResult);
  }
}

