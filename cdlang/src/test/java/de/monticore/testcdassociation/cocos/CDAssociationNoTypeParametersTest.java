/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation.cocos.ebnf.CDAssociationNoTypeParameters;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationArtifactScope;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

public class CDAssociationNoTypeParametersTest extends CDAssociationTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationNoTypeParameters());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdassociation/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTabAndAddTypeVariable(ast, false);
    coCoChecker.checkAll(ast);
    checkLogError();
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAssociationNoTypeParameters());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdassociation/cocos/CDAssociationNoTypeParametersInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    createSymTabAndAddTypeVariable(ast, true);
    checkLogError();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC72"));
  }

  // adds TypeVariable to class A.
  protected void createSymTabAndAddTypeVariable(ASTCDCompilationUnit ast, boolean addVar) {
    ITestCDAssociationArtifactScope as = createSymTab(ast);
    completeSymTab(ast);
    if (addVar) {
      as.resolveCDType("A")
          .get()
          .getSpannedScope()
          .add(CDAssociationMill.typeVarSymbolBuilder().setName("T").build());
    }
  }

  @Override
  public void after() {}
}
