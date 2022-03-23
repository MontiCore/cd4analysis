/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeCoCoTest extends CD4CodeTestBasis {

  @Test
  public void importModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Import.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);

    final ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    assertNotNull(scope.resolveCDType("C"));

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cdbasis/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }

  /**
   * tests if CoCo CDAssociationUniqueInHierarchy() works properly
   */
  @Test
  public void testGeneratorCoco() throws IOException {
    Log.enableFailQuick(false);

    // set-up GlobalScope
    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(gscope);

    // set-up CoCoChecker
    CD4AnalysisCoCos generalCoCos = new CD4AnalysisCoCos();
    CD4AnalysisCoCoChecker checker = generalCoCos.createNewChecker();
    checker.addCoCo(new CDAssociationUniqueInHierarchy());

    // assert that Auction.cd is suitable for code generation
    ASTCDCompilationUnit goodAST = prepareAST(getFilePath("cd4code"
        + "/generator/Auction.cd"));
    checker.checkAll(goodAST);
    assertNoErrors();

    // assert that Animals.cd is not suitable for code generation
    ASTCDCompilationUnit badAST = prepareAST(getFilePath("cd4code"
        + "/generator/Animals.cd"));

    try {
      checker.checkAll(badAST);
    } catch (Exception e){
      Log.warn(e.getMessage());
    }
    assertErrors("0xCDCE1: Ape redefines an association of Animal.");
    Log.clearFindings();
  }

  /**
   * helper-method for setting-up ASTCDCompilationUnit for testGeneratorCoco()
   */
  protected ASTCDCompilationUnit prepareAST(String filePath) throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(filePath);
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    new CD4CodeAfterParseTrafo().transform(node);
    new CD4CodeDirectCompositionTrafo().transform(node);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    return node;
  }
}
