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

import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the enum cocos.
 *
 * @author Robert Heim
 */
public class EnumTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/invalid/";
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcgCoCos();
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
  public void invalidModifiersCoCo() {
    String modelName = "C4A55.cd";
    String errorCode = "0xC4A55";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Enum A has invalid modifier \"abstract\"."),
        Finding.error(errorCode + " Enum B has invalid modifier \"derived\"."),
        Finding.error(errorCode + " Enum C has invalid modifier \"derived\"."),
        Finding.error(errorCode + " Enum D has invalid modifier \"final\"."),
        Finding.error(errorCode + " Enum E has invalid modifier \"private\"."),
        Finding.error(errorCode + " Enum F has invalid modifier \"protected\"."),
        Finding.error(errorCode + " Enum G has invalid modifier \"static\".")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
