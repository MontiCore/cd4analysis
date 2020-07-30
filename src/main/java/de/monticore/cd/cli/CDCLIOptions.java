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
        .desc("print this help")
        .build());

    options.addOption(Option
        .builder().longOpt("log")
        .hasArg().type(String.class)
        .argName("logLevel")
        .desc("activate specific loglevel, valid values are [trace, debug, info, warn, error]")
        .build());

    options.addOption(Option
        .builder("f").longOpt("no-fail-quick")
        .desc("disables fail-quick; default [false] (enabled fail-quick)")
        .build());

    initCheck();
    initPrettyPrinter();
    initPlantUML();
  }

  protected void initCheck() {
    // either have the model file provided by this option,
    // or use the first in getArgList
    options.addOption(Option
        .builder("i").longOpt("input")
        .hasArg().type(String.class)
        .argName("fileName")
        .desc("the model file to process")
        .build());

    options.addOption(Option
        .builder("p").longOpt("modelPath")
        .hasArg().type(String.class)
        .argName("modelPath")
        .desc("path in which additional models are searched for; default [\".\"]")
        .build());

    options.addOption(Option
        .builder("t").longOpt("use-builtin-types")
        .hasArg().type(Boolean.class)
        .argName("useBuiltinTypes")
        .desc("use built-in-types; default [true]")
        .build());

    options.addOption(Option
        .builder("s").longOpt("symtab")
        .hasArg().type(String.class)
        .argName("symTabName")
        .desc("name of the symbol table export; default [\"<INPUT>.cdsym\"]")
        .build());

    options.addOption(Option
        .builder("r").longOpt("report")
        .hasArg().type(String.class)
        .argName("reportDir")
        .desc("report directory; default [\".\"]")
        .build());
  }

  protected void initPrettyPrinter() {
    options.addOption(Option
        .builder().longOpt("pp")
        .hasArg().type(String.class)
        .argName("prettyPrintOutput")
        .build());

    // dependend on pp
    options.addOption(Option
        .builder().longOpt("puml")
        .desc("output as plantUML model")
        .build());
  }

  protected void initPlantUML() {
    final Options plantUMLOptions = new Options();

    plantUMLOptions.addOption(Option
        .builder().longOpt("svg")
        .desc("print as plantUML svg")
        .build());

    plantUMLOptions.addOption(Option
        .builder().longOpt("showAtt")
        .desc("show attributes; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showAssoc")
        .desc("show associations; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showRoles")
        .desc("show roles; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showCard")
        .desc("show cardinalities; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showModifier")
        .desc("show modifier; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("nodesep")
        .hasArg().type(Number.class)
        .argName("nodesep")
        .desc("set the node separator; default [-1]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("ranksep")
        .hasArg().type(Number.class)
        .argName("ranksep")
        .desc("set the rank separator; default [-1]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("ortho")
        .desc("show lines only orthogonal; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("shortenWords")
        .desc("shorten displayed words; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showComments")
        .desc("show comments; default [false]")
        .build());

    subCommands.put(SubCommand.PLANTUML, plantUMLOptions);
  }
}
