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

import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the CoCos that restrict ASTCDClass to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class ClassTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/invalid/";
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcg2EbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void modifierOnlyAbstractCoCoTest() {
    String modelName = "C4A61.cd";
    String errorCode = "0xC4A61";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding
            .error(errorCode + " Class A has invalid modifiers. Only \"abstract\" is permitted."),
        Finding
            .error(errorCode + " Class B has invalid modifiers. Only \"abstract\" is permitted."),
        Finding
            .error(errorCode + " Class C has invalid modifiers. Only \"abstract\" is permitted."),
        Finding
            .error(errorCode + " Class D has invalid modifiers. Only \"abstract\" is permitted."),
        Finding
            .error(errorCode + " Class E has invalid modifiers. Only \"abstract\" is permitted."),
        Finding
            .error(errorCode + " Class F has invalid modifiers. Only \"abstract\" is permitted."),
        Finding
            .error(errorCode + " Class G has invalid modifiers. Only \"abstract\" is permitted.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void noConstructorsCoCoTest() {
    String modelName = "C4A62.cd";
    String errorCode = "0xC4A62";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Class A may not have constructors."),
        Finding.error(errorCode + " Class B may not have constructors.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "C4A63.cd";
    String errorCode = "0xC4A63";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Class A may not have any methods."),
        Finding.error(errorCode + " Class B may not have any methods.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
