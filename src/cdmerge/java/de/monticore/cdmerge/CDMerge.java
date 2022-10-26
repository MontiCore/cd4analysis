package de.monticore.cdmerge;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CDMerge {

  public static ASTCDCompilationUnit merge(Set<ASTCDCompilationUnit> inputs){
    return merge(inputs,"Merge",new HashSet<>());
  }

  public static ASTCDCompilationUnit merge(Set<ASTCDCompilationUnit> inputs, String name,
      Set<MergeParameter> mergeParameters) {

    Optional<ASTCDCompilationUnit> optAST;

    if (inputs.size() < 2) {
      optAST = inputs.stream().findAny();
      if (optAST.isPresent()) {
        return optAST.get();
      }
      Log.error("No Input-CD!");
      return null;
    }

    try {
      optAST = new MergeTool(getConfig(inputs, name, mergeParameters)).mergeCDs().getMergedCD();

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

  private static CDMergeConfig getConfig(Set<ASTCDCompilationUnit> inputModels, String name,
      Set<MergeParameter> mergeParameters) {
    CDMergeConfig.Builder builder = new CDMergeConfig.Builder(false).withParam(
            MergeParameter.CHECK_ONLY)
        .withParam(MergeParameter.AST_BASED)
        .withParam(MergeParameter.LOG_SILENT)
        .withParam(MergeParameter.OUTPUT_NAME, name);

    mergeParameters.forEach(builder::withParam);

    for (ASTCDCompilationUnit cd : inputModels) {
      Preconditions.checkNotNull(cd);
      builder.addInputAST(cd);
    }
    return builder.build();
  }

}
