/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDAttributeInInterfaceIsPublic;
import de.monticore.testcdinterfaceandenum.CDInterfaceAndEnumTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAttributeInInterfaceIsPublicTest extends CDInterfaceAndEnumTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeInInterfaceIsPublic());
    Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdinterfaceenum/cocos/CDAttributeInInterfaceIsPublicValid.cd"));
    assertTrue(optAST.isPresent());
    ASTCDCompilationUnit ast = optAST.get();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeInInterfaceIsPublic());
    Optional<ASTCDCompilationUnit> optAST = p.parse(getFilePath("cdinterfaceenum/cocos/CDAttributeInInterfaceIsPublicInvalid.cd"));
    assertTrue(optAST.isPresent());
    ASTCDCompilationUnit ast = optAST.get();
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xCDCF7"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xCDCF7"));
    Log.getFindings().clear();
  }

}
