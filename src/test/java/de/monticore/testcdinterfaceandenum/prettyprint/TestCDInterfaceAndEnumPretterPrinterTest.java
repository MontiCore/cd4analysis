/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDInterfaceAndEnumPretterPrinterTest extends TestBasis {
  final TestCDInterfaceAndEnumParser p = new TestCDInterfaceAndEnumParser();
  final CDInterfaceAndEnumFullPrettyPrinter printer = new CDInterfaceAndEnumFullPrettyPrinter(new IndentPrinter());

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdinterfaceandenum/parser/PrettyPrinter.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
