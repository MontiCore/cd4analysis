/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code;

import static org.junit.Assert.fail;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Before;

public class CD4CodeTestBasis extends TestBasis {
  protected CD4CodeParser p;
  protected CD4CodeCoCos cd4CodeCoCos;
  protected CD4CodeFullPrettyPrinter printer;
  protected CD4CodeSymbols2Json symbols2Json;
  protected CD4CodeCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    LogStub.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    p = new CD4CodeParser();

    final ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);

    cd4CodeCoCos = new CD4CodeCoCos();
    printer = new CD4CodeFullPrettyPrinter();
    symbols2Json = new CD4CodeSymbols2Json();
    coCoChecker = new CD4CodeCoCoChecker();
  }

  protected ASTCDCompilationUnit parse(String filePath) {
    Optional<ASTCDCompilationUnit> astcdCompilationUnit = Optional.empty();
    try {
      astcdCompilationUnit = p.parse(getFilePath(filePath));
    } catch (IOException e) {
      fail("Exception during parsing");
    }
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    // Trafos after parsing
    new CD4CodeAfterParseTrafo().transform(node);
    return node;
  }

  protected void prepareST(ASTCDCompilationUnit node) {
    // First pass for symbol table
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    // Second pass for symbol table
    final CD4CodeTraverser traverser = new CD4CodeSymbolTableCompleter(node).getTraverser();
    node.accept(traverser);
    checkLogError();

    // transformations that need an already created symbol table
    final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
    final CDAssociationTraverser traverserA = CD4CodeMill.traverser();
    traverserA.add4CDAssociation(cdAssociationRoleNameTrafo);
    traverserA.setCDAssociationHandler(cdAssociationRoleNameTrafo);
    cdAssociationRoleNameTrafo.transform(node);
    checkLogError();
  }
}
