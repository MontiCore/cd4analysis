/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis.prettyprint;

import static org.junit.Assert.assertTrue;

import de.monticore.cd.TestBasis;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TestCD4CodeBasisPretterPrinterTest extends CD4CodeBasisTestBasis {
  final CD4CodeBasisFullPrettyPrinter printer =
      new CD4CodeBasisFullPrettyPrinter(new IndentPrinter());

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cd4codebasis/parser/Simple.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
        p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);

    // check deep equals
    assertTrue(astcdCompilationUnit.get().deepEquals(astcdCompilationUnitReParsed.get()));
  }
}
