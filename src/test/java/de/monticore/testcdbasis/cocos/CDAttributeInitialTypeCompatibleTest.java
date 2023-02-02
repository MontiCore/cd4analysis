/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4analysis.typescalculator.FullDeriveFromCD4Analysis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeInitialTypeCompatible;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;

public class CDAttributeInitialTypeCompatibleTest extends CDBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeInitialTypeCompatible(new FullDeriveFromCD4Analysis()));
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeInitialTypeCompatible(new FullDeriveFromCD4Analysis()));
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdbasis/cocos/CDAttributeInitialTypeCompatibleInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(3, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC02"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDC02"));
    assertTrue(Log.getFindings().get(2).getMsg().startsWith("0xCDC02"));
  }

  @After
  public void after() {}
}
