/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Preconditions;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.FailFastException;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class AttributeConflictDifferentTypes extends BaseTest {

  private static final String INPUT_MODEL_1 =
      "src/cdmergetest/resources/class_diagrams/Attribute" + "/conflictDifferentTypes/A.cd";

  private static final String INPUT_MODEL_2 =
      "src/cdmergetest/resources/class_diagrams/Attribute" + "/conflictDifferentTypes/B.cd";

  @Test
  public void testAttributeConflictDifferentTypes() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    try {
      MergeResult results = cdMerger.mergeCDs();
      processResult(results);
      fail("Exception expected: Attribute birthday is defined multiple times in class Person");
    } catch (FailFastException expected) {
      assertTrue(
          expected
              .getMessage()
              .contains(
                  "Name of the field or role 'birthday' is not unique for the class 'Person'"));
    }
  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder =
        getConfigBuilder()
            .withParam(MergeParameter.CHECK_ONLY, MergeParameter.ON)
            .withParam(MergeParameter.FAIL_FAST)
            .withParam(MergeParameter.OUTPUT_NAME, "mergedCD");

    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }
}
