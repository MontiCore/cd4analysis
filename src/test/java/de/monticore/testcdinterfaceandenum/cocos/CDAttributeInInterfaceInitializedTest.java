/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDAttributeInInterfaceInitialized;
import de.monticore.cdinterfaceandenum.cocos.ebnf.CDAttributeInInterfaceNotPublic;
import de.monticore.testcdinterfaceandenum.CDInterfaceAndEnumTestBasis;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDAttributeInInterfaceInitializedTest extends CDInterfaceAndEnumTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeInInterfaceInitialized());
    Optional<ASTCDCompilationUnit> optAST =
      p.parse(getFilePath("cdinterfaceenum/cocos/CDAttributeInInterfaceInitializedValid.cd"));
    assertTrue(optAST.isPresent());
    ASTCDCompilationUnit ast = optAST.get();
    coCoChecker.checkAll(ast);
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new CDAttributeInInterfaceInitialized());
    Optional<ASTCDCompilationUnit> optAST =
      p.parse(getFilePath("cdinterfaceenum/cocos/CDAttributeInInterfaceInitializedInvalid.cd"));
    assertTrue(optAST.isPresent());
    ASTCDCompilationUnit ast = optAST.get();
    coCoChecker.checkAll(ast);
    assertEquals(1, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(CDAttributeInInterfaceInitialized.ERROR_CODE));
    Log.getFindings().clear();
  }

}
