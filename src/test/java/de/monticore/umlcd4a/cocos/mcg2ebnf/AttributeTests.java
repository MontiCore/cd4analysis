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
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  @Test
  public void modifierOnlyDerivedCoCoTest() {
    String modelName = "C4A64.cd";
    String errorCode = "0xC4A64";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "Attribute a has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute b has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute c has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute d has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute e has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute f has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute g has invalid modifiers. Only \"/\" is permitted."),
        CoCoFinding.error(errorCode,
            "Attribute h has invalid modifiers. Only \"/\" is permitted.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
  
}
