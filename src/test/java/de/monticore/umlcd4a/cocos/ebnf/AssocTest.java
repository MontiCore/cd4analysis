/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.cocos.helper.Assert;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Tests the codes and messages of CoCos regarding associations.
 *
 * @author (last commit) $Author$
 */
public class AssocTest extends AbstractCoCoTest {
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/valid/";
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForEbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    LogStub.init();
    Log.getFindings().clear();
  }
  
  @Test
  public void testAssocName() {
    String modelName = "C4A16.cd";
    String errorCode = "0xC4A16";
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Association Assoc1 must start in lower-case."),
        Finding.error(errorCode + " Association Assoc2 must start in lower-case."),
        Finding.error(errorCode + " Association Assoc3 must start in lower-case.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }

  @Test
  public void testAssocNameConflictAttribute() {
    String modelName = "C4A25.cd";
    String errorCode = "0xC4A25";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Association a conflicts with the attribute a in B.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    
    modelName = "C4A25_1.cd";
    Log.getFindings().clear();
    expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Association attrA1 conflicts with the attribute attrA1 in A."),
        Finding.error(errorCode + " Association attrA2 conflicts with the attribute attrA2 in A."),
        Finding.error(errorCode + " Association attrA3 conflicts with the attribute attrA3 in A."),
        Finding.error(errorCode + " Association attrA4 conflicts with the attribute attrA4 in A."),
        Finding.error(errorCode + " Association attrA6 conflicts with the attribute attrA6 in A."),
        Finding.error(errorCode + " Association attrA5 conflicts with the attribute attrA5 in A.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    
    modelName = "C4A25_2.cd";
    Log.getFindings().clear();
    expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Association a1 conflicts with the attribute a1 in BSup.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    
    modelName = "C4A25_3.cd";
    Log.getFindings().clear();
    expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Association attr conflicts with the attribute attr in BSup.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    
    modelName = "C4A25_4.cd";
    Log.getFindings().clear();
    expectedErrors = Arrays.asList(
        Finding.error(errorCode
            + " Association theInterface conflicts with the attribute theInterface in BSup.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }

  @Test
  public void testAssocRoleConflictAttribute() {
    String modelName = "C4A27.cd";
    String errorCode = "0xC4A27";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(errorCode
                    + " The automatically introduced role name c of class C for association (C <- D) conflicts with an attribute in B."),
            Finding
                .error(errorCode
                    + " The role name assoc of class C for association (D <-> (assoc) C) conflicts with an attribute in B.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }

  @Test
  public void testAssocRoleConflictRole() {
    String modelName = "C4A28.cd";
    String errorCode = "0xC4A28";

   testModelNoErrors(MODEL_PATH_VALID + "C4A28_2.cd"); // read-only test
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
                // 0xCA33 gives a more concrete error message than 0xA28
                Finding.error("0xC4A33 Association `association E -> A ;` has same target role name and source type extends source type of association `association A <- B ;`. So the \"inherited\" association `association E -> A ;` should be a derived association."),
            /* Finding
                .error(errorCode
                    + " The automatically introduced role name a of class A for association (E -> A) conflicts with the automatically introduced role name a for association (A <- B)."), */
            Finding
                .error(errorCode
                    + " The automatically introduced role name c of class C for association (C <- D) conflicts with the role name c for association (A (c) <-> B)."),
            Finding
                .error(errorCode
                    + " The role name a of class C for association (D <-> (a) C) conflicts with the automatically introduced role name a for association (A <- B).")
        );
   testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);

   Log.getFindings().clear();

    expectedErrors = Arrays
            .asList(
                    Finding
                            .error(errorCode
                                    + " The role name foo of class X2 for association (X2 (foo) <- Y2) conflicts with the role name foo for association (X1 (foo) <- Y1).")
            );
    testModelForErrors(MODEL_PATH_INVALID + "C4A28_2.cd", expectedErrors); // overwriting inherited associations but without read-only
  }

  @Test
  public void testInvalidRoleNames() {
    // AssocTestGenerator.generateInvalidRoleNamesTests();
    String modelName = "C4A17.cd";
    String errorCode = "0xC4A17";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc0 (A -> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc1 (A -> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc2 (A -> (RightRole) E) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc3 (A -> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc4 (B -> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc5 (B -> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc6 (B -> (RightRole) E) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc7 (B -> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc8 (I -> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc9 (I -> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc10 (I -> (RightRole) E) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc11 (I -> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc12 (A (LeftRole) <- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc13 (A (LeftRole) <- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc14 (A (LeftRole) <- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc15 (B (LeftRole) <- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc16 (B (LeftRole) <- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc17 (B (LeftRole) <- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc18 (E (LeftRole) <- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc19 (E (LeftRole) <- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc20 (E (LeftRole) <- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc21 (I (LeftRole) <- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc22 (I (LeftRole) <- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc23 (I (LeftRole) <- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc24 (A (LeftRole) <-> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc25 (A (LeftRole) <-> A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc26 (A <-> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc27 (A (LeftRole) <-> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc28 (A (LeftRole) <-> B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc29 (A <-> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc30 (A (LeftRole) <-> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc31 (A (LeftRole) <-> I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc32 (A <-> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc33 (B (LeftRole) <-> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc34 (B (LeftRole) <-> A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc35 (B <-> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc36 (B (LeftRole) <-> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc37 (B (LeftRole) <-> B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc38 (B <-> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc39 (B (LeftRole) <-> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc40 (B (LeftRole) <-> I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc41 (B <-> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc42 (I (LeftRole) <-> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc43 (I (LeftRole) <-> A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc44 (I <-> (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc45 (I (LeftRole) <-> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc46 (I (LeftRole) <-> B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc47 (I <-> (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc48 (I (LeftRole) <-> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc49 (I (LeftRole) <-> I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc50 (I <-> (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc51 (A (LeftRole) -- (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc52 (A (LeftRole) -- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc53 (A -- (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc54 (A (LeftRole) -- (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc55 (A (LeftRole) -- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc56 (A -- (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc57 (A (LeftRole) -- (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc58 (A (LeftRole) -- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc59 (A -- (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc60 (B (LeftRole) -- (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc61 (B (LeftRole) -- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc62 (B -- (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc63 (B (LeftRole) -- (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc64 (B (LeftRole) -- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc65 (B -- (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc66 (B (LeftRole) -- (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc67 (B (LeftRole) -- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc68 (B -- (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc69 (I (LeftRole) -- (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc70 (I (LeftRole) -- A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc71 (I -- (RightRole) A) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc72 (I (LeftRole) -- (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc73 (I (LeftRole) -- B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc74 (I -- (RightRole) B) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc75 (I (LeftRole) -- (RightRole) I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role LeftRole of association assoc76 (I (LeftRole) -- I) must start in lower-case."),
            Finding
                .error(errorCode
                    +
                    " Role RightRole of association assoc77 (I -- (RightRole) I) must start in lower-case.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testCompositeCardinality() {
    // see AssocTestGenerator.generateInvalidCompositeCardinalities();
    String modelName = "C4A18.cd";
    String errorCode = "0xC4A18";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(errorCode
                    +
                    " The composition comp0 (A -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp1 (A -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp2 (A -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp3 (A -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp4 (A -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp5 (A -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp6 (A -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp7 (A -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp8 (A -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp9 (A -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp10 (A -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp11 (A -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp12 (A -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp13 (A -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp14 (A -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp15 (A -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp16 (A -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp17 (A -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp18 (A -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp19 (A -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp20 (A -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp21 (A -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp22 (A -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp23 (A -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp24 (A -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp25 (A -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp26 (A -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp27 (A -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp28 (A -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp29 (A -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp30 (A -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp31 (A -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp32 (A -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp33 (A -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp34 (A -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp35 (A -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp36 (A -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp37 (A -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp38 (A -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp39 (A -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp40 (B -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp41 (B -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp42 (B -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp43 (B -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp44 (B -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp45 (B -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp46 (B -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp47 (B -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp48 (B -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp49 (B -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp50 (B -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp51 (B -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp52 (B -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp53 (B -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp54 (B -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp55 (B -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp56 (B -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp57 (B -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp58 (B -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp59 (B -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp60 (B -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp61 (B -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp62 (B -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp63 (B -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp64 (B -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp65 (B -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp66 (B -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp67 (B -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp68 (B -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp69 (B -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp70 (B -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp71 (B -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp72 (B -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp73 (B -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp74 (B -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp75 (B -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp76 (B -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp77 (B -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp78 (B -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp79 (B -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp80 (I -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp81 (I -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp82 (I -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp83 (I -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp84 (I -> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp85 (I -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp86 (I -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp87 (I -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp88 (I -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp89 (I -> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp90 (I -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp91 (I -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp92 (I -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp93 (I -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp94 (I -> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp95 (I -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp96 (I -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp97 (I -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp98 (I -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp99 (I -> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp100 (I -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp101 (I -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp102 (I -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp103 (I -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp104 (I -> E) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp105 (I -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp106 (I -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp107 (I -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp108 (I -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp109 (I -> E) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp110 (I -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp111 (I -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp112 (I -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp113 (I -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp114 (I -> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp115 (I -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp116 (I -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp117 (I -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp118 (I -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp119 (I -> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp120 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp121 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp122 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp123 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp124 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp125 (A <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp126 (A <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp127 (A <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp128 (A <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp129 (A <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp130 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp131 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp132 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp133 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp134 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp135 (A <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp136 (A <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp137 (A <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp138 (A <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp139 (A <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp140 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp141 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp142 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp143 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp144 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp145 (A <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp146 (A <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp147 (A <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp148 (A <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp149 (A <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp150 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp151 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp152 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp153 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp154 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp155 (B <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp156 (B <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp157 (B <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp158 (B <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp159 (B <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp160 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp161 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp162 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp163 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp164 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp165 (B <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp166 (B <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp167 (B <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp168 (B <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp169 (B <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp170 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp171 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp172 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp173 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp174 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp175 (B <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp176 (B <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp177 (B <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp178 (B <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp179 (B <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp180 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp181 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp182 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp183 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp184 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp185 (I <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp186 (I <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp187 (I <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp188 (I <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp189 (I <-> A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp190 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp191 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp192 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp193 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp194 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp195 (I <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp196 (I <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp197 (I <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp198 (I <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp199 (I <-> B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp200 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp201 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp202 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp203 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp204 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp205 (I <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp206 (I <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp207 (I <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp208 (I <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp209 (I <-> I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp210 (A -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp211 (A -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp212 (A -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp213 (A -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp214 (A -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp215 (A -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp216 (A -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp217 (A -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp218 (A -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp219 (A -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp220 (A -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp221 (A -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp222 (A -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp223 (A -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp224 (A -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp225 (A -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp226 (A -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp227 (A -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp228 (A -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp229 (A -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp230 (A -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp231 (A -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp232 (A -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp233 (A -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp234 (A -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp235 (A -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp236 (A -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp237 (A -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp238 (A -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp239 (A -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp240 (B -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp241 (B -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp242 (B -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp243 (B -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp244 (B -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp245 (B -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp246 (B -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp247 (B -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp248 (B -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp249 (B -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp250 (B -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp251 (B -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp252 (B -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp253 (B -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp254 (B -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp255 (B -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp256 (B -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp257 (B -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp258 (B -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp259 (B -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp260 (B -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp261 (B -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp262 (B -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp263 (B -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp264 (B -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp265 (B -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp266 (B -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp267 (B -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp268 (B -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp269 (B -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp270 (I -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp271 (I -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp272 (I -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp273 (I -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp274 (I -- A) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp275 (I -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp276 (I -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp277 (I -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp278 (I -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp279 (I -- A) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp280 (I -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp281 (I -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp282 (I -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp283 (I -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp284 (I -- B) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp285 (I -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp286 (I -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp287 (I -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp288 (I -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp289 (I -- B) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp290 (I -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp291 (I -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp292 (I -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp293 (I -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp294 (I -- I) has an invalid cardinality [1..*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp295 (I -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp296 (I -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp297 (I -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp298 (I -- I) has an invalid cardinality [*] larger than one."),
            Finding
                .error(errorCode
                    +
                    " The composition comp299 (I -- I) has an invalid cardinality [*] larger than one.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testTypedQualifiedAssoc() {
    // AssocTestGenerator.generateQualifiedAssocTests(true,
    // "LeftTypedQualifier",
    // "RightTypedQualifier", new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The type %s of the typed qualified association %s could not be found. Only external datatypes and types defined within the classdiagram may be used.";
    // String undefinedType = null;
    // if (assoc.getLeftQualifier().isPresent()) {
    // undefinedType = assoc.getLeftQualifier().get().getName();
    // }
    // else {
    // if (assoc.getRightQualifier().isPresent()) {
    // undefinedType = assoc.getRightQualifier().get().getName();
    // }
    // }
    // if (null == undefinedType) {
    // throw new RuntimeException("At least one of the qualifies must be set.");
    // }
    // return "  Finding.error(errorCode + \""
    // + String.format(msg, undefinedType,
    // CD4AFinding.printAssociation(assoc)) + "\"),";
    // }
    // });
    String modelName = "C4A19.cd";
    String errorCode = "0xC4A19";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc0 (A -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc1 (A -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc2 (A -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc3 (A -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc4 (B -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc5 (B -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc6 (B -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc7 (B -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc8 (I -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc9 (I -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc10 (I -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc11 (I -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc12 (A <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc13 (A <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc14 (A <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc15 (B <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc16 (B <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc17 (B <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc18 (E <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc19 (E <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc20 (E <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc21 (I <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc22 (I <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc23 (I <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc24 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc25 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc26 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc27 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc28 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc29 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc30 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc31 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc32 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc33 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc34 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc35 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc36 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc37 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc38 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc39 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc40 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc41 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc42 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc43 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc44 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc45 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc46 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc47 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc48 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc49 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc50 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc51 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc52 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc53 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc54 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc55 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc56 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc57 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc58 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc59 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc60 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc61 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc62 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc63 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc64 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc65 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc66 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc67 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc68 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc69 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc70 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc71 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc72 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc73 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc74 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc75 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type LeftTypedQualifier of the typed qualified association assoc76 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            Finding
                .error(
                errorCode
                    +
                    " The type RightTypedQualifier of the typed qualified association assoc77 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testQualifiedAssoc() {
    
    // AssocTestGenerator.generateQualifiedAssocTests(true,
    // "leftAttributeQualifier",
    // "rightAttributeQualifier", new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The qualified association %s expects the attribute %s to exist in the referenced type %s.";
    // String attrQualifier = null;
    // String referencedClass = null;
    // if (assoc.getLeftQualifier().isPresent()) {
    // attrQualifier = assoc.getLeftQualifier().get().getName();
    // if (assoc.getRightReferenceName() != null
    // && assoc.getRightReferenceName().getParts().size() > 0) {
    // referencedClass =
    // Iterables.getLast(assoc.getRightReferenceName().getParts());
    // }
    //
    // }
    // else {
    // if (assoc.getRightQualifier().isPresent()) {
    // attrQualifier = assoc.getRightQualifier().get().getName();
    // if (assoc.getLeftReferenceName() != null
    // && assoc.getLeftReferenceName().getParts().size() > 0) {
    // referencedClass =
    // Iterables.getLast(assoc.getLeftReferenceName().getParts());
    // }
    // }
    // }
    // if (null == attrQualifier) {
    // throw new
    // RuntimeException("At least one of the qualifiers must be set.");
    // }
    // if (null == referencedClass) {
    // throw new RuntimeException("The referenced class must be set.");
    // }
    // return "  Finding.error(errorCode + \""
    // + String.format(msg, CD4AFinding.printAssociation(assoc),
    // attrQualifier,
    // referencedClass) + "\"),";
    // }
    // });
    String modelName = "C4A20.cd";
    String errorCode = "0xC4A20";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc0 (A -> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc1 (A -> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc2 (A -> E) expects the attribute leftAttributeQualifier to exist in the referenced type E."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc3 (A -> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc4 (B -> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc5 (B -> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc6 (B -> E) expects the attribute leftAttributeQualifier to exist in the referenced type E."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc7 (B -> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc8 (I -> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc9 (I -> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc10 (I -> E) expects the attribute leftAttributeQualifier to exist in the referenced type E."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc11 (I -> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc12 (A <- A) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc13 (A <- B) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc14 (A <- I) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc15 (B <- A) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc16 (B <- B) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc17 (B <- I) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc18 (E <- A) expects the attribute rightAttributeQualifier to exist in the referenced type E."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc19 (E <- B) expects the attribute rightAttributeQualifier to exist in the referenced type E."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc20 (E <- I) expects the attribute rightAttributeQualifier to exist in the referenced type E."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc21 (I <- A) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc22 (I <- B) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc23 (I <- I) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc24 (A <-> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc25 (A <-> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc26 (A <-> A) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc27 (A <-> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc28 (A <-> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc29 (A <-> B) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc30 (A <-> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc31 (A <-> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc32 (A <-> I) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc33 (B <-> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc34 (B <-> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc35 (B <-> A) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc36 (B <-> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc37 (B <-> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc38 (B <-> B) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc39 (B <-> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc40 (B <-> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc41 (B <-> I) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc42 (I <-> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc43 (I <-> A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc44 (I <-> A) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc45 (I <-> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc46 (I <-> B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc47 (I <-> B) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc48 (I <-> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc49 (I <-> I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc50 (I <-> I) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc51 (A -- A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc52 (A -- A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc53 (A -- A) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc54 (A -- B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc55 (A -- B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc56 (A -- B) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc57 (A -- I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc58 (A -- I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc59 (A -- I) expects the attribute rightAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc60 (B -- A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc61 (B -- A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc62 (B -- A) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc63 (B -- B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc64 (B -- B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc65 (B -- B) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc66 (B -- I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc67 (B -- I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc68 (B -- I) expects the attribute rightAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc69 (I -- A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc70 (I -- A) expects the attribute leftAttributeQualifier to exist in the referenced type A."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc71 (I -- A) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc72 (I -- B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc73 (I -- B) expects the attribute leftAttributeQualifier to exist in the referenced type B."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc74 (I -- B) expects the attribute rightAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc75 (I -- I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc76 (I -- I) expects the attribute leftAttributeQualifier to exist in the referenced type I."),
            Finding
                .error(
                errorCode
                    +
                    " The qualified association assoc77 (I -- I) expects the attribute rightAttributeQualifier to exist in the referenced type I.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testQualifiedAssocInvalidQualifierPosition() {
    // consists of 2 tests: typed qualifier and attribute qualifier
    
    // typed qualifier
    
    // ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The qualifier %s of the qualified association %s is at an invalid position regarding the association's direction.";
    // String qualifier = null;
    // if (assoc.isRightToLeft()) {
    // // left qualifier must present and is at an invalid position
    // qualifier = assoc.getLeftQualifier().get().getName().get();
    // }
    // else if (assoc.isLeftToRight()) {
    // // right qualifier must present and is at an invalid position
    // qualifier = assoc.getRightQualifier().get().getName().get();
    // }
    // else {
    // throw new RuntimeException("invalid test case.");
    // }
    // return "  Finding.error(errorCode + \""
    // + String.format(msg, qualifier, CD4ACoCoHelper.printAssociation(assoc)) +
    // "\"),";
    // }
    // };
    // AssocTestGenerator.generateQualifiedAssocTests(false,
    // "String",
    // "String", errorMessagePrinter);
    String modelName = "C4A35.cd";
    String errorCode = "0xC4A35";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc0 (A -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc1 (A -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc2 (A -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc3 (A -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc4 (A -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc5 (A -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc6 (A -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc7 (A -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc8 (B -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc9 (B -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc10 (B -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc11 (B -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc12 (B -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc13 (B -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc14 (B -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc15 (B -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc16 (I -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc17 (I -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc18 (I -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc19 (I -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc20 (I -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc21 (I -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc22 (I -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc23 (I -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc24 (A <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc25 (A <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc26 (A <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc27 (A <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc28 (A <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc29 (A <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc30 (B <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc31 (B <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc32 (B <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc33 (B <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc34 (B <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc35 (B <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc36 (E <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc37 (E <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc38 (E <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc39 (E <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc40 (E <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc41 (E <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc42 (I <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc43 (I <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc44 (I <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc45 (I <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc46 (I <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier String of the qualified association assoc47 (I <- I) is at an invalid position regarding the association's direction.")
        );
    
    ASTCDCompilationUnit root = loadModel(MODEL_PATH_INVALID + modelName);
    AssociationQualifierOnCorrectSide coco = new AssociationQualifierOnCorrectSide();
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    checker.addCoCo(coco);
    checker.checkAll(root);
    assertEquals(expectedErrors.size(),
        Log.getFindings().stream().filter(f -> f.getMsg().contains(errorCode)).count());
    Assert.assertErrorMsg(expectedErrors, Log.getFindings());
    
    Log.getFindings().clear();
    
    // attribute qualifier
    modelName = "C4A35_2.cd";
    
    // ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The qualifier %s of the qualified association %s is at an invalid position regarding the association's direction.";
    // String qualifier = null;
    // if (assoc.isRightToLeft()) {
    // // left qualifier must present and is at an invalid position
    // qualifier = assoc.getLeftQualifier().get().getName().get();
    // }
    // else if (assoc.isLeftToRight()) {
    // // right qualifier must present and is at an invalid position
    // qualifier = assoc.getRightQualifier().get().getName().get();
    // }
    // else {
    // throw new RuntimeException("invalid test case.");
    // }
    // return "  Finding.error(errorCode + \""
    // + String.format(msg, qualifier, CD4ACoCoHelper.printAssociation(assoc)) +
    // "\"),";
    // }
    // };
    // AssocTestGenerator.generateQualifiedAssocTests(false,
    // "leftAttributeQualifier",
    // "rightAttributeQualifier", errorMessagePrinter);
    
    expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc0 (A -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc1 (A -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc2 (A -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc3 (A -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc4 (A -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc5 (A -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc6 (A -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc7 (A -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc8 (B -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc9 (B -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc10 (B -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc11 (B -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc12 (B -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc13 (B -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc14 (B -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc15 (B -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc16 (I -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc17 (I -> A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc18 (I -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc19 (I -> B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc20 (I -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc21 (I -> E) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc22 (I -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier rightAttributeQualifier of the qualified association assoc23 (I -> I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc24 (A <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc25 (A <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc26 (A <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc27 (A <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc28 (A <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc29 (A <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc30 (B <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc31 (B <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc32 (B <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc33 (B <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc34 (B <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc35 (B <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc36 (E <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc37 (E <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc38 (E <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc39 (E <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc40 (E <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc41 (E <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc42 (I <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc43 (I <- A) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc44 (I <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc45 (I <- B) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc46 (I <- I) is at an invalid position regarding the association's direction."),
            Finding
                .error(
                errorCode
                    +
                    " The qualifier leftAttributeQualifier of the qualified association assoc47 (I <- I) is at an invalid position regarding the association's direction.")
        );
    
    root = loadModel(MODEL_PATH_INVALID + modelName);
    checker.checkAll(root);
    assertEquals(expectedErrors.size(),
        Log.getFindings().stream().filter(f -> f.getMsg().contains(errorCode)).count());
    Assert.assertErrorMsg(expectedErrors, Log.getFindings());
  }
  
  @Test
  public void testEnumAsSource() {
    // AssocTestGenerator.generateEnumAsSource();
    String modelName = "C4A21.cd";
    String errorCode = "0xC4A21";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    +
                    " Association assoc0 (E -> A) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc1 (E -> B) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc2 (E -> E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc3 (E -> I) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc4 (A <- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc5 (B <- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc6 (E <- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc7 (I <- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc8 (A <-> E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc9 (B <-> E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc10 (E <-> A) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc11 (E <-> B) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc12 (E <-> E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc13 (E <-> I) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc14 (I <-> E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc15 (A -- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc16 (B -- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc17 (E -- A) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc18 (E -- B) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc19 (E -- E) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc20 (E -- I) is invalid, because an association's source may not be an Enumeration."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc21 (I -- E) is invalid, because an association's source may not be an Enumeration.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testExternalTypeAsSource() {
    // AssocTestGenerator.generateEnumAsSource();
    String modelName = "C4A22.cd";
    String errorCode = "0xC4A22";
    
    Collection<Finding> expectedErrors = new ArrayList<>();
    for (int i = 0; i <= 4; i++) {
      expectedErrors.add(Finding.error(errorCode + " Association assoc" + i
          + " is invalid, because an association's source may not be an external type."));
    }
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidOrderedAssocs() {
    // AssocTestGenerator.generateInvalidOrderedAssocs();
    String modelName = "C4A24.cd";
    String errorCode = "0xC4A24";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(
                errorCode
                    +
                    " Association assoc0 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc1 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc2 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc3 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc4 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc5 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc6 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc7 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc8 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc9 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc10 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc11 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc12 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc13 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc14 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc15 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc16 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc17 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc18 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc19 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc20 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc21 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc22 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc23 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc24 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc25 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc26 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc27 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc28 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc29 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc30 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc31 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc32 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc33 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc34 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc35 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc36 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc37 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc38 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc39 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc40 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc41 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc42 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc43 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc44 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc45 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc46 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc47 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc48 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc49 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc50 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc51 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc52 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc53 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc54 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc55 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc56 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc57 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc58 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc59 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc60 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc61 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc62 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc63 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc64 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc65 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc66 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc67 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc68 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc69 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc70 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc71 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc72 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc73 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc74 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc75 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc76 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc77 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc78 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc79 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc80 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc81 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc82 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc83 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc84 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc85 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc86 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc87 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc88 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc89 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc90 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc91 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc92 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc93 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc94 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc95 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc96 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc97 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc98 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc99 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc100 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc101 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc102 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc103 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc104 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc105 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc106 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc107 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc108 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc109 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc110 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc111 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc112 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc113 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc114 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc115 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc116 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc117 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc118 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc119 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc120 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc121 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc122 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc123 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc124 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc125 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc126 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc127 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc128 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc129 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc130 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc131 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc132 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc133 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc134 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc135 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc136 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc137 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc138 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc139 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc140 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc141 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc142 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc143 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc144 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc145 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc146 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc147 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc148 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc149 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc150 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc151 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc152 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc153 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc154 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc155 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc156 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc157 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc158 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc159 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc160 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc161 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc162 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc163 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc164 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc165 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc166 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc167 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc168 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc169 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc170 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc171 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc172 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc173 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc174 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc175 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc176 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc177 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc178 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc179 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc180 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc181 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc182 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc183 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc184 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc185 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc186 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc187 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc188 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc189 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc190 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc191 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc192 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc193 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc194 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc195 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc196 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc197 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc198 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc199 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc200 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc201 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc202 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc203 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc204 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc205 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc206 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc207 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc208 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc209 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc210 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc211 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc212 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc213 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc214 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc215 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc216 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc217 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc218 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc219 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc220 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc221 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc222 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc223 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc224 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc225 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc226 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc227 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc228 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc229 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc230 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc231 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc232 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc233 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc234 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc235 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc236 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc237 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc238 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc239 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc240 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc241 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc242 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc243 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc244 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc245 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc246 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc247 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc248 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc249 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc250 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc251 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc252 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc253 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc254 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc255 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc256 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc257 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc258 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc259 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc260 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc261 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc262 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc263 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc264 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc265 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc266 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc267 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc268 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc269 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc270 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc271 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc272 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc273 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc274 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc275 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc276 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc277 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc278 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc279 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc280 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc281 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc282 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc283 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc284 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc285 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc286 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc287 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc288 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc289 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc290 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc291 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc292 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc293 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc294 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc295 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc296 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc297 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc298 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc299 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc300 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc301 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc302 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc303 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc304 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc305 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc306 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc307 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc308 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc309 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc310 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc311 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc312 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc313 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc314 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc315 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc316 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc317 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc318 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc319 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc320 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc321 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc322 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc323 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc324 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc325 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc326 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc327 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc328 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc329 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc330 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc331 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc332 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc333 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc334 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc335 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc336 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc337 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc338 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc339 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc340 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc341 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc342 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc343 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc344 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc345 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc346 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc347 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc348 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc349 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc350 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc351 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc352 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc353 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc354 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc355 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc356 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc357 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc358 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc359 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc360 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc361 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc362 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc363 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc364 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc365 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc366 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc367 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc368 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc369 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc370 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc371 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc372 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc373 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc374 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc375 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc376 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc377 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc378 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc379 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc380 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc381 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc382 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc383 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc384 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc385 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc386 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc387 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc388 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc389 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc390 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc391 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc392 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc393 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc394 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc395 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc396 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc397 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc398 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc399 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc400 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc401 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc402 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc403 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc404 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc405 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc406 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc407 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc408 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc409 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc410 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc411 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc412 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc413 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc414 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc415 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc416 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc417 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc418 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc419 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc420 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc421 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc422 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc423 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc424 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc425 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc426 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc427 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc428 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc429 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc430 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc431 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc432 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc433 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc434 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc435 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc436 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc437 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc438 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc439 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc440 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc441 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc442 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc443 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc444 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc445 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc446 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc447 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc448 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc449 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc450 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc451 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc452 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc453 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc454 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc455 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc456 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc457 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc458 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc459 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc460 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc461 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc462 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc463 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc464 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc465 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc466 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc467 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc468 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc469 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc470 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc471 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc472 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc473 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc474 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc475 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc476 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc477 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc478 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc479 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc480 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc481 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc482 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc483 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc484 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc485 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc486 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc487 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc488 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc489 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc490 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc491 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc492 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc493 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc494 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc495 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc496 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc497 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc498 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc499 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc500 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc501 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc502 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc503 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc504 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc505 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc506 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc507 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc508 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc509 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc510 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc511 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc512 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc513 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc514 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc515 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc516 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc517 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc518 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc519 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc520 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc521 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc522 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc523 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc524 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc525 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc526 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            Finding
                .error(
                errorCode
                    +
                    " Association assoc527 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }

  @Ignore
  @Test
  public void testAssocTypesExist() {
    String modelName = "C4A36.cd";
    String errorCode = "0xC4A36";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Type B of association (A -- B) is unknown."),
        Finding.error(errorCode + " Type C of association (C -- A) is unknown.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }

  @Test
  public void testAssocNameUniqueReadOnly() {
    String modelName = "C4A25_2.cd";

    testModelNoErrors(MODEL_PATH_VALID + modelName);
  }
}
