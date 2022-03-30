/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeNameLowerCaseIfNotStatic;
import de.monticore.io.paths.MCPath;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import de.monticore.testcdbasis.CDBasisTestBasis;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._cocos.TestCDBasisCoCoChecker;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._symboltable.ITestCDBasisGlobalScope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

import static de.monticore.cd.TestBasis.getFilePath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAttributeNameLowerCaseIfNotStaticTest extends CD4AnalysisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/CDAssociationUniqueInHierarchyValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdbasis/cocos/CDAttributeNameLowerCaseIfNotStaticInvalid.cd"));
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
