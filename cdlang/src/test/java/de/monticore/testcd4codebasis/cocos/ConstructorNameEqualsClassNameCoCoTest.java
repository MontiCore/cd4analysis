/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ConstructorNameEqualsClassNameCoCo;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ConstructorNameEqualsClassNameCoCoTest extends CD4CodeBasisTestBasis {
  
  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new ConstructorNameEqualsClassNameCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(
        getFilePath("cd4codebasis/cocos/ConstructorNameEqualsClassNameValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty(), "Valid constructors should not produce errors");
  }
  
  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new ConstructorNameEqualsClassNameCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(
        getFilePath("cd4codebasis/cocos/ConstructorNameEqualsClassNameInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size(), "Invalid constructors should produce 2 errors");
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC50"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDC50"));
  }
  
  @AfterEach
  public void after() {}
  
}