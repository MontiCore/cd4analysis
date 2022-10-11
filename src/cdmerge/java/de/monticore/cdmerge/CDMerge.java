package de.monticore.cdmerge;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import net.sourceforge.plantuml.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CDMerge {

  public static ASTCDCompilationUnit merge(Set<ASTCDCompilationUnit> inputs) {

    Optional<ASTCDCompilationUnit> optAST;

    List<ASTCDCompilationUnit> inputList = new ArrayList<>(inputs);

    if (inputs.size() < 2) {
      optAST = inputs.stream().findAny();
      if (optAST.isPresent()) {
        return optAST.get();
      }
      Log.error("No Input-CD!");
      return null;
    }

    try {
      optAST = new MergeTool(getConfig(inputList)).mergeCDs().getMergedCD();

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

  private static CDMergeConfig getConfig(List<ASTCDCompilationUnit> inputModels) {
    CDMergeConfig.Builder builder = new CDMergeConfig.Builder(false).withParam(
            MergeParameter.CHECK_ONLY)
        .withParam(MergeParameter.AST_BASED)
        .withParam(MergeParameter.LOG_SILENT)
        .withParam(MergeParameter.OUTPUT_NAME, "Merge");

    for (ASTCDCompilationUnit cd : inputModels) {
      Preconditions.checkNotNull(cd);
      builder.addInputAST(cd);
    }
    return builder.build();
  }

}
