/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymbolTableCreatorDelegator;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cocos.helper.Assert;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public abstract class AbstractCoCoTest {
  private final CD4AnalysisLanguage cd4AnalysisLang = new CD4AnalysisLanguage();
  
  private CD4AnalysisParser parser = new CD4AnalysisParser();
  
  private
  CD4AnalysisGlobalScope globalScope;
  
  protected ICD4AnalysisScope cdScope;
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.AbstractCoCoTest
   */
  public AbstractCoCoTest() {
  }
  
  /**
   * The {@link CD4AnalysisCoCoChecker} to use with a set of CoCos already
   * assigned.
   * 
   * @return
   */
  abstract protected CD4AnalysisCoCoChecker getChecker();
  
  /**
   * Asserts that each of the expectedErrors is found (checking code and msg) in
   * any of the actual produced errors that occurred when the
   * {@link CD4AnalysisCoCoChecker} run on the given modelName. Furthermore, it
   * is asserted that there are not any other errors.
   * 
   * @param model full qualified model path
   * @param expectedErrors
   */
  protected ASTCDCompilationUnit testModelForErrors(String model,
      Collection<Finding> expectedErrors) {
    CD4AnalysisCoCoChecker checker = getChecker();
    
    ASTCDCompilationUnit root = loadModel(model);
    checker.checkAll(root);
    Collection<Finding> errors = Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());
    Assert.assertEqualErrorCounts(expectedErrors, errors);
    Assert.assertErrorMsg(expectedErrors, errors);
    return root;
  }
  
  /**
   * Asserts that no error occurred when the {@link CD4AnalysisCoCoChecker} run
   * on the given modelName.
   * 
   * @param model full qualified model path
   */
  protected ASTCDCompilationUnit testModelNoErrors(String model) {
    CD4AnalysisCoCoChecker checker = getChecker();
    ASTCDCompilationUnit root = loadModel(model);
    checker.checkAll(root);
    assertEquals(0,
        Log.getFindings().stream().filter(f -> f.isError()).count());
    return root;
  }
  
  protected ASTCDCompilationUnit loadModel(String modelFullQualifiedFilename) {
    Path model = Paths.get(modelFullQualifiedFilename);
    
    try {
      Optional<ASTCDCompilationUnit> root = parser.parseCDCompilationUnit(model.toString());
      if (root.isPresent()) {
        // create Symboltable        
        ModelPath modelPath = new ModelPath(model.toAbsolutePath());
        this.globalScope = new CD4AnalysisGlobalScope(modelPath, cd4AnalysisLang);
        CD4AnalysisSymbolTableCreatorDelegator stc = cd4AnalysisLang
                .getSymbolTableCreator(globalScope);
        stc.createFromAST(root.get());

        cdScope = globalScope.getSubScopes().get(0).getSubScopes().iterator().next();
        return root.get();
      }
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Error during loading of model " + modelFullQualifiedFilename + ".");
  }
}
