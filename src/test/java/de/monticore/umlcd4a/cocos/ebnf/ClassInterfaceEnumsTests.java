/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;

/**
 * Tests the CoCos that deal with classes, interfaces and enums.
 *
 * @author Robert Heim
 */
public class ClassInterfaceEnumsTests extends AbstractCoCoTest {
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForEbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
  @Test
  public void testDuplicateTypeNames() {
    String modelName = "C4A04.cd";
    String errorCode = "0xC4A04";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "The name A is used several times. Classes, interfaces and enumerations may not use the same names."),
            CoCoFinding
                .error(
                    errorCode,
                    "The name B is used several times. Classes, interfaces and enumerations may not use the same names."),
            CoCoFinding
                .error(
                    errorCode,
                    "The name C is used several times. Classes, interfaces and enumerations may not use the same names.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidTypeName() {
    String modelName = "C4A05.cd";
    String errorCode = "0xC4A05";
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "The first character of the interface i must be upper-case."),
        CoCoFinding.error(errorCode,
            "The first character of the class c must be upper-case."),
        CoCoFinding.error(errorCode,
            "The first character of the enum e must be upper-case.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testDuplicateEnumConstant() {
    String modelName = "C4A06.cd";
    String errorCode = "0xC4A06";
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Duplicate enum constant: a.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInheritanceCycle() {
    String modelName = "C4A07.cd";
    String errorCode = "0xC4A07";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "The class C2 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The class C introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The class D introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The class D2 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The class D3 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The interface I introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The interface I2 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The interface J introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The interface J2 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoFinding.error(errorCode,
            "The interface J3 introduces an inheritance cycle. Inheritance may not be cyclic.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidClassExtends() {
    String modelName = "C4A08.cd";
    String errorCode = "0xC4A08";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "Class C1 cannot extend interface I. A class may only extend classes."),
        CoCoFinding.error(errorCode,
            "Class C2 cannot extend enum E. A class may only extend classes.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidInterfaceExtends() {
    String modelName = "C4A09.cd";
    String errorCode = "0xC4A09";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "Interface I1 cannot extend class C. An interface may only extend interfaces."),
        CoCoFinding.error(errorCode,
            "Interface I2 cannot extend enum E. An interface may only extend interfaces.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidImplements() {
    String modelName = "C4A10.cd";
    String errorCode = "0xC4A10";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "The class C1 cannot implement enum E. Only interfaces may be implemented."),
        CoCoFinding.error(errorCode,
            "The class C2 cannot implement class C. Only interfaces may be implemented."),
        CoCoFinding.error(errorCode,
            "The enum E1 cannot implement enum E. Only interfaces may be implemented."),
        CoCoFinding.error(errorCode,
            "The enum E2 cannot implement class C. Only interfaces may be implemented.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
