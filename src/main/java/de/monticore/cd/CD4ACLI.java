/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.cd;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreatorDelegator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


public class CD4ACLI {

  static Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);


  private static String JAR_NAME = "cd4anaylsis-<Version>-cli.jar";

  private static String SUCCESSFUL = "Parsing and CoCo check Successful!";

  private String modelFile;

  private ASTCDCompilationUnit ast;

  private CD4ACLI() {
  }

  public static void main(String[] args) throws IOException {

    root.setLevel(Level.WARN);

    CD4ACLI cli = new CD4ACLI();

    if (cli.handleArgs(args)) {
      cli.parse();
      cli.createSymTab();
      cli.checkCocos();
      System.out.println(SUCCESSFUL);
    }
  }

  protected void createSymTab() {
    CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    CD4AnalysisGlobalScope globalScope = new CD4AnalysisGlobalScope(new ModelPath(), cdLanguage);
    CD4AnalysisSymbolTableCreatorDelegator stc = cdLanguage
            .getSymbolTableCreator(globalScope);
    stc.createFromAST(ast);
  }

  protected void parse() throws IOException {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> cu = parser
        .parseCDCompilationUnit(modelFile);
    ast = cu.get();
  }

  protected void checkCocos() {
    new CD4ACoCos().getCheckerForAllCoCos().checkAll(ast);
  }

  protected boolean handleArgs(String[] args) throws NoSuchFileException {

    if (args.length != 1 || args.length == 1 && "-h".equals(args[0])) {
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
    System.out.println("Usage: " + JAR_NAME + " CDMODELFILE");
  }

}
