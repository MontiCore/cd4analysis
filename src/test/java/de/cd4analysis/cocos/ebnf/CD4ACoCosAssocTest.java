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
public class CD4ACoCosAssocTest extends AbstractCoCoTest {
  /**
   * Constructor for de.cd4analysis.cocos.ebnf.CD4ACoCosAssocTest
   */
  public CD4ACoCosAssocTest() {
    super(MODEL_PATH);
  }
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/invalid/";
  
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
    String modelName = "U0440.cd";
    String errorCode = "0xU0440";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc1 must start in lower-case."),
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc2 must start in lower-case."),
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc3 must start in lower-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  // TODO ... tests for all CoCos of CD4A
  @Ignore
  @Test
  public void testInvalidRoleNames() {
    String modelName = "U0441.cd";
    String errorCode = "0xU00441";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC0_1 of association assocName must start in lower-case."),
            // TODO maybe we should replace "association" with "composition"
            // in case that we actually check a composition?
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC0_1 of association (A RC0_2 -> B) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC1_1 of association (A RC1_1 -> B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC1_2 of association (A -> RC1_2 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC1_3 of association (A RC1_3 -> RC1_4 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC1_4 of association (A RC1_3 -> RC1_4 B) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC2_1 of association (A RC2_1 <- B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC2_2 of association (A <- RC2_2 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC2_3 of association (A RC2_3 <- RC2_4 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC2_4 of association (A RC2_3 <- RC2_4 B) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC3_1 of association (A RC3_1 <-> B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC3_2 of association (A <-> RC3_2 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC3_3 of association (A RC3_3 <-> RC3_4 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC3_4 of association (A RC3_3 <-> RC3_4 B) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC4_1 of association (A RC4_1 -- B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC4_2 of association (A -- RC4_2 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC4_3 of association (A RC4_3 -- RC4_4 B) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RC4_4 of association (A RC4_3 -- RC4_4 B) must start in lower-case."),
            
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI1_1 of association (A RI1_1 -> I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI1_2 of association (A -> RI1_2 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI1_3 of association (A RI1_3 -> RI1_4 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI1_4 of association (A RI1_3 -> RI1_4 I) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI2_1 of association (A RI2_1 <- I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI2_2 of association (A <- RI2_2 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI2_3 of association (A RI2_3 <- RI2_4 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI2_4 of association (A RI2_3 <- RI2_4 I) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI3_1 of association (A RI3_1 <-> I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI3_2 of association (A <-> RI3_2 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI3_3 of association (A RI3_3 <-> RI3_4 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI3_4 of association (A RI3_3 <-> RI3_4 I) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI4_1 of association (A RI4_1 -- I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI4_2 of association (A -- RI4_2 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI4_3 of association (A RI4_3 -- RI4_4 I) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RI4_4 of association (A RI4_3 -- RI4_4 I) must start in lower-case."),
            
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE1_1 of association (A RE1_1 -> E) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE1_2 of association (A -> RE1_2 E) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE1_3 of association (A RE1_3 -> RE1_4 E) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE1_4 of association (A RE1_3 -> RE1_4 E) must start in lower-case."),
            
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE4_1 of association (A RE4_1 -- E) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE4_2 of association (A -- RE4_2 E) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE4_3 of association (A RE4_3 -- RE4_4 E) must start in lower-case."),
            CoCoHelper.buildErrorMsg(errorCode,
                "Role RE4_4 of association (A RE4_3 -- RE4_4 E) must start in lower-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
}
