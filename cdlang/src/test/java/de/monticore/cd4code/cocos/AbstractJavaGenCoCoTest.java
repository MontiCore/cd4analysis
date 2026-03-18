/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractJavaGenCoCoTest extends CD4CodeTestBasis {
  
  protected CD4CodeCoCoChecker checker;
  
  @BeforeEach
  public void setup() {
    this.checker = createChecker();
  }
  
  /**
   * Subclasses must implement this to provide the specific checker with the CoCos to be tested.
   */
  protected abstract CD4CodeCoCoChecker createChecker();
  
  /**
   * Runs a test on the given model.
   *
   * @param model The class diagram model as a string.
   * @param expectError True if an error is expected, false otherwise.
   */
  protected void runTest(String model, boolean expectError) throws IOException {
    runTest(model, expectError, null);
  }
  
  /**
   * Runs a test on the given model, expecting a specific error code.
   *
   * @param model The class diagram model as a string.
   * @param errorCode The expected error code.
   */
  protected void runTestForErrorCode(String model, String errorCode) throws IOException {
    runTest(model, true, errorCode);
  }
  
  private void runTest(String model, boolean expectError, String errorCode) throws IOException {
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse_String(model);
    assertTrue("Failed to parse model: " + model, ast.isPresent());
    
    // Prepare Symbol Table from the base class
    prepareST(ast.get());
    
    // Check CoCos
    checker.checkAll(ast.get());
    
    if (expectError) {
      assertTrue("Expected an error, but none was logged.", Log.getErrorCount() > 0);
      if (errorCode != null) {
        Log.getFindings().forEach(f -> assertTrue("Expected error code '" + errorCode
            + "' but got: " + f.getMsg(), f.getMsg().startsWith(errorCode)));
      }
    }
    else {
      assertFalse("Expected no errors, but found some: " + Log.getFindings(), Log.getErrorCount()
          > 0);
    }
    // Clear findings for the next test
    Log.clearFindings();
  }
  
}
