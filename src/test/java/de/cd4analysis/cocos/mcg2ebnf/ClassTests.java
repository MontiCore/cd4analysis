/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.mcg2ebnf;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the CoCos that restrict ASTCDClass to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class ClassTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/mcg2ebnf/";
  
  /**
   * Constructor for de.cd4analysis.cocos.mcg2ebnf.ClassTests
   * 
   * @param modelPath
   */
  public ClassTests() {
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
  
  @Test
  public void modifierOnlyAbstractCoCoTest() {
    String modelName = "CD4AC3001.cd";
    String errorCode = "0xCD4AC3001";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "Class A has invalid modifiers. Only \"abstract\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class B has invalid modifiers. Only \"abstract\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class C has invalid modifiers. Only \"abstract\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class D has invalid modifiers. Only \"abstract\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class E has invalid modifiers. Only \"abstract\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class F has invalid modifiers. Only \"abstract\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Class G has invalid modifiers. Only \"abstract\" is permitted.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noConstructorsCoCoTest() {
    String modelName = "CD4AC3002.cd";
    String errorCode = "0xCD4AC3002";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Class A may not have constructors."),
        CoCoHelper.buildErrorMsg(errorCode, "Class B may not have constructors.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "CD4AC3003.cd";
    String errorCode = "0xCD4AC3003";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Class A may not have any methods."),
        CoCoHelper.buildErrorMsg(errorCode, "Class B may not have any methods.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}
