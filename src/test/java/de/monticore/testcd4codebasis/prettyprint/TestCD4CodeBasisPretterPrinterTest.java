/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcd4codebasis.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TestCD4CodeBasisPretterPrinterTest extends TestBasis {
  TestCD4CodeBasisParser p = new TestCD4CodeBasisParser();
  TestCD4CodeBasisPrettyPrinterDelegator printer = new TestCD4CodeBasisPrettyPrinterDelegator();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4codebasis/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
