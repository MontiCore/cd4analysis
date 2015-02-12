/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.ebnf;

import org.junit.Ignore;
import org.junit.Test;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis.cocos.AbstractCoCoTest;

/**
 * Checks that the valid models do not produce any CoCo-Errors
 *
 * @author Robert Heim
 */
public class ValidModelsTest extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/ebnf/valid/";
  
  public ValidModelsTest() {
    super(MODEL_PATH);
  }
  
  /**
   * @see de.cd4analysis.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return CD4ACoCos.getCheckerForEbnfCoCos();
  }
  
  @Ignore
  @Test
  public void testAssocToExternalTypes() {
    String modelName = "CD4AC0022.cd";
    String errorCode = "0xCD4AC0022";
    
    testModelNoErrors(modelName);
  }
}
