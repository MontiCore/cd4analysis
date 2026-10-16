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
  public void testVisibilityValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationModifierCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoVisibilityValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testVisibilityInValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationModifierCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoVisibilityInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().size() == 2);
  }
  
  //TODO: LD do we need a spoitive test or upgrade InvalidTest?
  //TODO: LD Improve (Log.getFindings().size() == 6)?
  
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
    assertTrue(Log.getFindings().size() == 6);
    Log.getFindings().clear();
  }
  
  //TODO: test readOnly
}
