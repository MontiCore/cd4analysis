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
 * Tests the CoCos for diagrams in general.
 *
 * @author Robert Heim
 */
public class DiagramTests extends AbstractCoCoTest {
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.DiagramTests
   */
  public DiagramTests() {
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
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/ebnf/invalid/";
  

  @Ignore
  @Test
  public void testDiagramName() {
    String modelName = "cD4AC0001.cd";
    String errorCode = "0xCD4AC0001";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "First character of the diagram name cD4AC0001 must be upper-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
    
    modelName = "CD4AC0002";
    errorCode = "0xCD4AC0002";
    expectedErrors = Arrays
        .asList(
        CoCoHelper
            .buildErrorMsg(
                errorCode,
                "The name of the diagram CD4AC0002Invalid is not identical to the name of the file CD4AC0002 (without its fileextension).")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  

  @Ignore
  @Test
  public void testNames() {
    String modelName = "CD4AC0003.cd";
    String errorCode = "0xCD4AC0003";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Name DAO is reserved for internal use."),
        CoCoHelper.buildErrorMsg(errorCode, "Name Factory is reserved for internal use.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  
}
