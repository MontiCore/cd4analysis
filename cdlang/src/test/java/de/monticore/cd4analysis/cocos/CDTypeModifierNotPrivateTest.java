/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.CDTypeModifierNotPrivate;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;

public class CDTypeModifierNotPrivateTest extends CD4AnalysisTestBasis {

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDTypeModifierNotPrivate());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cd4analysis/cocos/CDTypeModifierNotPrivateInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    CDBasisMill.scopesGenitorDelegator().createFromAST(ast);
    coCoChecker.checkAll(ast);
    assertEquals(3, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(CDTypeModifierNotPrivate.ERROR_CODE));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith(CDTypeModifierNotPrivate.ERROR_CODE));
    assertTrue(Log.getFindings().get(2).getMsg().startsWith(CDTypeModifierNotPrivate.ERROR_CODE));
  }

  @After
  public void after() {}
}
