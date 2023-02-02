/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * The corresponding coco is superfluous, as this case is already dealt with in the construction
 * of the symbol table.
 */
public class CDAssociationSrcAndTargetTypeExistCheckerTest extends CDAssociationTestBasis {

  @Test
  public void testValid() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(
            getFilePath("cdassociation/cocos/CDAssociationSrcAndTargetTypeExistCheckerInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0324"));
  }

  @After
  @Override
  public void after() {}
}
