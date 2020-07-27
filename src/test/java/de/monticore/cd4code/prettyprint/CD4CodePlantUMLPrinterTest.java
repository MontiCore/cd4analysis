/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class CD4CodePlantUMLPrinterTest extends TestBasis {
  @Test
  public void testLocally() throws IOException {
    final String fileName = new File(getFilePath("cd4code/parser/MyLife2.cd")).toString();
    PlantUMLUtil.printCD2PlantUMLLocally(fileName, getTmpFilePath("MyLife2.svg"), new PlantUMLConfig());
    assertTrue(modelFileExists(getTmpFilePath("MyLife2.svg")));
  }

  @Test
  public void testModelFileLocally() throws IOException {
    final String fileName = new File(getFilePath("cd4code/parser/MyLife2.cd")).toString();
    final PlantUMLConfig plantUMLConfig = new PlantUMLConfig(true, true, true, true, false, -1, -1, false, true, false);
    PlantUMLUtil.printCD2PlantUMLModelFileLocally(fileName, getTmpFilePath("MyLife2.puml"), plantUMLConfig);
    assertTrue(modelFileExists(getTmpFilePath("MyLife2.puml")));
  }

  @Ignore
  @Test
  public void testServer() throws IOException {
    final String fileName = new File(getFilePath("cd4code/parser/MyLife2.cd")).toString();
    PlantUMLUtil.printCD2PlantUMLServer(fileName, getTmpFilePath("MyLife2.svg"), new PlantUMLConfig());
    assertTrue(modelFileExists(getTmpFilePath("MyLife2.svg")));
  }
}
