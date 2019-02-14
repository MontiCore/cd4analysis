/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.all;

import de.monticore.cd.cocos.AbstractCoCoTest;
import de.monticore.cd.CD4ACoCos;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the built in types.
 *
 * @author Robert Heim
 */
public class BuiltInTypesTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/all/valid/";
  
  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForAllCoCos();
  }
  
  @BeforeClass
  public static void init() {
    Log.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  /**
   * Tests that the build-in types can be used without importing them.
   */
  @Test
  public void notMultipleVisibilitiesCoCoTest() {
    String modelName = "BuiltInTypes.cd";
    testModelNoErrors(MODEL_PATH_VALID + modelName);
  }
}
