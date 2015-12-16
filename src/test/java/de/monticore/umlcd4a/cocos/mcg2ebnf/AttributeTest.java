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
 * Tests the CoCos that restrict ASTCDAttribute to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class AttributeTest extends AbstractCoCoTest {
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
  public void modifierOnlyDerivedCoCoTest() {
    String modelName = "C4A64.cd";
    String errorCode = "0xC4A64";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Attribute a has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute b has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute c has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute d has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute e has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute f has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute g has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute h has invalid modifiers. Only \"/\" is permitted.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
}
