/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.ebnf;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.CoCoHelper;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * Tests CoCos dealing with types.
 *
 * @author Robert Heim
 */
public class TypesTest extends AbstractCoCoTest {
  
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.TypesTest
   */
  public TypesTest() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.cd4analysis.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return CD4ACoCos.getCheckerForEbnfCoCos();
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
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/ebnf/";
  
  @Ignore
  @Test
  public void testInvalidInitializationOfDerivedAttr() {
    String modelName = "CD4AC0034.cd";
    String errorCode = "0xCD4AC0034";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    " Invalid initialization of the derived attribute a. Derived attributes may not be initialized."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    " Invalid initialization of the derived attribute b. Derived attributes may not be initialized."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    " Invalid initialization of the derived attribute c. Derived attributes may not be initialized.")
        );
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
}
