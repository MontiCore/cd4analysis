package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import org.junit.Before;

public class CD4AnalysisPlantUMLFullPrettyPrinterTest extends CD4AnalysisTestBasis {

  protected CD4AnalysisPlantUMLFullPrettyPrinter printer;
  protected CD4AnalysisParser p;

  @Before
  public void initObjects() {

    printer = new CD4AnalysisPlantUMLFullPrettyPrinter();
    p = new CD4AnalysisParser();

  }


  // @Test
 /* public void completeModel() throws IOException {

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
      p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/Simple.cd"));

    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);
    String output = printer.prettyprint(node);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
      p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }*/

}
