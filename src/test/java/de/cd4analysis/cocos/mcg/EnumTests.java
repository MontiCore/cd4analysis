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
 * Tests the enum cocos.
 *
 * @author Robert Heim
 */
public class EnumTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/mcg/";
  
  /**
   * Constructor for de.cd4analysis.cocos.mcg2ebnf.ClassTests
   */
  public EnumTests() {
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
  public void invalidModifiersCoCo() {
    String modelName = "CD4AC2005.cd";
    String errorCode = "0xCD4AC2005";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Enum A has invalid modifier \"abstract\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum B has invalid modifier \"derived\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum C has invalid modifier \"derived\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum D has invalid modifier \"final\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum E has invalid modifier \"private\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum F has invalid modifier \"protected\"."),
        CoCoHelper.buildErrorMsg(errorCode, "Enum G has invalid modifier \"static\".")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}