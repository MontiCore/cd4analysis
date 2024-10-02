/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.stdefinition.STDefinitionTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class STDVariableUniqueTest extends STDefinitionTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new STDVariableUnique());
    final Optional<ASTCDCompilationUnit> optAST =
        parser.parse(getFilePath("stdefinition/cocos/STDVariableUniqueValid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    prepareST(ast);
    checkLogError();
    coCoChecker.checkAll(ast);
    checkLogError();
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new STDVariableUnique());
    final Optional<ASTCDCompilationUnit> optAST =
        parser.parse(getFilePath("stdefinition/cocos/STDVariableUniqueInvalid.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    prepareST(ast);
    checkLogError();
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xFDC23"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xFDC23"));
  }

  @Override
  public void after() {}
}
