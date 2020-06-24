/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CD4AnalysisPrettyPrinterTest extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();
  CD4AnalysisPrettyPrinter printer = new CD4AnalysisPrettyPrinter();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/Simple.cd"));

    checkNullAndPresence(p, astcdCompilationUnit);
    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }
}
