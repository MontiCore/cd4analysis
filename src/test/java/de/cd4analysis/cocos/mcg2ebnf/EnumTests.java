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
import org.junit.Ignore;
import org.junit.Test;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the CoCos that restrict ASTCDEnum to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class EnumTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/mcg2ebnf/";
  
  /**
   * Constructor for de.cd4analysis.cocos.mcg2ebnf.ClassTests
   */
  public EnumTests() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.cd4analysis.cocos.AbstractCoCoTest#getChecker()
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
    String modelName = "CD4AC3008.cd";
    String errorCode = "0xCD4AC3008";
    
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
 
  @Ignore("waiting for https://sselab.de/lab2/private/trac/MontiCore/ticket/1461")
  @Test
  public void noConstructorsCoCoTest() {
    String modelName = "CD4AC3009.cd";
    String errorCode = "0xCD4AC3009";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Enum A may not have constructors."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum B may not have constructors."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum C may not have constructors.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
}
