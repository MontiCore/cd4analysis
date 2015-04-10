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
 * Tests the enum cocos.
 *
 * @author Robert Heim
 */
public class EnumTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/";
  
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
  public void invalidModifiersCoCo() {
    String modelName = "C4A55.cd";
    String errorCode = "0xC4A55";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Enum A has invalid modifier \"abstract\"."),
        CoCoFinding.error(errorCode, "Enum B has invalid modifier \"derived\"."),
        CoCoFinding.error(errorCode, "Enum C has invalid modifier \"derived\"."),
        CoCoFinding.error(errorCode, "Enum D has invalid modifier \"final\"."),
        CoCoFinding.error(errorCode, "Enum E has invalid modifier \"private\"."),
        CoCoFinding.error(errorCode, "Enum F has invalid modifier \"protected\"."),
        CoCoFinding.error(errorCode, "Enum G has invalid modifier \"static\".")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
