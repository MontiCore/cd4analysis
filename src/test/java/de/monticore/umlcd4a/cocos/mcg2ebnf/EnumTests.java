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
 * Tests the CoCos that restrict ASTCDEnum to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class EnumTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/";
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.ClassTests
   */
  public EnumTests() {
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
    String modelName = "C4A68.cd";
    String errorCode = "0xC4A68";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Enum A may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum B may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum C may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum D may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum E may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum F may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum G may not have modifiers."),
        CoCoFinding.error(errorCode, "Enum H may not have modifiers.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noConstructorsCoCoTest() {
    String modelName = "C4A69.cd";
    String errorCode = "0xC4A69";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Enum A may not have constructors."),
        CoCoFinding.error(errorCode, "Enum B may not have constructors."),
        CoCoFinding.error(errorCode, "Enum C may not have constructors.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "C4A70.cd";
    String errorCode = "0xC4A70";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Enum A may not have methods."),
        CoCoFinding.error(errorCode, "Enum B may not have methods.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
