/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumCoCoChecker;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.ICDInterfaceAndEnumGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.testcdinterfaceandenum._symboltable.ITestCDInterfaceAndEnumArtifactScope;
import de.monticore.testcdinterfaceandenum._visitor.TestCDInterfaceAndEnumTraverser;
import org.junit.Before;

import static org.junit.Assert.fail;

public class CDInterfaceAndEnumTestBasis extends TestBasis {
  protected TestCDInterfaceAndEnumParser p;
  protected CDInterfaceAndEnumCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    TestCDInterfaceAndEnumMill.reset();
    TestCDInterfaceAndEnumMill.init();
    p = TestCDInterfaceAndEnumMill.parser();

    final ICDInterfaceAndEnumGlobalScope globalScope = CDInterfaceAndEnumMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    coCoChecker = new CDInterfaceAndEnumCoCoChecker();
  }

  protected ASTCDCompilationUnit parseModel(String modelName) {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit;
    try {
      astcdCompilationUnit = p.parseCDCompilationUnit(Paths.get(modelName).toString());
    } catch (IOException e) {
      fail("Failed while parsing the model `" + getFilePath(modelName) + "': " + e.getMessage());
      return null;
    }

    checkNullAndPresence(p, astcdCompilationUnit);
    return astcdCompilationUnit.get();
  }

  protected ITestCDInterfaceAndEnumArtifactScope createSymTab(ASTCDCompilationUnit astcdCompilationUnit) {
    final ITestCDInterfaceAndEnumArtifactScope st =
      TestCDInterfaceAndEnumMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    checkLogError();
    return st;
  }

  protected void completeSymTab(ASTCDCompilationUnit ast) {
    TestCDInterfaceAndEnumTraverser t = TestCDInterfaceAndEnumMill.traverser();

    // add 4 cd basis
    CDBasisSymbolTableCompleter symTabCompBasis = new CDBasisSymbolTableCompleter();
    t.add4CDBasis(symTabCompBasis);
    t.add4OOSymbols(symTabCompBasis);

    // add 4 cd interface and enum
    CDInterfaceAndEnumSymbolTableCompleter symTabCompIntEnum = new CDInterfaceAndEnumSymbolTableCompleter();
    t.add4CDInterfaceAndEnum(symTabCompIntEnum);

    ast.accept(t);
  }

}
