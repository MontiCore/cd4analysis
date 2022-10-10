package de.monticore;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import net.sourceforge.plantuml.Log;

import java.util.List;
import java.util.Optional;

public class CDToolUtils4Merge {

  public static ASTCDCompilationUnit merge(List<String> inputs, String cdName,
      List<MergeParameter> mergeParameters) {

    Optional<ASTCDCompilationUnit> optAST;
    try {
      optAST = new MergeTool(getConfig(inputs, cdName, mergeParameters)).mergeCDs().getMergedCD();
      if (optAST.isPresent()) {
        return optAST.get();
      }
    }
    catch (MergingException e) {
      Log.error(e.getMessage());
      return null;
    }
    Log.error("Unknown Error");
    return null;
  }

  private static CDMergeConfig getConfig(List<String> inputModels, String cdName,
      List<MergeParameter> mergeParameters) {
    CDMergeConfig.Builder builder = new CDMergeConfig.Builder(true).withParam(
            MergeParameter.CHECK_ONLY)
        .withParam(MergeParameter.OUTPUT_NAME, cdName)
        .withParam(MergeParameter.LOG_SILENT);

    mergeParameters.forEach(builder::withParam);

    for (String cd : inputModels) {
      Preconditions.checkNotNull(cd);
      builder.addInputFile(cd);
    }
    return builder.build();
  }

}
