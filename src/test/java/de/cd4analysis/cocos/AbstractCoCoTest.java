/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.antlr.v4.runtime.RecognitionException;

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
   * The {@link CD4AnalysisCoCoChecker} to use with a set of CoCos already
   * assigned.
   * 
   * @return
   */
  abstract protected CD4AnalysisCoCoChecker getChecker();
  
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
    CD4AnalysisCoCoChecker checker = getChecker();
    
    ASTCDCompilationUnit root = loadModel(modelName);
    checker.checkAll(root);
    Assert.assertEqualErrorCounts(expectedErrorSuffixes, LogMock.getFindings());
    Assert.assertErrorsWithSuffix(expectedErrorSuffixes, LogMock.getFindings());
  }
  
  /**
   * Asserts that no error occurred when the {@link CD4AnalysisCoCoChecker} run
   * on the given modelName.
   * 
   * @param modelName
   */
  protected void testModelNoErrors(String modelName) {
    CD4AnalysisCoCoChecker checker = getChecker();
    ASTCDCompilationUnit root = loadModel(modelName);
    checker.checkAll(root);
    Assert.assertEqualErrorCounts(new ArrayList<String>(), LogMock.getFindings());
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
