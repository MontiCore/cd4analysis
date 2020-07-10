/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd;

import de.monticore.cd.cli.CDCLI;
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
  public void testCLI() throws IOException {
    final File file = new File("src/test/resources/de/monticore/cd4code/parser/MyLife2.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();
    CDCLI.main(new String[] { "-m", fileName });

    assertEquals("Parsing and CoCo check Successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testHelp() throws IOException {
    final File file = new File("src/test/resources/de/monticore/cd4code/parser/MyLife2.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();
    CDCLI.main(new String[] { "-m", fileName, "-h" });

    assertTrue(getOut().startsWith("Usage: cd-"));
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testCLINoBuiltInTypes() throws IOException {
    final File file = new File("src/test/resources/de/monticore/cd4code/parser/MyLife2.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    thrown.expect(NullPointerException.class);
    CDCLI.main(new String[] { "-m", fileName, "-t" });

    assertEquals("Parsing and CoCo check Successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }
}
