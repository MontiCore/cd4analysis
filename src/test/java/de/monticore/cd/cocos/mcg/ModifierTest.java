/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg;

import de.monticore.cd.cocos.AbstractCoCoTest;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests the modifier cocos.
 *
 * @author Robert Heim
 */
public class ModifierTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/invalid/";
  
  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcgCoCos();
  }
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void notMultipleVisibilitiesCoCoTest() {
    String modelName = "C4A54.cd";
    String errorCode = "0xC4A54";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found."),
            Finding
                .error(
                errorCode
                    + " Only none or one visibility is supported, but multiple visibilities were found.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
