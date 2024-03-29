/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import java.io.IOException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDInterfaceAndEnumPretterPrinterTest extends TestBasis {
  final TestCDInterfaceAndEnumParser p = new TestCDInterfaceAndEnumParser();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cdinterfaceandenum/parser/PrettyPrinter.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = CDInterfaceAndEnumMill.prettyPrint(astcdCompilationUnit.get(), true);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
        p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
    Assert.assertTrue(astcdCompilationUnit.get().deepEquals(astcdCompilationUnitReParsed.get()));
  }
}
