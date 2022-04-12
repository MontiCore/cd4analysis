/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd2alloy.cocos.CD2AlloyCoCos;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Provides some helpers for tests.
 *
 */
abstract public class AbstractTest {

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
  }

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
      //assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      CD2AlloyCoCos cd2aCoCos = new CD2AlloyCoCos();
      CD4AnalysisCoCoChecker cocos = cd2aCoCos.getCheckerForAllCoCos();
      cocos.checkAll(optAutomaton.get());

      return optAutomaton.get();
    } catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": "
          + e.getMessage());
    }

    return null;
  }

  protected void prepareAST(ASTCDCompilationUnit ast){
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    new CD4CodeDirectCompositionTrafo().transform(ast);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    CD4CodeSymbolTableCompleter c = new CD4CodeSymbolTableCompleter(
        ast.getMCImportStatementList(),  MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
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
