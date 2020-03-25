/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.all;

import de.monticore.cd.cocos.AbstractCoCoTest;
import de.monticore.cd.CD4ACoCos;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests unique association names.
 *
 */
public class AssocTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/";

  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForAllCoCos();
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
  
  /**
   * Tests that the build-in types can be used without importing them.
   */
  @Ignore("Associatons name not unique, diagram should fail!")
  @Test
  public void assocSameNameCoCoTest() {
    Log.enableFailQuick(false);
    String errorCode = "0xC4A26";
    Collection<Finding> expectedErrors = Arrays
            .asList(
                    Finding.error(errorCode
                                    + " Association dimension is defined multiple times."),
                    Finding.error(errorCode
                            + " Association dimension is defined multiple times.")
            );
    String modelName = "MergedCD.cd";
    testModelForErrors(MODEL_PATH_VALID + modelName, expectedErrors);
  }
}
