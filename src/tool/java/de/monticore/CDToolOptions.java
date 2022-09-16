/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

public class CDToolOptions {
  final protected Options options = new Options();
  final protected Map<SubCommand, Options> subCommands = new HashMap<>();
  final protected DefaultParser parser = new DefaultParser();
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

  public CommandLine handleArgs(String[] args)
    throws ParseException {
    this.cmd = parser.parse(this.options, args, true);
    return this.cmd;
  }

  protected void init(boolean showPlantUML) {
    options.addOption(Option
      .builder("h").longOpt("help")
      .desc("Prints short help; other options are ignored. ")
      .build());

    initCheck();
    initPrettyPrinter(showPlantUML);
    initPlantUML();
    initDiffOptions();
    initSyntaxDiffOptions();
  }

  protected void initCheck() {
    options.addOption(Option
      .builder("i").longOpt("input")
      .hasArg().type(String.class)
      .argName("file").numberOfArgs(1)
      .desc("Reads the source file and parses the contents as a CD (mandatory, unless `--stdin` "
        + "is used).")
      .build());

    options.addOption(Option
      .builder().longOpt("stdin")
      .desc("Reads the input CD from stdin instead of argument `-i`.")
      .build());

    options.addOption(Option
      .builder().longOpt("path")
      .hasArgs()
      .desc("Artifact path for importable symbols, separated by spaces (default is: `.`).")
      .build());

    options.addOption(Option
      .builder("s").longOpt("symboltable")
      .hasArg().type(String.class)
      .argName("file").optionalArg(true).numberOfArgs(1)
      .desc("Stores the symbol table of the CD. The default value is `{CDName}.cdsym`.")
      .build());

    options.addOption(Option
      .builder("o").longOpt("output")
      .hasArg().type(String.class)
      .argName("dir").optionalArg(true).numberOfArgs(1)
      .desc("Defines the path for generated files (optional; default is: `.`).")
      .build());

    // specify template path
    options.addOption(Option.builder("fp")
      .longOpt("templatePath")
      .argName("pathlist")
      .hasArgs()
      .type(String.class)
      .desc("Directories and jars for handwritten templates to integrate when using `--gen` "
        + "(optional, but needed, when `-ct` is used).")
      .build());


    // configTemplate parameter
    options.addOption(Option.builder("ct")
      .longOpt("configTemplate")
      .argName("file")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Executes this template at the beginning of a generation with `--gen`. This allows "
        + "configuration of the generation process (optional, `-fp` is needed to specify the template path).")
      .build());


    // Prints reports of the cd artifact to the specified directory. This includes e.g. reachable states and branching degrees
    options.addOption(Option
      .builder("r").longOpt("report")
      .hasArg().type(String.class)
      .argName("dir").optionalArg(true).numberOfArgs(1)
      .desc("Prints reports of the parsed artifact to the specified directory (optional) or the"
        + " output directory specified by `-o` (default is: `.`) This includes e.g. all  "
        + "defined packages, classes, interfaces, enums, and associations. The file name is "
        + "\"report.{CDName}\".")
      .build());

    options.addOption(Option
      .builder("t").longOpt("usebuiltintypes")
      .hasArg().type(Boolean.class)
      .argName("boolean").optionalArg(true).numberOfArgs(1)
      .desc("Configures if built-in-types should be considered. Default: `true`; `-t` toggles "
        + "it to `--usebuiltintypes false`.")
      .build());

    options.addOption(Option
      .builder("d").longOpt("defaultpackage")
      .hasArg().type(Boolean.class)
      .argName("boolean").optionalArg(true).numberOfArgs(1)
      .desc("Configures if a default package should be created. Default: false. If `true`, all "
        + "classes, that are not already in a package, are moved to the default package.")
      .build());

    options.addOption(Option
      .builder().longOpt("fieldfromrole")
      .hasArg().type(String.class)
      .argName("fieldfromrole").numberOfArgs(1)
      .desc("Configures if explicit field symbols, which are typically used for implementing "
        + "associations, should be added, if derivable from role symbols (default: none). "
        + "Values: `none` is typical for modelling, `all` adds always on both classes, "
        + "`navigable` adds only if the association is navigable.")
      .build());

    options.addOption(Option
      .builder().longOpt("json")
      .desc("Write a \"Schema.json\" to the output directory.")
      .build());

    options.addOption(Option
      .builder().longOpt("gen")
      .desc("Generate .java-files corresponding to the classes defined in the input class "
        + "diagram.")
      .build());

    // specify template path
    options.addOption(Option.builder("hwc")
      .longOpt("handwritten-code")
      .argName("path")
      .hasArgs()
      .type(String.class)
      .desc("Configure a path for adding handwritten code when generating with the option '--gen'. " +
        "This leads to TOP-classes being generated for all classes that are already present in the handwritten code.")
      .build());

  }

  protected void initPrettyPrinter(boolean showPlantUML) {
    options.addOption(Option
      .builder("pp").longOpt("prettyprint")
      .hasArg().type(String.class)
      .argName("file").optionalArg(true).numberOfArgs(1)
      .desc("Prints the input CDs to stdout or to the specified file (optional). The output directory is specified by `-o`.")
      .build());

    if (showPlantUML) {
      // dependent on pp
      options.addOption(Option
        .builder("puml").longOpt("plantUML")
        .hasArg().type(String.class)
        .argName("file").optionalArg(true).numberOfArgs(1)
        .argName("puml")
        .desc("Transform the input model to a PlantUML model.")
        .build());
    }
  }

  /**
   * these options are only available, when "-puml" is used
   */
  protected void initPlantUML() {
    final Options plantUMLOptions = new Options();

    plantUMLOptions.addOption(Option
      .builder().longOpt("svg")
      .desc("print as plantUML svg")
      .build());

    plantUMLOptions.addOption(Option
      .builder("attr").longOpt("showAttributes")
      .desc("show attributes [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder("assoc").longOpt("showAssociations")
      .desc("show associations [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder().longOpt("showRoles")
      .desc("show roles [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder("card").longOpt("showCardinality")
      .desc("show cardinalities [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder("mod").longOpt("showModifier")
      .desc("show modifier [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder("nodesep").longOpt("nodeSeparator")
      .hasArg().type(Number.class)
      .argName("nodesep")
      .desc("set the node separator [number]. "
        + "The default value is \"-1\"")
      .build());
    plantUMLOptions.addOption(Option
      .builder().longOpt("rankSeparator")
      .hasArg().type(Number.class)
      .argName("ranksep")
      .desc("set the rank separator [number]. "
        + "The default value is \"-1\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder().longOpt("orthogonal")
      .desc("show lines only orthogonal [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder().longOpt("shortenWords")
      .desc("shorten displayed words [true] when used. "
        + "The default value is \"false\".")
      .build());
    plantUMLOptions.addOption(Option
      .builder("comment").longOpt("showComments")
      .desc("show comments [true] when used. "
        + "The default value is \"false\".")
      .build());

    subCommands.put(SubCommand.PLANTUML, plantUMLOptions);
  }

  /**
   * adds options for semantic differencing
   */
  public void initDiffOptions() {

    options.addOption(Option.builder()
      .longOpt("semdiff")
      .hasArg()
      .type(String.class)
      .argName("file")
      .numberOfArgs(2)
      .desc(
        "Input: 2 .cd-files. Output: object diagrams (witnesses) that are valid in the first CD, "
            + "but invalid in the second CD. This is a semantics-based, asymmetric diff. Details: "
            + "https://www.se-rwth.de/topics/Semantics.php")
      .build());

    options.addOption(Option.builder()
        .longOpt("rule-based")
        .desc("Uses a rule-based approach to `--semdiff` instead of the model-checker Alloy to "
            + "compute the diff witnesses. Improved performance.")
        .build());

    options.addOption(Option.builder()
        .longOpt("jsemdiff")
        .hasArg()
        .type(String.class)
        .argName("file")
        .numberOfArgs(1)
        .desc(
            "Alternative to `--semdiff`: use a prototype of a purely Java-based implementation of"
                + " a semantic diff.")
        .build());

    options.addOption(Option.builder()
      .longOpt("diffsize")
      .hasArg()
      .type(Integer.class)
      .argName("int")
      .numberOfArgs(1)
      .desc("Maximum number of objects in witnesses when comparing the semantic diff with "
        + "`--semdiff` (optional; default is based on a heuristic, but at least 20). "
        + "This constrains long searches.")
      .build());

    options.addOption(Option.builder()
      .longOpt("difflimit")
      .hasArg()
      .type(Integer.class)
      .argName("int")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Maximum number of shown witnesses when using `--semdiff` (optional; default is: 1,"
        + " i.e. only one witness is shown).")
      .build());

    options.addOption(Option.builder()
        .longOpt("open-world")
        .argName("method")
        .type(String.class)
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Compute the multi-instance open-world difference of 2 class diagrams when using "
            + "`--semdiff` (optional). The method is either `reduction-based` or "
            + "`alloy-based` (default is: `reduction-based`).")
        .build());
  }

  /**
   * adds options for syntax diff access
   */
  public void initSyntaxDiffOptions() {
    options.addOption(Option.builder()
      .longOpt("syntaxdiff")
      .hasArg()
      .type(String.class)
      .argName("file")
      .numberOfArgs(2)
      .desc(
        "Syntax based analysis of differences between class diagrams.")
      .build());

    options.addOption(Option.builder()
      .longOpt("print")
      .hasArg().type(String.class)
      .argName("printType").optionalArg(true).numberOfArgs(1)
      .desc("Type of print, options are: (default) diff, nocolor, cd1, cd2, both, all "
      + "diff will print a coloured version of all diffs and matchs "
      + "nocolor print an alternative with +, -, ~ indicators "
      + "cd1, cd2, both will provide coloured version of the provided models "
      + "all provides diff, cd1, cd2")
      .build());

    options.addOption(Option.builder()
      .longOpt("json")
      .desc("Creates a json report file in the output directory.")
      .build());

    options.addOption(Option.builder()
      .longOpt("semDiff")
      .desc("Creates at most one diff witness")
      .build());

    options.addOption(Option.builder()
      .longOpt("showpath")
      .desc("Each print contains the full path of the file, git commit hash included "
        + "without this option only the file name is printed")
      .build());
    options.addOption(Option.builder()
      .longOpt("cd1commit")
      .hasArg()
      .type(String.class)
      .numberOfArgs(1)
      .desc("Each print contains the full path of the file, git commit hash included "
        + "without this option only the file name is printed")
      .build());
    options.addOption(Option.builder()
      .longOpt("cd2commit")
      .hasArg()
      .type(String.class)
      .numberOfArgs(1)
      .desc("Each print contains the full path of the file, git commit hash included "
        + "without this option only the file name is printed")
      .build());
  }

}
