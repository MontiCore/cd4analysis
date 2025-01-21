/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.*;

public class CDToolOptions {
  protected final Options options = new Options();

  protected final Map<SubCommand, Options> subCommands = new HashMap<>();

  protected final DefaultParser parser = new DefaultParser();

  protected CommandLine cmd;

  public CommandLine parse(SubCommand subCommand) throws ParseException {
    return parser.parse(subCommands.get(subCommand), cmd.getArgs());
  }

  public enum SubCommand {
    PLANTUML,
  }

  public CDToolOptions() {
    init(false);
  }

  public CDToolOptions(boolean showPlantUML) {
    init(showPlantUML);
  }

  public Options getOptions() {
    return options;
  }

  public Options getOptions(SubCommand subCommand) {
    return subCommands.get(subCommand);
  }

  public CommandLine handleArgs(String[] args) throws ParseException {
    this.cmd = parser.parse(this.options, args, true);
    return this.cmd;
  }

  protected void init(boolean showPlantUML) {
    options.addOption(
        Option.builder("h")
            .longOpt("help")
            .desc("Prints short help; other options are ignored. ")
            .build());
    options.addOption(
        Option.builder("v").longOpt("version").desc("Prints version information").build());

    initCheck();
    initPrettyPrinter(showPlantUML);
    initPlantUML();
    initSemDiffOptions();
    initSyntaxDiffOptions();
    initMergeOptions();
    initConformanceCheckOptions();
    initTrafoTemplateOptions();
  }

  protected void initCheck() {
    options.addOption(
        Option.builder("i")
            .longOpt("input")
            .hasArg()
            .type(String.class)
            .argName("file")
            .numberOfArgs(1)
            .desc(
                "Reads the source file and parses the contents as a CD. Alternatively, `--stdin` "
                    + "can be used to read the input CD from stdin. Using one of the two options is "
                    + "mandatory for all further operations.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("stdin")
            .desc(
                "Reads the input CD from stdin instead of the source file specified by `-i`. Using "
                    + "one of the two options is mandatory for all further operations.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("path")
            .hasArgs()
            .desc("Artifact path for importable symbols, separated by spaces (default is: `.`).")
            .build());

    options.addOption(
        Option.builder("s")
            .longOpt("symboltable")
            .hasArg()
            .type(String.class)
            .argName("file")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc("Stores the symbol table of the CD. The default value is `{CDName}.cdsym`.")
            .build());

    options.addOption(
        Option.builder("o")
            .longOpt("output")
            .hasArg()
            .type(String.class)
            .argName("dir")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc("Defines the path for generated files (optional; default is: `.`).")
            .build());

    // specify template path
    options.addOption(
        Option.builder("fp")
            .longOpt("templatePath")
            .argName("pathlist")
            .hasArgs()
            .type(String.class)
            .desc(
                "Directories and jars for handwritten templates to integrate when using `--gen` "
                    + "(optional, but needed, when `-ct` is used).")
            .build());

    // configTemplate parameter
    options.addOption(
        Option.builder("ct")
            .longOpt("configTemplate")
            .argName("file")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Executes this template at the beginning of a generation with `--gen`. This allows "
                    + "configuration of the generation process (optional, `-fp` is needed to specify the "
                    + "template path).")
            .build());

    // Prints reports of the cd artifact to the specified directory. This includes e.g. reachable
    // states and branching degrees
    options.addOption(
        Option.builder("r")
            .longOpt("report")
            .hasArg()
            .type(String.class)
            .argName("dir")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Prints reports of the parsed artifact to the specified directory (optional) or the"
                    + " output directory specified by `-o` (default is: `.`) This includes e.g. all  "
                    + "defined packages, classes, interfaces, enums, and associations. The file name is "
                    + "\"report.{CDName}\".")
            .build());

    options.addOption(
        Option.builder("nt")
            .longOpt("nobuiltintypes")
            .desc("If this option is used, built-in-types will not be considered.")
            .build());

    options.addOption(
        Option.builder("d")
            .longOpt("defaultpackage")
            .hasArg()
            .type(Boolean.class)
            .argName("boolean")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Configures if a default package should be created. Default: false. If `true`, all "
                    + "classes, that are not already in a package, are moved to the default package.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("fieldfromrole")
            .hasArg()
            .type(String.class)
            .argName("fieldfromrole")
            .numberOfArgs(1)
            .desc(
                "Configures if explicit field symbols, which are typically used for implementing "
                    + "associations, should be added, if derivable from role symbols (default: none). "
                    + "Values: `none` is typical for modelling, `all` adds always on both classes, "
                    + "`navigable` adds only if the association is navigable.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("json")
            .desc("Write a \"Schema.json\" to the output directory.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("gen")
            .desc(
                "Generate .java-files corresponding to the classes defined in the input class "
                    + "diagram.")
            .build());

    // specify template path
    options.addOption(
        Option.builder("hwc")
            .longOpt("handwritten-code")
            .argName("path")
            .hasArgs()
            .type(String.class)
            .desc(
                "Configure a path for adding handwritten code when generating with the option "
                    + "'--gen'. This leads to TOP-classes being generated for all classes that are "
                    + "already present in the handwritten code.")
            .build());
  }

  protected void initPrettyPrinter(boolean showPlantUML) {
    options.addOption(
        Option.builder("pp")
            .longOpt("prettyprint")
            .hasArg()
            .type(String.class)
            .argName("file")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Prints the input CDs to stdout or to the specified file (optional). The output "
                    + "directory is specified by `-o`.")
            .build());

    if (showPlantUML) {
      // dependent on pp
      options.addOption(
          Option.builder("puml")
              .longOpt("plantUML")
              .hasArg()
              .type(String.class)
              .argName("file")
              .optionalArg(true)
              .numberOfArgs(1)
              .argName("puml")
              .desc("Transform the input model to a PlantUML model.")
              .build());
    }
  }

  /** these options are only available, when "-puml" is used */
  protected void initPlantUML() {
    final Options plantUMLOptions = new Options();

    plantUMLOptions.addOption(
        Option.builder().longOpt("svg").desc("print as plantUML svg").build());

    plantUMLOptions.addOption(
        Option.builder("attr")
            .longOpt("showAttributes")
            .desc("show attributes [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder("assoc")
            .longOpt("showAssociations")
            .desc("show associations [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder()
            .longOpt("showRoles")
            .desc("show roles [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder("card")
            .longOpt("showCardinality")
            .desc("show cardinalities [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder("mod")
            .longOpt("showModifier")
            .desc("show modifier [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder("nodesep")
            .longOpt("nodeSeparator")
            .hasArg()
            .type(Number.class)
            .argName("nodesep")
            .desc("set the node separator [number]. " + "The default value is \"-1\"")
            .build());
    plantUMLOptions.addOption(
        Option.builder()
            .longOpt("rankSeparator")
            .hasArg()
            .type(Number.class)
            .argName("ranksep")
            .desc("set the rank separator [number]. " + "The default value is \"-1\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder()
            .longOpt("orthogonal")
            .desc(
                "show lines only orthogonal [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder()
            .longOpt("shortenWords")
            .desc("shorten displayed words [true] when used. " + "The default value is \"false\".")
            .build());
    plantUMLOptions.addOption(
        Option.builder("comment")
            .longOpt("showComments")
            .desc("show comments [true] when used. " + "The default value is \"false\".")
            .build());

    subCommands.put(SubCommand.PLANTUML, plantUMLOptions);
  }

  /** adds options for semantic differencing */
  public void initSemDiffOptions() {

    options.addOption(
        Option.builder()
            .longOpt("semdiff")
            .hasArg()
            .type(String.class)
            .argName("file")
            .numberOfArgs(1)
            .desc(
                "Parses the file as a second CD and compares it semantically with the first CD that"
                    + " is currently in memory. Output: object diagrams (witnesses) that are valid"
                    + " in the first CD, but invalid in the second CD. This is a semantics-based, "
                    + "asymmetric diff. Details: https://www.se-rwth.de/topics/Semantics.php")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("rule-based")
            .desc(
                "Uses a rule-based approach to `--semdiff` instead of the model-checker Alloy to "
                    + "compute the diff witnesses. Improved performance.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("diffsize")
            .hasArg()
            .type(Integer.class)
            .argName("int")
            .numberOfArgs(1)
            .desc(
                "Maximum number of objects in witnesses when comparing the semantic diff with "
                    + "`--semdiff` (optional; default is based on a heuristic, but at least 20). "
                    + "This constrains long searches.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("difflimit")
            .hasArg()
            .type(Integer.class)
            .argName("int")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Maximum number of shown witnesses when using `--semdiff` (optional; default is: 1,"
                    + " i.e. only one witness is shown).")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("open-world")
            .argName("method")
            .type(String.class)
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Compute the multi-instance open-world difference of 2 class diagrams when using "
                    + "`--semdiff` (optional). The method is either `reduction-based` or "
                    + "`alloy-based` (default is: `reduction-based`).")
            .build());
  }

  public void initSyntaxDiffOptions() {

    options.addOption(
        Option.builder()
            .longOpt("syntaxdiff")
            .hasArg()
            .type(String.class)
            .argName("file")
            .numberOfArgs(1)
            .desc(
                "Performs a syntactical difference analysis on the current CD in memory (new) "
                    + "and a second CD (old) and prints the result to stdout. "
                    + "Default: Outputs color-coded differences (red for deleted, yellow for "
                    + "changed, and green for newly added elements) to stdout.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("show")
            .hasArg()
            .type(String.class)
            .argName("print_option")
            .optionalArg(true)
            .numberOfArgs(1)
            .desc(
                "Specifies the print option for `--syntaxdiff`: "
                    + "`diff` (default) prints only the differences in a color-coded format "
                    + "(red for deleted, yellow for changed, and green for newly added elements). "
                    + "`old` will print only the old CD with color-coded diffs and `new` only the "
                    + "new CD. "
                    + "`both` prints both CDs. `added` prints only the added CD-elements; "
                    + "`removed` prints only the removed CD-elements, "
                    + "and `changed` prints only the changed CD-elements.")
            .build());
  }

  /** adds options for CDMerge */
  public void initMergeOptions() {
    options.addOption(
        Option.builder()
            .longOpt("merge")
            .hasArgs()
            .type(String.class)
            .argName("files")
            .desc(
                "Parses the files as additional CDs and merges them with the input CD (iff "
                    + "semantically sound). The result is stored in memory.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("mrg-config")
            .hasArg()
            .type(String.class)
            .argName("file")
            .desc(
                "Parses a json-file containing a list of \"Merge Parameters\" that should be "
                    + "applied when using `--merge`. Unknown and unsupported parameters are "
                    + "ignored, only boolean parameters are supported at CLI level. "
                    + "The supported parameters are listed here: "
                    + "https://github.com/MontiCore/cd4analysis/tree/develop/cdmerge/index.md/#list-of-merge-parameters-supported-by-the-cd-tool. "
                    + "By default, \"LOG_TO_CONSOLE\" and \"FAIL_AMBIGUOUS\" are used.")
            .build());
  }

  /** adds options for Conformance Check */
  public void initConformanceCheckOptions() {
    options.addOption(
        Option.builder()
            .longOpt("reference")
            .hasArg()
            .type(String.class)
            .argName("file")
            .numberOfArgs(1)
            .desc(
                "Parses the file as a reference CD and checks if the the input CD specified by `-i`"
                    + " is conform to it.")
            .build());

    options.addOption(
        Option.builder()
            .longOpt("map")
            .hasArg()
            .type(String.class)
            .argName("names")
            .hasArgs()
            .desc(
                "Specify the names of stereotypes that are used as incarnation mappings in the "
                    + "concrete model. Default : 'incarnates'")
            .build());
  }

  public void initTrafoTemplateOptions() {
    options.addOption(
      Option.builder()
        .longOpt("trafoTemplate")
        .hasArg()
        .type(String.class)
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc(
          "Executes this template after the initial AST construction. This allows "
            + "the execution of transformations (optional, `-fp` is needed to specify the "
            + "template path).")
        .build());
  }
}
