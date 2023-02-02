/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDTypeNoInitializationOfDerivedAttribute;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._symboltable.ITestCDBasisArtifactScope;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTypeNoInitializationOfDerivedAttributeTest extends CDBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDTypeNoInitializationOfDerivedAttribute());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDTypeNoInitializationOfDerivedAttribute());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdbasis/cocos/CDTypeNoInitializationOfDerivedAttributeInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC0C"));
  }

  @After
  public void after() {}
}
