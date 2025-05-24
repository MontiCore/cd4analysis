/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.jupiter.api.Test;

/** Testing that the models can be parsed. */
public class ParserTest extends CDDiffTestBasis {

  @Test
  public void test() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/test/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
    assertNotNull(cd1);
  }
}
