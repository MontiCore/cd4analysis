
package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis.CD4AnalysisTool;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import net.sourceforge.plantuml.graph2.Plan;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisPlantUMLFullPrettyPrinterTest extends CD4AnalysisTestBasis {

  protected CD4AnalysisPlantUMLFullPrettyPrinter printer;
  protected CD4AnalysisParser p;
  protected PlantUMLPrettyPrintUtil util;
  protected PlantUMLConfig plantUMLConfig;

  @BeforeEach
  public void initObjects() {

    plantUMLConfig = new PlantUMLConfig(true, true, true, true, true);


    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);
    p = new CD4AnalysisParser();

  }

  /**
   * Tests the pretty printed result of each test case against the manually created expected result.
   * PlantUMLConfig is set to enable all options (getAtt, gettAssoc,getRoles, getCards, getModifier)
   *
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
      //"Methods",
      "NamedAssociations",
      "Packages",
      "QuantifiedAssociations",
      "QuantifiedNamedAssociations"
    })
  public void completeModel(String input) throws IOException {
    //String input = "FullExample";

    String outputPath = Paths.get(getFilePath("plantuml/results/" + input + ".svg")).toAbsolutePath().toString();

    Path expectedPath = Paths.get(getFilePath("plantuml/expected/allOptions/" + input + ".txt"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");


    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
      p.parseCDCompilationUnit(filePath);

    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    PlantUMLUtil.printCD2PlantUMLLocally(astcdCompilationUnit, outputPath, plantUMLConfig);
    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    //String output = Files.readString(Paths.get(outputPath));
    //output = output.substring(output.indexOf('\n')+1).split("\r?\nPlantUML version")[0].replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n"+output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Expected:\n"+expected);

    Assert.assertEquals(output, expected);
  }

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
      //"Methods",
      "NamedAssociations",
      "Packages",
      "QuantifiedAssociations",
      "QuantifiedNamedAssociations"
    })
  public void ModelWithOptionsFalse(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(false, false, false, false, false);

    //String input = "FullExample";
    String outputPath = Paths.get(getFilePath("plantuml/results/" + input + ".svg")).toAbsolutePath().toString();

    Path expectedPath = Paths.get(getFilePath("plantuml/expected/allOptions" + input + ".txt"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");


    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
      p.parseCDCompilationUnit(filePath);

    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    PlantUMLUtil.printCD2PlantUMLLocally(astcdCompilationUnit, outputPath, plantUMLConfig);
   // String output = printer.prettyprint(node);
    String output = Files.readString(Paths.get(outputPath));
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n "+output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Expected: \n " +expected);

    Assert.assertEquals(output, expected);
  }


}
