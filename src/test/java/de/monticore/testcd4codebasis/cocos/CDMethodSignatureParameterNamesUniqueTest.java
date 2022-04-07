/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.cocos;


import de.monticore.cd4codebasis.cocos.ebnf.CDMethodSignatureParameterNamesUnique;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.monticore.testcd4codebasis._cocos.TestCD4CodeBasisCoCoChecker;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.cd.TestBasis.getFilePath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDMethodSignatureParameterNamesUniqueTest extends CD4CodeBasisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDMethodSignatureParameterNamesUnique());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cd4codebasis/cocos/CDMethodSignatureParameterNamesUniqueValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDMethodSignatureParameterNamesUnique());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cd4codebasis/cocos/CDMethodSignatureParameterNamesUniqueInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC90"));
  }

  @After
  public void after(){}

}
