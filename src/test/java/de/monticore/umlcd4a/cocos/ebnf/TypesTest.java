/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

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
  
  private static class NoSystemExit extends Log {
    public static final void init() {
      setLog(new NoSystemExit());
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#doError(java.lang.String)
     */
    @Override
    protected void doError(String msg) {
      // prevent output
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#doError(java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    protected void doError(String msg, Throwable t) {
      // prevent output
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#doGetErrorCount()
     */
    @Override
    protected int doGetErrorCount() {
      // always prevents system exit
      return 0;
    }
  }
  
  @Test
  public void testUnparameterizedGenerics() {
    // Note that a generic with no type parameter results in a parse error and
    // hence there exists no explicit CoCo.
    
    String modelName = "C4A30.cd";
    String errorCode = "0xC4A30";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    // NOTE: the invalid models produce parse error !
    
    // prevent junit to system.exit... :)
    
    NoSystemExit.init();
    
    Collection<CoCoFinding> expectedErrors = new ArrayList<>();
    try {
      testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
      fail("Expected a parse error for model " + MODEL_PATH_INVALID + modelName);
    }
    catch (Exception e) {
      assertEquals("Error during loading of model " + MODEL_PATH_INVALID + modelName + ".",
          e.getMessage());
    }
    finally {
      // "restore" Logger? (actually we do not know which logger was set
      // before...)
      Slf4jLog.init();
    }
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
