/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.ebnf;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the CoCos that deal with classes, interfaces and enums.
 *
 * @author Robert Heim
 */
public class ClassInterfaceEnumsTests extends AbstractCoCoTest {
  
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.TypesTests
   */
  public ClassInterfaceEnumsTests() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.cd4analysis.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return CD4ACoCos.getCheckerForEbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    LogMock.init();
    Log.enableFailQuick(false);
    LogMock.setProduceOutput(false);
  }
  
  @Before
  public void setUp() {
    LogMock.getFindings().clear();
  }
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/ebnf/invalid/";
  
  @Ignore
  @Test
  public void testDuplicateTypeNames() {
    String modelName = "CD4AC0004.cd";
    String errorCode = "0xCD4AC0004";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The name A is used several times. Classes, interfaces and enumerations may not use the same names."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The name B is used several times. Classes, interfaces and enumerations may not use the same names."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The name C is used several times. Classes, interfaces and enumerations may not use the same names.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidTypeName() {
    String modelName = "0xCD4AC0005.cd";
    String errorCode = "0xCD4AC0005";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The first character of the interface i must be upper-case."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The first character of the class c must be upper-case."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The first character of the enum e must be upper-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testDuplicateEnumConstant() {
    String modelName = "CD4AC0006.cd";
    String errorCode = "0xCD4AC0006";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Duplicate enum constant: a.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInheritanceCycle() {
    String modelName = "CD4AC0007.cd";
    String errorCode = "0xCD4AC0007";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The class C2 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The interface I2 introduces an inheritance cycle. Inheritance may not be cyclic.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidClassExtends() {
    String modelName = "CD4AC0008.cd";
    String errorCode = "0xCD4AC0008";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "Class C1 cannot extend interface I. A class may only extend classes."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class C2 cannot extend enum E. A class may only extend classes.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidInterfaceExtends() {
    String modelName = "CD4AC0009.cd";
    String errorCode = "0xCD4AC0009";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "Interface I1 cannot extend class C. An interface may only extend interfaces."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Interface I2 cannot extend enum E. An interface may only extend interfaces.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidImplements() {
    String modelName = "CD4AC0010.cd";
    String errorCode = "0xCD4AC0010";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The class C1 cannot implement class C. Only interfaces may be implemented."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The class C2 cannot implement enum E. Only interfaces may be implemented."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The enum E1 cannot implement class C. Only interfaces may be implemented."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The enum E2 cannot implement enum E. Only interfaces may be implemented.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
}
