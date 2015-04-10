/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

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
 * Tests the CoCos for diagrams in general.
 *
 * @author Robert Heim
 */
public class DiagramTests extends AbstractCoCoTest {
  /**
   * Constructor for de.monticore.umlcd4a.cocos.ebnf.DiagramTests
   */
  public DiagramTests() {
    super(MODEL_PATH);
  }
  
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
  
  private static String MODEL_PATH = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
  @Test
  public void testDiagramName() {
    String modelName = "c4A01.cd";
    String errorCode = "0xC4A01";
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode,
            "First character of the diagram name c4A01 must be upper-case.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Ignore("TODO RH we cannot check this yet because the file name is not part of the ast/st")
  @Test
  public void testFileNameEqualsModelName() {
    
    String modelName = "C4A02.cd";
    String errorCode = "0xC4A02";
    
    modelName = "C4A02.cd";
    errorCode = "0xC4A02";
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
        CoCoFinding
            .error(
                errorCode,
                "The name of the diagram C4A02Invalid is not identical to the name of the file C4A02 (without its fileextension).")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  public void testNames() {
    String modelName = "C4A03.cd";
    String errorCode = "0xC4A03";
    
    Collection<CoCoFinding> expectedErrors = Arrays.asList(
        CoCoFinding.error(errorCode, "Name DAO is reserved for internal use."),
        CoCoFinding.error(errorCode, "Name Factory is reserved for internal use.")
        );
    testModelForErrors(modelName, expectedErrors);
  }
  
}
