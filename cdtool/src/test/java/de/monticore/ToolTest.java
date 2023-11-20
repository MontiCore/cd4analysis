/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.assertTrue;

import de.monticore.cd.OutTestBasis;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ToolTest extends OutTestBasis {
  @SuppressWarnings("deprecation")
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  protected static final String TOOL_PATH = "src/test/resources/de/monticore/";

  @Test
  public void testMerge() {
    final String cd1 = "src/test/doc/MyEmployees2.cd";
    final String cd2 = "src/test/doc/MyEmployees1.cd";
    CD4CodeTool.main(new String[] {"-i", cd1, "--merge", cd2});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testTool() throws IOException, ParseException {
    final File file = new File("src/test/resources/de/monticore/cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();
    CD4CodeTool.main(new String[] {"-i", fileName, "-f", "false"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testHelp() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();
    CD4CodeTool.main(new String[] {"-i", fileName, "-h", "-f", "false"});

    // assertTrue(getOut(), getOut().startsWith("usage: cd-"));
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testToolNoBuiltInTypes() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    CD4CodeTool.main(new String[] {"-i", fileName, "-nt", "-f", "false"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  public void testSymbolPath() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    CD4CodeTool.main(new String[] {"-i", fileName, "-f", "false"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr(), getErr().isEmpty());
  }

  @Test
  @Ignore // TODO MB
  public void testToolPlantUML() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "-f", "false", "-pp", getTmpFilePath("Complete.puml"), "-puml"
        });

    assertTrue(modelFileExists(getTmpFilePath("Complete.puml")));
  }

  @Test
  @Ignore // TODO MB
  public void testToolPlantUML2() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
        new String[] {
          "-i",
          fileName,
          "-f",
          "--pp",
          getTmpFilePath("Complete.svg"),
          "--puml",
          "--svg",
          "--showAttr"
        });

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }

  @Test
  @Ignore // TODO MB
  public void testToolPlantUML3() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
        new String[] {"-i", fileName, "-f", "--puml", "--svg", "-attr", "assoc", "--showRoles"});

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }
}
