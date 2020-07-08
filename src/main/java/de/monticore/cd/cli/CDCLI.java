/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreator;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.logging.Log;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CDCLI {

  static Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

  private static final String JAR_NAME = "cd-<Version>-cli.jar";

  private static final String SUCCESSFUL = "Parsing and CoCo check Successful!";

  private String modelFile;

  private boolean useBuiltInTypes;

  private boolean failQuick;

  private ASTCDCompilationUnit ast;

  private CDCLI() {
  }

  public static void main(String[] args) throws IOException {

    root.setLevel(Level.WARN);

    CDCLI cli = new CDCLI();
    Log.enableFailQuick(cli.failQuick);

    if (cli.handleArgs(args)) {
      cli.parse();
      cli.createSymTab();
      cli.checkCocos();
      System.out.println(SUCCESSFUL);
    }
  }

  protected void parse() throws IOException {
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> cu = parser
        .parseCDCompilationUnit(modelFile);
    ast = cu.get();
  }

  protected void createSymTab() {
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

  protected boolean handleArgs(String[] args)
      throws NoSuchFileException {
    final CLIArguments arguments = CLIArguments.forArguments(args);
    final CDCLIConfiguration configuration = CDCLIConfiguration.fromArguments(arguments);

    if (configuration.isSetHelp() || !configuration.isPresentModelFile()) {
      printHelp();
      return false;
    }

    modelFile = configuration.getModelFile().get();
    if (!modelFileExists()) {
      throw new NoSuchFileException(modelFile);
    }

    useBuiltInTypes = configuration.useBuiltInTypes();
    failQuick = configuration.isSetFailQuick();

    return true;
  }

  private boolean modelFileExists() {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  private void printHelp() {
    System.out.println("Usage: " + JAR_NAME + " <CD_MODEL_FILE>");
  }
}
