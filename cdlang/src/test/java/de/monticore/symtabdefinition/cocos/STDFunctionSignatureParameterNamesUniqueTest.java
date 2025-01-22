/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symtabdefinition.SymTabDefinitionTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class STDFunctionSignatureParameterNamesUniqueTest extends SymTabDefinitionTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new STDFunctionSignatureParameterNamesUnique());
    final ASTCDCompilationUnit ast =
        parse("stdefinition/cocos/STDFunctionSignatureParameterNamesUniqueValid.cd");
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    checkLogError();
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new STDFunctionSignatureParameterNamesUnique());
    final ASTCDCompilationUnit ast =
        parse("stdefinition/cocos/STDFunctionSignatureParameterNamesUniqueInvalid.cd");
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xFDC90"));
  }

  @Override
  public void after() {}
}
