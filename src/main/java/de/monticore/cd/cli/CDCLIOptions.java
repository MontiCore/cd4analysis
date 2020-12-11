package de.monticore.cd.cli;

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
    init();
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

  protected void init() {
    options.addOption(Option
        .builder("h").longOpt("help")
        .desc("Prints this help dialogue.")
        .build());

    options.addOption(Option
        .builder("f").longOpt("failquick")
        .hasArg().type(Boolean.class)
        .argName("value").numberOfArgs(1)
        .optionalArg(false)
        .desc("Configures if the application should quickfail on errors [true/false]. "
            + "The default value is \"false\".")
        .build());

    initCheck();
    initPrettyPrinter();
    initPlantUML();
  }

  protected void initCheck() {
    options.addOption(Option
        .builder("i").longOpt("input")
        .hasArg().type(String.class)
        .argName("file").numberOfArgs(1)
        .desc("Reads the input CD artifact given as argument.")
        .build());

    options.addOption(Option
        .builder("stdin").longOpt("stdin")
        .desc("Reads the path to the input CD artifact from stdin.")
        .build());

    options.addOption(Option
        .builder("p").longOpt("path")
        .hasArg().type(String.class)
        .argName("dirlist").numberOfArgs(1)
        .desc("Sets the artifact path for imported symbols separated by ';'. "
            + "The default value is \".\".")
        .build());

    options.addOption(Option
        .builder("s").longOpt("symboltable")
        .hasArg().type(String.class)
        .argName("file").optionalArg(true).numberOfArgs(1)
        .desc("Serializes and prints the symbol table to stdout or the specified output file (optional). "
            + "The default value is \"{inputArtifactName}.cdsym\".")
        .build());

    options.addOption(Option
        .builder("o").longOpt("output")
        .hasArg().type(String.class)
        .argName("dir").optionalArg(true).numberOfArgs(1)
        .desc("Path of generated files (optional). "
            + "The default value is \".\".")
        .build());

    options.addOption(Option
        .builder("r").longOpt("report")
        .hasArg().type(String.class)
        .argName("dir").optionalArg(true).numberOfArgs(1)
        .desc("Prints reports of the parsed artifact to the specified directory (optional). "
            + "Available reports are language-specific. "
            + "The default value is \"_output_path_\".")
        .build());

    options.addOption(Option
        .builder("t").longOpt("usebuiltintypes")
        .hasArg().type(Boolean.class)
        .argName("useBuiltinTypes").numberOfArgs(1)
        .desc("Configures if built-in-types should be considered [true/false]. "
            + "The default value is \"true\".")
        .build());
  }

  protected void initPrettyPrinter() {
    options.addOption(Option
        .builder("pp").longOpt("prettyprint")
        .hasArg().type(String.class)
        .argName("file").optionalArg(true).numberOfArgs(1)
        .argName("prettyprint")
        .desc("Prints the input SDs to stdout or to the specified file (optional).")
        .build());

    // dependent on pp
    options.addOption(Option
        .builder("puml").longOpt("plantUML")
        .hasArg().type(String.class)
        .argName("file").optionalArg(true).numberOfArgs(1)
        .argName("puml")
        .desc("Transform the input model to a PlantUML model.")
        .build());
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
        .builder("roles").longOpt("showRoles")
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
        .builder("ranksep").longOpt("rankSeparator")
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
        .builder("short").longOpt("shortenWords")
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
}
