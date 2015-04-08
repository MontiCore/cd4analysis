/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.RecognitionException;

import de.monticore.cocos.CoCoFinding;
import de.monticore.cocos.CoCoLog;
import de.monticore.cocos.helper.Assert;
import de.monticore.umlcd4a._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a._parser.CD4AnalysisParserFactory;
import de.monticore.umlcd4a._parser.CDCompilationUnitMCParser;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public abstract class AbstractCoCoTest {
  private String modelPath;
  
  private CDCompilationUnitMCParser parser = CD4AnalysisParserFactory.createCDCompilationUnitMCParser();
  
  /**
   * Constructor for de.monticore.umlcd4a.cocos.AbstractCoCoTest
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
      Collection<CoCoFinding> expectedErrorSuffixes) {
    CD4AnalysisCoCoChecker checker = getChecker();
    
    ASTCDCompilationUnit root = loadModel(modelName);
    checker.checkAll(root);
    Assert.assertEqualErrorCounts(expectedErrorSuffixes.stream().map(f -> f.buildMsg()).collect(Collectors.toList()), CoCoLog.getFindings().stream().map(f -> f.buildMsg()).collect(Collectors.toList()));
    Assert.assertErrorsWithSuffix(expectedErrorSuffixes.stream().map(f->f.buildMsg()).collect(Collectors.toList()), CoCoLog.getFindings().stream().map(f -> f.buildMsg()).collect(Collectors.toList()));
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
    assertTrue(CoCoLog.getFindings().isEmpty());
  }
  
  private ASTCDCompilationUnit loadModel(String modelFilename) {
    Path model = Paths.get(modelPath + modelFilename);
    try {
      Optional<ASTCDCompilationUnit> root = parser.parse(model.toString());
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
