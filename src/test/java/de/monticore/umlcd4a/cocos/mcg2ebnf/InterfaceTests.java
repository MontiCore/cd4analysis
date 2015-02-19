/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the CoCos that restrict ASTCDInterface to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class InterfaceTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/";
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.ClassTests
   */
  public InterfaceTests() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return CD4ACoCos.getCheckerForMcg2EbnfCoCos();
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
  
  @Test
  public void noModifierCoCo() {
    String modelName = "CD4AC3005.cd";
    String errorCode = "0xCD4AC3005";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Interface A may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface B may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface C may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface D may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface E may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface F may not have modifiers.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noAttributesCoCoTest() {
    String modelName = "CD4AC3006.cd";
    String errorCode = "0xCD4AC3006";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Interface A may not have attributes."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface B may not have attributes."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface C may not have attributes.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "CD4AC3007.cd";
    String errorCode = "0xCD4AC3007";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Interface A may not have methods."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface B may not have methods.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}
