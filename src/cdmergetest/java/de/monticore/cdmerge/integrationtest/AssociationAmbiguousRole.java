/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import com.google.common.base.Preconditions;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class AssociationAmbiguousRole extends BaseTest {

  private static final String INPUT_MODEL_1 = "src/cdmergetest/resources/class_diagrams"
      + "/Association/ambiguousRole/A.cd";

  private static final String INPUT_MODEL_2 = "src/cdmergetest/resources/class_diagrams"
      + "/Association/ambiguousRole/B.cd";

  @Test
  public void testAssociationAmbiguousRole() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    try {
      final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
      MergeResult res = cdMerger.mergeCDs();
      if (!res.getLog().hasLogWithMessageContaining("0xC4A29")) {
        fail("Expected warning because of ambiguous role");
      }
    }
    catch (RuntimeException expected) {
      if (!expected.getMessage().contains("0xC4A29")) {
        fail("Expected warning because of ambiguous role");
      }
    }

  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder = getConfigBuilder().withParam(MergeParameter.CHECK_ONLY)
        .withParam(MergeParameter.FAIL_FAST);
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }

}
