/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCDBasisPretterPrinterTest extends TestBasis {
  final TestCDBasisParser p = new TestCDBasisParser();
  final CDBasisFullPrettyPrinter printer = new CDBasisFullPrettyPrinter(new IndentPrinter());

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cdbasis/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
        p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
