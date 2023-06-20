package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import net.sourceforge.plantuml.graph2.Plan;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CD4AnalysisPlantUMLFullPrettyPrinterTest extends CD4AnalysisTestBasis {

  protected CD4AnalysisPlantUMLFullPrettyPrinter printer;
  protected CD4AnalysisParser p;

  @Before
  public void initObjects() {

    // printer = new CD4AnalysisFullPrettyPrinter(new IndentPrinter());
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter();
    p = new CD4AnalysisParser();

  }

  @Test
  public void completeModel() throws IOException {

    String filePath = getFilePath("cd4analysis/parser/Simple.cd");

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
      p.parseCDCompilationUnit(filePath);

    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    //String outputPath = PlantUMLUtil.printCD2PlantUMLLocally(astcdCompilationUnit, "./", new PlantUMLConfig());
    String output = printer.prettyprint(node);

    // new CD4AnalysisAfterParseTrafo().transform(node);
    // String output = printer.prettyprint(node);
//
    // final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
    //   p.parse_StringCDCompilationUnit(output);
    // checkNullAndPresence(p, astcdCompilationUnitReParsed);

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
