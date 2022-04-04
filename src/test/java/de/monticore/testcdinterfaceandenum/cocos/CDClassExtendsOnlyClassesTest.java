/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.cocos;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeNameLowerCaseIfNotStatic;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumCoCoChecker;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDClassExtendsOnlyClasses;
import de.monticore.testcdinterfaceandenum.CDInterfaceAndEnumTestBasis;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDClassExtendsOnlyClassesTest extends CD4AnalysisTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDClassExtendsOnlyClasses());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdinterfaceenum/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDClassExtendsOnlyClasses());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdinterfaceenum/cocos/CDClassExtendsOnlyClassesInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC08"));
  }

  private ICD4AnalysisArtifactScope createSymTab(ASTCDCompilationUnit ast) {
    ICD4AnalysisArtifactScope as = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(ast);
    CD4AnalysisSymbolTableCompleter c = new CD4AnalysisSymbolTableCompleter(
      ast.getMCImportStatementList(),  MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
    return as;
  }

  @After
  public void after(){}

}
