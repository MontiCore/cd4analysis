/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.RecognitionException;

import cd4analysis.cocos.CoCoChecker;
import cd4analysis.cocos.CoCoError;

import com.google.common.base.Optional;

import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.se_rwth.commons.logging.Log;

/**
 * This test loads invalid models and ensures that the CoCos fire their expected
 * error-codes and error-messages.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CoCosFireForInvalidModelsTest {
  
  private String logname;
  
  private String modelPath;
  
  /**
   * Constructor for de.cd4analysis.cocos.CoCosFireForInvalidModelsTest
   * 
   * @param modelTests
   * @param logname
   * @param modelPath
   */
  public CoCosFireForInvalidModelsTest(String logname, String modelPath) {
    this.logname = logname;
    this.modelPath = modelPath;
  }
  
  /**
   * Loads the models and executes all CoCos registered in the checker and
   * collects their errors and asserts, that all expected errors occur.
   * 
   * @param checker
   * @param invalidModelsCoCoTests
   */
  public void testCoCosForInvalidModels(CoCoChecker checker,
      Collection<InvalidModelTest> invalidModelsCoCoTests) {
    for (InvalidModelTest invalidModelTest : invalidModelsCoCoTests) {
      Log.info("Checking CoCos for invalid model " + invalidModelTest.modelFilename, logname);
      
      ASTCDCompilationUnit cdDef = loadModel(invalidModelTest.modelFilename);
      Collection<CoCoError> actualErrors = new ArrayList<CoCoError>();
      
      if (!checker.checkAll(cdDef)) {
        actualErrors.addAll(checker.getErrors());
        checker.clearErrors();
      }
      
      // log the coco errors
      Collection<String> fullMsgs = actualErrors.stream().map(e -> e.buildErrorMsg())
          .collect(Collectors.toList());
      for (String msg : fullMsgs) {
        Log.info(msg, logname);
      }
      
      // check that all expected error codes and their corresponding messages
      // are included in the actual errors.
      for (ExpectedCoCoError expectedError : invalidModelTest.expectedErrors) {
        boolean found = false;
        for (CoCoError actualError : actualErrors) {
          if (actualError.getErrorCode().equals(expectedError.code)
              && actualError.getErrorMessage().equals(expectedError.msg)) {
            found = true;
          }
        }
        assertTrue("Could not find expected CoCoError " + expectedError.toString() + " for model "
            + invalidModelTest.modelFilename, found);
      }
      
      // ensure that we do not have other errors than the ones expected
      assertEquals("Unexpected count of CoCoErrors for model " + invalidModelTest.modelFilename,
          invalidModelTest.expectedErrors.size(), actualErrors.size());
      
    }
  }
  
  private ASTCDCompilationUnit loadModel(String modelFilename) {
    Path model = Paths.get(modelPath + modelFilename);
    CDCompilationUnitMCParser parser = new CDCompilationUnitMCParser();
    try {
      Optional<ASTCDCompilationUnit> cdDef = parser.parse(model.toString());
      return cdDef.get();
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Error during model loading.");
  }
  
  protected static class InvalidModelTest {
    public String modelFilename;
    
    public Collection<ExpectedCoCoError> expectedErrors;
    
    public InvalidModelTest(
        String modelFilename,
        Collection<ExpectedCoCoError> expectedErrors) {
      this.modelFilename = modelFilename;
      this.expectedErrors = expectedErrors;
    }
  }
  
  protected static class ExpectedCoCoError {
    public String code;
    
    public String msg;
    
    public ExpectedCoCoError(String code, String msg) {
      this.code = code;
      this.msg = msg;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "ExpectedCoCoError [code=" + this.code + ", msg=" + this.msg + "]";
    }
  }
  
}
