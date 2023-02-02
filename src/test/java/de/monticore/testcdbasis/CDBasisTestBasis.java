/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.testcdbasis._cocos.TestCDBasisCoCoChecker;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._symboltable.ITestCDBasisArtifactScope;
import de.monticore.testcdbasis._symboltable.ITestCDBasisGlobalScope;
import de.monticore.testcdbasis._visitor.TestCDBasisTraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.nio.file.Paths;
import org.junit.Before;

public class CDBasisTestBasis extends TestBasis {
  protected TestCDBasisParser p;
  protected TestCDBasisCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    // reset the log
    LogStub.init();
    Log.enableFailQuick(false);

    TestCDBasisMill.reset();
    TestCDBasisMill.init();

    p = new TestCDBasisParser();

    final ITestCDBasisGlobalScope globalScope = TestCDBasisMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BasicSymbolsMill.initializePrimitives();

    coCoChecker = new TestCDBasisCoCoChecker();
  }

  protected ITestCDBasisArtifactScope createSymTab(ASTCDCompilationUnit ast) {
    ITestCDBasisArtifactScope as = TestCDBasisMill.scopesGenitorDelegator().createFromAST(ast);
    return as;
  }

  protected void completeSymTab(ASTCDCompilationUnit ast) {
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisSymbolTableCompleter symTabComp =
      new CDBasisSymbolTableCompleter();
    t.add4CDBasis(symTabComp);
    t.add4OOSymbols(symTabComp);
    ast.accept(t);
  }
}
