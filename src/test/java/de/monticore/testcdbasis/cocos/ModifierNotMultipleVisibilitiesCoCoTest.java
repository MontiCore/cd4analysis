/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.mcg.ModifierNotMultipleVisibilitiesCoCo;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;

public class ModifierNotMultipleVisibilitiesCoCoTest extends CDBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdbasis/cocos/ModifierNotMultipleVisibilitiesInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC10"));
  }

  @After
  public void after() {}
}
