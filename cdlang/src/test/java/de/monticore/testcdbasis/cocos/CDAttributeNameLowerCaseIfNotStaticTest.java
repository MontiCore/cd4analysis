/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import static de.monticore.cd.TestBasis.getFilePath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeNameLowerCaseIfNotStatic;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;

public class CDAttributeNameLowerCaseIfNotStaticTest extends CDBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdbasis/cocos/CDAttributeNameLowerCaseIfNotStaticInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC03"));
  }

  @After
  @Override
  public void after() {}
}
