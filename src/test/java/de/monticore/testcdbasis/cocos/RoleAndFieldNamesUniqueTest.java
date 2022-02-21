/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeNameLowerCaseIfNotStatic;
import de.monticore.cdbasis.cocos.ebnf.RoleAndFieldNamesUnique;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoleAndFieldNamesUniqueTest extends CD4AnalysisTestBasis {

  private static CD4AnalysisCoCoChecker coCoChecker;

  @Test
  public void testValid() throws IOException {
    coCoChecker = new CD4AnalysisCoCoChecker();
    coCoChecker.addCoCo(new RoleAndFieldNamesUnique());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker = new CD4AnalysisCoCoChecker();
    coCoChecker.addCoCo(new RoleAndFieldNamesUnique());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/RoleAndFieldNamesUniqueInvalid.cd"));
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
