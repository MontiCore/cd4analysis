/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.ConfigurationException;
import de.monticore.cdmerge.exceptions.FailFastException;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import de.monticore.cdmerge.merging.mergeresult.MergeStepResult;
import de.monticore.cdmerge.util.CDUtils;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Command-Line interface for merging class diagrams Minimal configuration: MergingTool -m
 * modelfile1 modelfile2 Further parameters @see {@link MergeParameter} (prefix each parameter with
 * "-")
 */
public class CliMergeTool {

  public static void main(String[] args) throws FileNotFoundException, IOException {

    PrintStream outStream = System.out;

    if (args.length == 0) {
      printUsageShort();
      return;
    }

    CDMergeConfig.Builder configBuilder = new CDMergeConfig.Builder(true);
    Optional<MergeParameter> currentParam = Optional.empty();
    for (String argument : args) {
      if (argument.startsWith("-") || argument.startsWith("--")) {
        currentParam = MergeParameter.getByShortcut(argument.replace("-", ""));
        if (!currentParam.isPresent()) {
          // Try long cli parameter
          currentParam = MergeParameter.getByCLIParameter(argument.substring(1));
        }
        if (!currentParam.isPresent()) {
          if (argument.contains("help")) {
            printUsage();
            return;
          }
          System.out.println("Unknown Parameter specified: " + argument);
          return;
        }
        // The parameter was specified in CLI so turn it on
        if (currentParam.get().isBooleanParameter()) {
          configBuilder.withParam(currentParam.get(), MergeParameter.ON);
        } else {
          // There will possibly be a value in the next iteration
          configBuilder.withParam(currentParam.get());
        }
      } else {
        // A parameter Value
        if (currentParam.isPresent()) {
          if (currentParam.get().isBooleanParameter()) {
            System.out.println(
                "Unexpected value " + argument + " for flag parameter " + currentParam);
            return;
          }
          configBuilder.withParam(currentParam.get(), argument);
        } else {
          System.out.println(
              "Unexpected value "
                  + argument
                  + "  was expecting parameter instead. Are you missing '-' or '--' as parameter "
                  + "prefix?");
        }
      }
    }
    if (!configBuilder.isDefinedParameter(MergeParameter.OUTPUT_PATH)) {
      System.out.println(
          String.format(
              "Will use current directory '%s' as output directory",
              System.getProperty("user.dir")));
      configBuilder.withParam(MergeParameter.OUTPUT_PATH, System.getProperty("user.dir"));
    }
    MergeTool merger;
    try {
      merger = new MergeTool(configBuilder.build());
    } catch (ConfigurationException e) {
      System.out.println("Configuration error " + e.getMessage());
      return;
    }
    try {
      if (merger.getConfig().isLogToStdErr()) {
        outStream = System.err;
      }

      if (merger.getConfig().isVerbose()) {
        outStream.println("Running CD Merge with parameters:\n");
        Map<MergeParameter, String> parameters = configBuilder.getMergeParameters();
        String value = "";
        for (MergeParameter param : MergeParameter.values()) {
          if (parameters.containsKey(param)) {
            value = parameters.get(param).isEmpty() ? "ON" : parameters.get(param);
          } else {
            value = "OFF";
          }
          outStream.println("\t" + param.getCLIParameter() + ": " + value);
        }
      }

      // Perform the actual cd mergings:
      MergeResult result = merger.mergeCDs();
      // Analyse the Reports...
      Iterator<MergeStepResult> reportIterator = result.getIntermediateResults().iterator();
      MergeStepResult mergeStepResult;
      int step = 0;

      while (reportIterator.hasNext()) {
        step++;
        mergeStepResult = reportIterator.next();
        if (merger.getConfig().isVerbose()) {
          outStream.println("==== MERGE LOG STEP " + step + " ====");
          outStream.println(CDUtils.prettyPrint(mergeStepResult.getMergedCD()));
          outStream.println("==== END MERGE STEP " + step + " ====");
        }
      }

      if (result.getMaxErrorLevel() != ErrorLevel.ERROR) {
        if (!merger.getConfig().isSilent()) {
          outStream.println("== MERGED CLASS DIAGRAM ==");
          outStream.println(CDUtils.prettyPrint(result.getMergedCD().get()));
        }
        if (!merger.getConfig().checkOnly() && merger.getConfig().printToFile()) {
          de.se_rwth.commons.Files.writeToTextFile(
              new StringReader(CDUtils.prettyPrint(result.getMergedCD().get())),
              new File(
                  merger.getConfig().getOutputPath() + merger.getConfig().getOutputName() + ".cd"));
          outStream.println(
              "Wrote successfully merged CD into file: "
                  + merger.getConfig().getOutputPath()
                  + merger.getConfig().getOutputName()
                  + ".cd");
        } else {
          outStream.println("Merging the class diagrams was succesfull!");
        }

      } else {
        outStream.println("Unable to merge input CDs!");
      }

    } catch (FailFastException e) {
      outStream.println("FAIL FAST EXIT");
      outStream.println("Unable to merge class diagramms: " + e.getMessage());
      outStream.println();
    } catch (ConfigurationException e) {
      outStream.println("Configuration error " + e.getMessage());
    } catch (MergingException e) {
      if (merger.getConfig().isVerbose()) {
        if (e.getReport().isPresent()) {
          outStream.println("== EXECUTION LOG ==");
          outStream.println(e.getReport().get().getFormattedExecutionLog());
          outStream.println("== END LOG ==");
        }
        outStream.println();
      }
      outStream.println("== FAILURE REASON ==");
      outStream.println(e.getMessage());
      outStream.println("Check the execution log for occurred merging problems.");
    }
  }

  private static void printUsageShort() {
    System.out.println("Usage: java -jar CDMerge -m <InputModel1> <InputModel2>  ");
    System.out.println(
        " Call java - jar  CDMerge -help paramater to print full list of parameters");
  }

  private static void printUsage() {
    URL usagedoc = CliMergeTool.class.getClassLoader().getResource("CDMergeUsage.md");
    if (usagedoc == null) {
      printUsageShort();
      System.out.println(".... could not find help doc resource 'CDMergeUsage.md' !");
    } else {
      try {
        StringBuilder buffer = new StringBuilder();
        File docFile = new File(usagedoc.toURI());
        if (docFile.exists() && docFile.canRead()) {

          InputStreamReader isr =
              new InputStreamReader(new FileInputStream(docFile), StandardCharsets.UTF_8);

          BufferedReader reader = new BufferedReader(isr);
          String str;
          while ((str = reader.readLine()) != null) {
            buffer.append(str + System.lineSeparator());
          }
          reader.close();
          System.out.println(
              buffer.toString().replace("#", "").replace("**", "").replace("+ ", " "));
        } else {
          throw new NoSuchFileException(docFile.getAbsolutePath());
        }
      } catch (Exception e) {
        printUsageShort();
        System.out.println(".... could not find help doc resource 'CDMergeUsage.md' !");
        System.out.println(e.getMessage());
      }
    }
  }
}
