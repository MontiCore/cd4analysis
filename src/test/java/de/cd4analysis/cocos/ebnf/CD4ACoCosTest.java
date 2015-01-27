/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.ebnf;

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
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/invalid/";
  
  // TODO ... tests for all CoCos of CD4A
  
  @Ignore
  @Test
  public void testDiagramName() {
    String modelName = "a0134.cd";
    String errorCode = "0xA0134";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "First character of the diagram name a0134 must be upper-case.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testFileName() {
    String modelName = "XXXNoCode1.cd";
    String errorCode = "XXXNoCode1";
    
    Collection<String> expectedErrors = Arrays
        .asList(
        CoCoHelper
            .buildErrorMsg(
                errorCode,
                "The name of the diagram XXXNoCode1_bad is not identical to the name of the filel XXXNoCode1.cd (without its fileextension).")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testReservedNames() {
    String modelName = "D00XX.cd";
    String errorCode = "0xD00XX";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Name DAO is reserved for internal use."),
        CoCoHelper.buildErrorMsg(errorCode, "Name Factory is reserved for internal use.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testDuplicateTypeNames() {
    String modelName = "XXXNoCode2.cd";
    String errorCode = "XXXNoCode2";
    
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
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidTypeName() {
    String modelName = "U0530.cd";
    String errorCode = "0xU0530";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The first character of the interface i must be upper-case."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The first character of the class c must be upper-case."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The first character of the enum e must be upper-case.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testDuplicateEnumConstant() {
    String modelName = "U0504.cd";
    String errorCode = "0xU0504";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Duplicate enum constant: a.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInheritanceCycle() {
    String modelName = "U0531.cd";
    String errorCode = "0xU0531";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The class C2 introduces an inheritance cycle. Inheritance may not be cyclic."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The interface I2 introduces an inheritance cycle. Inheritance may not be cyclic.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidClassExtends() {
    String modelName = "U0496_U0497.cd";
    String errorCode = "0xU0496_0xU0497";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "Class C1 cannot extend interface I. A class may only extend classes."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class C2 cannot extend enum E. A class may only extend classes.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidInterfaceExtends() {
    String modelName = "XXXNoCode3.cd";
    String errorCode = "XXXNoCode3";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "Interface I1 cannot extend class C. An interface may only extend interfaces."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Interface I2 cannot extend enum E. An interface may only extend interfaces.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidImplements() {
    String modelName = "U0533_U0534.cd";
    String errorCode = "0xU0533_0xU0534";
    
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
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidAssignment() {
    String modelName = "U0447.cd";
    String errorCode = "0xU0447";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "The value assignment for the attribute a in class C1 is not compatible to its type String."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "The value assignment for the attribute b in class C1 is not compatible to its type int.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidAttributes() {
    String modelName = "U0454.cd";
    String errorCode = "0xU0454";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "The value assignment for the attribute a in class C1 is not compatible to its type String."),
            CoCoHelper.buildErrorMsg(errorCode, "Attribute Attr must start in lower-case.",
                new SourcePosition(5, 12))
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidAttributeOverride() {
    String modelName = "U0455.cd";
    String errorCode = "0xU0455";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Class C2 overrides the attribute attr (type: String) of class C1 with the different type int."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Class C5 overrides the attribute attr (type: int) of class C4 with the different type String.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testUndefinedType() {
    String modelName = "D0410.cd";
    String errorCode = "0xD0410";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper
            .buildErrorMsg(errorCode, "Type AnUndefinedType of the attribute attr is unkown.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  public void testDuplicateAttributes() {
    String modelName = "XXXNoCode4.cd";
    String errorCode = "XXXNoCode4";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Attribute a is defined multiple times in class C."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute b is defined multiple times in class C.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
}
