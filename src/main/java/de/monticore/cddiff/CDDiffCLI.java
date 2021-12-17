package de.monticore.cddiff;

import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.alloycddiff.classDifference.ClassDifference;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * CLI app for cddiff.
 */
public class CDDiffCLI {

  protected String cd1PathName;

  protected String cd2PathName;

  protected int scope;

  protected String outputPathName;

  protected int limit;

  final protected Options options = new Options();

  final protected DefaultParser parser = new DefaultParser();

  protected CommandLine cmd;

  /**
   * run main for CLI functionality
   *
   * @param args -h for help or -cd1 ${pathName1} -cd2 ${pathName2} -k ${scope}$ [-o ${pathName3}]
   */
  public static void main(String[] args) {
    CDDiffCLI cdDiffCLI = new CDDiffCLI();
    cdDiffCLI.run(args);
  }

  /**
   * computes cddiff on args
   */
  public void run(String[] args) {
    try {
      Log.init();

      Log.enableFailQuick(false);
      if (handleArgs(args)) {

        // initialize CD4CodeMill
        CD4CodeMill.init();
        CD4CodeMill.globalScope().clear();

        //parse the CDs cd1 and cd2 from the corresponding files
        ASTCDCompilationUnit astV1 = parseModel(this.cd1PathName);
        ASTCDCompilationUnit astV2 = parseModel(this.cd2PathName);

        // Create Output Directory
        Path outputDirectory = Paths.get(outputPathName);
        if (!outputDirectory.toFile().exists()) {
          Files.createDirectory(outputDirectory);
        }

        // Create temporary directory for alloy modules
        Path tmpdir = Files.createTempDirectory(outputDirectory, "tmpDiffModules");

        //compute cddiff(cd1,cd2)
        Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(astV1, astV2, this.scope,
            tmpdir.toString());

        // test if solution is present
        if (!optS.isPresent()) {
          Log.error("could not compute cddiff");
          deleteDir(tmpdir.toFile());
          return;
        }
        AlloyDiffSolution sol = optS.get();

        if (!cmd.hasOption("limit")) {
          sol.setLimited(false);
        }
        else {
          sol.setSolutionLimit(this.limit);
          sol.setLimited(true);
        }

        if (cmd.hasOption("alldiff")) {
          sol.generateSolutionsToPath(outputDirectory);
        }
        else {
          sol.generateUniqueSolutionsToPath(outputDirectory);
        }
        deleteDir(tmpdir.toFile());

      }
      else {
        Log.error("no option chosen or incorrect use of options");
      }
    }
    catch (AmbiguousOptionException e) {
      Log.error(String.format("0xCDDE2: option '%s' can't match any valid option", e.getOption()));
    }
    catch (UnrecognizedOptionException e) {
      Log.error(String.format("0xCDDE3: unrecognized option '%s'", e.getOption()));
    }
    catch (MissingOptionException e) {
      Log.error(String.format("0xCDDE4: options [%s] are missing, but are required",
          Joiners.COMMA.join(e.getMissingOptions())));
    }
    catch (MissingArgumentException e) {
      Log.error(String.format("0xCDDE5: option '%s' is missing an argument", e.getOption()));
    }
    catch (Exception e) {
      Log.error(String.format("0xCDDE6: an error occurred: %s", e.getMessage()));
    }
  }

  /**
   * check if args have appropriate format
   */
  protected boolean handleArgs(String[] args) throws ParseException {
    initOptions();
    cmd = parser.parse(this.options, args, true);

    outputPathName = cmd.getOptionValue("o", ".");

    if (!cmd.hasOption("cddiff") || !cmd.hasOption("cd1") || !cmd.hasOption("cd2") || !cmd.hasOption(
        "scope")) {
      return false;
    }

    try {
      this.scope = Integer.parseInt(cmd.getOptionValue("scope"));
      this.limit = Integer.parseInt(cmd.getOptionValue("limit", "10"));
    }
    catch (NumberFormatException e) {
      Log.error("options -scope and -limit require an integer as argument");
      return false;
    }

    cd1PathName = cmd.getOptionValue("cd1");
    cd2PathName = cmd.getOptionValue("cd2");

    return true;
  }

  /**
   * initialize options for the CLI
   */
  protected void initOptions() {

    // initialize --output option for CDDiffCLI
    options.addOption(Option.builder("o")
        .longOpt("output")
        .hasArg()
        .type(String.class)
        .argName("dir")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Path for generated files (optional). Default is `.`.")
        .build());

    //initialize SemDiff options
    initDiffOptions(options);

  }

  /**
   * static method that adds semantic differencing options to parameter options initDiffOptions is
   * public so that it can be used by CDCLIOptions
   */
  public static void initDiffOptions(Options options) {

    options.addOption(Option.builder("cddiff")
        .longOpt("cddiff")
        .desc("Computes the semantic difference semDiff(cd1,cd2) of two CDs specified by Options "
            + "-cd1 and -cd2. The size of the solution is limited by an integer specified by "
            + "Option -scope (mandatory for cddiff).")
        .build());

    options.addOption(Option.builder("cd1")
        .longOpt("cd1")
        .hasArg()
        .type(String.class)
        .argName("cd1")
        .numberOfArgs(1)
        .desc(
            "Reads the source file and parses the contents as the first CD (mandatory for cddiff).")
        .build());

    options.addOption(Option.builder("cd2")
        .longOpt("cd2")
        .hasArg()
        .type(String.class)
        .argName("cd2")
        .numberOfArgs(1)
        .desc("Reads the source file and parses the contents as the second CD (mandatory for "
            + "cddiff).")
        .build());

    options.addOption(Option.builder("scope")
        .longOpt("scope")
        .hasArg()
        .type(int.class)
        .argName("scope")
        .numberOfArgs(1)
        .desc("An integer that defines the scope of the computation. Mandatory for cddiff where "
            + "it denotes the maximum size of solutions.")
        .build());

    options.addOption(Option.builder("limit")
        .longOpt("limit")
        .hasArg()
        .type(String.class)
        .argName("limit")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Limit for the number of solutions (optional). Default is no limit.")
        .build());

    options.addOption(Option.builder("alldiff")
        .longOpt("alldiffsolutions")
        .desc("Generate all diff-witnesses (optional). Default is to generate only unique "
            + "witnesses.")
        .build());
  }

  /**
   * prints description of options
   */
  public void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("OD2CDMatcher", options);
  }

  /**
   * parses CD from file and returns corresponding ASTCDCompilationUnit
   * TODO: migrate to a more appropriate location
   */
  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      //assertFalse(parser.hasErrors());
      if (optAutomaton.isPresent()) {
        return optAutomaton.get();
      }

      Log.error("could not find CD in " + modelFile);

    }
    catch (Exception e) {
      e.printStackTrace();
      Log.error("could not parse " + modelFile + " see " + e.getClass().getName());
    }

    return null;
  }

  /**
   * deletes directory with files
   */
  protected void deleteDir(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File f : contents) {
        if (!Files.isSymbolicLink(f.toPath())) {
          deleteDir(f);
        }
      }
    }
    if (!file.delete()) {
      Log.warn("Could not delete: " + file.getName());
    }
  }

}
