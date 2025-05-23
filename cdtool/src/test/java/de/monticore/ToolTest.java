/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd.OutTestBasis;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ToolTest extends OutTestBasis {
  @SuppressWarnings("deprecation")
  protected static final String TOOL_PATH = "src/test/resources/de/monticore/";

  @Test
  public void testMerge() {
    final String cd1 = "src/test/doc/MyEmployees2.cd";
    final String cd2 = "src/test/doc/MyEmployees1.cd";
    CD4CodeTool.main(new String[] {"-i", cd1, "--merge", cd2});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr().isEmpty(), getErr());
  }

  @Test
  public void testMultiMerge() {
    final String cd1 = TOOL_PATH + "cdmerge/Person/A.cd";
    final String cd2 = TOOL_PATH + "cdmerge/Person/B.cd";
    final String cd3 = TOOL_PATH + "cdmerge/Person/C.cd";
    final String out = "target/generated/multi-merge";
    CD4CodeTool.main(new String[] {"-i", cd1, "--merge", cd2, cd3, "-o", out, "-pp", "Merge.cd"});
    assertTrue(getErr().isEmpty(), getErr());
  }

  @Test
  public void testTool() throws IOException, ParseException {
    final File file = new File("src/test/resources/de/monticore/cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();
    CD4CodeTool.main(new String[] {"-i", fileName, "-f", "false"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr().isEmpty(), getErr());
  }

  @Test
  public void testHelp() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();
    CD4CodeTool.main(new String[] {"-i", fileName, "-h", "-f", "false"});

    // assertTrue(getOut(), getOut().startsWith("usage: cd-"));
    assertTrue(getErr().isEmpty(), getErr());
  }

  @Test
  @Disabled // todo test has always(?) been broken and not correctly tested to far... -> requires
  // rework
  public void testToolNoBuiltInTypes() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    CD4CodeTool.main(new String[] {"-i", fileName, "-nt", "-f", "false"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr().isEmpty(), getErr());
  }

  @Test
  public void testSymbolPath() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    CD4CodeTool.main(new String[] {"-i", fileName, "-f", "false"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertTrue(getErr().isEmpty(), getErr());
  }

  @Test
  @Disabled // TODO MB
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
  @Disabled // TODO MB
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
  @Disabled // TODO MB
  public void testToolPlantUML3() throws IOException, ParseException {
    final File file = new File(TOOL_PATH + "cd/Complete.cd");
    assertTrue(file.exists());
    final String fileName = file.toString();

    // for now check for the NullPointerException
    CD4CodeTool.main(
        new String[] {"-i", fileName, "-f", "--puml", "--svg", "-attr", "assoc", "--showRoles"});

    assertTrue(modelFileExists(getTmpFilePath("Complete.svg")));
  }

  // anti-System.exit-shenanigans:
  // adds (and later removes) a system hook to fail
  // if Log.error is called resulting in a System.exit()
  Thread failOnExitHook;

  @BeforeEach
  public void setUpFailOnExitHook() {
    // This will(should) result in an indefinitely blocked process,
    // s.
    // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Runtime.html#exit(int)
    // This is not ideal (It would be ideal to fail the test),
    // without properly initialized Log, this will not happen.
    // This one ensures that, even if the log has not been initialized correctly,
    // an issue will be noticed.
    // This is only an additional failsafe (as the case has happened),
    // and should not actually occur.
    failOnExitHook =
        new Thread(
            () -> {
              System.exit(3);
            });
    Runtime.getRuntime().addShutdownHook(failOnExitHook);
  }

  @AfterEach
  public void removeFailOnExitHook() {
    Runtime.getRuntime().removeShutdownHook(failOnExitHook);
  }
}
