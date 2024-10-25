/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.FileUtils;

public class CDMerge {

  /**
   * Set of Merge Parameters that are supported by the CD tool's CLI command `--mrg-config` as
   * defined in the README.
   */
  public static final Set<MergeParameter> SUPPORTED_PARAMETERS =
      Set.of(
          MergeParameter.ASSERT_ASSOCIATIVITY,
          MergeParameter.DISABLE_CONTEXT_CONDITIONS,
          MergeParameter.DISABLE_POSTMERGE_VALIDATION,
          MergeParameter.DISABLE_MODEL_REFACTORINGS,
          MergeParameter.FAIL_FAST,
          MergeParameter.FAIL_AMBIGUOUS,
          MergeParameter.LOG_DEBUG,
          MergeParameter.LOG_VERBOSE,
          MergeParameter.LOG_SILENT,
          MergeParameter.LOG_TO_CONSOLE,
          MergeParameter.MERGE_COMMENTS,
          MergeParameter.MERGE_ONLY_NAMED_ASSOCIATIONS,
          MergeParameter.MERGE_HETEROGENEOUS_TYPES,
          MergeParameter.PRIMITIVE_TYPE_CONVERSION,
          MergeParameter.STRICT,
          MergeParameter.WARNINGS_AS_ERRORS,
          MergeParameter.LOG_STDERR);

  @Deprecated
  public static ASTCDCompilationUnit merge(List<ASTCDCompilationUnit> inputs) {
    return merge(inputs, "Merge", new HashSet<>());
  }

  /**
   * merges inputCDs into composite CD according to specified mergeParameters
   *
   * @return composite CD
   */
  public static ASTCDCompilationUnit merge(
      List<ASTCDCompilationUnit> inputCDs,
      String compositeCDName,
      Set<MergeParameter> mergeParameters) {

    Optional<ASTCDCompilationUnit> optAST;

    if (inputCDs.size() < 2) {
      optAST = inputCDs.stream().findAny();
      if (optAST.isPresent()) {
        return optAST.get();
      }
      Log.error("No Input-CD!");
      return null;
    }

    try {
      optAST =
          new MergeTool(getConfig(inputCDs, compositeCDName, mergeParameters))
              .mergeCDs()
              .getMergedCD();

      if (optAST.isPresent()) {
        return optAST.get();
      }

    } catch (MergingException e) {
      Log.error(e.getMessage());
      return null;
    }
    Log.error("Unknown Error");
    return null;
  }

  /** helper-method that constructs the CDMergeConfig */
  private static CDMergeConfig getConfig(
      List<ASTCDCompilationUnit> inputModels, String name, Set<MergeParameter> mergeParameters) {
    CDMergeConfig.Builder builder =
        new CDMergeConfig.Builder(false)
            .withParam(MergeParameter.AST_BASED)
            .withParam(MergeParameter.OUTPUT_NAME, name);

    mergeParameters.forEach(builder::withParam);

    for (ASTCDCompilationUnit cd : inputModels) {
      Preconditions.checkNotNull(cd);
      builder.addInputAST(cd);
    }
    return builder.build();
  }

  /**
   * Parses a json-object containing "Merge Parameters" as a json-array. Unsupported and unknown
   * parameters are filtered out.
   *
   * @param file containing the json-object
   * @return set of supported MergeParameter specified in the json-array
   */
  public static Set<MergeParameter> parseMrgConfig(String file) {
    Set<MergeParameter> mergeParameters = new HashSet<>();
    try {
      String fileContent = FileUtils.readFileToString(new File(file), "UTF-8");
      JsonObject jsonObject = JsonParser.parseJsonObject(fileContent);
      for (MergeParameter param : SUPPORTED_PARAMETERS) {
        if (jsonObject.getMember("Merge Parameters").getAsJsonArray().getValues().stream()
            .anyMatch(e -> e.getAsJsonString().toString().equals(param.name()))) {
          mergeParameters.add(param);
        }
      }
    } catch (IOException e) {
      Log.error("Could not read file " + file, e);
    }
    return mergeParameters;
  }
}
