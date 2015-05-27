/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Arrays;
import java.util.Collection;

import mc.ast.SourcePosition;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AttributeTests extends AbstractCoCoTest {
  
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
  public void testInvalidAssignment() {
    String modelName = "C4A11.cd";
    String errorCode = "0xC4A11";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute a in class C1 is not compatible to its type String."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute b in class C1 is not compatible to its type int."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute b2 in class C1 is not compatible to its type Integer."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute c in class C1 is not compatible to its type float."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute c2 in class C1 is not compatible to its type float."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute c3 in class C1 is not compatible to its type Float."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute d in class C1 is not compatible to its type Double."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute d2 in class C1 is not compatible to its type double."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute e in class C1 is not compatible to its type long."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute e2 in class C1 is not compatible to its type Long."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute f in class C1 is not compatible to its type Character."),
            CoCoFinding
                .error(errorCode,
                    "The value assignment for the attribute f2 in class C1 is not compatible to its type char.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidAttributes() {
    String modelName = "C4A12.cd";
    String errorCode = "0xC4A12";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
        CoCoFinding.error(errorCode, "Attribute Attr must start in lower-case.",
            new SourcePosition(5, 12))
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidAttributeOverride() {
    String modelName = "C4A13.cd";
    String errorCode = "0xC4A13";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    "Class C2 overrides the attribute attr (type: String) of class C1 with the different type int."),
            CoCoFinding
                .error(errorCode,
                    "Class C5 overrides the attribute attr (type: int) of class C4 with the different type String."),
            CoCoFinding
                .error(errorCode,
                    "Class C8 overrides the attribute t1 (type: T1) of class C7 with the different type T2.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testUndefinedType() {
    String modelName = "C4A14.cd";
    String errorCode = "0xC4A14";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding
            .error(errorCode, "Type AnUndefinedType of the attribute attr is unknown.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testDuplicateAttributes() {
    String modelName = "C4A15.cd";
    String errorCode = "0xC4A15";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Attribute a is defined multiple times in class C."),
        CoCoFinding.error(errorCode, "Attribute b is defined multiple times in class C."),
        CoCoFinding.error(errorCode, "Attribute c is defined multiple times in class C.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
}
