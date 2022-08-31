/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import com.google.common.base.Preconditions;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AssociativityTest extends BaseTest {

  private static final String INPUT_MODEL_1 = "src/cdmergetest/resources/class_diagrams"
      + "/notAssociative/A.cd";

  private static final String INPUT_MODEL_2 = "src/cdmergetest/resources/class_diagrams"
      + "/notAssociative/B.cd";

  private static final String INPUT_MODEL_3 = "src/cdmergetest/resources/class_diagrams"
      + "/notAssociative/C.cd";

  @Test
  public void testAssociationNonAssociative() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    inputModels.add(INPUT_MODEL_3);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    try {
      cdMerger.mergeCDs();
      fail("Expected Merging Exception due to non associative input CDs");
    }
    catch (MergingException e) {
      assertTrue(e.getMessage().contains("Input CDs are NOT associative"));
    }

  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder = getConfigBuilder().withParam(MergeParameter.CHECK_ONLY,
            MergeParameter.ON)
        .withParam(MergeParameter.ASSERT_ASSOCIATIVITY)
        .withParam(MergeParameter.OUTPUT_NAME, "mergedCD");
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }

}
