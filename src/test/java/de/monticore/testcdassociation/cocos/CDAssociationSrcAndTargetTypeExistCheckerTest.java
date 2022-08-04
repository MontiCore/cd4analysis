/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;

/*
 * The corresponding coco is superfluous, as this case is already dealt with in the construction
 * of the symbol table.
 */
public class CDAssociationSrcAndTargetTypeExistCheckerTest extends CD4AnalysisTestBasis {

  @Test
  public void testValid() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdassociation/cocos/CDAssociationSrcAndTargetTypeExistCheckerInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0324"));
  }
  private ICD4AnalysisArtifactScope createSymTab(ASTCDCompilationUnit ast) {
    ICD4AnalysisArtifactScope as = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
    CD4AnalysisSymbolTableCompleter c = new CD4AnalysisSymbolTableCompleter(
      ast.getMCImportStatementList(),  MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
    return as;
  }


  @After
  @Override
  public void after() {}
}
