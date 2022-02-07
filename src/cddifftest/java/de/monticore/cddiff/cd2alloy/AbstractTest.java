/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy;

import de.monticore.cd2alloy.cocos.CD2AlloyCoCos;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Provides some helpers for tests.
 *
 */
abstract public class AbstractTest {

  public ASTCDCompilationUnit mvAst = parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
  public ASTCDCompilationUnit m1Ast  = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v1.cd");
  public ASTCDCompilationUnit m2Ast  = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v2.cd");


  /**
   * Parses a model and ensures that the root node is present.
   *
   * @param modelFile the full file name of the model.
   * @return the root of the parsed model.
   */
  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      return optAutomaton.get();
    } catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": "
          + e.getMessage());
    }

    return null;
  }

  /**
   * Parses an invalid model and ensures that an error is thrown
   *
   * @param modelFile the full file name of the model.
   * @return the root of the parsed model.
   * @throws IOException
   */
  protected ASTCDCompilationUnit parseInvalidModel(String modelFile) throws IOException {
    Path model = Paths.get(modelFile);
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> optAutomaton;

    optAutomaton = parser.parse(model.toString());
    //assertFalse(parser.hasErrors());
    assertTrue(optAutomaton.isPresent());
    CD2AlloyCoCos cd2aCoCos = new CD2AlloyCoCos();
    CD4AnalysisCoCoChecker cocos = cd2aCoCos.getCheckerForAllCoCos();

    cocos.checkAll(optAutomaton.get());

    return optAutomaton.get();

  }

  /**
   * Checks if a String matches a legal alloy struct
   *
   * @param prefix Name prefix of the struct
   * @param structs String representation of an alloy module
   * @param startIndex optional start index, if struct starts with comments
   */
  protected void checkAlloyStructs(String prefix, String[] structs, int... startIndex) {
    // Check inputs
    if(startIndex.length > 1) {
      fail();
      return;
    }

    // Set default to 0
    int start = (startIndex.length == 1) ? startIndex[0] : 0;

    // All skipped indexes must be comments
    for (int i = 0; i < start; i++) {
      assertTrue(structs[i].matches(".*\\/\\/.*"));
    }

    // Check structure
    for (int i = start; i < structs.length; i++) {
      // Remove all white spaces
      String currentLine = structs[i].replaceAll("\\p{Space}", "");

      // Check the general structure of the String
      assertTrue(currentLine.matches(prefix + "\\[.*[\\]]"));
    }
  }
}
