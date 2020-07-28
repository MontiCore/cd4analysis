/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CDCLI {

  static final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  protected static final String JAR_NAME = "cd-<Version>-cli.jar";
  protected static final String CHECK_SUCCESSFUL = "Parsing and CoCo check successful!";
  protected static final String PLANTUML_SUCCESSFUL = "Creation of file %s successful!\n";
  protected static final Level DEFAULT_LOG_LEVEL = Level.WARN;

  protected String modelFile;
  protected boolean failQuick;
  protected ASTCDCompilationUnit ast;
  protected final CDCLIOptions cdcliOptions = new CDCLIOptions();
  protected CommandLine cmd;
  protected CDCLIOptions.SubCommand subCommand;

  protected CDCLI() {
  }

  public static void main(String[] args) throws IOException, ParseException {

    root.setLevel(DEFAULT_LOG_LEVEL);

    CDCLI cli = new CDCLI();
    try {
      if (cli.handleArgs(args)) {
        Log.enableFailQuick(cli.failQuick);
        cli.run();
      }
    }
    catch (AmbiguousOptionException e) {
      Log.error(String.format("0xCD010: option '%s' can't match any valid option", e.getOption()));
    }
    catch (UnrecognizedOptionException e) {
      Log.error(String.format("0xCD011: unrecognized option '%s'", e.getOption()));
    }
    catch (MissingOptionException e) {
      Log.error(String.format("0xCD012: options [%s] is missing, but is required", Joiners.COMMA.join(e.getMissingOptions())));
    }
    catch (MissingArgumentException e) {
      Log.error(String.format("0xCD013: option '%s' is missing an argument", e.getOption()));
    }
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  protected void parse() throws IOException {
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> cu = parser
        .parse(modelFile);
    ast = cu.get();
  }

  protected void createSymTab(boolean useBuiltInTypes) {
    CD4CodeGlobalScope globalScope = CD4CodeMill
        .cD4CodeGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .addBuiltInTypes(useBuiltInTypes)
        .build();
    final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill
        .cD4CodeSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    symbolTableCreator.createFromAST(ast);
  }

  protected void checkCocos() {
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
  }

  protected String createPlantUML() throws IOException {
    final String outputPath = cmd.getOptionValue("o");

    final PlantUMLConfig plantUMLConfig = new PlantUMLConfig();

    if (cmd.hasOption("a")) {
      plantUMLConfig.setShowAtt(true);
    }
    if (cmd.hasOption("showAssoc")) {
      plantUMLConfig.setShowAssoc(true);
    }
    if (cmd.hasOption("r")) {
      plantUMLConfig.setShowRoles(true);
    }
    if (cmd.hasOption("c")) {
      plantUMLConfig.setShowCard(true);
    }
    if (cmd.hasOption("m")) {
      plantUMLConfig.setShowModifier(true);
    }
    if (cmd.hasOption("nodesep")) {
      plantUMLConfig.setNodesep(Integer.parseInt(cmd.getOptionValue("nodesep")));
    }
    if (cmd.hasOption("ranksep")) {
      plantUMLConfig.setRanksep(Integer.parseInt(cmd.getOptionValue("ranksep")));
    }
    if (cmd.hasOption("ortho")) {
      plantUMLConfig.setOrtho(true);
    }
    if (cmd.hasOption("s")) {
      plantUMLConfig.setShortenWords(true);
    }
    if (cmd.hasOption("showComments")) {
      plantUMLConfig.setShowComments(true);
    }

    if (cmd.hasOption("puml")) {
      return PlantUMLUtil.printCD2PlantUMLModelFileLocally(Optional.ofNullable(ast), outputPath, plantUMLConfig);
    }
    else {
      return PlantUMLUtil.printCD2PlantUMLLocally(Optional.ofNullable(ast), outputPath, plantUMLConfig);
    }
  }

  protected boolean handleArgs(String[] args)
      throws IOException, ParseException {
    final Pair<CDCLIOptions.SubCommand, CommandLine> c = cdcliOptions.handleArgs(args);

    this.cmd = c.getRight();
    this.subCommand = c.getLeft();

    if (cmd == null || cmd.hasOption("h") || (!cmd.hasOption("m") && cmd.getArgList().isEmpty())) {
      printHelp();
      return false;
    }
    else {
      if (cmd.hasOption("m")) {
        modelFile = cmd.getOptionValue("m");
      }
      else {
        modelFile = cmd.getArgList().get(0);
      }
      if (!modelFileExists()) {
        throw new NoSuchFileException(modelFile);
      }

      if (cmd.hasOption("log")) {
        root.setLevel(Level.toLevel(cmd.getOptionValue("log", DEFAULT_LOG_LEVEL.levelStr), DEFAULT_LOG_LEVEL));
      }
      return true;
    }
  }

  protected void run() throws IOException {
    if (cmd == null || cmd.hasOption("h")) {
      printHelp();
    }

    failQuick = !cmd.hasOption("q");

    switch (this.subCommand) {
      case CHECK: {
        boolean useBuiltInTypes = !(cmd.hasOption("t") && Boolean.parseBoolean(cmd.getOptionValue("t", "false")));
        parse();
        createSymTab(useBuiltInTypes);
        checkCocos();
        System.out.println(CHECK_SUCCESSFUL);
        break;
      }
      case PLANTUML: {
        parse();
        final String path = createPlantUML();
        System.out.printf(PLANTUML_SUCCESSFUL, path);
        break;
      }
    }
  }

  protected boolean modelFileExists() {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  protected void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    final Options options;
    if (subCommand == CDCLIOptions.SubCommand.HELP) {
      options = cdcliOptions.getOptions();
    }
    else {
      options = cdcliOptions.getOptions(subCommand);
    }
    formatter.printHelp(JAR_NAME, options);
  }
}
