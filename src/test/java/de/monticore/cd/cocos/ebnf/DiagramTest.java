/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cocos.AbstractCoCoTest;
import de.monticore.cd.CD4ACoCos;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

/**
 * Tests the CoCos for diagrams in general.
 *
 * @author Robert Heim
 */
public class DiagramTest extends AbstractCoCoTest {
  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForEbnfCoCos();
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
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";
  
  @Test
  public void testDiagramName() {
    String modelName = "c4A01.cd";
    String errorCode = "0xC4A01";
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " First character of the diagram name c4A01 must be upper-case.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testFileNameEqualsModelName() {
    
    String modelName = "C4A02.cd";
    String errorCode = "0xC4A02";
    
    modelName = "C4A02.cd";
    errorCode = "0xC4A02";
    String expectedFileName = Paths.get("src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/C4A02.cd").toString();
    Collection<Finding> expectedErrors = Arrays
        .asList(
        Finding
            .error(
            errorCode
                + " The name of the diagram C4A02Invalid is not identical to the name of the file "
                + expectedFileName + " (without its fileextension).")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void testPackageNameValid() {
    String modelName = "C4A03.cd";
    String errorCode = "0xC4A03";

    String packageName = "de.monticore.umlcd4a.cocos.ebnf";
    Collection<Finding> expectedErrors = Arrays.asList(
            Finding.error(errorCode + " The package declaration " + packageName + " of the diagram must not differ from the "
                    + "package of the diagram file.")
            );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
