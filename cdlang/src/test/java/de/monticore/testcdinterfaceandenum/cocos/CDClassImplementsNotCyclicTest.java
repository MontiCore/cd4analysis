/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.cocos; /* (c) https://github.com/MontiCore/monticore */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDClassImplementsNotCyclic;
import de.monticore.testcdinterfaceandenum.CDInterfaceAndEnumTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class CDClassImplementsNotCyclicTest extends CDInterfaceAndEnumTestBasis {
  
  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDClassImplementsNotCyclic());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdinterfaceenum/cocos/Valid.cd"));
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
    coCoChecker.addCoCo(new CDClassImplementsNotCyclic());
    final Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath(
        "cdinterfaceenum/cocos/CDClassImplementsNotCyclicInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    Log.getFindings().clear();
    createSymTab(ast);
    completeSymTab(ast);
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDC09"));
  }
  
  @AfterEach
  public void after() {}
  
}
