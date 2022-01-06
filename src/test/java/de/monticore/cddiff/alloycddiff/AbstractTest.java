/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff;

import de.monticore.cddiff.cd2alloy.cocos.CD2AlloyCoCos;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
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
}
