/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeLanguage;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
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

  private ASTCDCompilationUnit ast;

  private CDCLI() {
  }

  public static void main(String[] args) throws IOException {

    root.setLevel(Level.WARN);

    CDCLI cli = new CDCLI();

    if (cli.handleArgs(args)) {
      cli.parse();
      cli.createSymTab();
      cli.checkCocos();
      System.out.println(SUCCESSFUL);
    }
  }

  protected void createSymTab() {
    CD4CodeLanguage cdLanguage = new CD4CodeLanguage();
    CD4CodeGlobalScope globalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder().setModelPath(new ModelPath()).setCD4CodeLanguage(cdLanguage).build();
    CD4CodeSymbolTableCreatorDelegator stc = cdLanguage
        .getSymbolTableCreator(globalScope);
    stc.createFromAST(ast);
  }

  protected void parse() throws IOException {
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> cu = parser
        .parseCDCompilationUnit(modelFile);
    ast = cu.get();
  }

  protected void checkCocos() {
    new CD4AnalysisCoCos().getCheckerForAllCoCos().checkAll(ast);
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
  }

  protected boolean handleArgs(String[] args) throws NoSuchFileException {
    if (args.length != 1 || "-h".equals(args[0])) {
      printUsage();
      return false;
    }

    modelFile = args[0];
    if (!modelFileExists()) {
      throw new NoSuchFileException(modelFile);
    }
    return true;
  }

  private boolean modelFileExists() {
    Path filePath = Paths.get(modelFile);
    return Files.exists(filePath);
  }

  private void printUsage() {
    System.out.println("Usage: " + JAR_NAME + " <CD_MODEL_FILE>");
  }
}
