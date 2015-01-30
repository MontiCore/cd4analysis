/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.antlr.v4.runtime.RecognitionException;

import cd4analysis.CD4ACoCos;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.cocos.LogMock;
import de.monticore.cocos.helper.Assert;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public abstract class AbstractCoCoTest {
  private String modelPath;
  
  private CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
  
  /**
   * Constructor for de.cd4analysis.cocos.AbstractCoCoTest
   * 
   * @param modelPath
   */
  public AbstractCoCoTest(String modelPath) {
    this.modelPath = modelPath;
  }
  
  /**
   * Asserts that each of the expectedErrorSuffixes is found as a suffix in any
   * of the actual produced errors that occurred when the
   * {@link CD4AnalysisCoCoChecker} run on the given modelName. Furthermore, it
   * is asserted that there are not any other errors.
   * 
   * @param modelName
   * @param expectedErrorSuffixes
   */
  protected void testModelForErrorSuffixes(String modelName,
      Collection<String> expectedErrorSuffixes) {
    CD4AnalysisCoCoChecker checker = new CD4ACoCos().getCheckerForAllCoCos();
    
    ASTCDCompilationUnit root = loadModel(modelName);
    checker.checkAll(root);
    assertEquals(expectedErrorSuffixes.size(), LogMock.getFindings().size());
    Assert.assertErrorsWithSuffix(expectedErrorSuffixes, LogMock.getFindings());    
  }
  
  private ASTCDCompilationUnit loadModel(String modelFilename) {
    Path model = Paths.get(modelPath + modelFilename);
    try {
      com.google.common.base.Optional<ASTCDCompilationUnit> root = parser.parse(model.toString());
      if (root.isPresent()) {
        return root.get();
      }
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Error during loading of model " + modelFilename + ".");
  }
}
