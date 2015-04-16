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

/**
 * Tests the attribute cocos.
 *
 * @author Robert Heim
 */
public class AttributeTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/";
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.ClassTests
   */
  public AttributeTests() {
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
  public void attributesNotAbstractCoCoTest() {
    String modelName = "C4A52.cd";
    String errorCode = "0xC4A52";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Attribute a may not be abstract."),
        CoCoFinding.error(errorCode, "Attribute b may not be abstract."),
        CoCoFinding.error(errorCode, "Attribute c may not be abstract."),
        CoCoFinding.error(errorCode, "Attribute d may not be abstract.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
