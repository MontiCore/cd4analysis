package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis.CD4AnalysisTool;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
   * Test to check the pretty printed result of each tested CD4A model against the manually created
   * expected PlantUML result. PlantUMLConfig is set to enable all options (getAtt, getAssoc,
   * getRoles, getCards, getModifier)
   *
   * @param input name of the model tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void completeModel(String input) throws IOException {
    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/allOptions/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n" + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected:\n" + expected);

    Assert.assertEquals(expected, output);
  }

  /**
   * Tests the pretty printed result of each tested CD4A model against the manually created expected
   * PlantUML result. PlantUMLConfig is set to disable all options (getAtt, getAssoc, getRoles,
   * getCards, getModifier)
   *
   * @param input name of the CD4A model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithOptionsFalse(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(false, false, false, false, false);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/noOptions/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }

  /**
   * Test to check if CD4A models are pretty printed correctly in PlantUML when using a
   * PlantUmlConfig where showAtt is set to true but showModifier, showAssoc, showRoles and showCard
   * are set to false
   *
   * @param input name of the CD4A model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithOnlyShowAtt(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(true, false, false, false, false);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/showAttributes/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }

  /**
   * Test to check if CD4A models are pretty printed correctly in PlantUML when using a
   * PlantUmlConfig where showAssoc is set to true but showAtt, showModifier, showRoles and showCard
   * are set to false
   *
   * @param input name of the CD4A model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithOnlyShowAssociations(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(false, true, false, false, false);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/showAssociations/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }

  /**
   * Test to check if CD4A models are printed correctly when using a PlantUmlConfig where showRoles
   * is set to true but showAtt, showAssoc, showModifier and showCard are set to false
   *
   * @param input name of the model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithOnlyShowRoles(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(false, false, true, false, false);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/showRoles/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }

  /**
   * Test to check if CD4A models are printed correctly when using a PlantUmlConfig where showCard
   * is set to true but showAtt, showAssoc, showRoles and showModifier are set to false
   *
   * @param input name of the CD4A model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithOnlyShowCardinalities(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(false, false, false, true, false);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/showCardinalities/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }

  /**
   * Test to check if CD4A models are printed correctly when using a PlantUmlConfig where
   * showModifier is set to true but showAtt, showAssoc, showRoles and showCard are set to false
   *
   * @param input name of the CD4A model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithOnlyShowModifier(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(false, false, false, false, true);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/showModifier/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }

  /**
   * Test to check if CD4A models are printed correctly when using a PlantUmlConfig where
   * showModifier and showAtt are set to true but showAssoc, showRoles and showCard are set to false
   *
   * @param input name of the CD4A model that is tested in a run
   * @throws IOException
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "AllModifiers",
        "Attributes",
        "BasicAssociations",
        "ClassTypes",
        "Compositions",
        "Enums",
        "Extensions",
        "FullExample",
        "NamedAssociations",
        "Packages",
        "QuantifiedAssociations",
        "QuantifiedNamedAssociations"
      })
  public void modelWithShowAttShowModifier(String input) throws IOException {
    plantUMLConfig = new PlantUMLConfig(true, false, false, false, true);
    util = new PlantUMLPrettyPrintUtil(new IndentPrinter(), plantUMLConfig);
    printer = new CD4AnalysisPlantUMLFullPrettyPrinter(util);

    Path expectedPath =
        Paths.get(getFilePath("plantuml/expected/showAttshowMod/" + input + ".plantuml"));
    String filePath = getFilePath("cd4analysis/prettyprint/" + input + ".cd");

    var tool = new CD4AnalysisTool();
    tool.init();

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(filePath);
    Assertions.assertTrue(astcdCompilationUnit.isPresent());

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    tool.createSymbolTable(node);

    String output = printer.prettyprint(node);
    output = output.replaceAll("(?m)^[ \t]*\r?\n", "");
    System.out.println("Output:\n " + output);

    String expected = Files.readString(expectedPath, Charset.defaultCharset());
    expected = expected.replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\r\n", "\n");
    System.out.println("Expected: \n " + expected);

    Assertions.assertEquals(expected, output);
  }
}
