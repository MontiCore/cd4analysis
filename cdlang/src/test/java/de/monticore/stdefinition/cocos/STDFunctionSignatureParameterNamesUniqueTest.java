/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.stdefinition.STDefinitionTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class STDFunctionSignatureParameterNamesUniqueTest extends STDefinitionTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new STDFunctionSignatureParameterNamesUnique());
    final Optional<ASTCDCompilationUnit> optAST =
        parser.parse(
            getFilePath("stdefinition/cocos/STDFunctionSignatureParameterNamesUniqueValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    checkLogError();
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new STDFunctionSignatureParameterNamesUnique());
    final Optional<ASTCDCompilationUnit> optAST =
        parser.parse(
            getFilePath("stdefinition/cocos/STDFunctionSignatureParameterNamesUniqueInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xFDC90"));
  }

  @Override
  public void after() {}
}
