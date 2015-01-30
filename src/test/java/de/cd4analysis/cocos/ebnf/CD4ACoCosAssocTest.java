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
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/ebnf/invalid/";
  
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
    String modelName = "CD4AC0016.cd";
    String errorCode = "0xCD4AC0016";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc1 must start in lower-case."),
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc2 must start in lower-case."),
        CoCoHelper.buildErrorMsg(errorCode, "Association Assoc3 must start in lower-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidRoleNames() {
    String modelName = "CD4AC0017.cd";
    String errorCode = "0xCD4AC0017";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc0 (A -> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc1 (A -> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc2 (A -> (RightRole) E) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc3 (A -> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc4 (B -> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc5 (B -> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc6 (B -> (RightRole) E) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc7 (B -> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc8 (I -> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc9 (I -> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc10 (I -> (RightRole) E) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc11 (I -> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc12 (A (LeftRole) <- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc13 (A (LeftRole) <- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc14 (A (LeftRole) <- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc15 (B (LeftRole) <- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc16 (B (LeftRole) <- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc17 (B (LeftRole) <- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc18 (E (LeftRole) <- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc19 (E (LeftRole) <- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc20 (E (LeftRole) <- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc21 (I (LeftRole) <- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc22 (I (LeftRole) <- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc23 (I (LeftRole) <- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc24 (A (LeftRole) <-> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc25 (A (LeftRole) <-> A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc26 (A <-> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc27 (A (LeftRole) <-> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc28 (A (LeftRole) <-> B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc29 (A <-> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc30 (A (LeftRole) <-> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc31 (A (LeftRole) <-> I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc32 (A <-> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc33 (B (LeftRole) <-> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc34 (B (LeftRole) <-> A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc35 (B <-> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc36 (B (LeftRole) <-> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc37 (B (LeftRole) <-> B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc38 (B <-> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc39 (B (LeftRole) <-> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc40 (B (LeftRole) <-> I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc41 (B <-> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc42 (I (LeftRole) <-> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc43 (I (LeftRole) <-> A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc44 (I <-> (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc45 (I (LeftRole) <-> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc46 (I (LeftRole) <-> B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc47 (I <-> (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc48 (I (LeftRole) <-> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc49 (I (LeftRole) <-> I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc50 (I <-> (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc51 (A (LeftRole) -- (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc52 (A (LeftRole) -- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc53 (A -- (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc54 (A (LeftRole) -- (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc55 (A (LeftRole) -- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc56 (A -- (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc57 (A (LeftRole) -- (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc58 (A (LeftRole) -- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc59 (A -- (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc60 (B (LeftRole) -- (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc61 (B (LeftRole) -- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc62 (B -- (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc63 (B (LeftRole) -- (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc64 (B (LeftRole) -- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc65 (B -- (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc66 (B (LeftRole) -- (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc67 (B (LeftRole) -- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc68 (B -- (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc69 (I (LeftRole) -- (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc70 (I (LeftRole) -- A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc71 (I -- (RightRole) A) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc72 (I (LeftRole) -- (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc73 (I (LeftRole) -- B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc74 (I -- (RightRole) B) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc75 (I (LeftRole) -- (RightRole) I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role LeftRole of association assoc76 (I (LeftRole) -- I) must start in lower-case."),
            CoCoHelper
                .buildErrorMsg(errorCode,
                    "Role RightRole of association assoc77 (I -- (RightRole) I) must start in lower-case.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testCompositeCardinality() {
    String modelName = "CD4AC0018.cd";
    String errorCode = "0xCD4AC0018";
    
    Collection<String> expectedErrors = Arrays.asList(
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp1 has an invalid cardinality 0."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp2 has an invalid cardinality *."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp3 has an invalid cardinality 0."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp4 has an invalid cardinality *."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp5 has an invalid cardinality 0."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp6 has an invalid cardinality *."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp7 has an invalid cardinality 0."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp8 has an invalid cardinality *."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp9 has an invalid cardinality 0."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp10 has an invalid cardinality *."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp11 has an invalid cardinality 0."),
        CoCoHelper.buildErrorMsg(errorCode,
            "The composite of composition comp12 has an invalid cardinality *.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testTypedQualifiedAssoc() {
    // AssocTestGenerator.generateTypedQualifiedAssocTests("LeftTypedQualifier",
    // "RightTypedQualifier");
    String modelName = "CD4AC0019.cd";
    String errorCode = "0xCD4AC0019";
    
    Collection<String> expectedErrors = Arrays
        .asList(
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc0 (A -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc1 (A -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc2 (A -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc3 (A -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc4 (B -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc5 (B -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc6 (B -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc7 (B -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc8 (I -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc9 (I -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc10 (I -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc11 (I -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc12 (A <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc13 (A <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc14 (A <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc15 (B <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc16 (B <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc17 (B <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc18 (E <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc19 (E <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc20 (E <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc21 (I <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc22 (I <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc23 (I <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc24 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc25 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc26 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc27 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc28 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc29 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc30 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc31 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc32 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc33 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc34 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc35 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc36 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc37 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc38 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc39 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc40 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc41 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc42 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc43 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc44 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc45 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc46 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc47 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc48 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc49 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc50 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc51 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc52 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc53 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc54 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc55 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc56 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc57 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc58 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc59 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc60 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc61 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc62 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc63 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc64 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc65 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc66 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc67 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc68 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc69 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc70 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc71 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc72 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc73 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc74 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc75 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc76 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoHelper
                .buildErrorMsg(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc77 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used.")
        );
    testModelForErrorSuffixes(modelName, expectedErrors);
  }
}
