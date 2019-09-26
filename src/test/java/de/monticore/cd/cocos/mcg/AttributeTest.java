/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg;

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
 * Tests the attribute cocos.
 *
 * @author Robert Heim
 */
public class AttributeTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg/invalid/";
  
  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcgCoCos();
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
  public void attributesNotAbstractCoCoTest() {
    String modelName = "C4A52.cd";
    String errorCode = "0xC4A52";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Attribute a may not be abstract."),
        Finding.error(errorCode + " Attribute b may not be abstract."),
        Finding.error(errorCode + " Attribute c may not be abstract."),
        Finding.error(errorCode + " Attribute d may not be abstract.")
        );
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
