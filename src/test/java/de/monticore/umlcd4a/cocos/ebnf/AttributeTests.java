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
import org.junit.Ignore;
import org.junit.Test;

import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AttributeTests extends AbstractCoCoTest {
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.ebnf.CD4ACoCosTest
   * 
   * @param modelPath
   */
  public AttributeTests() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForEbnfCoCos();
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
  
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
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
