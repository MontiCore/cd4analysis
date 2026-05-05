/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ConstructorNameEqualsClassNameCoCo;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ConstructorNameEqualsClassNameCoCoTest extends CD4CodeBasisTestBasis {
  
  @AfterEach
  public void after() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testValid() throws IOException {
    // Given
    coCoChecker.addCoCo(new ConstructorNameEqualsClassNameCoCo());
    
    // When
    final ASTCDCompilationUnit ast = p.parse(getFilePath(
        "cd4codebasis/cocos/ConstructorNameEqualsClassNameValid.cd")).orElseThrow();
    coCoChecker.checkAll(ast);
    
    // Then
    assertTrue(Log.getFindings().isEmpty(), "Valid constructors should not produce errors");
  }
  
  @Test
  public void testInvalid() throws IOException {
    // Given
    coCoChecker.addCoCo(new ConstructorNameEqualsClassNameCoCo());
    
    // When
    final ASTCDCompilationUnit ast = p.parse(getFilePath(
        "cd4codebasis/cocos/ConstructorNameEqualsClassNameInvalid.cd")).orElseThrow();
    coCoChecker.checkAll(ast);
    
    // Then
    assertEquals(2, Log.getFindings().size(), "Invalid constructors should produce 2 errors");
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC50"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDC50"));
  }
  
}
