package de.monticore.cd.cli;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CDCLIOptions {
  final Options options = new Options();
  final Map<SubCommand, Options> subCommands = new HashMap<>();

  public enum SubCommand {
    HELP,
    CHECK,
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

  public Pair<SubCommand, CommandLine> handleArgs(String[] args)
      throws ParseException {
    final DefaultParser parser = new DefaultParser();
    final CommandLine cmd = parser.parse(this.options, args, true);

    final String[] params = Arrays.stream(args).skip(1).toArray(String[]::new);

    if (cmd.hasOption("c")) {
      return new ImmutablePair<>(SubCommand.CHECK, parser.parse(subCommands.get(SubCommand.CHECK), params));
    }
    else if (cmd.hasOption("p")) {
      return new ImmutablePair<>(SubCommand.PLANTUML, parser.parse(subCommands.get(SubCommand.PLANTUML), params));
    }

    return new ImmutablePair<>(SubCommand.HELP, null);
  }

  protected void init() {
    final OptionGroup optionGroup = new OptionGroup();
    optionGroup.addOption(Option
        .builder("h").longOpt("help")
        .build());
    optionGroup.addOption(Option
        .builder("c").longOpt("check")
        .desc("check the provided cd")
        .build());
    optionGroup.addOption(Option
        .builder("p").longOpt("plantUML")
        .desc("transform provided cd to PlantUML")
        .build());
    options.addOptionGroup(optionGroup);

    initCheck();
    initPlantUML();
  }

  protected void initCheck() {
    final Options checkOptions = new Options();

    checkOptions.addOption(Option
        .builder("h").longOpt("help")
        .build());

    // either have the model file provided by this option,
    // or use the first in getArgList
    checkOptions.addOption(Option
        .builder("m").longOpt("model")
        .hasArg().type(String.class)
        .argName("fileName")
        .desc("the model file to check")
        .build());

    checkOptions.addOption(Option
        .builder("q").longOpt("no-fail-quick")
        .desc("disables fail-quick for the coco checks; default [false]")
        .build());

    checkOptions.addOption(Option
        .builder("t").longOpt("no-builtin-types")
        .hasArg().type(Boolean.class)
        .optionalArg(true)
        .argName("useBuiltinTypes")
        .desc("don't use built-in-types; default [false]")
        .build());

    subCommands.put(SubCommand.CHECK, checkOptions);
  }

  protected void initPlantUML() {
    final Options plantUMLOptions = new Options();

    plantUMLOptions.addOption(Option
        .builder("h").longOpt("help")
        .build());

    // either have the model file provided by this option,
    // or use the first in getArgList
    plantUMLOptions.addOption(Option
        .builder("m").longOpt("model")
        .hasArg().type(String.class)
        .argName("fileName")
        .desc("the model file to print")
        .build());

    plantUMLOptions.addOption(Option
        .builder("q").longOpt("no-fail-quick")
        .desc("disables fail-quick for the parser, st-creation and cocos; default [false]")
        .build());

    plantUMLOptions.addOption(Option
        .builder("o").longOpt("out")
        .hasArg().type(String.class)
        .argName("outputFileName")
        .required()
        .build());

    plantUMLOptions.addOption(Option
        .builder("a").longOpt("showAtt")
        .desc("show attributes; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showAssoc")
        .desc("show associations; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder("r").longOpt("showRoles")
        .desc("show roles; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder("c").longOpt("showCard")
        .desc("show cardinalities; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showModifier")
        .desc("show modifier; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("nodesep")
        .hasArg().type(Number.class).argName("nodesep")
        .desc("set the node separator; default [-1]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("ranksep")
        .hasArg().type(Number.class).argName("ranksep")
        .desc("set the rank separator; default [-1]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("ortho")
        .desc("show lines only orthogonal; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder("s").longOpt("shortenWords")
        .desc("shorten displayed words; default [false]")
        .build());
    plantUMLOptions.addOption(Option
        .builder().longOpt("showComments")
        .desc("show comments; default [false]")
        .build());

    subCommands.put(SubCommand.PLANTUML, plantUMLOptions);
  }
}
