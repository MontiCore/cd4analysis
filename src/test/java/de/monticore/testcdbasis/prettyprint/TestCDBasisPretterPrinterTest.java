/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDBasisPretterPrinterTest extends TestBasis {
  TestCDBasisParser p = new TestCDBasisParser();
  TestCDBasisPrettyPrinterDelegator printer = new TestCDBasisPrettyPrinterDelegator();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdbasis/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
