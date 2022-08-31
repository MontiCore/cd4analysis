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

public class AssociationQualifierConflict extends BaseTest {

  private static final String INPUT_MODEL_1 = "src/cdmergetest/resources/class_diagrams"
      + "/Association/qualifierConflict/A.cd";

  private static final String INPUT_MODEL_2 = "src/cdmergetest/resources/class_diagrams"
      + "/Association/qualifierConflict/B.cd";

  @Test
  public void testAssociationQualifierConflict() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    try {
      MergeResult results = cdMerger.mergeCDs();
      processResult(results);
      fail(
          "Expected exception because of  Association with same name but incompatible "
              + "cardinalities");
    }
    catch (FailFastException expected) {
      assertTrue(expected.getMessage()
          .contains("Association with same name but incompatible cardinalities"));
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
