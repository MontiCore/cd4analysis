/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDInterfaceExtendsOnlyInterfaces;
import de.monticore.testcdinterfaceandenum.CDInterfaceAndEnumTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDInterfaceExtendsOnlyInterfacesTest extends CDInterfaceAndEnumTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDInterfaceExtendsOnlyInterfaces());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdinterfaceenum/cocos/Valid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDInterfaceExtendsOnlyInterfaces());
    final Optional<ASTCDCompilationUnit> optAST =
        p.parse(getFilePath("cdinterfaceenum/cocos/CDInterfaceExtendsOnlyInterfacesInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDCF6"));
  }

  @After
  public void after() {}
}
