/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cocos.AbstractCoCoTest;
import de.monticore.cd.CD4ACoCos;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests the CoCos that restrict ASTCDAttribute to match the EBNF grammar.
 *
 */
public class AttributeTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/invalid/";
  
  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcg2EbnfCoCos();
  }
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void modifierOnlyDerivedCoCoTest() {
    String modelName = "C4A64.cd";
    String errorCode = "0xC4A64";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Attribute a has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute b has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute c has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute d has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute e has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute f has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute g has invalid modifiers. Only \"/\" is permitted."),
        Finding.error(errorCode + " Attribute h has invalid modifiers. Only \"/\" is permitted.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
}
