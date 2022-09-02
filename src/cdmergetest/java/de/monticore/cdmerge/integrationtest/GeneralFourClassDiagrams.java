/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import de.monticore.cdmerge.util.CDUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class GeneralFourClassDiagrams extends BaseTest {

  private static final String INPUT_MODEL_1 = "src/cdmergetest/resources/class_diagrams/General"
      + "/four_classdiagrams/A.cd";

  private static final String INPUT_MODEL_2 = "src/cdmergetest/resources/class_diagrams/General"
      + "/four_classdiagrams/B.cd";

  private static final String INPUT_MODEL_3 = "src/cdmergetest/resources/class_diagrams/General"
      + "/four_classdiagrams/C.cd";

  private static final String INPUT_MODEL_4 = "src/cdmergetest/resources/class_diagrams/General"
      + "/four_classdiagrams/D.cd";

  private static final String EXPECTED = "src/cdmergetest/resources/class_diagrams/General"
      + "/four_classdiagrams/mergedCD.cd";

  @Test
  public void testFourCDs() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    inputModels.add(INPUT_MODEL_3);
    inputModels.add(INPUT_MODEL_4);
    final ASTCDCompilationUnit expectedCD = loadModel(Paths.get(EXPECTED));
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    try {
      MergeResult results = cdMerger.mergeCDs();
      processResult(results);
      org.junit.Assert.assertTrue(
          parseCD(CDUtils.prettyPrint(results.getMergedCD().get())).deepEquals(expectedCD, false));

    }
    catch (MergingException e) {
      if (e.getLog().isPresent()) {
        fail("Merge Unsuccesful " + e.getMessage() + "\n" + e.getLog()
            .get()
            .toString(ErrorLevel.ERROR));
      }
      else {
        fail("Merge Unsuccesful " + e.getMessage());
      }
    }

  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder = getConfigBuilder().withParam(MergeParameter.CHECK_ONLY,
            MergeParameter.ON)
        .withParam(MergeParameter.OUTPUT_NAME, "mergedCD")
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES, MergeParameter.ON)
        // FIXME Tool works but CoCo Fails
        .withParam(MergeParameter.DISABLE_CONTEXT_CONDITIONS);
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }

}
