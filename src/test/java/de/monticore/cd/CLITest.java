/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import de.monticore.cd4code.CD4CodeTool;
import org.apache.commons.cli.ParseException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

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
    CD4CodeTool.main(new String[] { "-i", fileName, "-f", "false" });

    //assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testHelp() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();
    CD4CodeTool.main(new String[] { "-i", fileName, "-h", "-f", "false" });

    //assertTrue(getOut(), getOut().startsWith("usage: cd-"));
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testCLINoBuiltInTypes() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    CD4CodeTool.main(new String[] { "-i", fileName, "-t", "false", "-f", "false" });

    //assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testSymbolPath() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    CD4CodeTool.main(new String[] { "-i", fileName, "-f", "false" });

    //assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  @Ignore // TODO MB
  public void testCLIPlantUML() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(new String[] { "-i", fileName, "-f", "false", "-pp", "-puml", getTmpFilePath("Complete.puml") });

    assertTrue(modelFileExists(getTmpFilePath("Complete.puml")));
  }

  @Test
  @Ignore // TODO MB
  public void testCLIPlantUML2() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(new String[] { "-i", fileName, "-f", "--pp", getTmpFilePath("Complete.svg"), "--puml", "--svg", "--showAttr" });

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }

  @Test
  @Ignore // TODO MB
  public void testCLIPlantUML3() throws IOException, ParseException {
    final File file = new File(getFilePath("cd/Complete.cd"));
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(new String[] { "-i", fileName, "-f", "--puml", "--svg", "-attr", "assoc", "--showRoles" });

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }
}
