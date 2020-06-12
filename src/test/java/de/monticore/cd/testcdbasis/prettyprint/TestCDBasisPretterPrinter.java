/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.testcdbasis.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cd.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd.testcdbasis._parser.TestCDBasisParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCDBasisPretterPrinter extends TestBasis {
  TestCDBasisParser p = new TestCDBasisParser();
  TestCDBasisPrettyPrinterDelegator printer = new TestCDBasisPrettyPrinterDelegator();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cdbasis/parser/Complete.cd"));
    System.out.println(printer.prettyprint(astcdCompilationUnit.get()));
  }
}
