/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;

/**
 * Tests the CoCos that restrict ASTCDAssociation to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class AssociationTests extends AbstractCoCoTest {
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/";
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcg2EbnfCoCos();
  }
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.mcg2ebnf.AttributeTests
   */
  public AssociationTests() {
    super(MODEL_PATH);
  }
  
  @BeforeClass
  public static void init() {
    CoCoLog.setDelegateToLog(false);
  }
  
  @Before
  public void setUp() {
    CoCoLog.getFindings().clear();
  }
  
  @Test
  public void noStereotypesCoCoTest() {
    String modelName = "C4A71.cd";
    String errorCode = "0xC4A71";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding.error(errorCode,
                "Association assoc1 (A -> B) may not have stereotypes."),
            CoCoFinding.error(errorCode,
                "Association (A <- B) may not have stereotypes."),
            CoCoFinding.error(errorCode,
                "Association (A <-> B) may not have stereotypes."),
            CoCoFinding.error(errorCode,
                "Association (A -- B) may not have stereotypes.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }

  @Test
  public void associationEndModifierRestrictionCoCoTest() {
    String modelName = "C4A72.cd";
    String errorCode = "0xC4A72";
    
    testModelNoErrors("valid/" + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(
                    errorCode,
                    "Association ends of association assoc1 (A -> B) may not have modifieres except the stereotype <<ordered>>."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association ends of association (A -> B) may not have modifieres except the stereotype <<ordered>>."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association ends of association (A -> B) may not have modifieres except the stereotype <<ordered>>."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association ends of association (A -> B) may not have modifieres except the stereotype <<ordered>>."),
            CoCoFinding
                .error(
                    errorCode,
                    "Association ends of association (A -> B) may not have modifieres except the stereotype <<ordered>>.")
        );
    
    testModelForErrors("invalid/" + modelName, expectedErrors);
  }
}
