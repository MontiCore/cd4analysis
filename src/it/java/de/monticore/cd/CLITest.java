/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd;

import de.monticore.cd.cli.CDCLI;
import org.apache.commons.cli.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CLITest extends OutTestBasis {
  @SuppressWarnings("deprecation")
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCLI() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();
    CDCLI.main(new String[] { "-i", fileName, "-f" });

    assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testHelp() throws IOException, ParseException {
    final File file = new File(getFilePath("ccd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();
    CDCLI.main(new String[] { "-i", fileName, "-h", "-f" });

    assertTrue(getOut(), getOut().startsWith("usage: cd-"));
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testCLINoBuiltInTypes() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    CDCLI.main(new String[] { "-i", fileName, "-t", "false", "-f" });

    assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testModelPath() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    CDCLI.main(new String[] { "-i", fileName, "-f" });

    assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testCLIPlantUML() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CDCLI.main(new String[] { "-i", fileName, "-f", "--pp", getTmpFilePath("Complete.puml"), "--puml" });

    assertTrue(modelFileExists(getTmpFilePath("Complete.puml")));
  }

  @Test
  public void testCLIPlantUML2() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CDCLI.main(new String[] { "-i", fileName, "-f", "--pp", getTmpFilePath("Complete.svg"), "--puml", "--svg", "--showAttr" });

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }
}
