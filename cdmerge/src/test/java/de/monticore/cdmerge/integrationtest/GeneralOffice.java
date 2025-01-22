/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import de.monticore.cdmerge.util.CDMergeUtils;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class GeneralOffice extends BaseTest {

  private static final String INPUT_MODEL_1 =
      "src/test/resources/class_diagrams/General" + "/office/A.cd";

  private static final String INPUT_MODEL_2 =
      "src/test/resources/class_diagrams/General" + "/office/B.cd";

  private static final String EXPECTED =
      "src/test/resources/class_diagrams/General/office" + "/mergedCD.cd";

  @Test
  public void testOffice() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    final ASTCDCompilationUnit expectedCD = loadModel(Paths.get(EXPECTED));
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    MergeResult result = cdMerger.mergeCDs();
    processResult(result);
    org.junit.Assert.assertTrue(
        parseCD(CDMergeUtils.prettyPrint(result.getMergedCD().get()))
            .deepEquals(expectedCD, false));
  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder =
        getConfigBuilder()
            .withParam(MergeParameter.CHECK_ONLY, MergeParameter.ON)
            .withParam(MergeParameter.OUTPUT_NAME, "mergedCD")
            .withParam(MergeParameter.MERGE_HETEROGENEOUS_TYPES)
            .withParam(MergeParameter.DISABLE_CONTEXT_CONDITIONS);

    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }
}
