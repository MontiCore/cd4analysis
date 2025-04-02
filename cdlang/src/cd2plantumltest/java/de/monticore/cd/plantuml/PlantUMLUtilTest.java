package de.monticore.cd.plantuml;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class PlantUMLUtilTest extends CD4AnalysisTestBasis {

  /**
   * Checks if the PlantUMLUtil.writeCdToPlantUmlModelFile works correctly and does print a
   * .plantuml file into the correct output folder.
   */
  @Test
  public void testWriteCdToPlantUmlModelFile(@TempDir Path tempDir) throws IOException {
    String pathCD = getFilePath("cd4analysis/prettyprint/QuantifiedNamedAssociations.cd");
    Path outputPath = tempDir.resolve("QuantifiedNamedAssociations.plantuml");
    PlantUMLConfig config = new PlantUMLConfig();

    try {
      PlantUMLUtil.writeCdToPlantUmlModelFile(pathCD, outputPath, config);
    } catch (IOException ex) {
      fail(ex.getMessage());
    }

    assertTrue(outputPath.toFile().exists());

    try {
      File file = new File(outputPath.toUri());
      // Read and strip empty lines
      String puml = FileUtils.readFileToString(file, "UTF-8").replaceAll("(?m)^[ \t]*\r?\n", "");
      assertNotNull(puml);
      assertNotEquals("", puml);
      assertTrue(puml.startsWith("@startuml"));
      assertTrue(puml.endsWith("@enduml\n"));
      // Original pretty printer printed twice
      assertEquals(1, StringUtils.countMatches(puml, "@startuml"));
      assertEquals(1, StringUtils.countMatches(puml, "@enduml"));
    } catch (IOException ex) {
      fail(ex.getMessage());
    }
  }

  /**
   * Checks if the PlantUMLUtil.writeCdToPlantUmlModelFile works correctly and does print a .svg
   * file into the correct output folder.
   */
  @Test
  public void testWriteCdToPlantUmlSvg(@TempDir Path tempDir) {
    String pathCD = getFilePath("cd4analysis/prettyprint/QuantifiedNamedAssociations.cd");
    Path outputPath = tempDir.resolve("QuantifiedNamedAssociations.svg");
    PlantUMLConfig config = new PlantUMLConfig();

    try {
      PlantUMLUtil.writeCdToPlantUmlSvg(pathCD, outputPath, config);
    } catch (IOException ex) {
      fail(ex.getMessage());
    }

    assertTrue(outputPath.toFile().exists());

    try {
      File file = new File(outputPath.toUri());
      // Read and strip empty lines
      String puml = FileUtils.readFileToString(file, "UTF-8").replaceAll("(?m)^[ \t]*\r?\n", "");
      assertNotNull(puml);
      assertNotEquals("", puml);
      assertEquals(0, StringUtils.countMatches(puml, "Syntax Error"));
      assertEquals(0, StringUtils.countMatches(puml, "Cannot find Graphviz"));
    } catch (IOException ex) {
      fail(ex.getMessage());
    }
  }
}
