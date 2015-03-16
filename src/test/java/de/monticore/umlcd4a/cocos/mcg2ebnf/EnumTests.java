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
    String modelName = "C4A68.cd";
    String errorCode = "0xC4A68";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Enum A may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum B may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum C may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum D may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum E may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum F may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum G may not have modifiers."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum H may not have modifiers.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noConstructorsCoCoTest() {
    String modelName = "C4A69.cd";
    String errorCode = "0xC4A69";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Enum A may not have constructors."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum B may not have constructors."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum C may not have constructors.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "C4A70.cd";
    String errorCode = "0xC4A70";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Enum A may not have methods."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum B may not have methods.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}
