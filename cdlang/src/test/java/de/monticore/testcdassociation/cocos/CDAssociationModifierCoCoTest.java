/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import de.monticore.cdassociation.cocos.ebnf.CDAssociationModifierCoCo;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNameUnique;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDAssociationModifierCoCoTest extends CDAssociationTestBasis {
  
  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationNameUnique());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  //visibility-modifiers tests
  @Test
  public void testModifiersValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationModifierCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  // static/abstract/local modifier test
  @Test
  public void testModifierInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationModifierCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(13, Log.getFindings().size());
    Log.getFindings().clear();
  }
  
}
