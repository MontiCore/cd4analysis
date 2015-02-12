/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
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
 * Tests the codes and messages of CoCos regarding associations.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCosAssocTest2 extends AbstractCoCoTest {
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.CD4ACoCosAssocTest
   */
  public CD4ACoCosAssocTest2() {
    super(MODEL_PATH);
  }
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/ebnf/invalid/";
  
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
  
  @Ignore
  @Test
  public void testAssocName() {
    String modelName = "CD4AC0025.cd";
    String errorCode = "0xCD4AC0025";
    String msg = "Association %s conflicts with attribute % in %.";
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc1 must start in lower-case."),
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc2 must start in lower-case."),
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc3 must start in lower-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
}
