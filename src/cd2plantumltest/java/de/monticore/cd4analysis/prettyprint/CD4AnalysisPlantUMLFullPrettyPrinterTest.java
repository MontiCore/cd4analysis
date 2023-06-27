
package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis.CD4AnalysisTool;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import net.sourceforge.plantuml.graph2.Plan;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisPlantUMLFullPrettyPrinterTest extends CD4AnalysisTestBasis {

  protected CD4AnalysisPlantUMLFullPrettyPrinter printer;
  protected CD4AnalysisParser p;

  @BeforeEach
  public void initObjects() {

    // printer = new CD4AnalysisFullPrettyPrinter(new IndentPrinter());
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter();
    p = new CD4AnalysisParser();

  }

  /**
   * Tests the pretty printed result of each test case against the manually created expected result.
   * PlantUMLConfig is set to enable all options (getAtt, gettAssoc,getRoles, getCards, getModifier)
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(strings =
    {"AllModifiers",
      "Attributes",
      "BasicAssociations",
      "ClassTypes",
      "Compositions",
      "Enums",
      "Extensions",
      "FullExample",
      "Methods",
      "NamedAssociations",
      "Packages",
      "QuantifiedAssociations",
      "QuantifiedNamedAssociations"
  })
  public void completeModel(String input) throws IOException {
    //String outputPath = "C:\\Users\\yvonn\\OneDrive\\Documents\\uni\\Master\\2.Semester\\SLE\\model.svg";
    String outputPath = Paths.get(getFilePath("plantuml/results/" + input +".svg")).toAbsolutePath().toString();

    String resultPath = getFilePath("plantuml/expected/"+input+".svg");
    String filePath = getFilePath("cd4analysis/prettyprint/"+input+".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
      p.parseCDCompilationUnit(filePath);

    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);
    PlantUMLConfig plantUMLConfig = new PlantUMLConfig(true,true,true,true,true);

    PlantUMLUtil.printCD2PlantUMLLocally(astcdCompilationUnit,outputPath, plantUMLConfig);
    String output = printer.prettyprintWithConfig(node,plantUMLConfig);
    System.out.println(output);

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
