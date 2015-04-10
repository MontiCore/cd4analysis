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

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the modifier cocos.
 *
 * @author Robert Heim
 */
public class ModifierTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/";
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.ClassTests
   */
  public ModifierTests() {
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
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  @Test
  public void notMultipleVisibilitiesCoCoTest() {
    String modelName = "C4A54.cd";
    String errorCode = "0xC4A54";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found."),
            CoCoFinding.error(errorCode,
                "Only none or one visibility is supported, but multiple visibilities were found.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
