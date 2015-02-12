/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.mcg;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the class cocos.
 *
 * @author Robert Heim
 */
public class ClassTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/mcg/";
  
  /**
   * Constructor for de.cd4analysis.cocos.mcg2ebnf.ClassTests
   */
  public ClassTests() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.cd4analysis.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return CD4ACoCos.getCheckerForMcgCoCos();
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
  public void invalidModifiersCoCoTest() {
    String modelName = "CD4AC2003.cd";
    String errorCode = "0xCD4AC2003";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Class A has invalid modifier \"derived\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Class B has invalid modifier \"derived\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Class C has invalid modifier \"static\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Class D has invalid modifier \"static\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Class E has invalid modifier \"derived\".")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}
