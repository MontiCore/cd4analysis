/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symtabdefinition.SymTabDefinitionTestBasis;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * SymTabDefinition inherits sealed, non-sealed, and permits from CD4Code, so it checks them too.
 */
public class SealedCoCosTest extends SymTabDefinitionTestBasis {
  
  @Test
  public void testInvalid() {
    final ASTCDCompilationUnit ast = parse("stdefinition/cocos/SealedInvalid.cd");
    prepareST(ast);
    new SymTabDefinitionCoCos().getCheckerForAllCoCos().checkAll(ast);
    assertEquals(List.of("0xCDC92", "0xCDC96", "0xCDC99", "0xCDC9B"), Log.getFindings().stream()
        .filter(Finding::isError).map(f -> f.getMsg().substring(0, 7)).sorted().collect(Collectors
            .toList()), getJoinedErrors());
  }
  
  @Override
  public void after() {}
  
}
