package de.monticore.testcdbasis.cocos;

import de.monticore.cdbasis._cocos.CDBasisASTCDTargetImportStatementCoCo;
import de.monticore.cdbasis.cocos.CDOnlyResolvableImportStatements;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesASTMCImportStatementCoCo;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDOnlyResolvableImportStatementsTest extends CDBasisTestBasis {

  @Test
  public void testInvalidTargetImportStatement() throws IOException {
    CDOnlyResolvableImportStatements coCo = new CDOnlyResolvableImportStatements();
    coCoChecker.addCoCo((CDBasisASTCDTargetImportStatementCoCo) coCo);
    coCoChecker.addCoCo((MCBasicTypesASTMCImportStatementCoCo) coCo);
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdbasis/cocos/CDUnresolvableTargetImportStatementsInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC40"));
  }

  @Test
  public void testInvalidMCImportStatement() throws IOException {
    CDOnlyResolvableImportStatements coCo = new CDOnlyResolvableImportStatements();
    coCoChecker.addCoCo((CDBasisASTCDTargetImportStatementCoCo) coCo);
    coCoChecker.addCoCo((MCBasicTypesASTMCImportStatementCoCo) coCo);
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdbasis/cocos/CDUnresolvableMCImportStatementsInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC41"));
  }

  @Override
  public void after() {}
}
