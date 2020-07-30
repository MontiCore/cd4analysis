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
import de.monticore.cd4code._symboltable.CD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CDCLI {

  static final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  protected static final String JAR_NAME = "cd-<Version>-cli.jar";
  protected static final String CHECK_SUCCESSFUL = "Parsing and CoCo check successful!";
  protected static final String PLANTUML_SUCCESSFUL = "Creation of plantUML file %s successful!\n";
  protected static final String PRETTYPRINT_SUCCESSFUL = "Creation of model file %s successful!\n";
  protected static final String STEXPORT_SUCCESSFUL = "Creation of symbol table %s successful!\n";
  protected static final Level DEFAULT_LOG_LEVEL = Level.WARN;

  protected String modelFile;
  protected boolean failQuick;
  protected ASTCDCompilationUnit ast;
  protected CD4CodeArtifactScope artifactScope;
  protected final CDCLIOptions cdcliOptions = new CDCLIOptions();
  protected CommandLine cmd;

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
      Log.error(String.format("0xCD012: options [%s] are missing, but are required", Joiners.COMMA.join(e.getMissingOptions())));
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

  protected void createSymTab(boolean useBuiltInTypes, ModelPath modelPath) {
    CD4CodeGlobalScope globalScope = CD4CodeMill
        .cD4CodeGlobalScopeBuilder()
        .setModelPath(modelPath)
        .addBuiltInTypes(useBuiltInTypes)
        .build();
    final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill
        .cD4CodeSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    artifactScope = symbolTableCreator.createFromAST(ast);
  }

  protected void checkCocos() {
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
  }

  protected String createPlantUML(CommandLine plantUMLCmd) throws IOException {
    final String outputPath = cmd.getOptionValue("pp");

    final PlantUMLConfig plantUMLConfig = new PlantUMLConfig();

    if (plantUMLCmd.hasOption("a")) {
      plantUMLConfig.setShowAtt(true);
    }
    if (plantUMLCmd.hasOption("showAssoc")) {
      plantUMLConfig.setShowAssoc(true);
    }
    if (plantUMLCmd.hasOption("r")) {
      plantUMLConfig.setShowRoles(true);
    }
    if (plantUMLCmd.hasOption("c")) {
      plantUMLConfig.setShowCard(true);
    }
    if (plantUMLCmd.hasOption("m")) {
      plantUMLConfig.setShowModifier(true);
    }
    if (plantUMLCmd.hasOption("nodesep")) {
      plantUMLConfig.setNodesep(Integer.parseInt(plantUMLCmd.getOptionValue("nodesep")));
    }
    if (plantUMLCmd.hasOption("ranksep")) {
      plantUMLConfig.setRanksep(Integer.parseInt(plantUMLCmd.getOptionValue("ranksep")));
    }
    if (plantUMLCmd.hasOption("ortho")) {
      plantUMLConfig.setOrtho(true);
    }
    if (plantUMLCmd.hasOption("s")) {
      plantUMLConfig.setShortenWords(true);
    }
    if (plantUMLCmd.hasOption("showComments")) {
      plantUMLConfig.setShowComments(true);
    }

    if (plantUMLCmd.hasOption("svg")) {
      return PlantUMLUtil.printCD2PlantUMLLocally(Optional.ofNullable(ast), outputPath, plantUMLConfig);
    }
    else {
      return PlantUMLUtil.printCD2PlantUMLModelFileLocally(Optional.ofNullable(ast), outputPath, plantUMLConfig);
    }
  }

  protected boolean handleArgs(String[] args)
      throws IOException, ParseException {
    cmd = cdcliOptions.handleArgs(args);

    if (cmd.hasOption("h")) {
      if (cmd.hasOption("pp")) {
        printHelp(CDCLIOptions.SubCommand.PLANTUML);
      }
      else {
        printHelp(null);
      }
      return false;
    }
    else {
      if (!cmd.hasOption("i")) {
        Log.error(String.format("0xCD014: option '%s' is missing, but an input is required", "i"));
        printHelp(null);
        return false;
      }

      modelFile = cmd.getOptionValue("i");

      if (!modelFileExists()) {
        throw new NoSuchFileException(modelFile);
      }

      if (cmd.hasOption("log")) {
        root.setLevel(Level.toLevel(cmd.getOptionValue("log", DEFAULT_LOG_LEVEL.levelStr), DEFAULT_LOG_LEVEL));
      }

      failQuick = !cmd.hasOption("f");

      return true;
    }
  }

  protected void run() throws IOException, ParseException {
    parse();
    boolean useBuiltInTypes = !cmd.hasOption("t") || Boolean.parseBoolean(cmd.getOptionValue("t", "true"));
    String modelPath = cmd.getOptionValue("p", ".");
    createSymTab(useBuiltInTypes, new ModelPath(Paths.get(modelPath)));
    //checkCocos();
    System.out.println(CHECK_SUCCESSFUL);

    if (cmd.hasOption("pp")) { // pretty print
      final String fileName = cmd.getOptionValue("pp");

      if (cmd.hasOption("puml")) { // plantUML
        final CommandLine plantUMLCmd = cdcliOptions.parse(CDCLIOptions.SubCommand.PLANTUML);
        final String path = createPlantUML(plantUMLCmd);
        System.out.printf(PLANTUML_SUCCESSFUL, path);
      }
      else { // print model
        final CD4CodePrettyPrinter cd4CodePrettyPrinter = CD4CodeMill.cD4CodePrettyPrinter();
        ast.accept(cd4CodePrettyPrinter);

        try (PrintWriter out = new PrintWriter(fileName)) {
          out.println(cd4CodePrettyPrinter.getPrinter().getContent());
          System.out.printf(PRETTYPRINT_SUCCESSFUL, fileName);
        }
      }
    }

    if (cmd.hasOption("s")) { // symbol table export
      CD4CodeMill.cD4CodeScopeDeSerBuilder().build().store(artifactScope, Paths.get(cmd.getOptionValue("s")));
      System.out.printf(STEXPORT_SUCCESSFUL, cmd.getOptionValue("s"));
    }
  }

  protected boolean modelFileExists() {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  protected void printHelp(CDCLIOptions.SubCommand subCommand) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(JAR_NAME, cdcliOptions.getOptions());

    if (subCommand != null) {
      formatter.printHelp(subCommand.toString(), cdcliOptions.getOptions(subCommand));
    }
  }
}
