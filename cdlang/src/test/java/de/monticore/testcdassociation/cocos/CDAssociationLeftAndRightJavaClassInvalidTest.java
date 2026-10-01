/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import de.monticore.cdassociation.cocos.ebnf.CDAssociationJavaClassTypeCoCo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CDAssociationLeftAndRightJavaClassInvalidTest extends CDAssociationTestBasis {
  
  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationJavaClassTypeCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationLeftAndRightJavaClassValid.cd"));
    assertTrue(optAST.isPresent());
    
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testInvalid() throws IOException {
    
    coCoChecker.addCoCo(new CDAssociationJavaClassTypeCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationLeftAndRightJavaClassInvalid.cd"));
    assertTrue(optAST.isPresent());
    
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDA64"));
    assertEquals(4, Log.getFindings().size());
    Log.clearFindings();
  }
  
}
