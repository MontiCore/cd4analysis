/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilities;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import org.antlr.v4.runtime.RecognitionException;

/**
 * Class to store input and output parameters and reads and writes an ast (classdiagram) from and to
 * files
 *
 * <p>Created by
 *
 * @author KE
 */
public class FileUtility {

  // To store ast read from the path inputFolder/inputFile
  protected ASTCDCompilationUnit ast;
  private static final String INPUTFOLDER = "src/main/models/";
  private static final String OUTPUTFOLDER = "target/generated-models/";

  // Constructor for setting input file and folder
  public FileUtility(String inputFile, String inputFolder) {
    ast = readAstFromFile(inputFile, inputFolder);
  }

  // Constructor for input file
  public FileUtility(String inputFile) {
    ast = readAstFromFile(inputFile, INPUTFOLDER);
  }

  // Returns stored ast
  public ASTCDCompilationUnit getAst() {
    return ast;
  }

  /**
   * Reads an AST (classdiagram) from an input file
   *
   * @return AST of the class diagram
   */
  private ASTCDCompilationUnit readAstFromFile(String inputFile, String inputFolder) {
    Optional<ASTCDCompilationUnit> ast;

    try {
      ast = CD4CodeMill.parser().parse(inputFolder + inputFile + ".cd");
      if (ast.isPresent()) {
        return ast.get();
      } else {
        Log.error("AST from File " + inputFolder + inputFile + ".cd ist not present.");
        return null;
      }
    } catch (RecognitionException e) {
      Log.error("Could not read File " + inputFolder + inputFile + ".cd.", e);
      return null;
    } catch (IOException e) {
      Log.error("Could not read ast from file " + inputFolder + inputFile + ".cd", e);
      return null;
    }
  }

  /**
   * Writes an AST of a class diagram to an file
   *
   * <p>of the output file
   */
  public void writeAst(String outputFile, String outputFolder) {

    // Write AST to String
    IndentPrinter i = new IndentPrinter();
    CD4CodeFullPrettyPrinter prettyprinter = new CD4CodeFullPrettyPrinter(i);
    String output = prettyprinter.prettyprint(ast);

    // Create folder
    File dir = new File(outputFolder);
    dir.mkdirs();

    // Write file
    FileWriter outputFileWriter;
    try {
      outputFileWriter = new FileWriter(outputFolder + outputFile + ".cd");
      BufferedWriter bw = new BufferedWriter(outputFileWriter);
      bw.write(output);
      bw.close();
    } catch (IOException e) {
      Log.error("Could not write File " + outputFolder + outputFile + ".cd" + ".", e);
    }
  }

  // Write stored ast to a file with name outputFile
  public void writeAst(String outputFile) {
    writeAst(outputFile, OUTPUTFOLDER);
  }
}
