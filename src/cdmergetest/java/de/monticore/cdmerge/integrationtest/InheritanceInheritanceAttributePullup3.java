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
import de.monticore.cdmerge.util.CDUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InheritanceInheritanceAttributePullup3 extends BaseTest {

  private static final String INPUT_MODEL_1 = "src/cdmergetest/resources/class_diagrams"
      + "/Inheritance/inheritanceAttributePullup3/A.cd";

  private static final String INPUT_MODEL_2 = "src/cdmergetest/resources/class_diagrams"
      + "/Inheritance/inheritanceAttributePullup3/B.cd";

  private static final String EXPECTED = "src/cdmergetest/resources/class_diagrams/Inheritance"
      + "/inheritanceAttributePullup3/mergedCD.cd";

  @Test
  public void testInheritanceInheritanceAttributePullup3() throws IOException, MergingException {
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_1);
    inputModels.add(INPUT_MODEL_2);
    final ASTCDCompilationUnit expectedCD = loadModel(Paths.get(EXPECTED));
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    MergeResult results = cdMerger.mergeCDs();
    processResult(results);
    org.junit.Assert.assertTrue(
        parseCD(CDUtils.prettyPrint(results.getMergedCD().get())).deepEquals(expectedCD, false));

  }

  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder = getConfigBuilder().withParam(MergeParameter.CHECK_ONLY,
        MergeParameter.ON).withParam(MergeParameter.OUTPUT_NAME, "mergedCD");
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }

}
