/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;

/**
 * Tests the codes and messages of CoCos regarding associations.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AssocTests extends AbstractCoCoTest {
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForEbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  @Ignore
  @Test
  public void testAssocName() {
    String modelName = "C4A16.cd";
    String errorCode = "0xC4A16";
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Association Assoc1 must start in lower-case."),
        CoCoFinding.error(errorCode, "Association Assoc2 must start in lower-case."),
        CoCoFinding.error(errorCode, "Association Assoc3 must start in lower-case.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testInvalidRoleNames() {
    // AssocTestGenerator.generateInvalidRoleNamesTests();
    String modelName = "C4A17.cd";
    String errorCode = "0xC4A17";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc0 (A -> (RightRole) A) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc1 (A -> (RightRole) B) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc2 (A -> (RightRole) E) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc3 (A -> (RightRole) I) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc4 (B -> (RightRole) A) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc5 (B -> (RightRole) B) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc6 (B -> (RightRole) E) must start in lower-case."),
            CoCoFinding.error(errorCode,
                    "Role RightRole of association assoc7 (B -> (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc8 (I -> (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc9 (I -> (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc10 (I -> (RightRole) E) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc11 (I -> (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc12 (A (LeftRole) <- A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc13 (A (LeftRole) <- B) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc14 (A (LeftRole) <- I) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc15 (B (LeftRole) <- A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc16 (B (LeftRole) <- B) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc17 (B (LeftRole) <- I) must start in lower-case."),
                    CoCoFinding.error(errorCode,        "Role LeftRole of association assoc18 (E (LeftRole) <- A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc19 (E (LeftRole) <- B) must start in lower-case."),
                    CoCoFinding.error(errorCode,        "Role LeftRole of association assoc20 (E (LeftRole) <- I) must start in lower-case."),
                    CoCoFinding.error(errorCode,        "Role LeftRole of association assoc21 (I (LeftRole) <- A) must start in lower-case."),
                    CoCoFinding.error(errorCode,        "Role LeftRole of association assoc22 (I (LeftRole) <- B) must start in lower-case."),
                    CoCoFinding.error(errorCode,        "Role LeftRole of association assoc23 (I (LeftRole) <- I) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc24 (A (LeftRole) <-> (RightRole) A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc25 (A (LeftRole) <-> A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role RightRole of association assoc26 (A <-> (RightRole) A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc27 (A (LeftRole) <-> (RightRole) B) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc28 (A (LeftRole) <-> B) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role RightRole of association assoc29 (A <-> (RightRole) B) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc30 (A (LeftRole) <-> (RightRole) I) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc31 (A (LeftRole) <-> I) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role RightRole of association assoc32 (A <-> (RightRole) I) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc33 (B (LeftRole) <-> (RightRole) A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc34 (B (LeftRole) <-> A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role RightRole of association assoc35 (B <-> (RightRole) A) must start in lower-case."),
                    CoCoFinding.error(errorCode,                    "Role LeftRole of association assoc36 (B (LeftRole) <-> (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc37 (B (LeftRole) <-> B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc38 (B <-> (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc39 (B (LeftRole) <-> (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc40 (B (LeftRole) <-> I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc41 (B <-> (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc42 (I (LeftRole) <-> (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc43 (I (LeftRole) <-> A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc44 (I <-> (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc45 (I (LeftRole) <-> (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc46 (I (LeftRole) <-> B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc47 (I <-> (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc48 (I (LeftRole) <-> (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc49 (I (LeftRole) <-> I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc50 (I <-> (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc51 (A (LeftRole) -- (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc52 (A (LeftRole) -- A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc53 (A -- (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc54 (A (LeftRole) -- (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc55 (A (LeftRole) -- B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc56 (A -- (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc57 (A (LeftRole) -- (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc58 (A (LeftRole) -- I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc59 (A -- (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc60 (B (LeftRole) -- (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc61 (B (LeftRole) -- A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc62 (B -- (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc63 (B (LeftRole) -- (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc64 (B (LeftRole) -- B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc65 (B -- (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc66 (B (LeftRole) -- (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc67 (B (LeftRole) -- I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc68 (B -- (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc69 (I (LeftRole) -- (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc70 (I (LeftRole) -- A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc71 (I -- (RightRole) A) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc72 (I (LeftRole) -- (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc73 (I (LeftRole) -- B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc74 (I -- (RightRole) B) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc75 (I (LeftRole) -- (RightRole) I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role LeftRole of association assoc76 (I (LeftRole) -- I) must start in lower-case."),
            CoCoFinding
                .error(errorCode,
                    "Role RightRole of association assoc77 (I -- (RightRole) I) must start in lower-case.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testCompositeCardinality() {
    // AssocTestGenerator.generateInvalidCompositeCardinalities();
    String modelName = "C4A18.cd";
    String errorCode = "0xC4A18";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp0 (A -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp1 (A -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp2 (A -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp3 (A -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp4 (A -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp5 (A -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp6 (A -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp7 (A -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp8 (A -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp9 (A -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp10 (A -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp11 (A -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp12 (A -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp13 (A -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp14 (A -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp15 (A -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp16 (A -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp17 (A -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp18 (A -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp19 (A -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp20 (A -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp21 (A -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp22 (A -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp23 (A -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp24 (A -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp25 (A -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp26 (A -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp27 (A -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp28 (A -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp29 (A -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp30 (A -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp31 (A -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp32 (A -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp33 (A -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp34 (A -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp35 (A -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp36 (A -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp37 (A -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp38 (A -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp39 (A -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp40 (B -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp41 (B -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp42 (B -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp43 (B -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp44 (B -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp45 (B -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp46 (B -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp47 (B -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp48 (B -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp49 (B -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp50 (B -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp51 (B -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp52 (B -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp53 (B -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp54 (B -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp55 (B -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp56 (B -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp57 (B -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp58 (B -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp59 (B -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp60 (B -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp61 (B -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp62 (B -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp63 (B -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp64 (B -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp65 (B -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp66 (B -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp67 (B -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp68 (B -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp69 (B -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp70 (B -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp71 (B -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp72 (B -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp73 (B -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp74 (B -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp75 (B -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp76 (B -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp77 (B -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp78 (B -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp79 (B -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp80 (I -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp81 (I -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp82 (I -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp83 (I -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp84 (I -> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp85 (I -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp86 (I -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp87 (I -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp88 (I -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp89 (I -> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp90 (I -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp91 (I -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp92 (I -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp93 (I -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp94 (I -> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp95 (I -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp96 (I -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp97 (I -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp98 (I -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp99 (I -> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp100 (I -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp101 (I -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp102 (I -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp103 (I -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp104 (I -> E) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp105 (I -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp106 (I -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp107 (I -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp108 (I -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp109 (I -> E) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp110 (I -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp111 (I -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp112 (I -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp113 (I -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp114 (I -> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp115 (I -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp116 (I -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp117 (I -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp118 (I -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp119 (I -> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp120 (A <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp121 (A <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp122 (A <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp123 (A <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp124 (A <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp125 (A <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp126 (A <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp127 (A <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp128 (A <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp129 (A <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp130 (A <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp131 (A <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp132 (A <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp133 (A <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp134 (A <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp135 (A <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp136 (A <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp137 (A <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp138 (A <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp139 (A <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp140 (A <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp141 (A <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp142 (A <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp143 (A <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp144 (A <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp145 (A <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp146 (A <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp147 (A <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp148 (A <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp149 (A <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp150 (B <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp151 (B <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp152 (B <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp153 (B <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp154 (B <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp155 (B <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp156 (B <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp157 (B <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp158 (B <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp159 (B <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp160 (B <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp161 (B <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp162 (B <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp163 (B <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp164 (B <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp165 (B <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp166 (B <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp167 (B <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp168 (B <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp169 (B <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp170 (B <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp171 (B <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp172 (B <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp173 (B <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp174 (B <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp175 (B <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp176 (B <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp177 (B <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp178 (B <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp179 (B <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp180 (E <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp181 (E <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp182 (E <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp183 (E <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp184 (E <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp185 (E <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp186 (E <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp187 (E <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp188 (E <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp189 (E <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp190 (E <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp191 (E <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp192 (E <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp193 (E <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp194 (E <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp195 (E <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp196 (E <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp197 (E <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp198 (E <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp199 (E <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp200 (E <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp201 (E <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp202 (E <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp203 (E <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp204 (E <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp205 (E <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp206 (E <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp207 (E <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp208 (E <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp209 (E <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp210 (I <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp211 (I <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp212 (I <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp213 (I <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp214 (I <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp215 (I <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp216 (I <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp217 (I <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp218 (I <- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp219 (I <- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp220 (I <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp221 (I <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp222 (I <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp223 (I <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp224 (I <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp225 (I <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp226 (I <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp227 (I <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp228 (I <- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp229 (I <- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp230 (I <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp231 (I <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp232 (I <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp233 (I <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp234 (I <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp235 (I <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp236 (I <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp237 (I <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp238 (I <- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp239 (I <- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp240 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp241 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp242 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp243 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp244 (A <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp245 (A <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp246 (A <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp247 (A <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp248 (A <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp249 (A <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp250 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp251 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp252 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp253 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp254 (A <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp255 (A <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp256 (A <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp257 (A <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp258 (A <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp259 (A <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp260 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp261 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp262 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp263 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp264 (A <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp265 (A <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp266 (A <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp267 (A <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp268 (A <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp269 (A <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp270 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp271 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp272 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp273 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp274 (B <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp275 (B <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp276 (B <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp277 (B <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp278 (B <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp279 (B <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp280 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp281 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp282 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp283 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp284 (B <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp285 (B <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp286 (B <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp287 (B <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp288 (B <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp289 (B <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp290 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp291 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp292 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp293 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp294 (B <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp295 (B <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp296 (B <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp297 (B <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp298 (B <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp299 (B <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp300 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp301 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp302 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp303 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp304 (I <-> A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp305 (I <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp306 (I <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp307 (I <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp308 (I <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp309 (I <-> A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp310 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp311 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp312 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp313 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp314 (I <-> B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp315 (I <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp316 (I <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp317 (I <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp318 (I <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp319 (I <-> B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp320 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp321 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp322 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp323 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp324 (I <-> I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp325 (I <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp326 (I <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp327 (I <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp328 (I <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp329 (I <-> I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp330 (A -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp331 (A -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp332 (A -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp333 (A -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp334 (A -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp335 (A -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp336 (A -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp337 (A -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp338 (A -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp339 (A -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp340 (A -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp341 (A -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp342 (A -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp343 (A -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp344 (A -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp345 (A -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp346 (A -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp347 (A -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp348 (A -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp349 (A -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp350 (A -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp351 (A -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp352 (A -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp353 (A -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp354 (A -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp355 (A -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp356 (A -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp357 (A -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp358 (A -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp359 (A -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp360 (B -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp361 (B -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp362 (B -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp363 (B -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp364 (B -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp365 (B -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp366 (B -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp367 (B -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp368 (B -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp369 (B -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp370 (B -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp371 (B -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp372 (B -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp373 (B -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp374 (B -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp375 (B -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp376 (B -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp377 (B -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp378 (B -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp379 (B -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp380 (B -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp381 (B -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp382 (B -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp383 (B -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp384 (B -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp385 (B -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp386 (B -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp387 (B -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp388 (B -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp389 (B -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp390 (I -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp391 (I -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp392 (I -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp393 (I -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp394 (I -- A) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp395 (I -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp396 (I -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp397 (I -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp398 (I -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp399 (I -- A) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp400 (I -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp401 (I -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp402 (I -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp403 (I -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp404 (I -- B) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp405 (I -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp406 (I -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp407 (I -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp408 (I -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp409 (I -- B) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp410 (I -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp411 (I -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp412 (I -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp413 (I -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp414 (I -- I) has an invalid cardinality [1..*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp415 (I -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp416 (I -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp417 (I -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp418 (I -- I) has an invalid cardinality [*] larger than one."),
            CoCoFinding
                .error(errorCode,
                    "The composite of composition comp419 (I -- I) has an invalid cardinality [*] larger than one.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Ignore
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
    // return "  CoCoFinding.error(errorCode, \""
    // + String.format(msg, undefinedType,
    // CD4ACoCoFinding.printAssociation(assoc)) + "\"),";
    // }
    // });
    String modelName = "C4A19.cd";
    String errorCode = "0xC4A19";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc0 (A -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc1 (A -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc2 (A -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc3 (A -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc4 (B -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc5 (B -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc6 (B -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc7 (B -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc8 (I -> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc9 (I -> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc10 (I -> E) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc11 (I -> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc12 (A <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc13 (A <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc14 (A <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc15 (B <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc16 (B <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc17 (B <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc18 (E <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc19 (E <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc20 (E <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc21 (I <- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc22 (I <- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc23 (I <- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc24 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc25 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc26 (A <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc27 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc28 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc29 (A <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc30 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc31 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc32 (A <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc33 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc34 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc35 (B <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc36 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc37 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc38 (B <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc39 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc40 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc41 (B <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc42 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc43 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc44 (I <-> A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc45 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc46 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc47 (I <-> B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc48 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc49 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc50 (I <-> I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc51 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc52 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc53 (A -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc54 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc55 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc56 (A -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc57 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc58 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc59 (A -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc60 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc61 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc62 (B -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc63 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc64 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc65 (B -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc66 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc67 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc68 (B -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc69 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc70 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc71 (I -- A) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc72 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc73 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc74 (I -- B) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc75 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type LeftTypedQualifier of the typed qualified association assoc76 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used."),
            CoCoFinding
                .error(
                    errorCode,
                    "The type RightTypedQualifier of the typed qualified association assoc77 (I -- I) could not be found. Only external datatypes and types defined within the classdiagram may be used.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testQualifiedAssoc() {
    
    // AssocTestGenerator.generateQualifiedAssocTests(true,
    // "leftAttributeQualifier",
    // "rightAttributeQualifier", new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The qualified association %s expects the attribute %s to exist in the referenced class %s.";
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
    // return "  CoCoFinding.error(errorCode, \""
    // + String.format(msg, CD4ACoCoFinding.printAssociation(assoc),
    // attrQualifier,
    // referencedClass) + "\"),";
    // }
    // });
    String modelName = "C4A20.cd";
    String errorCode = "0xC4A20";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc0 (A -> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc1 (A -> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc2 (A -> E) expects the attribute leftAttributeQualifier to exist in the referenced class E."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc3 (A -> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc4 (B -> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc5 (B -> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc6 (B -> E) expects the attribute leftAttributeQualifier to exist in the referenced class E."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc7 (B -> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc8 (I -> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc9 (I -> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc10 (I -> E) expects the attribute leftAttributeQualifier to exist in the referenced class E."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc11 (I -> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc12 (A <- A) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc13 (A <- B) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc14 (A <- I) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc15 (B <- A) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc16 (B <- B) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc17 (B <- I) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc18 (E <- A) expects the attribute rightAttributeQualifier to exist in the referenced class E."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc19 (E <- B) expects the attribute rightAttributeQualifier to exist in the referenced class E."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc20 (E <- I) expects the attribute rightAttributeQualifier to exist in the referenced class E."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc21 (I <- A) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc22 (I <- B) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc23 (I <- I) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc24 (A <-> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc25 (A <-> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc26 (A <-> A) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc27 (A <-> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc28 (A <-> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc29 (A <-> B) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc30 (A <-> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc31 (A <-> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc32 (A <-> I) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc33 (B <-> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc34 (B <-> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc35 (B <-> A) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc36 (B <-> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc37 (B <-> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc38 (B <-> B) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc39 (B <-> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc40 (B <-> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc41 (B <-> I) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc42 (I <-> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc43 (I <-> A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc44 (I <-> A) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc45 (I <-> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc46 (I <-> B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc47 (I <-> B) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc48 (I <-> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc49 (I <-> I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc50 (I <-> I) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc51 (A -- A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc52 (A -- A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc53 (A -- A) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc54 (A -- B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc55 (A -- B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc56 (A -- B) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc57 (A -- I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc58 (A -- I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc59 (A -- I) expects the attribute rightAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc60 (B -- A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc61 (B -- A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc62 (B -- A) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc63 (B -- B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc64 (B -- B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc65 (B -- B) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc66 (B -- I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc67 (B -- I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc68 (B -- I) expects the attribute rightAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc69 (I -- A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc70 (I -- A) expects the attribute leftAttributeQualifier to exist in the referenced class A."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc71 (I -- A) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc72 (I -- B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc73 (I -- B) expects the attribute leftAttributeQualifier to exist in the referenced class B."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc74 (I -- B) expects the attribute rightAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc75 (I -- I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc76 (I -- I) expects the attribute leftAttributeQualifier to exist in the referenced class I."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualified association assoc77 (I -- I) expects the attribute rightAttributeQualifier to exist in the referenced class I.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testQualifiedAssocInvalidQualifierPosition() {
    String modelName = "C4A35.cd";
    String errorCode = "0xC4A35";
    
    // attribute qualifier
    
    // ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The qualifier %s of the qualified association %s is at an invalid position regarding the association's direction.";
    // String qualifier = null;
    // String referencedClass = null;
    // if (assoc.getLeftQualifier().isPresent()) {
    // qualifier = assoc.getLeftQualifier().get().getName();
    // if (assoc.getRightReferenceName() != null
    // && assoc.getRightReferenceName().getParts().size() > 0) {
    // referencedClass =
    // Iterables.getLast(assoc.getRightReferenceName().getParts());
    // }
    //
    // }
    // else {
    // if (assoc.getRightQualifier().isPresent()) {
    // qualifier = assoc.getRightQualifier().get().getName();
    // if (assoc.getLeftReferenceName() != null
    // && assoc.getLeftReferenceName().getParts().size() > 0) {
    // referencedClass =
    // Iterables.getLast(assoc.getLeftReferenceName().getParts());
    // }
    // }
    // }
    // if (null == qualifier) {
    // throw new
    // RuntimeException("At least one of the qualifiers must be set.");
    // }
    // if (null == referencedClass) {
    // throw new RuntimeException("The referenced class must be set.");
    // }
    // return "  CoCoFinding.error(errorCode, \""
    // + String.format(msg, qualifier, CD4ACoCoFinding.printAssociation(assoc),
    // referencedClass) + "\"),";
    // }
    // };
    // AssocTestGenerator.generateQualifiedAssocTests(false,
    // "leftAttributeQualifier",
    // "rightAttributeQualifier", errorMessagePrinter);
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc0 (A -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc1 (A -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc2 (A -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc3 (A -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc4 (A -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc5 (A -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc6 (A -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc7 (A -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc8 (B -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc9 (B -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc10 (B -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc11 (B -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc12 (B -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc13 (B -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc14 (B -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc15 (B -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc16 (I -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc17 (I -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc18 (I -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc19 (I -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc20 (I -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc21 (I -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier rightAttributeQualifier of the qualified association assoc22 (I -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc23 (I -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc24 (A <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc25 (A <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc26 (A <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc27 (A <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc28 (A <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc29 (A <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc30 (B <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc31 (B <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc32 (B <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc33 (B <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc34 (B <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc35 (B <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc36 (E <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc37 (E <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc38 (E <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc39 (E <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc40 (E <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc41 (E <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc42 (I <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc43 (I <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc44 (I <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc45 (I <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc46 (I <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier leftAttributeQualifier of the qualified association assoc47 (I <- I) is at an invalid position regarding the association's direction.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    
    // typed qualifier
    
    // ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "The qualifier %s of the qualified association %s is at an invalid position regarding the association's direction.";
    // String qualifier = null;
    // String referencedClass = null;
    // if (assoc.getLeftQualifier().isPresent()) {
    // qualifier = assoc.getLeftQualifier().get().getName();
    // if (assoc.getRightReferenceName() != null
    // && assoc.getRightReferenceName().getParts().size() > 0) {
    // referencedClass =
    // Iterables.getLast(assoc.getRightReferenceName().getParts());
    // }
    //
    // }
    // else {
    // if (assoc.getRightQualifier().isPresent()) {
    // qualifier = assoc.getRightQualifier().get().getName();
    // if (assoc.getLeftReferenceName() != null
    // && assoc.getLeftReferenceName().getParts().size() > 0) {
    // referencedClass =
    // Iterables.getLast(assoc.getLeftReferenceName().getParts());
    // }
    // }
    // }
    // if (null == qualifier) {
    // throw new
    // RuntimeException("At least one of the qualifiers must be set.");
    // }
    // if (null == referencedClass) {
    // throw new RuntimeException("The referenced class must be set.");
    // }
    // return "  CoCoFinding.error(errorCode, \""
    // + String.format(msg, qualifier, CD4ACoCoFinding.printAssociation(assoc),
    // referencedClass) + "\"),";
    // }
    // };
    // AssocTestGenerator.generateQualifiedAssocTests(false, "String", "String",
    // errorMessagePrinter);
    modelName = "C4A35_2.cd";
    expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc0 (A -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc1 (A -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc2 (A -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc3 (A -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc4 (A -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc5 (A -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc6 (A -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc7 (A -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc8 (B -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc9 (B -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc10 (B -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc11 (B -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc12 (B -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc13 (B -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc14 (B -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc15 (B -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc16 (I -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc17 (I -> A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc18 (I -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc19 (I -> B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc20 (I -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc21 (I -> E) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc22 (I -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc23 (I -> I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc24 (A <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc25 (A <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc26 (A <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc27 (A <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc28 (A <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc29 (A <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc30 (B <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc31 (B <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc32 (B <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc33 (B <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc34 (B <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc35 (B <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc36 (E <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc37 (E <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc38 (E <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc39 (E <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc40 (E <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc41 (E <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc42 (I <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc43 (I <- A) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc44 (I <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc45 (I <- B) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc46 (I <- I) is at an invalid position regarding the association's direction."),
            CoCoFinding
                .error(
                    errorCode,
                    "The qualifier String of the qualified association assoc47 (I <- I) is at an invalid position regarding the association's direction.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testEnumAsSource() {
    // AssocTestGenerator.generateEnumAsSource();
    String modelName = "C4A21.cd";
    String errorCode = "0xC4A21";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc0 (E -> A) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc1 (E -> B) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc2 (E -> E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc3 (E -> I) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc4 (A <- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc5 (B <- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc6 (E <- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc7 (I <- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc8 (A <-> E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc9 (B <-> E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc10 (E <-> A) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc11 (E <-> B) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc12 (E <-> E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc13 (E <-> I) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc14 (I <-> E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc15 (A -- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc16 (B -- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc17 (E -- A) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc18 (E -- B) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc19 (E -- E) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc20 (E -- I) is invalid, because an association's source may not be an Enumeration."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc21 (I -- E) is invalid, because an association's source may not be an Enumeration.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testExternalTypeAsSource() {
    // AssocTestGenerator.generateEnumAsSource();
    String modelName = "C4A22.cd";
    String errorCode = "0xC4A22";
    
    Collection<CoCoFinding> expectedErrors = new ArrayList<>();
    for (int i = 0; i <= 4; i++) {
      expectedErrors.add(CoCoFinding.error(errorCode, "Association assoc" + i
          + " is invalid, because an association's source may not be an external type."));
    }
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Ignore
  @Test
  public void testInvalidOrderedAssocs() {
    // AssocTestGenerator.generateInvalidOrderedAssocs();
    String modelName = "C4A24.cd";
    String errorCode = "0xC4A24";
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc0 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc1 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc2 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc3 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc4 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc5 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc6 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc7 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc8 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc9 (A -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc10 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc11 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc12 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc13 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc14 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc15 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc16 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc17 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc18 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc19 (A -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc20 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc21 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc22 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc23 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc24 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc25 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc26 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc27 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc28 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc29 (A -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc30 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc31 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc32 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc33 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc34 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc35 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc36 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc37 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc38 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc39 (A -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc40 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc41 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc42 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc43 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc44 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc45 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc46 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc47 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc48 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc49 (B -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc50 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc51 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc52 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc53 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc54 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc55 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc56 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc57 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc58 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc59 (B -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc60 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc61 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc62 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc63 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc64 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc65 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc66 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc67 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc68 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc69 (B -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc70 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc71 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc72 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc73 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc74 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc75 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc76 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc77 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc78 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc79 (B -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc80 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc81 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc82 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc83 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc84 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc85 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc86 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc87 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc88 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc89 (I -> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc90 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc91 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc92 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc93 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc94 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc95 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc96 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc97 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc98 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc99 (I -> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc100 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc101 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc102 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc103 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc104 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc105 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc106 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc107 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc108 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc109 (I -> E) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc110 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc111 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc112 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc113 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc114 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc115 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc116 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc117 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc118 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc119 (I -> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc120 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc121 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc122 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc123 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc124 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc125 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc126 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc127 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc128 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc129 (A <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc130 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc131 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc132 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc133 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc134 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc135 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc136 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc137 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc138 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc139 (A <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc140 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc141 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc142 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc143 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc144 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc145 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc146 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc147 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc148 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc149 (A <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc150 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc151 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc152 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc153 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc154 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc155 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc156 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc157 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc158 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc159 (B <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc160 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc161 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc162 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc163 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc164 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc165 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc166 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc167 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc168 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc169 (B <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc170 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc171 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc172 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc173 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc174 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc175 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc176 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc177 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc178 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc179 (B <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc180 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc181 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc182 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc183 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc184 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc185 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc186 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc187 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc188 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc189 (E <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc190 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc191 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc192 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc193 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc194 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc195 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc196 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc197 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc198 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc199 (E <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc200 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc201 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc202 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc203 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc204 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc205 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc206 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc207 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc208 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc209 (E <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc210 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc211 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc212 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc213 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc214 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc215 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc216 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc217 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc218 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc219 (I <- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc220 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc221 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc222 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc223 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc224 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc225 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc226 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc227 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc228 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc229 (I <- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc230 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc231 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc232 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc233 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc234 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc235 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc236 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc237 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc238 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc239 (I <- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc240 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc241 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc242 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc243 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc244 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc245 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc246 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc247 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc248 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc249 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc250 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc251 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc252 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc253 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc254 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc255 (A <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc256 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc257 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc258 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc259 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc260 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc261 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc262 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc263 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc264 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc265 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc266 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc267 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc268 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc269 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc270 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc271 (A <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc272 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc273 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc274 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc275 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc276 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc277 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc278 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc279 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc280 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc281 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc282 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc283 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc284 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc285 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc286 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc287 (A <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc288 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc289 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc290 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc291 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc292 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc293 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc294 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc295 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc296 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc297 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc298 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc299 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc300 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc301 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc302 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc303 (B <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc304 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc305 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc306 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc307 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc308 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc309 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc310 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc311 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc312 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc313 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc314 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc315 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc316 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc317 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc318 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc319 (B <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc320 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc321 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc322 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc323 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc324 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc325 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc326 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc327 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc328 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc329 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc330 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc331 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc332 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc333 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc334 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc335 (B <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc336 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc337 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc338 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc339 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc340 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc341 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc342 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc343 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc344 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc345 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc346 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc347 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc348 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc349 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc350 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc351 (I <-> A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc352 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc353 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc354 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc355 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc356 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc357 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc358 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc359 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc360 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc361 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc362 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc363 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc364 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc365 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc366 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc367 (I <-> B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc368 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc369 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc370 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc371 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc372 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc373 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc374 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc375 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc376 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc377 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc378 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc379 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc380 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc381 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc382 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc383 (I <-> I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc384 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc385 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc386 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc387 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc388 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc389 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc390 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc391 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc392 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc393 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc394 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc395 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc396 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc397 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc398 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc399 (A -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc400 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc401 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc402 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc403 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc404 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc405 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc406 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc407 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc408 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc409 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc410 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc411 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc412 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc413 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc414 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc415 (A -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc416 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc417 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc418 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc419 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc420 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc421 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc422 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc423 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc424 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc425 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc426 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc427 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc428 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc429 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc430 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc431 (A -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc432 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc433 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc434 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc435 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc436 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc437 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc438 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc439 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc440 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc441 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc442 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc443 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc444 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc445 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc446 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc447 (B -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc448 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc449 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc450 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc451 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc452 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc453 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc454 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc455 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc456 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc457 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc458 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc459 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc460 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc461 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc462 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc463 (B -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc464 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc465 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc466 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc467 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc468 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc469 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc470 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc471 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc472 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc473 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc474 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc475 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc476 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc477 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc478 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc479 (B -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc480 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc481 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc482 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc483 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc484 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc485 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc486 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc487 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc488 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc489 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc490 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc491 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc492 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc493 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc494 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc495 (I -- A) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc496 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc497 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc498 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc499 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc500 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc501 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc502 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc503 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc504 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc505 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc506 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc507 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc508 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc509 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc510 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc511 (I -- B) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc512 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc513 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc514 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc515 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc516 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc517 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc518 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc519 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc520 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc521 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc522 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc523 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc524 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc525 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc526 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association assoc527 (I -- I) is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
