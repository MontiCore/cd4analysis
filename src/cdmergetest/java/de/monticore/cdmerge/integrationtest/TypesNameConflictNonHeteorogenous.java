/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import com.google.common.base.Preconditions;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.FailFastException;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TypesNameConflictNonHeteorogenous extends BaseTest {

  private static final String INPUT_MODEL_1 = "src/cdmergetest/resources/class_diagrams/Types"
      + "/NameConflictNonHeteorogenous/A.cd";

  private static final String INPUT_MODEL_2 = "src/cdmergetest/resources/class_diagrams/Types"
      + "/NameConflictNonHeteorogenous/B.cd";

  @Test
  public void testTypesTypeNameConflictNonHeteorogenous() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    try {
      MergeResult result = cdMerger.mergeCDs();
      processResult(result);
      fail("Exception expected: 'Color' is defined several times");
    }
    catch (FailFastException expected) {
      assertTrue(expected.getMessage().contains("The name Color is used several times"));
    }

  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder = getConfigBuilder().withParam(MergeParameter.CHECK_ONLY,
            MergeParameter.ON)
        .withParam(MergeParameter.FAIL_FAST)
        .withParam(MergeParameter.OUTPUT_NAME, "mergedCD");
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }

}
