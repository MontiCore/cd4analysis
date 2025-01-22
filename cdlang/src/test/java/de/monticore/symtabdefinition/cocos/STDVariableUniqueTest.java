/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symtabdefinition.SymTabDefinitionTestBasis;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class STDVariableUniqueTest extends SymTabDefinitionTestBasis {

  @Test
  public void testValid() throws IOException {
    coCoChecker.addCoCo(new STDVariableUnique());
    final ASTCDCompilationUnit ast = parse("stdefinition/cocos/STDVariableUniqueValid.cd");
    prepareST(ast);
    coCoChecker.checkAll(ast);
    checkLogError();
  }

  @Test
  public void testInvalid() throws IOException {
    coCoChecker.addCoCo(new STDVariableUnique());
    final ASTCDCompilationUnit ast = parse("stdefinition/cocos/STDVariableUniqueInvalid.cd");
    prepareST(ast);
    coCoChecker.checkAll(ast);
    assertEquals(2, Log.getFindings().size());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xFDC23"));
    assertTrue(Log.getFindings().get(1).getMsg().startsWith("0xFDC23"));
  }

  @Override
  public void after() {}
}
