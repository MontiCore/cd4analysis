/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

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
 * Tests the interface cocos.
 *
 * @author Robert Heim
 */
public class InterfaceTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/";
  
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
    return new CD4ACoCos().getCheckerForMcgCoCos();
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
  public void attributesStaticCoCoTest() {
    String modelName = "CD4AC2001.cd";
    String errorCode = "0xCD4AC2001";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Attribute a in interface A must be static."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute b in interface A must be static."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute c in interface A must be static."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute d in interface A must be static."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute e in interface A must be static."),
        CoCoHelper.buildErrorMsg(errorCode, "Attribute f in interface A must be static.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void invalidModifiersCoCo() {
    String modelName = "CD4AC2006.cd";
    String errorCode = "0xCD4AC2006";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Interface A has invalid modifier \"abstract\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface B has invalid modifier \"derived\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface C has invalid modifier \"derived\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface D has invalid modifier \"final\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface E has invalid modifier \"private\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface F has invalid modifier \"protected\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Interface G has invalid modifier \"static\".")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}
