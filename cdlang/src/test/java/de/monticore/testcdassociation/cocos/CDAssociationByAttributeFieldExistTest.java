/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cdassociation.cocos.ebnf.CDAssociationByAttributeFieldExist;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class CDAssociationByAttributeFieldExistTest extends CDAssociationTestBasis {
  
  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationByAttributeFieldExist());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdassociation/cocos"
        + "/Valid.cd"));
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
    coCoChecker.addCoCo(new CDAssociationByAttributeFieldExist());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationByAttributeFieldExistInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC6B"));
  }
  
  @AfterEach
  @Override
  public void after() {}
  
}
