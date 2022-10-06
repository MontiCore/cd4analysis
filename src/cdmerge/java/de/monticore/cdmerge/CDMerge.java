package de.monticore.cdmerge;

import com.google.common.base.Preconditions;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import net.sourceforge.plantuml.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CDMerge {

  private final static String TEMP_DIR = "target/temp-merge-dir";

  public static ASTCDCompilationUnit merge(List<ASTCDCompilationUnit> inputs) {

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


      MergeResult merged = null;
      MergeTool cdMergeTool = new MergeTool(getConfig());

          merged = cdMergeTool.mergeCDs(inputs);

        if (merged.mergeSuccess() && merged.getMergedCD().isPresent()){
          return merged.getMergedCD().get();
        }

      }
    catch (MergingException e) {
        Log.error(e.getMessage());
        return null;
      }
      Log.error("Unknown Error");
      return null;
    }


  private static CDMergeConfig getConfig() {
    CDMergeConfig.Builder builder = new CDMergeConfig.Builder(false).withParam(MergeParameter.AST_BASED);
    return builder.build();
  }

}
