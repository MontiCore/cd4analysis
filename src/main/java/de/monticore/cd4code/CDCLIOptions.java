/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code;

import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

public class CDCLIOptions {
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

  public CDCLIOptions() {
    init(false);
  }

  public CDCLIOptions(boolean showPlantUML) {
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
        .desc("Prints short help information")
        .build());

    initCheck();
    initPrettyPrinter(showPlantUML);
    initPlantUML();
    initCDDiff();
  }

  protected void initCheck() {
    options.addOption(Option
        .builder("i").longOpt("input")
        .hasArg().type(String.class)
        .argName("file").numberOfArgs(1)
        .desc("Reads the source file (mandatory) and parses the contents as CD")
        .build());

    options.addOption(Option
        .builder("stdin").longOpt("stdin")
        .desc("Reads the input CD from stdin instead of argument `-i`.")
        .build());

    options.addOption(Option
        .builder().longOpt("path")
        .hasArgs()
        .desc("Artifact path for importable symbols, space separated.")
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
        .desc("Path for generated files (optional). Default is `.`.")
        .build());

    // specify template path
    options.addOption(Option.builder("fp")
        .longOpt("templatePath")
        .argName("pathlist")
        .hasArgs()
        .type(String.class)
        .desc("Optional list of directories to look for handwritten templates to integrate.")
        .build());


    // configTemplate parameter
    options.addOption(Option.builder("ct")
        .longOpt("configTemplate")
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Provides a config template (optional)")
        .build());


    // Prints reports of the cd artifact to the specified directory. This includes e.g. reachable states and branching degrees
    options.addOption(Option
        .builder("r").longOpt("report")
        .hasArg().type(String.class)
        .argName("dir").optionalArg(true).numberOfArgs(1)
        .desc("Prints reports of the parsed artifact to the specified directory (optional) (default `.`). "
            + "This includes e.g. all defined packages, classes, interfaces, enums, and associations. "
            + "The file name is \"report.{CDName}\".")
        .build());

    options.addOption(Option
        .builder("t").longOpt("usebuiltintypes")
        .hasArg().type(Boolean.class)
        .argName("useBuiltinTypes").optionalArg(true).numberOfArgs(1)
        .desc("Configures if built-in-types should be considered. "
                + "Default: `true`. `-t` toggles it to `--usebuiltintypes false`")
        .build());

    options.addOption(Option
        .builder("d").longOpt("defaultpackage")
        .hasArg().type(Boolean.class)
        .argName("defaultpackage").optionalArg(true).numberOfArgs(1)
        .desc("Configures if a default package should be created. Default: false. "
            + "If `true`, all classes, that are not already in a package, are moved to the default package.")
        .build());

    options.addOption(Option
        .builder().longOpt("fieldfromrole")
        .hasArg().type(String.class)
        .argName("fieldfromrole").numberOfArgs(1)
        .desc("Configures if explicit field symbols, which are typically used for implementing associations, "
            + "should be added, if derivable from role symbols (default: none). "
            + "Values: `none` is typical for modelling, `all` adds always on both classes, "
            + "`navigable` adds only if the association is navigable.")
        .build());
  }

  protected void initPrettyPrinter(boolean showPlantUML) {
    options.addOption(Option
        .builder("pp").longOpt("prettyprint")
        .hasArg().type(String.class)
        .argName("file").optionalArg(true).numberOfArgs(1)
        .argName("prettyprint")
        .desc("Prints the input CDs to stdout or to the specified file (optional).")
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
   * We initialize all options for CDDiffCLI that have no equivalent in CD4CodeCLI
   */
  protected void initCDDiff(){

    options.addOption(Option
        .builder("cd1").longOpt("cddiff1")
        .hasArg().type(String.class)
        .argName("cd1").numberOfArgs(1)
        .desc("Reads the source file and parses the contents as the first CD (mandatory).")
        .build());

    options.addOption(Option
        .builder("cd2").longOpt("cddiff2")
        .hasArg().type(String.class)
        .argName("cd2").numberOfArgs(1)
        .desc("Reads the source files and parses the contents as the second CD (mandatory).")
        .build());

    options.addOption(Option
        .builder("k").longOpt("scope")
        .hasArg().type(int.class)
        .argName("k").numberOfArgs(1)
        .desc("Scope of the alloy solutions (mandatory).")
        .build());

    options.addOption(Option
        .builder("l").longOpt("limit")
        .hasArg().type(String.class)
        .argName("dir").optionalArg(true).numberOfArgs(1)
        .desc("Limit for the number of diff-witnesses (optional). Default is 10.")
        .build());

    options.addOption(Option
        .builder("nl").longOpt("nolimit")
        .desc("No limit for the number of diff-witnesses (optional).")
        .build());

    options.addOption(Option
        .builder("a").longOpt("all")
        .desc("Generate all solutions (optional). Default is to generate only unique solutions. Limit still applies unless option -nl is selected.")
        .build());

  }
}
