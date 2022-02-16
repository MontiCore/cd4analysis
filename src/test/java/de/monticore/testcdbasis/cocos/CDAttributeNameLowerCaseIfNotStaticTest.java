/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeNameLowerCaseIfNotStatic;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._cocos.TestCDBasisCoCoChecker;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.cd.TestBasis.getFilePath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAttributeNameLowerCaseIfNotStaticTest {

  private static final TestCDBasisCoCoChecker checker = new TestCDBasisCoCoChecker();

  @Before
  public void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestCDBasisMill.reset();
    TestCDBasisMill.init();
    checker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
  }

  @Test
  public void testValid() throws IOException {
    TestCDBasisParser parser = new TestCDBasisParser();
    final Optional<ASTCDCompilationUnit> optAST = parser.parse(getFilePath("cdbasis/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    checker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    TestCDBasisParser parser = new TestCDBasisParser();
    final Optional<ASTCDCompilationUnit> optAST = parser.parse(getFilePath("cdbasis/cocos/CDAttributeNameLowerCaseIfNotStaticInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    checker.checkAll(ast);
    Log.getFindings().stream().map(Finding::getMsg).forEach(System.out::println);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC03"));
  }


}
