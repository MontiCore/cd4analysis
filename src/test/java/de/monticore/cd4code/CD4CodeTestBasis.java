/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CD4CodeTestBasis extends TestBasis {
  protected CD4CodeParser p;
  protected CD4CodeCoCos cd4CodeCoCos;
  protected CD4CodeFullPrettyPrinter printer;
  protected CD4CodeSymbols2Json symbols2Json;

  @Before
  public void initObjects() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    p = new CD4CodeParser();

    final ICD4CodeGlobalScope globalScope = CD4CodeMill
        .globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);

    cd4CodeCoCos = new CD4CodeCoCos();
    printer = new CD4CodeFullPrettyPrinter();
    symbols2Json = new CD4CodeSymbols2Json();
  }

  public static void assertNoErrors() {
    if (Log.getErrorCount() > 0) {
      System.err.println("Expected no errors, but got: ");
      for (Finding f : Log.getFindings()) {
        if (f.isError()) {
          System.err.println(f.toString());
        }
      }
      fail();
    }
  }

  public static void assertErrors(String... errorCodes) {
    assertEquals("Expected and actual number of errors differ!",
        errorCodes.length, Log.getErrorCount());

    for (String expectedErrorCode : errorCodes) {
      boolean foundError = false;
      for (Finding f : Log.getFindings()) {
        if (f.isError() && f.getMsg().startsWith(expectedErrorCode)) {
          foundError = true;
          break;
        }
      }
      if (!foundError) {
        fail("Expected to find an error with the error code '" +
            expectedErrorCode + "', but the error did not occur!");
      }
    }
  }

  @Override
  protected ICDBasisArtifactScope createST(ASTCDCompilationUnit astcdCompilationUnit) {
    final ICD4CodeArtifactScope st = CD4CodeMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    checkLogError();
    return st;
  }
}
