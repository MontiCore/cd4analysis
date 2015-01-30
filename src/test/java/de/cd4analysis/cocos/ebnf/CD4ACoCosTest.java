/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.ebnf;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import mc.ast.SourcePosition;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCosTest extends AbstractCoCoTest {
  
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.CD4ACoCosTest
   * 
   * @param modelPath
   */
  public CD4ACoCosTest() {
    super(MODEL_PATH);
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
  
  // TODO ... tests for all CoCos of CD4A
  
  @Ignore
  @Test
  public void testDiagramName() {
    String modelName = "cD4AC0001.cd";
    String errorCode = "0xCD4AC0001";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "First character of the diagram name cD4AC0001 must be upper-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
    
    modelName = "CD4AC0002";
    errorCode = "0xCD4AC0002";
    expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The name of the diagram CD4AC0002Invalid is not identical to the name of the file CD4AC0002 (without its fileextension).")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testNames() {
    String modelName = "CD4AC0003.cd";
    String errorCode = "0xCD4AC0003";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Name DAO is reserved for internal use."),
        CoCoHelper.buildErrorMsg(errorCode, "Name Factory is reserved for internal use.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
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
  
  @Ignore
  @Test
  public void testInvalidAssignment() {
    String modelName = "CD4AC0011.cd";
    String errorCode = "0xCD4AC0011";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "The value assignment for the attribute a in class C1 is not compatible to its type String."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "The value assignment for the attribute b in class C1 is not compatible to its type int.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidAttributes() {
    String modelName = "CD4AC0012.cd";
    String errorCode = "CD4AC0012";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper.buildErrorMsg(errorCode, "Attribute Attr must start in lower-case.",
                new SourcePosition(5, 12))
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidAttributeOverride() {
    String modelName = "CD4AC0013.cd";
    String errorCode = "0xCD4AC0013";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Class C2 overrides the attribute attr (type: String) of class C1 with the different type int."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Class C5 overrides the attribute attr (type: int) of class C4 with the different type String."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Class C8 overrides the attribute t1 (type: T1) of class C7 with the different type T2.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testUndefinedType() {
    String modelName = "CD4AC0014.cd";
    String errorCode = "0xCD4AC0014";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper
            .buildErrorMsg(errorCode, "Type AnUndefinedType of the attribute attr is unkown.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Test
  public void testDuplicateAttributes() {
    String modelName = "CD4AC0015.cd";
    String errorCode = "0xCD4AC0015";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Attribute a is defined multiple times in class C."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute b is defined multiple times in class C."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute c is defined multiple times in class C.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
}
