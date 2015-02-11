/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos.mcg;

import static de.monticore.cocos.CoCoHelper.buildErrorMsg;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis.cocos.AbstractCoCoTest;
import de.monticore.cocos.LogMock;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the codes and messages of CoCos regarding associations.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCosAssocTest extends AbstractCoCoTest {
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.CD4ACoCosAssocTest
   */
  public CD4ACoCosAssocTest() {
    super(MODEL_PATH);
  }
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/mcg/invalid/";
  
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
  public void testAssocInvalidModifier() {
    String modelName = "CD_1_44.cd";
    String errorCode = "CD-1-44";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            buildErrorMsg(errorCode,
                "The modifier abstract can not be used for associations at association Assoc1 (A -> B)."),
            buildErrorMsg(errorCode,
                "The modifier abstract can not be used for associations at association Assoc2 (A -> B).")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
}
