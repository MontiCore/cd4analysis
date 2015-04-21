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
  
  @Ignore
  @Test
  public void testInvalidInitializationOfDerivedAttr() {
    String modelName = "C4A34.cd";
    String errorCode = "0xC4A34";
    
    /* (Hinweis fuer Implementierung: UMLP verbietet das, da UMLP eine
     * Derivation-Rule oder eine Initialisierung erwartet - in CD4A gibt es
     * derivation-rules aber nicht und es soll eine leere Methode generiert
     * werden, die dann ueberschrieben werden kann). Die entsprechende UMLP CoCo
     * soll in CD4A geloescht werden. */
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<CoCoFinding> expectedErrors = Arrays
        .asList(
            CoCoFinding
                .error(errorCode,
                    " Invalid initialization of the derived attribute a. Derived attributes may not be initialized."),
            CoCoFinding
                .error(errorCode,
                    " Invalid initialization of the derived attribute b. Derived attributes may not be initialized."),
            CoCoFinding
                .error(errorCode,
                    " Invalid initialization of the derived attribute c. Derived attributes may not be initialized.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
