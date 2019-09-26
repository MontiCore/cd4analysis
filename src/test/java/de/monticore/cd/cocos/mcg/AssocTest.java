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
 * Tests the codes and messages of CoCos regarding associations.
 *
 * @author (last commit) $Author$
 * @since TODO: add version number
 */
public class AssocTest extends AbstractCoCoTest {
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
  public void testAssocInvalidModifier() {
    String modelName = "C4A57.cd";
    String errorCode = "0xC4A57";
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(errorCode
                    + " The modifier abstract can not be used for associations at association Assoc1 (A -> B)."),
            Finding
                .error(errorCode
                    + " The modifier abstract can not be used for associations at association Assoc2 (A -> B).")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
}
