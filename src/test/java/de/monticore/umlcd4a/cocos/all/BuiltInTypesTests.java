/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.all;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;

/**
 * Tests the built in types.
 *
 * @author Robert Heim
 */
public class BuiltInTypesTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/all/";
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.ClassTests
   */
  public BuiltInTypesTests() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForAllCoCos();
  }
  
  @BeforeClass
  public static void init() {
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  /**
   * Tests that the build-in types can be used without importing them.
   */
  @Test
  public void notMultipleVisibilitiesCoCoTest() {
    String modelName = "BuiltInTypes.cd";

    testModelNoErrors("valid/" + modelName);
  }
}
