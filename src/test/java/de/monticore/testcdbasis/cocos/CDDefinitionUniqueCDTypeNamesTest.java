/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDDefinitionUniqueCDTypeNames;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;

public class CDDefinitionUniqueCDTypeNamesTest extends CD4AnalysisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDDefinitionUniqueCDTypeNames());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    CDBasisMill.scopesGenitorDelegator().createFromAST(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDDefinitionUniqueCDTypeNames());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdbasis/cocos/CDDefinitionUniqueCDTypeNamesInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    CDBasisMill.scopesGenitorDelegator().createFromAST(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC0D"));
  }

  @After
  public void after() {}
}
