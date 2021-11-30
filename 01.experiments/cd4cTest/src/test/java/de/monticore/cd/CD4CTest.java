/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd;

import de.monticore.cd4code.CD4CodeCLI;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CD4CTest  {

  public final static String PATH = "src/test/resources/de/monticore/";

  public static String getFilePath(String path) {
    return Paths.get(PATH + path).toString();
  }

  @Test
  public void testCD4C() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();
    CD4CodeCLI.main(new String[] { "-i", fileName, "-o", "target/generated", "-ct", "de.monticore.cd.CD2Java" });

    //assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertEquals(0, Log.getErrorCount());
  }

}
