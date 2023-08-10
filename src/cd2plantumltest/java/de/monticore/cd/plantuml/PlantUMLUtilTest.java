package de.monticore.cd.plantuml;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;


public class PlantUMLUtilTest extends CD4AnalysisTestBasis {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testWriteCdToPlantUmlModelFile() {
    String pathCD = getFilePath("cd4analysis/prettyprint/QuantifiedNamedAssociations.cd");
    Path outputPath = Paths.get(folder.getRoot().getAbsolutePath(), "QuantifiedNamedAssociations.puml");
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

  @Test
  public void testWriteCdToPlantUmlSvg() {
    String pathCD = getFilePath("cd4analysis/prettyprint/QuantifiedNamedAssociations.cd");
    Path outputPath = Paths.get(folder.getRoot().getAbsolutePath(), "QuantifiedNamedAssociations.svg");
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
