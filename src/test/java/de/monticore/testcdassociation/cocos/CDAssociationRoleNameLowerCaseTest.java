/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationRoleNameLowerCase;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;

public class CDAssociationRoleNameLowerCaseTest extends CD4AnalysisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationRoleNameLowerCase());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  private ICD4AnalysisArtifactScope createSymTab(ASTCDCompilationUnit ast) {
    ICD4AnalysisArtifactScope as = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
    CD4AnalysisSymbolTableCompleter c =
        new CD4AnalysisSymbolTableCompleter(
            ast.getMCImportStatementList(), MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
    return as;
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationRoleNameLowerCase());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdassociation/cocos/CDAssociationRoleNameLowerCaseInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC66"));
  }

  @After
  @Override
  public void after() {}
}
