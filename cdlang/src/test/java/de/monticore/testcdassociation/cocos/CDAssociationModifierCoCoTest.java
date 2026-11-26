/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import de.monticore.cdassociation.cocos.ebnf.CDAssociationModifierCoCo;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationVisibilityConflictCoCo;
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
  public void testVisibilityConflictWithoutSymbolTable() throws IOException {
    // The visibility check must also work before the symbol table is created
    coCoChecker.addCoCo(new CDAssociationVisibilityConflictCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoInvalid.cd"));
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    coCoChecker.checkAll(optAST.get());
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().stream().allMatch(f -> f.isWarning() && f.getMsg().startsWith(
        CDAssociationVisibilityConflictCoCo.ERROR_CODE)));
    Log.getFindings().clear();
  }
  
  @Test
  public void testVisibilityConflictAfterTrafos() throws IOException {
    // The variant for transformed models reports a different error code
    coCoChecker.addCoCo(new CDAssociationModifierCoCo(true));
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().stream().filter(f -> f.isWarning() && f.getMsg().startsWith(
        CDAssociationVisibilityConflictCoCo.ERROR_CODE_AFTER_TRAFOS)).count());
    assertTrue(Log.getFindings().stream().noneMatch(f -> f.getMsg().startsWith(
        CDAssociationVisibilityConflictCoCo.ERROR_CODE)));
    Log.getFindings().clear();
  }
  
  @Test
  public void testMatchingVisibilityWithoutSymbolTable() throws IOException {
    coCoChecker.addCoCo(new CDAssociationVisibilityConflictCoCo());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationModifierCoCoValid.cd"));
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    coCoChecker.checkAll(optAST.get());
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
