/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.mcg2ebnf;

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
 * Tests the CoCos that restrict ASTCDAssociation to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class AssociationTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/mcg2ebnf/";
  
  /**
   * @see de.cd4analysis.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return CD4ACoCos.getCheckerForMcg2EbnfCoCos();
  }
  
  /**
   * Constructor for de.cd4analysis.cocos.mcg2ebnf.AttributeTests
   */
  public AssociationTests() {
    super(MODEL_PATH);
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
  public void noStereotypesCoCoTest() {
    String modelName = "CD4AC3011.cd";
    String errorCode = "0xCD4AC3011";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper.buildErrorMsg(errorCode,
                "Association assoc1 (A -> B) may not have stereotypes."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Association (A <- B) may not have stereotypes."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Association (A <-> B) may not have stereotypes."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Association (A -- B) may not have stereotypes.")
        );
    
    testModelForErrorSuffixes("invalid/" + modelName, expectedErrors);
  }
  
}
