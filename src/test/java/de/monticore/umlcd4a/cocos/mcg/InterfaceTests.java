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
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  @Test
  public void attributesStaticCoCoTest() {
    String modelName = "C4A51.cd";
    String errorCode = "0xC4A51";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Attribute a in interface A must be static."),
        CoCoFinding.error(errorCode, "Attribute b in interface A must be static."),
        CoCoFinding.error(errorCode, "Attribute c in interface A must be static."),
        CoCoFinding.error(errorCode, "Attribute d in interface A must be static."),
        CoCoFinding.error(errorCode, "Attribute e in interface A must be static."),
        CoCoFinding.error(errorCode, "Attribute f in interface A must be static.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
  
  @Test
  public void invalidModifiersCoCo() {
    String modelName = "C4A56.cd";
    String errorCode = "0xC4A56";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Interface A has invalid modifier \"abstract\"."),
        CoCoFinding.error(errorCode, "Interface B has invalid modifier \"derived\"."),
        CoCoFinding.error(errorCode, "Interface C has invalid modifier \"derived\"."),
        CoCoFinding.error(errorCode, "Interface D has invalid modifier \"final\"."),
        CoCoFinding.error(errorCode, "Interface E has invalid modifier \"private\"."),
        CoCoFinding.error(errorCode, "Interface F has invalid modifier \"protected\"."),
        CoCoFinding.error(errorCode, "Interface G has invalid modifier \"static\".")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
