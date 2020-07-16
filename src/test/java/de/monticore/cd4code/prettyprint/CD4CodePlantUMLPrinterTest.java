/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@Ignore
public class CD4CodePlantUMLPrinterTest {
  @Test
  public void testLocally() throws IOException {
    final String fileName = new File("src/test/resources/de/monticore/cd4code/parser/MyLife2.cd").toString();
    PlantUMLUtil.printCD2PlantUMLLocally(fileName, "MyLife2.svg", new PlantUMLConfig());
  }

  @Test
  public void testModelFileLocally() throws IOException {
    final String fileName = new File("src/test/resources/de/monticore/cd4code/parser/MyLife2.cd").toString();
    final PlantUMLConfig plantUMLConfig = new PlantUMLConfig(true, true, true, true, false, -1, -1, false, true, false);
    PlantUMLUtil.printCD2PlantUMLModelFileLocally(fileName, "MyLife2.puml", plantUMLConfig);
  }

  @Test
  public void testServer() throws IOException {
    final String fileName = new File("src/test/resources/de/monticore/cd4code/parser/MyLife2.cd").toString();
    PlantUMLUtil.printCD2PlantUMLServer(fileName, "MyLife2.svg", new PlantUMLConfig());
  }
}
