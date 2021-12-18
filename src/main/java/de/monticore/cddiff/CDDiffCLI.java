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

  protected int diffsize;

  protected String outputPathName;

  protected int difflimit;

  final protected Options options = new Options();

  final protected DefaultParser parser = new DefaultParser();

  protected CommandLine cmd;

  /**
   * run main for CLI functionality
   *
   * @param args i- ${pathName1} -semdiff ${pathName2} [-diffsize ${int}$] [-difflimit ${int}$]
   *             [-alldiff] [-o ${pathName3}]
   */
  public static void main(String[] args) {
    CDDiffCLI cdDiffCLI = new CDDiffCLI();
    cdDiffCLI.run(args);
  }

  /**
   * computes semdiff on args
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

        //compute semDiff(cd1,cd2)
        Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(astV1, astV2, this.diffsize,
            tmpdir.toString());

        // test if solution is present
        if (!optS.isPresent()) {
          Log.error("could not compute semdiff");
          deleteDir(tmpdir.toFile());
          return;
        }
        AlloyDiffSolution sol = optS.get();

        if (!cmd.hasOption("difflimit")) {
          sol.setLimited(false);
        }
        else {
          sol.setSolutionLimit(this.difflimit);
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

    if (!cmd.hasOption("i") || !cmd.hasOption("semdiff")) {
      Log.error("Options -i and --semdiff are required for the computation of semantic "
          + "differences. Option --stdin is not supported for this computation.");
      return false;
    }

    try {
      this.diffsize = Integer.parseInt(cmd.getOptionValue("diffsize", "3"));
      this.difflimit = Integer.parseInt(cmd.getOptionValue("difflimit", "100"));
    }
    catch (NumberFormatException e) {
      Log.error("options --diffsize and --difflimit each require an integer as argument");
      return false;
    }

    cd1PathName = cmd.getOptionValue("i");
    cd2PathName = cmd.getOptionValue("semdiff");

    return true;
  }

  /**
   * initialize options for the CLI
   */
  protected void initOptions() {

    // initialize Options --input and --output for CDDiffCLI

    options.addOption(Option.builder("i")
        .longOpt("input")
        .hasArg()
        .type(String.class)
        .argName("file")
        .numberOfArgs(1)
        .desc("Reads the source file (mandatory) and parses the contents as CD.")
        .build());

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

    options.addOption(Option.builder()
        .longOpt("semdiff")
        .hasArg()
        .type(String.class)
        .argName("file")
        .numberOfArgs(1)
        .desc(
            "Reads `<file>` as second CD and compares it semantically with the first CD given "
                + "with the `-i` option. Output: Object diagrams (witnesses) that are valid in "
                + "the `-i`-CD, but invalid in the second CD. This is a semantic based, "
                + "asymmetric diff.")
        .build());

    options.addOption(Option.builder()
        .longOpt("diffsize")
        .hasArg()
        .type(int.class)
        .argName("diffsize")
        .numberOfArgs(1)
        .desc("Maximum size of found witnesses when comparing the semantic diff with `--semdiff` "
            + "(default is: 3). This constrains long searches.")
        .build());

    options.addOption(Option.builder()
        .longOpt("difflimit")
        .hasArg()
        .type(String.class)
        .argName("difflimit")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Maximum number of found witnesses")
        .build());

    options.addOption(Option.builder()
        .longOpt("alldiff")
        .desc("Show all diff-witnesses (Default is to show only unique, minimal ones)")
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
