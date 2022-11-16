/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.prettyprint;

import static org.junit.Assert.assertTrue;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import de.monticore.cd4code.CD4CodeTestBasis;
import java.io.File;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CD4CodePlantUMLPrinterTest extends CD4CodeTestBasis {
  @Test
  public void testLocally1() throws IOException {
    final String fileName = new File(getFilePath("cd4analysis/parser/MyLife.cd")).toString();
    PlantUMLUtil.printCD2PlantUMLLocally(
        fileName, getTmpFilePath("MyLife.svg"), new PlantUMLConfig());
    assertTrue(modelFileExists(getTmpFilePath("MyLife.svg")));
  }

  @Test
  public void testLocally2() throws IOException {
    final String fileName = new File(getFilePath("cd4code/parser/MyLife2.cd")).toString();
    PlantUMLUtil.printCD2PlantUMLLocally(
        fileName, getTmpFilePath("MyLife2.svg"), new PlantUMLConfig());
    assertTrue(modelFileExists(getTmpFilePath("MyLife2.svg")));
  }

  @Test
  public void testModelFileLocally() throws IOException {
    final String fileName = new File(getFilePath("cd4code/parser/MyLife2.cd")).toString();
    final PlantUMLConfig plantUMLConfig =
        new PlantUMLConfig(true, true, true, true, false, -1, -1, false, true, false);
    PlantUMLUtil.printCD2PlantUMLModelFileLocally(
        fileName, getTmpFilePath("MyLife2.puml"), plantUMLConfig);
    assertTrue(modelFileExists(getTmpFilePath("MyLife2.puml")));
  }
}
