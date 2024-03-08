/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import static org.junit.Assert.fail;

import com.google.common.base.Preconditions;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class AssociationCompositionDesignissue extends BaseTest {

  private static final String INPUT_MODEL_1 =
      "src/test/resources/class_diagrams" + "/Association/compositionDesignissue/A.cd";

  private static final String INPUT_MODEL_2 =
      "src/test/resources/class_diagrams" + "/Association/compositionDesignissue/B.cd";

  @Test
  public void testAssociationCompositionDesignissue() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));

    MergeResult result = cdMerger.mergeCDs();
    processResult(result);
    if (result.getMaxErrorLevel() != ErrorLevel.DESIGN_ISSUE) {
      fail("Expected design issue because of ambiguous composition");
    }
  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder =
        getConfigBuilder()
            .withParam(MergeParameter.CHECK_ONLY)
            .withParam(MergeParameter.OUTPUT_NAME, "mergedCD");
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }
}
