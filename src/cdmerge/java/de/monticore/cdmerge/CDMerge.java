package de.monticore.cdmerge;

import com.google.common.base.Preconditions;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
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

  public static ASTCDCompilationUnit merge(Set<ASTCDCompilationUnit> inputs) {

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

      List<String> cdPaths = new ArrayList<>();

      int index = 0;
      for (ASTCDCompilationUnit cd : inputs) {
        index++;
        Preconditions.checkNotNull(cd);
        cdPaths.add(saveCD2File(cd, index + "_"));
      }

      optAST = new MergeTool(getConfig(cdPaths)).mergeCDs().getMergedCD();

      FileUtils.forceDelete(new File("target/temp-merge-dir"));

      if (optAST.isPresent()) {
        return optAST.get();
      }

    }
    catch (IOException | MergingException e) {
      Log.error(e.getMessage());
      return null;
    }
    Log.error("Unknown Error");
    return null;
  }

  private static String saveCD2File(ASTCDCompilationUnit cd, String prefix) throws IOException {

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    cd.accept(pprinter.getTraverser());
    String content = pprinter.getPrinter().getContent();

    Path outputFile = Paths.get(CDMerge.TEMP_DIR, prefix + cd.getCDDefinition().getName() + ".cd");

    // Write results into a file
    FileUtils.writeStringToFile(outputFile.toFile(), content, Charset.defaultCharset());

    return outputFile.toString();
  }

  private static CDMergeConfig getConfig(List<String> inputModels) {
    CDMergeConfig.Builder builder = new CDMergeConfig.Builder(false).withParam(
            MergeParameter.OUTPUT_PATH, TEMP_DIR)
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES, MergeParameter.ON)
        .withParam(MergeParameter.OUTPUT_NAME, "mergedCD");
    for (String m : inputModels) {
      builder.addInputFile(m);
    }
    return builder.build();
  }

}
