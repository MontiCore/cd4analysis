/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cd.misc.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Before;

public class CD4AnalysisTestBasis extends TestBasis {
  protected CD4AnalysisCoCoChecker coCoChecker;
  protected CD4AnalysisParser p;
  protected CD4AnalysisFullPrettyPrinter printer;
  protected CD4AnalysisSymbols2Json symbols2Json;

  @Before
  public void initObjects() {
    LogStub.init();
    Log.enableFailQuick(false);
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    p = new CD4AnalysisParser();
    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill.globalScope();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);

    coCoChecker = new CD4AnalysisCoCoChecker();
    printer = new CD4AnalysisFullPrettyPrinter();
    symbols2Json = new CD4AnalysisSymbols2Json();
  }

  protected ASTCDCompilationUnit parse(String filePath) throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(filePath));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    // Trafos after parsing
    new CD4AnalysisAfterParseTrafo().transform(node);
    return node;
  }

  protected void prepareST(ASTCDCompilationUnit node) throws IOException {
    // First pass for symbol table
    ICD4AnalysisArtifactScope scope = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);
    scope.addImports(new ImportStatement("java.lang", true));
    checkLogError();

    // Second pass for symbol table
    final CD4AnalysisTraverser traverser = new CD4AnalysisSymbolTableCompleter(node).getTraverser();
    node.accept(traverser);
    checkLogError();

    // transformations that need an already created symbol table
    final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
    final CDAssociationTraverser traverserA = CD4AnalysisMill.traverser();
    traverserA.add4CDAssociation(cdAssociationRoleNameTrafo);
    traverserA.setCDAssociationHandler(cdAssociationRoleNameTrafo);
    cdAssociationRoleNameTrafo.transform(node);
    checkLogError();
  }
}
