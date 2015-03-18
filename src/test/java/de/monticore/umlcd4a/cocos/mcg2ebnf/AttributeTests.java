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

import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the CoCos that restrict ASTCDAttribute to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class AttributeTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/";

  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcg2EbnfCoCos();
  }
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.AttributeTests
   */
  public AttributeTests() {
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
  public void modifierOnlyDerivedCoCoTest() {
    String modelName = "C4A64.cd";
    String errorCode = "0xC4A64";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute a has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute b has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute c has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute d has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute e has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute f has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute g has invalid modifiers. Only \"/\" is permitted."),
        CoCoHelper.buildErrorMsg(errorCode,
            "Attribute h has invalid modifiers. Only \"/\" is permitted.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
}
