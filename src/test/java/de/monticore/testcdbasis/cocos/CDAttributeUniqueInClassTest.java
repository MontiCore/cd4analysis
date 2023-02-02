/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeUniqueInClass;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAttributeUniqueInClassTest extends CDBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeUniqueInClass());
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
    coCoChecker.addCoCo(new CDAttributeUniqueInClass());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdbasis/cocos/CDAttributeUniqueInClassInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    CDBasisMill.scopesGenitorDelegator().createFromAST(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC06"));
  }

  @After
  @Override
  public void after() {}
}
