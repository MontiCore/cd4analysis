/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdinterfaceandenum.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdinterfaceandenum._parser.TestCDInterfaceAndEnumParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDInterfaceAndEnumPretterPrinter extends TestBasis {
  TestCDInterfaceAndEnumParser p = new TestCDInterfaceAndEnumParser();
  TestCDInterfaceAndEnumPrettyPrinterDelegator printer = new TestCDInterfaceAndEnumPrettyPrinterDelegator();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdinterfaceandenum/parser/PrettyPrinter.cd"));
    System.out.println(printer.prettyprint(astcdCompilationUnit.get()));
  }
}
