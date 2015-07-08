/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;

/**
 * Tests CoCos dealing with types.
 *
 * @author Robert Heim
 */
public class TypesTest extends AbstractCoCoTest {
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
  
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
  @Test
  public void testNestedGeneric() {
    String modelName = "C4A29.cd";
    String errorCode = "0xC4A29";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    "Invalid type parameter List<Optional<String>>. Generic types may not be nested."),
            CoCoFinding
                .error(errorCode,
                    "Invalid type parameter Optional<List<String>>. Generic types may not be nested.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testUnparameterizedGenerics() throws RecognitionException, IOException {
    String modelName = "C4A30.cd";
    String errorCode = "0xC4A30";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    "Generic type List has no type-parameter. References to generic types must be parametrized."),
            CoCoFinding
                .error(errorCode,
                    "Generic type Optional has no type-parameter. References to generic types must be parametrized."),
            CoCoFinding
                .error(errorCode,
                    "Generic type Set has no type-parameter. References to generic types must be parametrized.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    
    // some further tests
    Optional<ASTSimpleReferenceType> type = CD4AnalysisParserFactory
        .createSimpleReferenceTypeMCParser().parseString("a.b.C<S>");
    assertTrue(type.isPresent());
    CoCoLog.getFindings().clear();
    CoCoLog.setDelegateToLog(false);
    // check the coco
    new GenericTypeHasParameters().check(type.get());
    // asert expected error
    assertEquals(0, CoCoLog.getFindings().size());
    
    type = CD4AnalysisParserFactory.createSimpleReferenceTypeMCParser().parseString("a.b.C<>");
    assertTrue(type.isPresent());
    new GenericTypeHasParameters().check(type.get());
    // asert expected error
    assertEquals(1, CoCoLog.getFindings().size());
    
    assertEquals(1, CoCoLog.getFindings().stream().filter(f -> f.buildMsg().contains(errorCode))
        .count());
  }
  
  @Test
  public void testInvalidInitializationOfDerivedAttr() {
    String modelName = "C4A34.cd";
    String errorCode = "0xC4A34";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    "Invalid initialization of the derived attribute a. Derived attributes may not be initialized."),
            CoCoFinding
                .error(errorCode,
                    "Invalid initialization of the derived attribute b. Derived attributes may not be initialized."),
            CoCoFinding
                .error(errorCode,
                    "Invalid initialization of the derived attribute c. Derived attributes may not be initialized.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
