/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/*
 * The corresponding coco is superfluous, as this case is already dealt with in the construction
 * of the symbol table.
 */
public class CDAssociationSrcAndTargetTypeExistCheckerTest extends CDAssociationTestBasis {
  
  @Test
  public void testValid() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testInvalid() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdassociation/cocos/CDAssociationSrcAndTargetTypeExistCheckerInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    assertEquals(1, Log.getFindings().size(), Log.getFindings().toString());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0324"));
  }
  
  @AfterEach
  @Override
  public void after() {}
  
}
