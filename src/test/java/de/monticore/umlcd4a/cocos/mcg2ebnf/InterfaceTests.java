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

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
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
    return new CD4ACoCos().getCheckerForMcg2EbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  @Test
  public void noModifierCoCo() {
    String modelName = "C4A65.cd";
    String errorCode = "0xC4A65";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Interface A may not have modifiers."),
        CoCoFinding.error(errorCode, "Interface B may not have modifiers."),
        CoCoFinding.error(errorCode, "Interface C may not have modifiers."),
        CoCoFinding.error(errorCode, "Interface D may not have modifiers."),
        CoCoFinding.error(errorCode, "Interface E may not have modifiers."),
        CoCoFinding.error(errorCode, "Interface F may not have modifiers.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noAttributesCoCoTest() {
    String modelName = "C4A66.cd";
    String errorCode = "0xC4A66";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Interface A may not have attributes."),
        CoCoFinding.error(errorCode, "Interface B may not have attributes."),
        CoCoFinding.error(errorCode, "Interface C may not have attributes.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "C4A67.cd";
    String errorCode = "0xC4A67";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Interface A may not have methods."),
        CoCoFinding.error(errorCode, "Interface B may not have methods.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
