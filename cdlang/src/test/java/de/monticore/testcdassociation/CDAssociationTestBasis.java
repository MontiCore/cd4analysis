/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation;

import static org.junit.Assert.fail;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdassociation._cocos.CDAssociationCoCoChecker;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationArtifactScope;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationGlobalScope;
import de.monticore.testcdassociation._visitor.TestCDAssociationTraverser;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Before;

public class CDAssociationTestBasis extends TestBasis {
  protected TestCDAssociationParser p;
  protected CDAssociationCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    TestCDAssociationMill.reset();
    TestCDAssociationMill.init();
    p = TestCDAssociationMill.parser();

    final ITestCDAssociationGlobalScope globalScope = TestCDAssociationMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);

    coCoChecker = new CDAssociationCoCoChecker();
  }

  protected ASTCDCompilationUnit parseModel(String modelName) {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit;
    try {
      astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath(modelName));
    } catch (IOException e) {
      fail("Failed while parsing the model `" + getFilePath(modelName) + "': " + e.getMessage());
      return null;
    }

    checkNullAndPresence(p, astcdCompilationUnit);
    return astcdCompilationUnit.get();
  }

  protected void afterParseTrafo(ASTCDCompilationUnit ast) {
    // first transform every direct composition into teal ones
    TestCDAssociationTraverser t1 = TestCDAssociationMill.traverser();
    CDAssociationDirectCompositionTrafo dirComp = new CDAssociationDirectCompositionTrafo();
    t1.add4CDBasis(dirComp);
    t1.add4CDAssociation(dirComp);
    ast.accept(t1);

    // in a second pass, a missing role names
    TestCDAssociationTraverser t2 = TestCDAssociationMill.traverser();
    CDAssociationRoleNameTrafo roleName = new CDAssociationRoleNameTrafo();
    t2.add4CDAssociation(roleName);
    ast.accept(t2);
  }

  protected ITestCDAssociationArtifactScope createSymTab(
      ASTCDCompilationUnit astcdCompilationUnit) {
    final ITestCDAssociationArtifactScope st =
        TestCDAssociationMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    checkLogError();
    return st;
  }

  protected void completeSymTab(ASTCDCompilationUnit ast) {
    TestCDAssociationTraverser t = TestCDAssociationMill.traverser();

    // add 4 cd basis
    CDBasisSymbolTableCompleter symTabCompBasis = new CDBasisSymbolTableCompleter();
    t.add4CDBasis(symTabCompBasis);
    t.add4OOSymbols(symTabCompBasis);

    // add 4 cd association
    CDAssociationSymbolTableCompleter symTabCompAssoc = new CDAssociationSymbolTableCompleter();
    t.add4CDAssociation(symTabCompAssoc);
    t.setCDAssociationHandler(symTabCompAssoc);

    ast.accept(t);
  }
}
