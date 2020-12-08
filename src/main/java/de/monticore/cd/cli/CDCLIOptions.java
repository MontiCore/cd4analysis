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
        .desc("Print this help")
        .build());

    options.addOption(Option
        .builder("log")//.longOpt("log")
        .hasArg().type(String.class)
        .argName("logLevel")
        .desc("Activate specific loglevel, valid values are [trace, debug, info, warn, error]")
        .build());

    options.addOption(Option
        .builder("f").longOpt("failquick")
        .hasArg().type(Boolean.class)
        .argName("value").numberOfArgs(1)
        .optionalArg(false)
        .desc("Sets if the application should quickfail on errors [true/false]. "
            + "{default [true] (enabled fail-quick)}")
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
        .desc("Reads the source file resp. the contents of the model. Has to be a class diagram. "
            + "{no default}")
        .build());

    options.addOption(Option
        .builder().longOpt("stdin")
        .desc("Reads the source from stdin. Has to be a class diagram. "
            + "{no default}")
        .build());

    options.addOption(Option
        .builder("p").longOpt("path")
        .hasArg().type(String.class)
        .argName("dirlist").numberOfArgs(1)
        .desc("Sets the artifact path for imported symbols separated by ';'. "
            + "{default \".\"}")
        .build());

    options.addOption(Option
        .builder("s").longOpt("symboltable")
        .hasArg().type(String.class)
        .argName("file").optionalArg(true).numberOfArgs(1)
        .desc("Serializes and prints the symbol table to stdout or the specified output file (optional). "
            + "{default \"_input_file_name_.cdsym\"}")
        .build());

    options.addOption(Option
        .builder("o").longOpt("output")
        .hasArg().type(String.class)
        .argName("dir").optionalArg(true).numberOfArgs(1)
        .desc("Path of generated files (optional). "
            + "{default \".\"}")
        .build());

    options.addOption(Option
        .builder("r").longOpt("report")
        .hasArg().type(String.class)
        .argName("dir").numberOfArgs(1)
        .desc("Prints reports of the parsed artifact to the specified directory (optional). "
            + "Available reports are language-specific. "
            + "{default \"_output_path_\"}")
        .build());

    options.addOption(Option
        .builder("t").longOpt("usebuiltintypes")
        .hasArg().type(Boolean.class)
        .argName("useBuiltinTypes").numberOfArgs(1)
        .desc("Uses built-in-types [true/false]."
            + " {default \"true\"}")
        .build());
  }

  protected void initPrettyPrinter() {
    options.addOption(Option
        .builder("pp").longOpt("prettyprint")
        .hasArg().type(String.class)
        .numberOfArgs(1)
        .argName("prettyPrintOutput")
        .desc("Pretty prints the input cd ")
        .build());

    // dependent on pp
    options.addOption(Option
        .builder("puml").longOpt("plantUML")
        .desc("output as plantUML model")
        .build());
  }

  /**
   * these options are only available, when "--puml" is used
   */
  protected void initPlantUML() {
    final Options plantUMLOptions = new Options();

    plantUMLOptions.addOption(Option
        .builder().longOpt("svg")
        .desc("print as plantUML svg")
        .build());

    plantUMLOptions.addOption(Option
        .builder().longOpt("showAtt")
        .desc("show attributes [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showAssoc")
        .desc("show associations [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showRoles")
        .desc("show roles [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showCard")
        .desc("show cardinalities [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showModifier")
        .desc("show modifier [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("nodesep")
        .hasArg().type(Number.class)
        .argName("nodesep")
        .desc("set the node separator [number]. "
            + "{default \"-1\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("ranksep")
        .hasArg().type(Number.class)
        .argName("ranksep")
        .desc("set the rank separator [number]. "
            + "{default \"-1\"})]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("ortho")
        .desc("show lines only orthogonal [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("shortenWords")
        .desc("shorten displayed words [true] when used. "
            + "{default \"false\"}")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showComments")
        .desc("show comments [true] when used. "
            + "{default \"false\"}")
        .build());

    subCommands.put(SubCommand.PLANTUML, plantUMLOptions);
  }
}
